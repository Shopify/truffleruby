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
import org.truffleruby.core.array.ArrayUtils;
import org.truffleruby.core.hash.Entry;
import org.truffleruby.core.hash.RubyHash;
import org.truffleruby.core.hash.library.PackedHashStoreLibrary;
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

import java.util.ArrayList;

public final class RubyArguments {

    private enum ArgumentIndicies {
        DECLARATION_FRAME, // 0
        CALLER_FRAME_OR_VARIABLES, // 1
        METHOD, // 2
        DECLARATION_CONTEXT, // 3
        FRAME_ON_STACK_MARKER, // 4
        SELF, // 5
        BLOCK, // 6
        KEYWORD_ARGUMENTS_DESCRIPTOR, // 7
        KEYWORD_ARGUMENTS_VALUES, // 8
    }

    private static final int RUNTIME_ARGUMENT_COUNT = ArgumentIndicies.values().length;

    /** In most cases the DeclarationContext is the one of the InternalMethod. */
    public static Object[] pack(
            MaterializedFrame declarationFrame,
            Object callerFrameOrVariables,
            InternalMethod method,
            FrameOnStackMarker frameOnStackMarker,
            Object self,
            Object block,
            KeywordArgumentsDescriptor keywordArgumentsDescriptor,
            Object[] arguments) {
        return pack(
                declarationFrame,
                callerFrameOrVariables,
                method,
                method.getDeclarationContext(),
                frameOnStackMarker,
                self,
                block,
                keywordArgumentsDescriptor,
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
            KeywordArgumentsDescriptor keywordArgumentsDescriptor,
            Object[] arguments) {
        assert assertValues(callerFrameOrVariables, method, declarationContext, self, block, keywordArgumentsDescriptor, arguments);

        final Object[] packed = new Object[RUNTIME_ARGUMENT_COUNT + arguments.length];

        packed[ArgumentIndicies.DECLARATION_FRAME.ordinal()] = declarationFrame;
        packed[ArgumentIndicies.CALLER_FRAME_OR_VARIABLES.ordinal()] = callerFrameOrVariables;
        packed[ArgumentIndicies.METHOD.ordinal()] = method;
        packed[ArgumentIndicies.DECLARATION_CONTEXT.ordinal()] = declarationContext;
        packed[ArgumentIndicies.FRAME_ON_STACK_MARKER.ordinal()] = frameOnStackMarker;
        packed[ArgumentIndicies.SELF.ordinal()] = self;
        packed[ArgumentIndicies.BLOCK.ordinal()] = block;
        packed[ArgumentIndicies.KEYWORD_ARGUMENTS_DESCRIPTOR.ordinal()] = keywordArgumentsDescriptor;

        Object[] argumentValues;
        int hashIndex = getIndexOfKeywordArguments(arguments);
        if (hashIndex >= 0) {
            argumentValues = getKeywordArgumentsValues((RubyHash) arguments[hashIndex]);
        } else {
            argumentValues = null;
        }
        packed[ArgumentIndicies.KEYWORD_ARGUMENTS_VALUES.ordinal()] = argumentValues;

        ArrayUtils.arraycopy(arguments, 0, packed, RUNTIME_ARGUMENT_COUNT, arguments.length);

        return packed;
    }

    private static int getIndexOfKeywordArguments(Object[] arguments) {
        for (int i = arguments.length - 1; i >= 0; i--) {
            if (arguments[i] instanceof RubyHash) {
                return i;
            }
        }
        return -1;
    }
    private static Object[] getKeywordArgumentsValues(RubyHash arguments) {
        ArrayList<Object> values = new ArrayList<>();

        Entry entry = arguments.firstInSequence;
        if (entry != null) {
            while (entry != null) {
                values.add(entry.getValue());
                entry = entry.getNextInSequence();
            }
        } else if ((arguments.store != null) && (arguments.store instanceof Object[])) {
            Object[] store = (Object[]) arguments.store;
            for (int n = 0; n < arguments.size; n ++) {
                values.add(store[n * PackedHashStoreLibrary.ELEMENTS_PER_ENTRY + 2]);
            }
        }
        return values.toArray();
    }

    public static boolean assertValues(
            Object callerFrameOrVariables,
            InternalMethod method,
            DeclarationContext declarationContext,
            Object self,
            Object block,
            KeywordArgumentsDescriptor keywordArgumentsDescriptor,
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

        assert keywordArgumentsDescriptor != null;

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

    public static KeywordArgumentsDescriptor getKeywordArgumentsDescriptor(Frame frame) {
        final KeywordArgumentsDescriptor keywordArgumentsDescriptor = (KeywordArgumentsDescriptor) frame.getArguments()[ArgumentIndicies.KEYWORD_ARGUMENTS_DESCRIPTOR.ordinal()];
        assert keywordArgumentsDescriptor != null;
        return keywordArgumentsDescriptor;
    }

    public static Object[] getKeywordArgumentsValues(Frame frame) {
        return (Object[]) frame.getArguments()[ArgumentIndicies.KEYWORD_ARGUMENTS_VALUES.ordinal()];
    }

    public static int getArgumentsCount(Frame frame) {
        return frame.getArguments().length - RUNTIME_ARGUMENT_COUNT;
    }

    public static Object getArgument(Frame frame, int index) {
        assert index >= 0 && index < (frame.getArguments().length - RUNTIME_ARGUMENT_COUNT);
        return frame.getArguments()[RUNTIME_ARGUMENT_COUNT + index];
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
