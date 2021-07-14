/*
 * Copyright (c) 2013, 2020 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.language.arguments;

import com.oracle.truffle.api.CompilerAsserts;
import org.truffleruby.collections.Memo;
import org.truffleruby.core.array.ArrayUtils;
import org.truffleruby.core.proc.RubyProc;
import org.truffleruby.core.string.StringUtils;
import org.truffleruby.language.FrameAndVariables;
import org.truffleruby.language.Nil;
import org.truffleruby.language.RubyBaseNode;
import org.truffleruby.language.control.FrameOnStackMarker;
import org.truffleruby.language.methods.DeclarationContext;
import org.truffleruby.language.methods.InternalMethod;
import org.truffleruby.language.threadlocal.SpecialVariableStorage;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public final class RubyArguments {

    public enum ArgumentIndicies {
        DECLARATION_FRAME, // 0
        CALLER_FRAME_OR_VARIABLES, // 1
        METHOD, // 2
        DECLARATION_CONTEXT, // 3
        FRAME_ON_STACK_MARKER, // 4
        SELF, // 5
        BLOCK, // 6
        CALLING_CONVENTION // 7
    }

    public static final int RUNTIME_ARGUMENT_COUNT = ArgumentIndicies.values().length;

    /** In most cases the DeclarationContext is the one of the InternalMethod. */
    public static Object[] pack(
            MaterializedFrame declarationFrame,
            Object callerFrameOrVariables,
            InternalMethod method,
            FrameOnStackMarker frameOnStackMarker,
            Object self,
            Object block,
            Object[] arguments) {
        return pack(
                declarationFrame,
                callerFrameOrVariables,
                method,
                method.getDeclarationContext(),
                frameOnStackMarker,
                self,
                block,
                arguments);
    }

    public static Object[] pack(
            MaterializedFrame declarationFrame,
            Object callerFrameOrVariables,
            InternalMethod method,
            DeclarationContext declarationContext,
            FrameOnStackMarker frameOnStackMarker,
            Object self,
            Object block,
            Object[] arguments) {
        assert assertValues(callerFrameOrVariables, method, declarationContext, self, block, arguments);

        // Default is empty string
        OptimizedKeywordArguments.CallingConvention callingConvention = OptimizedKeywordArguments.CallingConvention.UNOPTIMIZED;

        // We need to know the final array length to create the `packed` array
        if (OptimizedKeywordArguments.canArityUseOptimizedCallingConvention(method.getSharedMethodInfo().getArity())) {
            final Memo<OptimizedKeywordArguments.CallingConvention> flattenArgumentsFlagMemo = new Memo<>(callingConvention);
            arguments = OptimizedKeywordArguments.packOptimizedArguments(arguments, flattenArgumentsFlagMemo);
            callingConvention = flattenArgumentsFlagMemo.get();
        }

        final Object[] packed = new Object[RUNTIME_ARGUMENT_COUNT + arguments.length];

        packed[ArgumentIndicies.DECLARATION_FRAME.ordinal()] = declarationFrame;
        packed[ArgumentIndicies.CALLER_FRAME_OR_VARIABLES.ordinal()] = callerFrameOrVariables;
        packed[ArgumentIndicies.METHOD.ordinal()] = method;
        packed[ArgumentIndicies.DECLARATION_CONTEXT.ordinal()] = declarationContext;
        packed[ArgumentIndicies.FRAME_ON_STACK_MARKER.ordinal()] = frameOnStackMarker;
        packed[ArgumentIndicies.SELF.ordinal()] = self;
        packed[ArgumentIndicies.BLOCK.ordinal()] = block;

        // TODO: We need to somehow set it to a unique keyword arg signature, for now it's
        //  a String describing the type of Hash
        packed[ArgumentIndicies.CALLING_CONVENTION.ordinal()] = callingConvention;

        // Copy arguments into `packed`, with the correct `packed` size
        ArrayUtils.arraycopy(arguments, 0, packed, RUNTIME_ARGUMENT_COUNT, arguments.length);

        return packed;
    }

    public static boolean assertValues(
            Object callerFrameOrVariables,
            InternalMethod method,
            DeclarationContext declarationContext,
            Object self,
            Object block,
            Object[] arguments) {
        assert method != null;
        assert declarationContext != null;
        assert self != null;
        assert arguments != null;
        assert ArrayUtils.assertNoNullElement(arguments);

        assert callerFrameOrVariables == null ||
                callerFrameOrVariables instanceof MaterializedFrame ||
                callerFrameOrVariables instanceof SpecialVariableStorage ||
                callerFrameOrVariables instanceof FrameAndVariables;

        /* The block in the arguments array is always either a Nil or RubyProc. The provision of Nil if the caller
         * doesn't want to provide a block is done at the caller, because it will know the type of values within its
         * compilation unit.
         *
         * When you read the block back out in the callee, you'll therefore get a Nil or RubyProc. */
        assert block instanceof Nil || block instanceof RubyProc : block;

        return true;
    }

    // Getters

    public static MaterializedFrame getDeclarationFrame(Frame frame) {
        return (MaterializedFrame) frame.getArguments()[ArgumentIndicies.DECLARATION_FRAME.ordinal()];
    }

    public static MaterializedFrame getCallerFrame(Frame frame) {
        Object frameOrVariables = frame.getArguments()[ArgumentIndicies.CALLER_FRAME_OR_VARIABLES.ordinal()];
        if (frameOrVariables == null) {
            return null;
        } else if (frameOrVariables instanceof FrameAndVariables) {
            return ((FrameAndVariables) frameOrVariables).frame;
        } else if (frameOrVariables instanceof SpecialVariableStorage) {
            return null;
        } else {
            return (MaterializedFrame) frameOrVariables;
        }
    }

    public static SpecialVariableStorage getCallerStorage(Frame frame) {
        Object frameOrVariables = frame.getArguments()[ArgumentIndicies.CALLER_FRAME_OR_VARIABLES.ordinal()];
        if (frameOrVariables == null) {
            return null;
        } else if (frameOrVariables instanceof FrameAndVariables) {
            return ((FrameAndVariables) frameOrVariables).variables;
        } else if (frameOrVariables instanceof SpecialVariableStorage) {
            return (SpecialVariableStorage) frameOrVariables;
        } else {
            return null;
        }
    }

    public static InternalMethod getMethod(Frame frame) {
        return (InternalMethod) frame.getArguments()[ArgumentIndicies.METHOD.ordinal()];
    }

    public static DeclarationContext getDeclarationContext(Frame frame) {
        return (DeclarationContext) frame.getArguments()[ArgumentIndicies.DECLARATION_CONTEXT.ordinal()];
    }

    public static FrameOnStackMarker getFrameOnStackMarker(Frame frame) {
        return (FrameOnStackMarker) frame.getArguments()[ArgumentIndicies.FRAME_ON_STACK_MARKER.ordinal()];
    }

    public static Object getSelf(Frame frame) {
        return frame.getArguments()[ArgumentIndicies.SELF.ordinal()];
    }

    public static Object getBlock(Frame frame) {
        final Object block = frame.getArguments()[ArgumentIndicies.BLOCK.ordinal()];
        /* We put into the arguments array either a Nil or RubyProc, so that's all we'll get out at this point. */
        assert block instanceof Nil || block instanceof RubyProc : StringUtils.toString(block);
        return block;
    }

    public static int getArgumentsCount(Frame frame) {
        if (OptimizedKeywordArguments.doesFrameContainOptimizedKeywordArguments(frame)) {
            return OptimizedKeywordArguments.numberOfGivenArguments(frame);
        } else {
            return frame.getArguments().length - RUNTIME_ARGUMENT_COUNT;
        }
    }

    public static Object getArgument(Frame frame, int index) {
        assert index >= 0 && index < (frame.getArguments().length - RUNTIME_ARGUMENT_COUNT);
        return frame.getArguments()[RUNTIME_ARGUMENT_COUNT + index];
    }

    public static OptimizedKeywordArguments.CallingConvention getCallingConvention(Frame frame) {
        return (OptimizedKeywordArguments.CallingConvention) frame.getArguments()[RubyArguments.ArgumentIndicies.CALLING_CONVENTION.ordinal()];
    }

    public static Object[] getArguments(Frame frame) {
        Object[] arguments = frame.getArguments();
        return ArrayUtils.extractRange(arguments, RUNTIME_ARGUMENT_COUNT, arguments.length);
    }

    public static Object[] getArguments(Frame frame, int start) {
        Object[] arguments = frame.getArguments();
        return ArrayUtils.extractRange(arguments, RUNTIME_ARGUMENT_COUNT + start, arguments.length);
    }

    // Getters for the declaration frame that let you reach up several levels

    public static Frame getDeclarationFrame(Frame topFrame, int level) {
        assert topFrame != null;
        assert level >= 0;

        CompilerAsserts.partialEvaluationConstant(level);
        if (level == 0) {
            return topFrame;
        } else {
            return getDeclarationFrame(RubyArguments.getDeclarationFrame(topFrame), level - 1);
        }
    }


    public static MaterializedFrame getDeclarationFrame(MaterializedFrame frame, int level) {
        assert frame != null;
        assert level >= 0;

        CompilerAsserts.partialEvaluationConstant(level);
        return level <= RubyBaseNode.MAX_EXPLODE_SIZE
                ? getDeclarationFrameExplode(frame, level)
                : getDeclarationFrameLoop(frame, level);
    }

    @ExplodeLoop
    private static MaterializedFrame getDeclarationFrameExplode(MaterializedFrame frame, int level) {
        for (int n = 0; n < level; n++) {
            frame = RubyArguments.getDeclarationFrame(frame);
        }
        return frame;
    }

    private static MaterializedFrame getDeclarationFrameLoop(MaterializedFrame frame, int level) {
        for (int n = 0; n < level; n++) {
            frame = RubyArguments.getDeclarationFrame(frame);
        }
        return frame;
    }

    // Getters that fail safely for when you aren't even sure if this is a Ruby frame

    public static MaterializedFrame tryGetDeclarationFrame(Frame frame) {
        if (ArgumentIndicies.DECLARATION_FRAME.ordinal() >= frame.getArguments().length) {
            return null;
        }

        final Object declarationFrame = frame.getArguments()[ArgumentIndicies.DECLARATION_FRAME.ordinal()];

        if (declarationFrame instanceof MaterializedFrame) {
            return (MaterializedFrame) declarationFrame;
        }

        return null;
    }

    public static Object tryGetSelf(Frame frame) {
        if (ArgumentIndicies.SELF.ordinal() >= frame.getArguments().length) {
            return null;
        }

        return frame.getArguments()[ArgumentIndicies.SELF.ordinal()];
    }

    public static RubyProc tryGetBlock(Frame frame) {
        if (ArgumentIndicies.BLOCK.ordinal() >= frame.getArguments().length) {
            return null;
        }

        Object proc = frame.getArguments()[ArgumentIndicies.BLOCK.ordinal()];
        return proc instanceof RubyProc ? (RubyProc) proc : null;
    }

    public static InternalMethod tryGetMethod(Frame frame) {
        if (ArgumentIndicies.METHOD.ordinal() >= frame.getArguments().length) {
            return null;
        }

        final Object method = frame.getArguments()[ArgumentIndicies.METHOD.ordinal()];

        if (method instanceof InternalMethod) {
            return (InternalMethod) method;
        }

        return null;
    }

    public static DeclarationContext tryGetDeclarationContext(Frame frame) {
        if (frame == null) {
            return null;
        }

        if (ArgumentIndicies.DECLARATION_CONTEXT.ordinal() >= frame.getArguments().length) {
            return null;
        }

        final Object declarationContext = frame.getArguments()[ArgumentIndicies.DECLARATION_CONTEXT.ordinal()];

        if (declarationContext instanceof DeclarationContext) {
            return (DeclarationContext) declarationContext;
        }

        return null;
    }

    // Setters

    public static void setDeclarationFrame(Frame frame, MaterializedFrame declarationFrame) {
        frame.getArguments()[ArgumentIndicies.DECLARATION_FRAME.ordinal()] = declarationFrame;
    }

    public static void setDeclarationContext(Frame frame, DeclarationContext declarationContext) {
        frame.getArguments()[ArgumentIndicies.DECLARATION_CONTEXT.ordinal()] = declarationContext;
    }

    public static void setSelf(Frame frame, Object self) {
        frame.getArguments()[ArgumentIndicies.SELF.ordinal()] = self;
    }

    public static void setArgument(Frame frame, int index, Object value) {
        frame.getArguments()[RUNTIME_ARGUMENT_COUNT + index] = value;
    }

}
