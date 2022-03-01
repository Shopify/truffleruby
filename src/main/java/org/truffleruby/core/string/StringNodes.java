/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 *
 * Some of the code in this class is transposed from org.jruby.RubyString
 * and String Support and licensed under the same EPL 2.0/GPL 2.0/LGPL 2.1
 * used throughout.
 *
 * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
 * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
 * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
 * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
 * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
 * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
 * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
 * Copyright (C) 2005 Tim Azzopardi <tim@tigerfive.com>
 * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
 * Copyright (C) 2006 Ola Bini <ola@ologix.com>
 * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
 *
 * Some of the code in this class is transposed from org.jruby.util.ByteList,
 * licensed under the same EPL 2.0/GPL 2.0/LGPL 2.1 used throughout.
 *
 * Copyright (C) 2007-2010 JRuby Community
 * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
 * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
 * Copyright (C) 2007 Ola Bini <ola@ologix.com>
 * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
 *
 * Some of the code in this class is transliterated from C++ code in Rubinius.
 *
 * Copyright (c) 2007-2014, Evan Phoenix and contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of Rubinius nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.truffleruby.core.string;

import static org.truffleruby.core.rope.CodeRange.CR_7BIT;
import static org.truffleruby.core.rope.CodeRange.CR_BROKEN;
import static org.truffleruby.core.rope.CodeRange.CR_UNKNOWN;
import static org.truffleruby.core.rope.RopeConstants.EMPTY_BINARY_TSTRING;
import static org.truffleruby.core.string.StringSupport.MBCLEN_CHARFOUND_LEN;
import static org.truffleruby.core.string.StringSupport.MBCLEN_CHARFOUND_P;
import static org.truffleruby.core.string.StringSupport.MBCLEN_INVALID_P;
import static org.truffleruby.core.string.StringSupport.MBCLEN_NEEDMORE_P;

import java.nio.charset.StandardCharsets;

import com.oracle.truffle.api.TruffleSafepoint;
import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached.Exclusive;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.profiles.LoopConditionProfile;
import com.oracle.truffle.api.strings.AbstractTruffleString;
import com.oracle.truffle.api.strings.MutableTruffleString;
import com.oracle.truffle.api.strings.TruffleString;
import org.graalvm.collections.Pair;
import org.jcodings.Config;
import org.jcodings.Encoding;
import org.jcodings.specific.ASCIIEncoding;
import org.jcodings.specific.USASCIIEncoding;
import org.jcodings.specific.UTF8Encoding;
import org.truffleruby.Layouts;
import org.truffleruby.SuppressFBWarnings;
import org.truffleruby.builtins.CoreMethod;
import org.truffleruby.builtins.CoreMethodArrayArgumentsNode;
import org.truffleruby.builtins.CoreMethodNode;
import org.truffleruby.builtins.CoreModule;
import org.truffleruby.builtins.NonStandard;
import org.truffleruby.builtins.Primitive;
import org.truffleruby.builtins.PrimitiveArrayArgumentsNode;
import org.truffleruby.builtins.PrimitiveNode;
import org.truffleruby.builtins.YieldingCoreMethodNode;
import org.truffleruby.collections.ByteArrayBuilder;
import org.truffleruby.core.CoreLibrary;
import org.truffleruby.core.array.ArrayUtils;
import org.truffleruby.core.array.RubyArray;
import org.truffleruby.core.cast.BooleanCastNode;
import org.truffleruby.core.cast.ToIntNode;
import org.truffleruby.core.cast.ToLongNode;
import org.truffleruby.core.cast.ToRopeNodeGen;
import org.truffleruby.core.cast.ToStrNode;
import org.truffleruby.core.cast.ToStrNodeGen;
import org.truffleruby.core.encoding.EncodingNodes;
import org.truffleruby.core.encoding.EncodingNodes.NegotiateCompatibleRopeEncodingNode;
import org.truffleruby.core.encoding.IsCharacterHeadNode;
import org.truffleruby.core.encoding.EncodingNodes.CheckEncodingNode;
import org.truffleruby.core.encoding.EncodingNodes.GetActualEncodingNode;
import org.truffleruby.core.encoding.EncodingNodes.NegotiateCompatibleEncodingNode;
import org.truffleruby.core.encoding.Encodings;
import org.truffleruby.core.encoding.RubyEncoding;
import org.truffleruby.core.encoding.TStringGuards;
import org.truffleruby.core.encoding.TStringUtils;
import org.truffleruby.core.format.FormatExceptionTranslator;
import org.truffleruby.core.format.exceptions.FormatException;
import org.truffleruby.core.format.unpack.ArrayResult;
import org.truffleruby.core.format.unpack.UnpackCompiler;
import org.truffleruby.core.kernel.KernelNodes;
import org.truffleruby.core.kernel.KernelNodesFactory;
import org.truffleruby.core.klass.RubyClass;
import org.truffleruby.core.numeric.FixnumLowerNode;
import org.truffleruby.core.numeric.FixnumOrBignumNode;
import org.truffleruby.core.proc.RubyProc;
import org.truffleruby.core.range.RubyIntRange;
import org.truffleruby.core.range.RubyLongRange;
import org.truffleruby.core.range.RubyObjectRange;
import org.truffleruby.core.regexp.RubyRegexp;
import org.truffleruby.core.rope.Bytes;
import org.truffleruby.core.rope.CodeRange;
import org.truffleruby.core.rope.LeafRope;
import org.truffleruby.core.rope.NativeRope;
import org.truffleruby.core.rope.Rope;
import org.truffleruby.core.rope.RopeBuilder;
import org.truffleruby.core.rope.RopeConstants;
import org.truffleruby.core.rope.RopeGuards;
import org.truffleruby.core.rope.RopeNodes;
import org.truffleruby.core.rope.RopeNodes.BytesNode;
import org.truffleruby.core.rope.RopeNodes.CalculateCharacterLengthNode;
import org.truffleruby.core.rope.RopeNodes.CharacterLengthNode;
import org.truffleruby.core.rope.RopeNodes.CodeRangeNode;
import org.truffleruby.core.rope.RopeNodes.GetByteNode;
import org.truffleruby.core.rope.RopeNodes.GetBytesObjectNode;
import org.truffleruby.core.rope.RopeNodes.GetCodePointNode;
import org.truffleruby.core.rope.RopeNodes.SingleByteOptimizableNode;
import org.truffleruby.core.rope.RopeOperations;
import org.truffleruby.core.rope.RopeWithEncoding;
import org.truffleruby.core.rope.SubstringRope;
import org.truffleruby.core.rope.TStringWithEncoding;
import org.truffleruby.core.string.StringNodesFactory.ByteIndexFromCharIndexNodeGen;
import org.truffleruby.core.string.StringNodesFactory.ByteSizeNodeFactory;
import org.truffleruby.core.string.StringNodesFactory.CheckIndexNodeGen;
import org.truffleruby.core.string.StringNodesFactory.CountRopesNodeFactory;
import org.truffleruby.core.string.StringNodesFactory.DeleteBangNodeFactory;
import org.truffleruby.core.string.StringNodesFactory.DeleteBangRopesNodeFactory;
import org.truffleruby.core.string.StringNodesFactory.InvertAsciiCaseBytesNodeGen;
import org.truffleruby.core.string.StringNodesFactory.InvertAsciiCaseNodeGen;
import org.truffleruby.core.string.StringNodesFactory.MakeStringNodeGen;
import org.truffleruby.core.string.StringNodesFactory.NormalizeIndexNodeGen;
import org.truffleruby.core.string.StringNodesFactory.StringAppendNodeGen;
import org.truffleruby.core.string.StringNodesFactory.StringAppendPrimitiveNodeFactory;
import org.truffleruby.core.string.StringNodesFactory.StringByteCharacterIndexNodeFactory;
import org.truffleruby.core.string.StringNodesFactory.StringByteSubstringPrimitiveNodeFactory;
import org.truffleruby.core.string.StringNodesFactory.StringEqualNodeGen;
import org.truffleruby.core.string.StringNodesFactory.StringSubstringPrimitiveNodeFactory;
import org.truffleruby.core.string.StringNodesFactory.SumNodeFactory;
import org.truffleruby.core.string.StringSupport.TrTables;
import org.truffleruby.core.support.RubyByteArray;
import org.truffleruby.core.symbol.RubySymbol;
import org.truffleruby.extra.ffi.Pointer;
import org.truffleruby.language.Nil;
import org.truffleruby.language.NotProvided;
import org.truffleruby.language.RubyBaseNode;
import org.truffleruby.language.RubyBaseNodeWithExecute;
import org.truffleruby.language.RubyGuards;
import org.truffleruby.language.RubyNode;
import org.truffleruby.language.Visibility;
import org.truffleruby.language.arguments.ReadCallerVariablesNode;
import org.truffleruby.language.control.DeferredRaiseException;
import org.truffleruby.language.control.RaiseException;
import org.truffleruby.language.dispatch.DispatchNode;
import org.truffleruby.language.library.RubyStringLibrary;
import org.truffleruby.language.objects.AllocationTracing;
import org.truffleruby.language.objects.WriteObjectFieldNode;
import org.truffleruby.language.threadlocal.SpecialVariableStorage;
import org.truffleruby.language.yield.CallBlockNode;
import org.truffleruby.utils.Utils;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.CreateCast;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.ReportPolymorphism;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.profiles.ConditionProfile;

@CoreModule(value = "String", isClass = true)
public abstract class StringNodes {

    @GenerateUncached
    public abstract static class MakeStringNode extends RubyBaseNode {

        public final RubyString executeMake(String javaString, RubyEncoding encoding, Object codeRange) {
            return executeMake((Object) javaString, encoding, codeRange);
        }

        public final RubyString executeMake(byte[] bytes, RubyEncoding encoding, Object codeRange) {
            return executeMake((Object) bytes, encoding, codeRange);
        }

        public abstract RubyString executeMake(Object payload, RubyEncoding encoding, Object codeRange);

        public RubyString fromBuilder(RopeBuilder builder, RubyEncoding encoding, CodeRange codeRange) {
            assert builder.getEncoding() == encoding.jcoding;
            return executeMake(builder.getBytes(), encoding, codeRange);
        }

        /** All callers of this factory method must guarantee that the builder's byte array cannot change after this
         * call, otherwise the rope built from the builder will end up in an inconsistent state. */
        public RubyString fromBuilderUnsafe(RopeBuilder builder, RubyEncoding encoding, CodeRange codeRange) {
            assert builder.getEncoding() == encoding.jcoding;
            final byte[] unsafeBytes = builder.getUnsafeBytes();
            final byte[] ropeBytes;

            // While the caller must guarantee the builder's byte[] cannot change after this call, it's possible
            // the builder has allocated more space than it needs. Ropes require that the backing byte array
            // is the exact length required. If the builder doesn't satisfy this constraint, we must make a copy.
            // Alternatively, we could make a leaf rope and then take a substring of it, but that would complicate
            // the specializations here.
            if (unsafeBytes.length == builder.getLength()) {
                ropeBytes = unsafeBytes;
            } else {
                ropeBytes = builder.getBytes();
            }

            return executeMake(ropeBytes, encoding, codeRange);
        }

        public static MakeStringNode create() {
            return MakeStringNodeGen.create();
        }

        public static MakeStringNode getUncached() {
            return MakeStringNodeGen.getUncached();
        }

        @Specialization
        protected RubyString makeStringFromBytes(byte[] bytes, RubyEncoding encoding, CodeRange codeRange,
                @Cached TruffleString.FromByteArrayNode fromByteArrayNode) {
            return createString(fromByteArrayNode, bytes, encoding);
        }

        @Specialization(guards = "is7Bit(codeRange)")
        protected RubyString makeAsciiStringFromString(String string, RubyEncoding encoding, CodeRange codeRange) {
            final byte[] bytes = RopeOperations.encodeAsciiBytes(string);

            return executeMake(bytes, encoding, codeRange);
        }

        @Specialization(guards = "!is7Bit(codeRange)")
        protected RubyString makeStringFromString(String string, RubyEncoding encoding, CodeRange codeRange) {
            final byte[] bytes = StringOperations.encodeBytes(string, encoding.jcoding);

            return executeMake(bytes, encoding, codeRange);
        }

        protected static boolean is7Bit(CodeRange codeRange) {
            return codeRange == CR_7BIT;
        }

    }

    @CoreMethod(names = { "__allocate__", "__layout_allocate__" }, constructor = true, visibility = Visibility.PRIVATE)
    public abstract static class AllocateNode extends CoreMethodArrayArgumentsNode {

        @Specialization
        protected RubyString allocate(RubyClass rubyClass) {
            final RubyString string = new RubyString(
                    rubyClass,
                    getLanguage().stringShape,
                    false,
                    EMPTY_BINARY_TSTRING,
                    Encodings.BINARY);
            AllocationTracing.trace(string, this);
            return string;
        }

    }

    @CoreMethod(names = "+", required = 1)
    @NodeChild(value = "string", type = RubyNode.class)
    @NodeChild(value = "other", type = RubyBaseNodeWithExecute.class)
    @ImportStatic(StringGuards.class)
    public abstract static class AddNode extends CoreMethodNode {

        @CreateCast("other")
        protected ToStrNode coerceOtherToString(RubyBaseNodeWithExecute other) {
            return ToStrNodeGen.create(other);
        }

        @Specialization
        protected RubyString add(Object string, Object other,
                @Cached StringAppendNode stringAppendNode) {
            return stringAppendNode.executeStringAppend(string, other);
        }
    }

    @CoreMethod(names = "*", required = 1)
    @NodeChild(value = "string", type = RubyNode.class)
    @NodeChild(value = "times", type = RubyBaseNodeWithExecute.class)
    @ImportStatic(StringGuards.class)
    public abstract static class MulNode extends CoreMethodNode {

        @CreateCast("times")
        protected RubyBaseNodeWithExecute coerceToInteger(RubyBaseNodeWithExecute times) {
            // Not ToIntNode, because this works with empty strings, and must throw a different error
            // for long values that don't fit in an int.
            return FixnumLowerNode.create(ToLongNode.create(times));
        }

        @Specialization(guards = "times == 0")
        protected RubyString multiplyZero(Object string, int times,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            final RubyEncoding encoding = libString.getEncoding(string);
            return createString(encoding.tencoding.getEmpty(), encoding);
        }

        @Specialization(guards = "times < 0")
        protected RubyString multiplyTimesNegative(Object string, long times) {
            throw new RaiseException(getContext(), coreExceptions().argumentError("negative argument", this));
        }

        @Specialization(guards = { "times > 0", "!isEmpty(libString.getRope(string))" })
        protected RubyString multiply(Object string, int times,
                @Cached BranchProfile tooBigProfile,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @Cached TruffleString.RepeatNode repeatNode) {
            var tstring = libString.getTString(string);
            var encoding = libString.getEncoding(string);

            long longLength = (long) times * tstring.byteLength(encoding.tencoding);
            if (longLength > Integer.MAX_VALUE) {
                tooBigProfile.enter();
                throw tooBig();
            }

            return createString(repeatNode.execute(tstring, times, encoding.tencoding), encoding);
        }

        @Specialization(guards = { "times > 0", "libString.getTString(string).isEmpty()" })
        protected RubyString multiplyEmpty(Object string, long times,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            var encoding = libString.getEncoding(string);
            return createString(encoding.tencoding.getEmpty(), encoding);
        }

        @Specialization(guards = { "times > 0", "!isEmpty(strings.getRope(string))" })
        protected RubyString multiplyNonEmpty(Object string, long times,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            assert !CoreLibrary.fitsIntoInteger(times);
            throw tooBig();
        }

        private RaiseException tooBig() {
            // MRI throws this error whenever the total size of the resulting string would exceed LONG_MAX.
            // In TruffleRuby, strings have max length Integer.MAX_VALUE.
            return new RaiseException(getContext(), coreExceptions().argumentError("argument too big", this));
        }
    }

    @CoreMethod(names = { "==", "===", "eql?" }, required = 1)
    public abstract static class EqualNode extends CoreMethodArrayArgumentsNode {

        @Child private StringEqualNode stringEqualNode = StringEqualNodeGen.create();
        @Child private KernelNodes.RespondToNode respondToNode;
        @Child private DispatchNode objectEqualNode;
        @Child private BooleanCastNode booleanCastNode;

        @Specialization(guards = "libB.isRubyString(b)")
        protected boolean equalString(Object a, Object b,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libA,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libB,
                @Cached NegotiateCompatibleRopeEncodingNode negotiateCompatibleRopeEncodingNode) {
            var stringA = libA.getTString(a);
            var encodingA = libA.getEncoding(a);
            var stringB = libB.getTString(b);
            var encodingB = libB.getEncoding(b);
            RubyEncoding compatibleEncoding = negotiateCompatibleRopeEncodingNode.execute(stringA, encodingA, stringB,
                    encodingB);
            return stringEqualNode.executeStringEqual(stringA, stringB, compatibleEncoding);
        }

        @Specialization(guards = "isNotRubyString(b)")
        protected boolean equal(Object a, Object b) {
            if (respondToNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                respondToNode = insert(KernelNodesFactory.RespondToNodeFactory.create(null, null, null));
            }

            if (respondToNode.executeDoesRespondTo(null, b, coreStrings().TO_STR.createInstance(getContext()), false)) {
                if (objectEqualNode == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    objectEqualNode = insert(DispatchNode.create());
                }

                if (booleanCastNode == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    booleanCastNode = insert(BooleanCastNode.create());
                }

                return booleanCastNode.executeToBoolean(objectEqualNode.call(b, "==", a));
            }

            return false;
        }

    }

    // compatibleEncoding is RubyEncoding or null in this node
    public abstract static class StringEqualNode extends RubyBaseNode {

        public abstract boolean executeStringEqual(AbstractTruffleString a, AbstractTruffleString b,
                RubyEncoding compatibleEncoding);

        @Specialization(guards = "a.isEmpty() || b.isEmpty()")
        protected boolean empty(AbstractTruffleString a, AbstractTruffleString b, RubyEncoding compatibleEncoding) {
            assert compatibleEncoding != null;
            return a.isEmpty() && b.isEmpty();
        }

        @Specialization(guards = { "compatibleEncoding != null", "!a.isEmpty()", "!b.isEmpty()" })
        protected boolean equalBytes(AbstractTruffleString a, AbstractTruffleString b, RubyEncoding compatibleEncoding,
                @Cached TruffleString.EqualNode equalNode) {
            return equalNode.execute(a, b, compatibleEncoding.tencoding);
        }

        @Specialization(guards = "compatibleEncoding == null")
        protected boolean notComparable(
                AbstractTruffleString a, AbstractTruffleString b, RubyEncoding compatibleEncoding) {
            return false;
        }
    }

    // compatibleEncoding is RubyEncoding or Nil in this node
    @Primitive(name = "string_cmp")
    public abstract static class CompareNode extends CoreMethodArrayArgumentsNode {

        @Specialization(guards = "first.isEmpty() || second.isEmpty()")
        protected int empty(Object a, Object b, RubyEncoding compatibleEncoding,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libA,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libB,
                @Bind("libA.getTString(a)") AbstractTruffleString first,
                @Bind("libB.getTString(b)") AbstractTruffleString second,
                @Cached ConditionProfile bothEmpty) {
            if (bothEmpty.profile(first.isEmpty() && second.isEmpty())) {
                return 0;
            } else {
                return first.isEmpty() ? -1 : 1;
            }
        }

        @Specialization(guards = { "!first.isEmpty()", "!second.isEmpty()" })
        protected int compatible(Object a, Object b, RubyEncoding compatibleEncoding,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libA,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libB,
                @Bind("libA.getTString(a)") AbstractTruffleString first,
                @Bind("libB.getTString(b)") AbstractTruffleString second,
                @Cached ConditionProfile sameRopeProfile,
                @Cached TruffleString.CompareBytesNode compareBytesNode) {
            if (sameRopeProfile.profile(first == second)) {
                return 0;
            }

            return compareBytesNode.execute(first, second, compatibleEncoding.tencoding);
        }

        @Specialization
        protected int notCompatible(Object a, Object b, Nil compatibleEncoding,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libA,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libB,
                @Cached ConditionProfile sameRopeProfile,
                @Cached TruffleString.CompareBytesNode compareBytesNode,
                @Cached TruffleString.ForceEncodingNode forceEncoding1Node,
                @Cached TruffleString.ForceEncodingNode forceEncoding2Node,
                @Cached ConditionProfile equalProfile,
                @Cached ConditionProfile encodingIndexGreaterThanProfile) {
            var first = libA.getTString(a);
            var firstEncoding = libA.getEncoding(a);
            var second = libB.getTString(b);
            var secondEncoding = libB.getEncoding(b);

            if (sameRopeProfile.profile(first == second)) {
                return 0;
            }

            // Compare as binary as CRuby compares bytes regardless of the encodings
            var firstBinary = forceEncoding1Node.execute(first, firstEncoding.tencoding, Encodings.BINARY.tencoding);
            var secondBinary = forceEncoding2Node.execute(second, secondEncoding.tencoding, Encodings.BINARY.tencoding);
            int result = compareBytesNode.execute(firstBinary, secondBinary, Encodings.BINARY.tencoding);

            if (equalProfile.profile(result == 0)) {
                if (encodingIndexGreaterThanProfile.profile(firstEncoding.index > secondEncoding.index)) {
                    return 1;
                } else {
                    return -1;
                }
            }

            return result;
        }

    }

    @Primitive(name = "dup_as_string_instance")
    public abstract static class StringDupAsStringInstanceNode extends PrimitiveArrayArgumentsNode {
        @Specialization
        protected RubyString dupAsStringInstance(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            final RubyEncoding encoding = strings.getEncoding(string);
            return createString(strings.getTString(string), encoding);
        }
    }

    @CoreMethod(names = { "<<", "concat" }, optional = 1, rest = true, raiseIfNotMutableSelf = true)
    @ImportStatic(StringGuards.class)
    public abstract static class StringConcatNode extends CoreMethodArrayArgumentsNode {

        public static StringConcatNode create() {
            return StringNodesFactory.StringConcatNodeFactory.create(null);
        }

        public abstract Object executeConcat(RubyString string, Object first, Object[] rest);

        @Specialization(guards = "rest.length == 0")
        protected RubyString concatZero(RubyString string, NotProvided first, Object[] rest) {
            return string;
        }

        @Specialization(guards = { "rest.length == 0", "libFirst.isRubyString(first)" })
        protected RubyString concat(RubyString string, Object first, Object[] rest,
                @Cached StringAppendPrimitiveNode stringAppendNode,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libFirst) {
            return stringAppendNode.executeStringAppend(string, first);
        }

        @Specialization(guards = { "rest.length == 0", "isNotRubyString(first)", "wasProvided(first)" })
        protected Object concatGeneric(RubyString string, Object first, Object[] rest,
                @Cached DispatchNode callNode) {
            return callNode.call(coreLibrary().truffleStringOperationsModule, "concat_internal", string, first);
        }

        @ExplodeLoop
        @Specialization(
                guards = {
                        "wasProvided(first)",
                        "rest.length > 0",
                        "rest.length == cachedLength",
                        "cachedLength <= MAX_EXPLODE_SIZE" })
        protected Object concatMany(RubyString string, Object first, Object[] rest,
                @Cached("rest.length") int cachedLength,
                @Cached StringConcatNode argConcatNode,
                @Cached ConditionProfile selfArgProfile) {
            var tstring = string.tstring;
            Object result = argConcatNode.executeConcat(string, first, EMPTY_ARGUMENTS);
            for (int i = 0; i < cachedLength; ++i) {
                final Object argOrCopy = selfArgProfile.profile(rest[i] == string)
                        ? createString(tstring, string.encoding)
                        : rest[i];
                result = argConcatNode.executeConcat(string, argOrCopy, EMPTY_ARGUMENTS);
            }
            return result;
        }

        /** Same implementation as {@link #concatMany}, safe for the use of {@code cachedLength} */
        @Specialization(guards = { "wasProvided(first)", "rest.length > 0" }, replaces = "concatMany")
        protected Object concatManyGeneral(RubyString string, Object first, Object[] rest,
                @Cached StringConcatNode argConcatNode,
                @Cached ConditionProfile selfArgProfile) {
            var tstring = string.tstring;
            Object result = argConcatNode.executeConcat(string, first, EMPTY_ARGUMENTS);
            for (Object arg : rest) {
                if (selfArgProfile.profile(arg == string)) {
                    Object copy = createString(tstring, string.encoding);
                    result = argConcatNode.executeConcat(string, copy, EMPTY_ARGUMENTS);
                } else {
                    result = argConcatNode.executeConcat(string, arg, EMPTY_ARGUMENTS);
                }
            }
            return result;
        }
    }

    @CoreMethod(
            names = { "[]", "slice" },
            required = 1,
            optional = 1,
            lowerFixnum = { 1, 2 },
            argumentNames = { "index_start_range_string_or_regexp", "length_capture" })
    public abstract static class GetIndexNode extends CoreMethodArrayArgumentsNode {
        //region Fields

        @Child private NormalizeIndexNode normalizeIndexNode;
        @Child private StringSubstringPrimitiveNode substringNode;
        @Child private ToLongNode toLongNode;
        @Child private TruffleString.CodePointLengthNode codePointLengthNode;
        private final BranchProfile outOfBounds = BranchProfile.create();

        // endregion
        // region GetIndex Specializations

        @Specialization
        protected Object getIndex(Object string, int index, NotProvided length,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            return index == codePointLength(strings.getTString(string), strings.getEncoding(string)) // Check for the only difference from str[index, 1]
                    ? outOfBoundsNil()
                    : substring(string, index, 1);
        }

        @Specialization
        protected Object getIndex(Object string, long index, NotProvided length) {
            assert (int) index != index : "verified via lowerFixnum";
            return outOfBoundsNil();
        }

        @Specialization(
                guards = {
                        "!isRubyRange(index)",
                        "!isRubyRegexp(index)",
                        "isNotRubyString(index)" })
        protected Object getIndex(Object string, Object index, NotProvided length,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            long indexLong = toLong(index);
            int indexInt = (int) indexLong;
            return indexInt != indexLong
                    ? outOfBoundsNil()
                    : getIndex(string, indexInt, length, strings);
        }

        // endregion
        // region Two-Arg Slice Specializations

        @Specialization
        protected Object slice(Object string, int start, int length) {
            return substring(string, start, length);
        }

        @Specialization
        protected Object slice(Object string, long start, long length) {
            int lengthInt = (int) length;
            if (lengthInt != length) {
                lengthInt = Integer.MAX_VALUE; // go to end of string
            }

            final int startInt = (int) start;
            return startInt != start
                    ? outOfBoundsNil()
                    : substring(string, startInt, lengthInt);
        }

        @Specialization(guards = "wasProvided(length)")
        protected Object slice(Object string, long start, Object length) {
            return slice(string, start, toLong(length));
        }

        @Specialization(
                guards = {
                        "!isRubyRange(start)",
                        "!isRubyRegexp(start)",
                        "isNotRubyString(start)",
                        "wasProvided(length)" })
        protected Object slice(Object string, Object start, Object length) {
            return slice(string, toLong(start), toLong(length));
        }

        // endregion
        // region Range Slice Specializations

        @Specialization
        protected Object sliceIntegerRange(Object string, RubyIntRange range, NotProvided length,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            return sliceRange(string, libString, range.begin, range.end, range.excludedEnd);
        }

        @Specialization
        protected Object sliceLongRange(Object string, RubyLongRange range, NotProvided length,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            return sliceRange(string, libString, range.begin, range.end, range.excludedEnd);
        }

        @Specialization(guards = "range.isEndless()")
        protected Object sliceEndlessRange(Object string, RubyObjectRange range, NotProvided length,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            final int stringEnd = range.excludedEnd ? Integer.MAX_VALUE : Integer.MAX_VALUE - 1;
            return sliceRange(string, libString, toLong(range.begin), stringEnd, range.excludedEnd);
        }

        @Specialization(guards = "range.isBeginless()")
        protected Object sliceBeginlessRange(Object string, RubyObjectRange range, NotProvided length,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            return sliceRange(string, libString, 0L, toLong(range.end), range.excludedEnd);
        }

        @Specialization(guards = "range.isBounded()")
        protected Object sliceObjectRange(Object string, RubyObjectRange range, NotProvided length,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            return sliceRange(string, libString, toLong(range.begin), toLong(range.end), range.excludedEnd);
        }

        @Specialization(guards = "range.isBoundless()")
        protected Object sliceBoundlessRange(Object string, RubyObjectRange range, NotProvided length,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            final int stringEnd = range.excludedEnd ? Integer.MAX_VALUE : Integer.MAX_VALUE - 1;
            return sliceRange(string, libString, 0L, stringEnd, range.excludedEnd);
        }

        // endregion
        // region Range Slice Logic

        private Object sliceRange(Object string, RubyStringLibrary libString, long begin, long end,
                boolean excludesEnd) {
            final int beginInt = (int) begin;
            if (beginInt != begin) {
                return outOfBoundsNil();
            }

            int endInt = (int) end;
            if (endInt != end) {
                // Get until the end of the string.
                endInt = excludesEnd ? Integer.MAX_VALUE : Integer.MAX_VALUE - 1;
            }

            return sliceRange(string, libString, beginInt, endInt, excludesEnd);
        }

        private Object sliceRange(Object string, RubyStringLibrary libString, int begin, int end, boolean excludesEnd) {
            final int stringLength = codePointLength(libString.getTString(string), libString.getEncoding(string));
            begin = normalizeIndex(begin, stringLength);
            if (begin < 0 || begin > stringLength) {
                return outOfBoundsNil();
            }
            end = normalizeIndex(end, stringLength);
            int length = StringOperations.clampExclusiveIndex(stringLength, excludesEnd ? end : end + 1) - begin;
            return substring(string, begin, Math.max(length, 0));
        }

        // endregion
        // region Regexp Slice Specializations

        @Specialization
        protected Object sliceCapture(VirtualFrame frame, Object string, RubyRegexp regexp, Object maybeCapture,
                @Cached @Exclusive DispatchNode callNode,
                @Cached ReadCallerVariablesNode readCallerStorageNode,
                @Cached ConditionProfile unsetProfile,
                @Cached ConditionProfile sameThreadProfile) {
            final Object capture = RubyGuards.wasProvided(maybeCapture) ? maybeCapture : 0;
            final Object matchStrPair = callNode.call(
                    getContext().getCoreLibrary().truffleStringOperationsModule,
                    "subpattern",
                    string,
                    regexp,
                    capture);

            final SpecialVariableStorage variables = readCallerStorageNode.execute(frame);
            if (matchStrPair == nil) {
                variables.setLastMatch(nil, getContext(), unsetProfile, sameThreadProfile);
                return nil;
            } else {
                final Object[] array = (Object[]) ((RubyArray) matchStrPair).store;
                variables.setLastMatch(array[0], getContext(), unsetProfile, sameThreadProfile);
                return array[1];
            }
        }

        // endregion
        // region String Slice Specialization

        @Specialization(guards = "stringsMatchStr.isRubyString(matchStr)")
        protected Object slice2(Object string, Object matchStr, NotProvided length,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary stringsMatchStr,
                @Cached @Exclusive DispatchNode includeNode,
                @Cached BooleanCastNode booleanCastNode) {

            final Object included = includeNode.call(string, "include?", matchStr);

            if (booleanCastNode.executeToBoolean(included)) {
                final RubyEncoding encoding = stringsMatchStr.getEncoding(matchStr);
                return createString(stringsMatchStr.getTString(matchStr), encoding);
            }

            return nil;
        }

        // endregion
        // region Helpers

        private Object outOfBoundsNil() {
            outOfBounds.enter();
            return nil;
        }

        private Object substring(Object string, int start, int length) {
            if (substringNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                substringNode = insert(StringSubstringPrimitiveNodeFactory.create(null));
            }

            return substringNode.execute(string, start, length);
        }

        private long toLong(Object value) {
            if (toLongNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                toLongNode = insert(ToLongNode.create());
            }

            // The long cast is necessary to avoid the invalid `(long) Integer` situation.
            return toLongNode.execute(value);
        }

        private int codePointLength(AbstractTruffleString string, RubyEncoding encoding) {
            if (codePointLengthNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                codePointLengthNode = insert(TruffleString.CodePointLengthNode.create());
            }

            return codePointLengthNode.execute(string, encoding.tencoding);
        }

        private int normalizeIndex(int index, int length) {
            if (normalizeIndexNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                normalizeIndexNode = insert(NormalizeIndexNode.create());
            }

            return normalizeIndexNode.executeNormalize(index, length);
        }

        // endregion
    }

    @CoreMethod(names = "ascii_only?")
    public abstract static class ASCIIOnlyNode extends CoreMethodArrayArgumentsNode {

        @Specialization
        protected boolean asciiOnly(Object string,
                @Cached CodeRangeNode codeRangeNode,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            final CodeRange codeRange = codeRangeNode.execute(libString.getRope(string));

            return codeRange == CR_7BIT;
        }

    }

    @CoreMethod(names = "bytes", needsBlock = true)
    public abstract static class StringBytesNode extends YieldingCoreMethodNode {

        @Child private BytesNode bytesNode = BytesNode.create();

        @Specialization
        protected RubyArray bytes(Object string, Nil block,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            final Rope rope = strings.getRope(string);
            final byte[] bytes = bytesNode.execute(rope);

            final int[] store = new int[bytes.length];

            for (int n = 0; n < store.length; n++) {
                store[n] = bytes[n] & 0xFF;
            }

            return createArray(store);
        }

        @Specialization
        protected Object bytes(Object string, RubyProc block,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            Rope rope = strings.getRope(string);
            byte[] bytes = bytesNode.execute(rope);

            for (int i = 0; i < bytes.length; i++) {
                callBlock(block, bytes[i] & 0xff);
            }

            return string;
        }

    }

    @CoreMethod(names = "bytesize")
    public abstract static class ByteSizeNode extends CoreMethodArrayArgumentsNode {

        public static ByteSizeNode create() {
            return ByteSizeNodeFactory.create(null);
        }

        public abstract int executeByteSize(Object string);

        @Specialization
        protected int byteSize(RubyString string) {
            return string.rope.byteLength();
        }

        @Specialization
        protected int immutableByteSize(ImmutableRubyString string) {
            return string.rope.byteLength();
        }

    }

    @Primitive(name = "string_casecmp")
    @NodeChild(value = "string", type = RubyNode.class)
    @NodeChild(value = "other", type = RubyBaseNodeWithExecute.class)
    public abstract static class CaseCmpNode extends PrimitiveNode {

        @Child private NegotiateCompatibleEncodingNode negotiateCompatibleEncodingNode = NegotiateCompatibleEncodingNode
                .create();
        @Child SingleByteOptimizableNode singleByteOptimizableNode = SingleByteOptimizableNode
                .create();
        private final ConditionProfile incompatibleEncodingProfile = ConditionProfile.create();

        @CreateCast("other")
        protected ToStrNode coerceOtherToString(RubyBaseNodeWithExecute other) {
            return ToStrNodeGen.create(other);
        }

        @Specialization(guards = "bothSingleByteOptimizable(strings.getRope(string), stringsOther.getRope(other))")
        protected Object caseCmpSingleByte(Object string, Object other,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary stringsOther) {
            // Taken from org.jruby.RubyString#casecmp19.

            final RubyEncoding encoding = negotiateCompatibleEncodingNode.executeNegotiate(string, other);
            if (incompatibleEncodingProfile.profile(encoding == null)) {
                return nil;
            }

            return RopeOperations.caseInsensitiveCmp(strings.getRope(string), stringsOther.getRope(other));
        }

        @Specialization(guards = "!bothSingleByteOptimizable(strings.getRope(string), stringsOther.getRope(other))")
        protected Object caseCmp(Object string, Object other,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary stringsOther) {
            // Taken from org.jruby.RubyString#casecmp19 and

            final RubyEncoding encoding = negotiateCompatibleEncodingNode.executeNegotiate(string, other);

            if (incompatibleEncodingProfile.profile(encoding == null)) {
                return nil;
            }

            return StringSupport
                    .multiByteCasecmp(encoding.jcoding, strings.getRope(string), stringsOther.getRope(other));
        }

        protected boolean bothSingleByteOptimizable(Rope stringRope, Rope otherRope) {
            return singleByteOptimizableNode.execute(stringRope) && singleByteOptimizableNode.execute(otherRope);
        }
    }

    /** Returns true if the last bytes in string are equal to the bytes in suffix. */
    @Primitive(name = "string_end_with?")
    public abstract static class EndWithNode extends CoreMethodArrayArgumentsNode {

        @Child IsCharacterHeadNode isCharacterHeadNode;

        @Specialization
        protected boolean endWithBytes(Object string, Object suffix, RubyEncoding enc,
                @Cached BytesNode stringBytesNode,
                @Cached BytesNode suffixBytesNode,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary stringsSuffix,
                @Cached ConditionProfile isCharacterHeadProfile) {

            final Rope stringRope = strings.getRope(string);
            final Rope suffixRope = stringsSuffix.getRope(suffix);
            final int stringByteLength = stringRope.byteLength();
            final int suffixByteLength = suffixRope.byteLength();

            if (stringByteLength < suffixByteLength) {
                return false;
            }
            if (suffixByteLength == 0) {
                return true;
            }
            final byte[] stringBytes = stringBytesNode.execute(stringRope);
            final byte[] suffixBytes = suffixBytesNode.execute(suffixRope);

            final int offset = stringByteLength - suffixByteLength;

            if (isCharacterHeadProfile.profile(!isCharacterHead(enc, stringByteLength, stringBytes, offset))) {
                return false;
            }

            return ArrayUtils.regionEquals(stringBytes, offset, suffixBytes, 0, suffixByteLength);
        }

        private boolean isCharacterHead(RubyEncoding enc, int stringByteLength, byte[] stringBytes, int offset) {
            if (isCharacterHeadNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                isCharacterHeadNode = insert(IsCharacterHeadNode.create());
            }
            return isCharacterHeadNode.execute(enc, stringBytes, offset, stringByteLength);
        }

    }

    @CoreMethod(names = "count", rest = true)
    public abstract static class CountNode extends CoreMethodArrayArgumentsNode {

        @Child private ToStrNode toStr = ToStrNode.create();
        @Child private CountRopesNode countRopesNode = CountRopesNode.create();
        @Child private RubyStringLibrary rubyStringLibrary = RubyStringLibrary.getFactory().createDispatched(2);

        @Specialization(
                guards = "args.length == size",
                limit = "getDefaultCacheLimit()")
        protected int count(Object string, Object[] args,
                @Cached("args.length") int size) {
            final TStringWithEncoding[] ropesWithEncs = argRopesWithEncs(args, size);
            return countRopesNode.executeCount(string, ropesWithEncs);
        }

        @Specialization(replaces = "count")
        protected int countSlow(Object string, Object[] args) {
            final TStringWithEncoding[] ropesWithEncs = argRopesSlow(args);
            return countRopesNode.executeCount(string, ropesWithEncs);
        }

        @ExplodeLoop
        protected TStringWithEncoding[] argRopesWithEncs(Object[] args, int size) {
            final TStringWithEncoding[] strs = new TStringWithEncoding[args.length];
            for (int i = 0; i < size; i++) {
                final Object string = toStr.execute(args[i]);
                strs[i] = new TStringWithEncoding(
                        rubyStringLibrary.getTString(string),
                        rubyStringLibrary.getEncoding(string));
            }
            return strs;
        }

        protected TStringWithEncoding[] argRopesSlow(Object[] args) {
            final TStringWithEncoding[] strs = new TStringWithEncoding[args.length];
            for (int i = 0; i < args.length; i++) {
                final Object string = toStr.execute(args[i]);
                strs[i] = new TStringWithEncoding(
                        rubyStringLibrary.getTString(string),
                        rubyStringLibrary.getEncoding(string));
            }
            return strs;
        }
    }

    @ImportStatic({ StringGuards.class, StringOperations.class })
    public abstract static class CountRopesNode extends TrTableNode {

        public static CountRopesNode create() {
            return CountRopesNodeFactory.create(null);
        }

        public abstract int executeCount(Object string, TStringWithEncoding[] ropesWithEncs);

        @Specialization(guards = "isEmpty(strings.getRope(string))")
        protected int count(Object string, Object[] args,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            return 0;
        }

        @Specialization(
                guards = {
                        "cachedArgs.length > 0",
                        "!isEmpty(libString.getRope(string))",
                        "cachedArgs.length == args.length",
                        "argsMatch(cachedArgs, args)",
                        "encodingsMatch(libString.getRope(string), cachedEncoding)" })
        protected int countFast(Object string, TStringWithEncoding[] args,
                @Cached(value = "args", dimensions = 1) TStringWithEncoding[] cachedArgs,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @Cached("libString.getRope(string).encoding") Encoding cachedEncoding,
                @Cached(value = "squeeze()", dimensions = 1) boolean[] squeeze,
                @Cached("findEncoding(libString.getTString(string), libString.getEncoding(string), cachedArgs)") RubyEncoding compatEncoding,
                @Cached("makeTables(cachedArgs, squeeze, compatEncoding)") TrTables tables) {
            return processStr(libString.getRope(string), squeeze, compatEncoding, tables);
        }

        @TruffleBoundary
        private int processStr(Rope rope, boolean[] squeeze, RubyEncoding compatEncoding, TrTables tables) {
            return StringSupport.strCount(rope, squeeze, tables, compatEncoding.jcoding, this);
        }

        @Specialization(guards = "!isEmpty(libString.getRope(string))")
        protected int count(Object string, TStringWithEncoding[] ropesWithEncs,
                @Cached BranchProfile errorProfile,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            if (ropesWithEncs.length == 0) {
                errorProfile.enter();
                throw new RaiseException(getContext(), coreExceptions().argumentErrorEmptyVarargs(this));
            }

            RubyEncoding enc = findEncoding(libString.getTString(string), libString.getEncoding(string), ropesWithEncs);
            return countSlow(libString.getRope(string), ropesWithEncs, enc);
        }

        @TruffleBoundary
        private int countSlow(Rope stringRope, TStringWithEncoding[] ropesWithEncs, RubyEncoding enc) {
            final boolean[] table = squeeze();
            final StringSupport.TrTables tables = makeTables(ropesWithEncs, table, enc);
            return processStr(stringRope, table, enc, tables);
        }
    }

    public abstract static class TrTableNode extends CoreMethodArrayArgumentsNode {
        @Child protected EncodingNodes.CheckStringEncodingNode checkEncodingNode = EncodingNodes.CheckStringEncodingNode
                .create();
        @Child protected TruffleString.EqualNode equalNode = TruffleString.EqualNode.create();

        protected boolean[] squeeze() {
            return new boolean[StringSupport.TRANS_SIZE + 1];
        }

        protected RopeWithEncoding stringToRopeWithEncoding(RubyStringLibrary strings, Object string) {
            return new RopeWithEncoding(strings.getRope(string), strings.getEncoding(string));
        }

        protected RubyEncoding findEncoding(AbstractTruffleString tstring, RubyEncoding encoding,
                TStringWithEncoding[] ropes) {
            RubyEncoding enc = checkEncodingNode.executeCheckEncoding(tstring, encoding, ropes[0].tstring,
                    ropes[0].encoding);
            for (int i = 1; i < ropes.length; i++) {
                enc = checkEncodingNode.executeCheckEncoding(tstring, encoding, ropes[i].tstring, ropes[i].encoding);
            }
            return enc;
        }

        protected TrTables makeTables(TStringWithEncoding[] ropesWithEncs, boolean[] squeeze, RubyEncoding enc) {
            // The trSetupTable method will consume the bytes from the rope one encoded character at a time and
            // build a TrTable from this. Previously we started with the encoding of rope zero, and at each
            // stage found a compatible encoding to build that TrTable with. Although we now calculate a single
            // encoding with which to build the tables it must be compatible with all ropes, so will not
            // affect the consumption of characters from those ropes.
            StringSupport.TrTables tables = StringSupport.trSetupTable(
                    ropesWithEncs[0].tstring,
                    ropesWithEncs[0].encoding,
                    squeeze,
                    null,
                    true,
                    enc.jcoding,
                    this);

            for (int i = 1; i < ropesWithEncs.length; i++) {
                tables = StringSupport
                        .trSetupTable(ropesWithEncs[i].tstring, ropesWithEncs[i].encoding, squeeze, tables, false,
                                enc.jcoding, this);
            }
            return tables;
        }

        protected boolean encodingsMatch(Rope rope, Encoding encoding) {
            return encoding == rope.getEncoding();
        }

        @ExplodeLoop
        protected boolean argsMatch(TStringWithEncoding[] cachedRopes, TStringWithEncoding[] ropes) {
            for (int i = 0; i < cachedRopes.length; i++) {
                if (cachedRopes[i].encoding != ropes[i].encoding) {
                    return false;
                }
                if (!equalNode.execute(cachedRopes[i].tstring, ropes[i].tstring, cachedRopes[i].encoding.tencoding)) {
                    return false;
                }
            }
            return true;
        }
    }

    @CoreMethod(names = "delete!", rest = true, raiseIfNotMutableSelf = true)
    @ImportStatic(StringGuards.class)
    public abstract static class DeleteBangNode extends CoreMethodArrayArgumentsNode {

        @Child private ToStrNode toStr = ToStrNode.create();
        @Child private DeleteBangRopesNode deleteBangRopesNode = DeleteBangRopesNode.create();
        @Child private RubyStringLibrary rubyStringLibrary = RubyStringLibrary.getFactory().createDispatched(2);

        public static DeleteBangNode create() {
            return DeleteBangNodeFactory.create(null);
        }

        public abstract Object executeDeleteBang(RubyString string, Object[] args);

        @Specialization(guards = "args.length == size", limit = "getDefaultCacheLimit()")
        protected Object deleteBang(RubyString string, Object[] args,
                @Cached("args.length") int size) {
            final TStringWithEncoding[] ropesWithEncs = argRopesWithEncs(args, size);
            return deleteBangRopesNode.executeDeleteBang(string, ropesWithEncs);
        }

        @Specialization(replaces = "deleteBang")
        protected Object deleteBangSlow(RubyString string, Object[] args) {
            final TStringWithEncoding[] ropes = argRopesWithEncsSlow(args);
            return deleteBangRopesNode.executeDeleteBang(string, ropes);
        }

        @ExplodeLoop
        protected TStringWithEncoding[] argRopesWithEncs(Object[] args, int size) {
            final TStringWithEncoding[] strs = new TStringWithEncoding[size];
            for (int i = 0; i < size; i++) {
                final Object string = toStr.execute(args[i]);
                strs[i] = new TStringWithEncoding(
                        rubyStringLibrary.getTString(string),
                        rubyStringLibrary.getEncoding(string));
            }
            return strs;
        }

        protected TStringWithEncoding[] argRopesWithEncsSlow(Object[] args) {
            final TStringWithEncoding[] strs = new TStringWithEncoding[args.length];
            for (int i = 0; i < args.length; i++) {
                final Object string = toStr.execute(args[i]);
                strs[i] = new TStringWithEncoding(
                        rubyStringLibrary.getTString(string),
                        rubyStringLibrary.getEncoding(string));
            }
            return strs;
        }
    }

    @ImportStatic({ StringGuards.class, StringOperations.class })
    public abstract static class DeleteBangRopesNode extends TrTableNode {

        public static DeleteBangRopesNode create() {
            return DeleteBangRopesNodeFactory.create(null);
        }

        public abstract Object executeDeleteBang(RubyString string, TStringWithEncoding[] ropesWithEncs);

        @Specialization(guards = "isEmpty(string.rope)")
        protected Object deleteBangEmpty(RubyString string, Object[] args) {
            return nil;
        }

        @Specialization(
                guards = {
                        "cachedArgs.length > 0",
                        "!isEmpty(string.rope)",
                        "cachedArgs.length == args.length",
                        "argsMatch(cachedArgs, args)",
                        "encodingsMatch(libString.getRope(string), cachedEncoding)" })
        protected Object deleteBangFast(RubyString string, TStringWithEncoding[] args,
                @Cached(value = "args", dimensions = 1) TStringWithEncoding[] cachedArgs,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @Cached("libString.getRope(string).encoding") Encoding cachedEncoding,
                @Cached(value = "squeeze()", dimensions = 1) boolean[] squeeze,
                @Cached("findEncoding(libString.getTString(string), libString.getEncoding(string), cachedArgs)") RubyEncoding compatEncoding,
                @Cached("makeTables(cachedArgs, squeeze, compatEncoding)") TrTables tables,
                @Cached BranchProfile nullProfile) {
            final Rope processedRope = processStr(string, squeeze, compatEncoding, tables);
            if (processedRope == null) {
                nullProfile.enter();
                return nil;
            }

            string.setRope(processedRope);
            return string;
        }

        @Specialization(guards = "!isEmpty(string.rope)")
        protected Object deleteBang(RubyString string, TStringWithEncoding[] args,
                @Cached BranchProfile errorProfile) {
            if (args.length == 0) {
                errorProfile.enter();
                throw new RaiseException(getContext(), coreExceptions().argumentErrorEmptyVarargs(this));
            }

            RubyEncoding enc = findEncoding(string.tstring, string.encoding, args);

            return deleteBangSlow(string, args, enc);
        }

        @TruffleBoundary
        private Object deleteBangSlow(RubyString string, TStringWithEncoding[] ropesWithEncs, RubyEncoding enc) {
            final boolean[] squeeze = new boolean[StringSupport.TRANS_SIZE + 1];

            final StringSupport.TrTables tables = makeTables(ropesWithEncs, squeeze, enc);

            final Rope processedRope = processStr(string, squeeze, enc, tables);
            if (processedRope == null) {
                return nil;
            }

            string.setRope(processedRope);
            // REVIEW encoding set

            return string;
        }

        @TruffleBoundary
        private Rope processStr(RubyString string, boolean[] squeeze, RubyEncoding enc, StringSupport.TrTables tables) {
            return StringSupport.delete_bangCommon19(string.rope, squeeze, tables, enc.jcoding, this);
        }
    }

    @Primitive(name = "string_downcase!", raiseIfNotMutable = 0, lowerFixnum = 1)
    @ImportStatic({ StringGuards.class, Config.class })
    public abstract static class StringDowncaseBangPrimitiveNode extends PrimitiveArrayArgumentsNode {

        @Child SingleByteOptimizableNode singleByteOptimizableNode = SingleByteOptimizableNode
                .create();

        @Specialization(guards = { "isSingleByteCaseMapping(string, caseMappingOptions, singleByteOptimizableNode)" })
        protected Object downcaseSingleByte(RubyString string, int caseMappingOptions,
                @Cached("createUpperToLower()") InvertAsciiCaseNode invertAsciiCaseNode) {
            return invertAsciiCaseNode.executeInvert(string);
        }

        @Specialization(guards = { "isSimpleAsciiCaseMapping(string, caseMappingOptions, singleByteOptimizableNode)" })
        protected Object downcaseMultiByteAsciiSimple(RubyString string, int caseMappingOptions,
                @Cached @Shared("bytesNode") BytesNode bytesNode,
                @Cached @Shared("codeRangeNode") CodeRangeNode codeRangeNode,
                @Cached @Shared("fromByteArrayNode") TruffleString.FromByteArrayNode fromByteArrayNode,
                @Cached @Shared("dummyEncodingProfile") ConditionProfile dummyEncodingProfile,
                @Cached @Shared("modifiedProfile") ConditionProfile modifiedProfile) {
            final Rope rope = string.rope;
            final Encoding encoding = rope.getEncoding();

            if (dummyEncodingProfile.profile(encoding.isDummy())) {
                throw new RaiseException(
                        getContext(),
                        coreExceptions().encodingCompatibilityErrorIncompatibleWithOperation(encoding, this));
            }

            final CodeRange cr = codeRangeNode.execute(rope);
            final byte[] inputBytes = bytesNode.execute(rope);
            final byte[] outputBytes = StringSupport.downcaseMultiByteAsciiSimple(encoding, cr, inputBytes);

            if (modifiedProfile.profile(inputBytes != outputBytes)) {
                string.setTString(fromByteArrayNode.execute(outputBytes, string.encoding.tencoding)); // cr, characterLengthNode.execute(rope)
                return string;
            } else {
                return nil;
            }
        }

        @Specialization(guards = { "isComplexCaseMapping(string, caseMappingOptions, singleByteOptimizableNode)" })
        protected Object downcaseMultiByteComplex(RubyString string, int caseMappingOptions,
                @Cached @Shared("bytesNode") BytesNode bytesNode,
                @Cached @Shared("codeRangeNode") CodeRangeNode codeRangeNode,
                @Cached @Shared("fromByteArrayNode") TruffleString.FromByteArrayNode fromByteArrayNode,
                @Cached @Shared("dummyEncodingProfile") ConditionProfile dummyEncodingProfile,
                @Cached @Shared("modifiedProfile") ConditionProfile modifiedProfile) {
            final Rope rope = string.rope;
            final Encoding encoding = rope.getEncoding();

            if (dummyEncodingProfile.profile(encoding.isDummy())) {
                throw new RaiseException(
                        getContext(),
                        coreExceptions().encodingCompatibilityErrorIncompatibleWithOperation(encoding, this));
            }

            final RopeBuilder builder = RopeBuilder.createRopeBuilder(bytesNode.execute(rope), rope.getEncoding());
            final boolean modified = StringSupport
                    .downcaseMultiByteComplex(encoding, codeRangeNode.execute(rope), builder, caseMappingOptions, this);

            if (modifiedProfile.profile(modified)) {
                string.setTString(fromByteArrayNode.execute(builder.getBytes(), string.encoding.tencoding));
                return string;
            } else {
                return nil;
            }
        }

    }

    @CoreMethod(names = "each_byte", needsBlock = true, enumeratorSize = "bytesize")
    public abstract static class EachByteNode extends YieldingCoreMethodNode {

        @SuppressFBWarnings("SA")
        @Specialization
        protected Object eachByte(Object string, RubyProc block,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached BytesNode bytesNode,
                @Cached BytesNode updatedBytesNode,
                @Cached ConditionProfile ropeChangedProfile) {
            Rope rope = strings.getRope(string);
            byte[] bytes = bytesNode.execute(rope);

            for (int i = 0; i < bytes.length; i++) {
                callBlock(block, bytes[i] & 0xff);

                // Don't be tempted to extract the rope from the passed string. If the block being yielded to modifies the
                // source string, you'll get a different rope.
                Rope updatedRope = strings.getRope(string);
                if (ropeChangedProfile.profile(rope != updatedRope)) {
                    rope = updatedRope;
                    bytes = updatedBytesNode.execute(updatedRope);
                }
            }

            return string;
        }

    }

    @CoreMethod(names = "each_char", needsBlock = true, enumeratorSize = "size")
    @ImportStatic(StringGuards.class)
    public abstract static class EachCharNode extends YieldingCoreMethodNode {

        @Specialization
        protected Object eachChar(Object string, RubyProc block,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached CalculateCharacterLengthNode calculateCharacterLengthNode,
                @Cached CodeRangeNode codeRangeNode,
                @Cached TruffleString.GetInternalByteArrayNode bytesNode,
                @Cached TruffleString.SubstringByteIndexNode substringNode) {
            // Unlike String#each_byte, String#each_char does not make
            // modifications to the string visible to the rest of the iteration.
            final Rope rope = strings.getRope(string);
            var tstring = strings.getTString(string);
            final RubyEncoding encoding = strings.getEncoding(string);
            var bytes = bytesNode.execute(tstring, encoding.tencoding);
            final int len = bytes.getLength();
            final CodeRange cr = codeRangeNode.execute(rope);

            int clen;
            for (int i = 0; i < len; i += clen) {
                clen = calculateCharacterLengthNode.characterLengthWithRecovery(encoding.jcoding, cr,
                        Bytes.fromRange(bytes.getArray(), bytes.getOffset() + i, bytes.getEnd()));
                callBlock(block, createSubString(substringNode, tstring, encoding, i, clen));
            }

            return string;
        }

    }

    @ImportStatic(StringGuards.class)
    @CoreMethod(names = "force_encoding", required = 1, raiseIfNotMutableSelf = true)
    public abstract static class ForceEncodingNode extends CoreMethodArrayArgumentsNode {

        public abstract RubyString execute(Object string, Object other);

        protected abstract RubyString execute(Object string, RubyEncoding other);

        public static ForceEncodingNode create() {
            return StringNodesFactory.ForceEncodingNodeFactory.create(null);
        }

        @Specialization(guards = "string.encoding == newEncoding")
        protected RubyString sameEncoding(RubyString string, RubyEncoding newEncoding) {
            return string;
        }

        @Specialization(guards = { "string.encoding != newEncoding", "tstring.isImmutable()" })
        protected RubyString immutable(RubyString string, RubyEncoding newEncoding,
                @Bind("string.tstring") AbstractTruffleString tstring,
                @Cached TruffleString.ForceEncodingNode forceEncodingNode) {
            var newTString = forceEncodingNode.execute(tstring, string.encoding.tencoding, newEncoding.tencoding);
            string.setTString(newTString, newEncoding);
            return string;
        }

        @Specialization(
                guards = { "string.encoding != newEncoding", "!tstring.isImmutable()", "!tstring.isNative()" })
        protected RubyString mutableManaged(RubyString string, RubyEncoding newEncoding,
                @Bind("string.tstring") AbstractTruffleString tstring,
                @Cached MutableTruffleString.ForceEncodingNode forceEncodingNode) {
            var newTString = forceEncodingNode.execute(tstring, string.encoding.tencoding, newEncoding.tencoding);
            string.setTString(newTString, newEncoding);
            return string;
        }

        @Specialization(
                guards = { "string.encoding != newEncoding", "!tstring.isImmutable()", "tstring.isNative()" })
        protected RubyString mutableNative(RubyString string, RubyEncoding newEncoding,
                @Bind("string.tstring") AbstractTruffleString tstring,
                @Cached TruffleString.GetInternalNativePointerNode getInternalNativePointerNode,
                @Cached MutableTruffleString.FromNativePointerNode fromNativePointerNode) {
            var currentEncoding = string.encoding.tencoding;
            var pointer = (Pointer) getInternalNativePointerNode.execute(tstring, currentEncoding);
            var byteLength = tstring.byteLength(currentEncoding);
            var newTString = fromNativePointerNode.execute(pointer, 0, byteLength, newEncoding.tencoding, false);
            string.setTString(newTString, newEncoding);
            return string;
        }

        @Specialization(guards = "libEncoding.isRubyString(newEncoding)")
        protected RubyString forceEncodingString(RubyString string, Object newEncoding,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libEncoding,
                @Cached BranchProfile errorProfile) {
            final String stringName = libEncoding.getJavaString(newEncoding);
            final RubyEncoding rubyEncoding = getContext().getEncodingManager().getRubyEncoding(stringName);

            if (rubyEncoding == null) {
                errorProfile.enter();
                throw new RaiseException(
                        getContext(),
                        coreExceptions().argumentError(Utils.concat("unknown encoding name - ", stringName), this));
            }

            return execute(string, rubyEncoding);
        }

        @Specialization(guards = { "!isRubyEncoding(newEncoding)", "isNotRubyString(newEncoding)" })
        protected RubyString forceEncoding(RubyString string, Object newEncoding,
                @Cached ToStrNode toStrNode,
                @Cached ForceEncodingNode forceEncodingNode) {
            return forceEncodingNode.execute(string, toStrNode.execute(newEncoding));
        }
    }

    @CoreMethod(names = "getbyte", required = 1, lowerFixnum = 1)
    public abstract static class StringGetByteNode extends CoreMethodArrayArgumentsNode {

        @Child private NormalizeIndexNode normalizeIndexNode = NormalizeIndexNode.create();
        @Child private GetByteNode ropeGetByteNode = GetByteNode.create();

        @Specialization
        protected Object getByte(Object string, int index,
                @Cached ConditionProfile indexOutOfBoundsProfile,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            final Rope rope = libString.getRope(string);
            final int normalizedIndex = normalizeIndexNode.executeNormalize(index, rope.byteLength());

            if (indexOutOfBoundsProfile.profile((normalizedIndex < 0) || (normalizedIndex >= rope.byteLength()))) {
                return nil;
            }

            return ropeGetByteNode.executeGetByte(rope, normalizedIndex);
        }

    }

    @GenerateUncached
    public abstract static class HashStringNode extends RubyBaseNode {

        protected static final int CLASS_SALT = 54008340; // random number, stops hashes for similar values but different classes being the same, static because we want deterministic hashes

        public static HashStringNode create() {
            return StringNodesFactory.HashStringNodeGen.create();
        }

        public abstract long execute(Object string);

        @Specialization
        protected long hash(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached RopeNodes.HashNode hashNode) {
            return getContext().getHashing(this).hash(CLASS_SALT, hashNode.execute(strings.getRope(string)));
        }
    }

    @CoreMethod(names = "hash")
    public abstract static class HashNode extends CoreMethodArrayArgumentsNode {

        protected static final int CLASS_SALT = 54008340; // random number, stops hashes for similar values but different classes being the same, static because we want deterministic hashes

        public static HashNode create() {
            return StringNodesFactory.HashNodeFactory.create(null);
        }

        public abstract long execute(Object string);

        @Specialization
        protected long hash(Object string,
                @Cached HashStringNode hash) {
            return hash.execute(string);
        }
    }

    @Primitive(name = "string_initialize")
    public abstract static class InitializeNode extends CoreMethodArrayArgumentsNode {

        @Specialization
        protected RubyString initializeJavaString(RubyString string, String from, RubyEncoding encoding) {
            string.setRope(StringOperations.encodeRope(from, encoding.jcoding), encoding);
            return string;
        }

        @Specialization
        protected RubyString initializeJavaStringNoEncoding(RubyString string, String from, Nil encoding) {
            throw new RaiseException(
                    getContext(),
                    coreExceptions().argumentError(
                            "String.new(javaString) needs to be called with an Encoding like String.new(javaString, encoding: someEncoding)",
                            this));
        }

        @Specialization(guards = "stringsFrom.isRubyString(from)")
        protected RubyString initialize(RubyString string, Object from, Object encoding,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary stringsFrom) {
            string.setRope(stringsFrom.getRope(from), stringsFrom.getEncoding(from));
            return string;
        }

        @Specialization(guards = { "isNotRubyString(from)", "!isString(from)" })
        protected RubyString initialize(VirtualFrame frame, RubyString string, Object from, Object encoding,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary stringLibrary,
                @Cached ToStrNode toStrNode) {
            final Object stringFrom = toStrNode.execute(from);
            string.setRope(stringLibrary.getRope(stringFrom), stringLibrary.getEncoding(stringFrom));
            return string;
        }

    }

    @Primitive(name = "string_get_coderange")
    public abstract static class GetCodeRangeNode extends CoreMethodArrayArgumentsNode {

        @Specialization
        protected int getCodeRange(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached CodeRangeNode codeRangeNode) {
            final var tString = strings.getTString(string);

            return codeRangeNode.execute(strings.getRope(string)).toInt();
        }

    }

    @Primitive(name = "string_get_rope")
    public abstract static class GetRopeNode extends CoreMethodArrayArgumentsNode {

        @Specialization
        protected Rope getRope(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            return strings.getRope(string);
        }
    }

    public abstract static class StringGetAssociatedNode extends RubyBaseNode {

        public static StringNodes.StringGetAssociatedNode create() {
            return StringNodesFactory.StringGetAssociatedNodeGen.create();
        }

        public abstract Object execute(Object string);

        @Specialization(limit = "getDynamicObjectCacheLimit()")
        protected Object getAssociated(RubyString string,
                @CachedLibrary("string") DynamicObjectLibrary objectLibrary) {
            return objectLibrary.getOrDefault(string, Layouts.ASSOCIATED_IDENTIFIER, null);
        }

        @Specialization
        protected Object getAssociatedImmutable(ImmutableRubyString string) {
            return null;
        }

    }

    @CoreMethod(names = "initialize_copy", required = 1, raiseIfNotMutableSelf = true)
    public abstract static class InitializeCopyNode extends CoreMethodArrayArgumentsNode {

        @Child private WriteObjectFieldNode writeAssociatedNode; // for synchronization

        @Specialization(guards = "areEqual(self, from)")
        protected Object initializeCopySelfIsSameAsFrom(RubyString self, Object from) {
            return self;
        }

        @Specialization(
                guards = {
                        "stringsFrom.isRubyString(from)",
                        "!areEqual(self, from)",
                        "!isNativeRope(stringsFrom.getRope(from))" })
        protected Object initializeCopy(RubyString self, Object from,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary stringsFrom,
                @Cached @Shared("stringGetAssociatedNode") StringGetAssociatedNode stringGetAssociatedNode) {
            self.setRope(stringsFrom.getRope(from), stringsFrom.getEncoding(from));
            final Object associated = stringGetAssociatedNode.execute(from);
            copyAssociated(self, associated);
            return self;
        }

        @Specialization(
                guards = {
                        "stringsFrom.isRubyString(from)",
                        "!areEqual(self, from)",
                        "isNativeRope(stringsFrom.getRope(from))" })
        protected Object initializeCopyFromNative(RubyString self, Object from,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary stringsFrom,
                @Cached @Shared("stringGetAssociatedNode") StringGetAssociatedNode stringGetAssociatedNode) {
            self.setRope(
                    ((NativeRope) stringsFrom.getRope(from)).makeCopy(getContext()),
                    stringsFrom.getEncoding(from));
            final Object associated = stringGetAssociatedNode.execute(from);
            copyAssociated(self, associated);
            return self;
        }

        protected static boolean areEqual(Object one, Object two) {
            return one == two;
        }

        private void copyAssociated(RubyString self, Object associated) {
            if (associated != null) {
                if (writeAssociatedNode == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    writeAssociatedNode = insert(WriteObjectFieldNode.create());
                }

                writeAssociatedNode.execute(self, Layouts.ASSOCIATED_IDENTIFIER, associated);
            }
        }

        protected boolean isNativeRope(Rope other) {
            return other instanceof NativeRope;
        }
    }

    @CoreMethod(names = "lstrip!", raiseIfNotMutableSelf = true)
    @ImportStatic(StringGuards.class)
    public abstract static class LstripBangNode extends CoreMethodArrayArgumentsNode {

        @Child TruffleString.CodePointAtByteIndexNode codePointAtByteIndexNode = TruffleString.CodePointAtByteIndexNode.create();
        @Child TruffleString.SubstringByteIndexNode substringNode = TruffleString.SubstringByteIndexNode.create();

        @Specialization(guards = "isEmpty(string.rope)")
        protected Object lstripBangEmptyString(RubyString string) {
            return nil;
        }

        @Specialization(
                guards = { "!isEmpty(string.rope)", "isSingleByteOptimizable(string, singleByteOptimizableNode)" })
        protected Object lstripBangSingleByte(RubyString string,
                @Cached BytesNode bytesNode,
                @Cached SingleByteOptimizableNode singleByteOptimizableNode,
                @Cached ConditionProfile noopProfile) {
            // Taken from org.jruby.RubyString#lstrip_bang19 and org.jruby.RubyString#singleByteLStrip.

            final Rope rope = string.rope;
            var tstring = string.tstring;
            var encoding = string.encoding;
            int firstCodePoint = codePointAtByteIndexNode.execute(tstring, 0, encoding.tencoding);

            // Check the first code point to see if it's a space. In the case of strings without leading spaces,
            // this check can avoid having to materialize the entire byte[] (a potentially expensive operation
            // for ropes) and can avoid having to compile the while loop.
            if (noopProfile.profile(!StringSupport.isAsciiSpace(firstCodePoint))) {
                return nil;
            }

            final int end = rope.byteLength();
            final byte[] bytes = bytesNode.execute(rope);

            int p = 0;
            while (p < end && StringSupport.isAsciiSpace(bytes[p])) {
                p++;
            }

            string.setTString(substringNode.execute(tstring, p, end - p, encoding.tencoding, true));
            return string;
        }

        @TruffleBoundary
        @Specialization(
                guards = { "!isEmpty(string.rope)", "!isSingleByteOptimizable(string, singleByteOptimizableNode)" })
        protected Object lstripBang(RubyString string,
                @Cached SingleByteOptimizableNode singleByteOptimizableNode,
                @Cached GetActualEncodingNode getActualEncodingNode,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            // Taken from org.jruby.RubyString#lstrip_bang19 and org.jruby.RubyString#multiByteLStrip.

            final Rope rope = string.rope;
            var tstring = string.tstring;
            final RubyEncoding enc = getActualEncodingNode.execute(rope, strings.getEncoding(string));
            final int s = 0;
            final int end = s + rope.byteLength();

            int p = s;
            while (p < end) {
                int c = codePointAtByteIndexNode.execute(tstring, p, enc.tencoding);
                if (!ASCIIEncoding.INSTANCE.isSpace(c)) {
                    break;
                }
                p += StringSupport.codeLength(enc.jcoding, c);
            }

            if (p > s) {
                string.setTString(substringNode.execute(tstring, p - s, end - p, enc.tencoding, true));
                return string;
            }

            return nil;
        }
    }

    @CoreMethod(names = "ord")
    @ImportStatic(StringGuards.class)
    public abstract static class OrdNode extends CoreMethodArrayArgumentsNode {

        @Specialization(guards = { "isEmpty(strings.getRope(string))" })
        protected int ordEmpty(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            throw new RaiseException(getContext(), coreExceptions().argumentError("empty string", this));
        }

        @Specialization(guards = { "!isEmpty(strings.getRope(string))" })
        protected int ord(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached GetCodePointNode getCodePointNode) {
            return getCodePointNode.executeGetCodePoint(strings.getEncoding(string), strings.getRope(string), 0);
        }

    }

    @CoreMethod(names = "replace", required = 1, raiseIfNotMutableSelf = true)
    @NodeChild(value = "string", type = RubyNode.class)
    @NodeChild(value = "other", type = RubyBaseNodeWithExecute.class)
    public abstract static class ReplaceNode extends CoreMethodNode {

        @CreateCast("other")
        protected ToStrNode coerceOtherToString(RubyBaseNodeWithExecute other) {
            return ToStrNodeGen.create(other);
        }

        @Specialization(guards = "string == other")
        protected RubyString replaceStringIsSameAsOther(RubyString string, RubyString other) {
            return string;
        }


        @Specialization(guards = { "string != other" })
        protected RubyString replace(RubyString string, RubyString other) {
            string.setRope(other.rope, other.encoding);
            return string;
        }

        @Specialization
        protected RubyString replace(RubyString string, ImmutableRubyString other) {
            string.setRope(other.rope, other.getEncoding());
            return string;
        }

    }

    @CoreMethod(names = "rstrip!", raiseIfNotMutableSelf = true)
    @ImportStatic(StringGuards.class)
    public abstract static class RstripBangNode extends CoreMethodArrayArgumentsNode {

        @Child GetCodePointNode getCodePointNode = GetCodePointNode.create();
        @Child SingleByteOptimizableNode singleByteOptimizableNode = SingleByteOptimizableNode.create();
        @Child TruffleString.SubstringByteIndexNode substringNode = TruffleString.SubstringByteIndexNode.create();

        @Specialization(guards = "isEmpty(string.rope)")
        protected Object rstripBangEmptyString(RubyString string) {
            return nil;
        }

        @Specialization(
                guards = { "!isEmpty(string.rope)", "isSingleByteOptimizable(string, singleByteOptimizableNode)" })
        protected Object rstripBangSingleByte(RubyString string,
                @Cached BytesNode bytesNode,
                @Cached @Exclusive ConditionProfile noopProfile) {
            // Taken from org.jruby.RubyString#rstrip_bang19 and org.jruby.RubyString#singleByteRStrip19.

            var encoding = string.encoding;
            final Rope rope = string.rope;
            var tstring = string.tstring;
            final int lastCodePoint = getCodePointNode
                    .executeGetCodePoint(encoding, rope, rope.byteLength() - 1);

            // Check the last code point to see if it's a space or NULL. In the case of strings without leading spaces,
            // this check can avoid having to materialize the entire byte[] (a potentially expensive operation
            // for ropes) and can avoid having to compile the while loop.
            final boolean willStrip = lastCodePoint == 0x00 || StringSupport.isAsciiSpace(lastCodePoint);
            if (noopProfile.profile(!willStrip)) {
                return nil;
            }

            final int end = rope.byteLength();
            final byte[] bytes = bytesNode.execute(rope);

            int endp = end - 1;
            while (endp >= 0 && (bytes[endp] == 0 || StringSupport.isAsciiSpace(bytes[endp]))) {
                endp--;
            }

            string.setTString(substringNode.execute(tstring, 0, endp + 1, encoding.tencoding, true));
            return string;
        }

        @TruffleBoundary
        @Specialization(
                guards = { "!isEmpty(string.rope)", "!isSingleByteOptimizable(string, singleByteOptimizableNode)" })
        protected Object rstripBang(RubyString string,
                @Cached GetActualEncodingNode getActualEncodingNode,
                @Cached @Exclusive ConditionProfile dummyEncodingProfile,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            // Taken from org.jruby.RubyString#rstrip_bang19 and org.jruby.RubyString#multiByteRStrip19.

            final Rope rope = string.rope;
            var tstring = string.tstring;
            final RubyEncoding enc = getActualEncodingNode.execute(rope, strings.getEncoding(string));

            if (dummyEncodingProfile.profile(enc.jcoding.isDummy())) {
                throw new RaiseException(
                        getContext(),
                        coreExceptions().encodingCompatibilityErrorIncompatibleWithOperation(enc.jcoding, this));
            }

            final byte[] bytes = rope.getBytes();
            final int start = 0;
            final int end = rope.byteLength();

            int endp = end;
            int prev;
            while ((prev = prevCharHead(enc.jcoding, bytes, start, endp, end)) != -1) {
                int point = getCodePointNode.executeGetCodePoint(enc, rope, prev);
                if (point != 0 && !ASCIIEncoding.INSTANCE.isSpace(point)) {
                    break;
                }
                endp = prev;
            }

            if (endp < end) {
                string.setTString(substringNode.execute(tstring, 0, endp - start, enc.tencoding, true));
                return string;
            }
            return nil;
        }

        @TruffleBoundary
        private int prevCharHead(Encoding enc, byte[] bytes, int p, int s, int end) {
            return enc.prevCharHead(bytes, p, s, end);
        }
    }

    @Primitive(name = "string_scrub")
    @ImportStatic(StringGuards.class)
    public abstract static class ScrubNode extends PrimitiveArrayArgumentsNode {

        @Child private CallBlockNode yieldNode = CallBlockNode.create();
        @Child CodeRangeNode codeRangeNode = CodeRangeNode.create();
        @Child private TruffleString.ConcatNode concatNode = TruffleString.ConcatNode.create();
        @Child private CalculateCharacterLengthNode calculateCharacterLengthNode = CalculateCharacterLengthNode
                .create();
        @Child private BytesNode bytesNode = BytesNode.create();
        @Child TruffleString.SubstringByteIndexNode substringNode = TruffleString.SubstringByteIndexNode.create();

        @Specialization(
                guards = {
                        "isBrokenCodeRange(rope, codeRangeNode)",
                        "isAsciiCompatible(rope)" })
        protected RubyString scrubAsciiCompat(Object string, RubyProc block,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Bind("strings.getRope(string)") Rope rope) {
            final CodeRange cr = codeRangeNode.execute(rope);
            var encoding = strings.getEncoding(string);
            final Encoding enc = encoding.jcoding;
            var tencoding = encoding.tencoding;
            var tstring = strings.getTString(string);
            TruffleString buf = EMPTY_BINARY_TSTRING;

            final byte[] pBytes = bytesNode.execute(rope);
            final int e = pBytes.length;

            int p = 0;
            int p1 = 0;

            p = StringSupport.searchNonAscii(pBytes, p, e);
            if (p == -1) {
                p = e;
            }
            while (p < e) {
                int clen = calculateCharacterLengthNode.characterLength(enc, CR_BROKEN, Bytes.fromRange(pBytes, p, e));
                if (MBCLEN_NEEDMORE_P(clen)) {
                    break;
                } else if (MBCLEN_CHARFOUND_P(clen)) {
                    p += MBCLEN_CHARFOUND_LEN(clen);
                } else if (MBCLEN_INVALID_P(clen)) {
                    // p1~p: valid ascii/multibyte chars
                    // p ~e: invalid bytes + unknown bytes
                    clen = enc.maxLength();
                    if (p1 < p) {
                        buf = concatNode.execute(buf, substringNode.execute(tstring, p1, p - p1, tencoding, true),
                                tencoding, true);
                    }

                    if (e - p < clen) {
                        clen = e - p;
                    }
                    if (clen <= 2) {
                        clen = 1;
                    } else {
                        clen--;
                        for (; clen > 1; clen--) {
                            int clen2 = StringSupport.characterLength(enc, cr, pBytes, p, p + clen);
                            if (MBCLEN_NEEDMORE_P(clen2)) {
                                break;
                            }
                        }
                    }
                    Object repl = yieldNode.yield(block,
                            createSubString(substringNode, tstring, encoding, p, clen));
                    buf = concatNode.execute(buf, strings.getTString(repl), tencoding, true);
                    p += clen;
                    p1 = p;
                    p = StringSupport.searchNonAscii(pBytes, p, e);
                    if (p == -1) {
                        p = e;
                        break;
                    }
                }
            }
            if (p1 < p) {
                buf = concatNode.execute(buf, substringNode.execute(tstring, p1, p - p1, tencoding, true), tencoding,
                        true);
            }
            if (p < e) {
                Object repl = yieldNode.yield(block, createSubString(substringNode, tstring, encoding, p, e - p));
                buf = concatNode.execute(buf, strings.getTString(repl), tencoding, true);
            }
            return createString(buf, encoding);
        }

        @Specialization(
                guards = {
                        "isBrokenCodeRange(rope, codeRangeNode)",
                        "!isAsciiCompatible(rope)" })
        protected RubyString scrubAsciiIncompatible(Object string, RubyProc block,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Bind("strings.getRope(string)") Rope rope,
                @Cached CalculateCharacterLengthNode calculateCharacterLengthNode) {
            final CodeRange cr = codeRangeNode.execute(rope);
            var encoding = strings.getEncoding(string);
            final Encoding enc = encoding.jcoding;
            var tencoding = encoding.tencoding;
            var tstring = strings.getTString(string);
            TruffleString buf = EMPTY_BINARY_TSTRING;

            final byte[] pBytes = bytesNode.execute(rope);
            final int e = pBytes.length;

            int p = 0;
            int p1 = 0;
            final int mbminlen = enc.minLength();


            while (p < e) {
                int clen = calculateCharacterLengthNode.characterLength(enc, CR_BROKEN, Bytes.fromRange(pBytes, p, e));
                if (MBCLEN_NEEDMORE_P(clen)) {
                    break;
                } else if (MBCLEN_CHARFOUND_P(clen)) {
                    p += MBCLEN_CHARFOUND_LEN(clen);
                } else if (MBCLEN_INVALID_P(clen)) {
                    final int q = p;
                    clen = enc.maxLength();

                    if (p1 < p) {
                        buf = concatNode.execute(buf, substringNode.execute(tstring, p1, p - p1, tencoding, true),
                                tencoding, true);
                    }

                    if (e - p < clen) {
                        clen = e - p;
                    }
                    if (clen <= mbminlen * 2) {
                        clen = mbminlen;
                    } else {
                        clen -= mbminlen;
                        for (; clen > mbminlen; clen -= mbminlen) {
                            int clen2 = calculateCharacterLengthNode.characterLength(enc, cr,
                                    new Bytes(pBytes, q, clen));
                            if (MBCLEN_NEEDMORE_P(clen2)) {
                                break;
                            }
                        }
                    }

                    RubyString repl = (RubyString) yieldNode.yield(block,
                            createSubString(substringNode, tstring, encoding, p, clen));
                    buf = concatNode.execute(buf, repl.tstring, tencoding, true);
                    p += clen;
                    p1 = p;
                }
            }
            if (p1 < p) {
                buf = concatNode.execute(buf, substringNode.execute(tstring, p1, p - p1, tencoding, true), tencoding,
                        true);
            }
            if (p < e) {
                RubyString repl = (RubyString) yieldNode.yield(block,
                        createSubString(substringNode, tstring, encoding, p, e - p));
                buf = concatNode.execute(buf, repl.tstring, tencoding, true);
            }

            return createString(buf, encoding);
        }

    }

    @Primitive(name = "string_swapcase!", raiseIfNotMutable = 0, lowerFixnum = 1)
    @ImportStatic({ StringGuards.class, Config.class })
    public abstract static class StringSwapcaseBangPrimitiveNode extends PrimitiveArrayArgumentsNode {

        @Child SingleByteOptimizableNode singleByteOptimizableNode = SingleByteOptimizableNode.create();

        @Specialization(guards = { "isSingleByteCaseMapping(string, caseMappingOptions, singleByteOptimizableNode)" })
        protected Object swapcaseSingleByte(RubyString string, int caseMappingOptions,
                @Cached("createSwapCase()") InvertAsciiCaseNode invertAsciiCaseNode) {
            return invertAsciiCaseNode.executeInvert(string);
        }

        @Specialization(guards = { "isSimpleAsciiCaseMapping(string, caseMappingOptions, singleByteOptimizableNode)" })
        protected Object swapcaseMultiByteAsciiSimple(RubyString string, int caseMappingOptions,
                @Cached @Shared("bytesNode") BytesNode bytesNode,
                @Cached @Shared("codeRangeNode") CodeRangeNode codeRangeNode,
                @Cached @Shared("fromByteArrayNode") TruffleString.FromByteArrayNode fromByteArrayNode,
                @Cached @Shared("dummyEncodingProfile") ConditionProfile dummyEncodingProfile,
                @Cached @Shared("modifiedProfile") ConditionProfile modifiedProfile) {
            // Taken from org.jruby.RubyString#swapcase_bang19.

            final Rope rope = string.rope;
            final Encoding enc = rope.getEncoding();

            if (dummyEncodingProfile.profile(enc.isDummy())) {
                throw new RaiseException(
                        getContext(),
                        coreExceptions().encodingCompatibilityErrorIncompatibleWithOperation(enc, this));
            }

            final CodeRange cr = codeRangeNode.execute(rope);
            final byte[] inputBytes = bytesNode.execute(rope);
            final byte[] outputBytes = StringSupport.swapcaseMultiByteAsciiSimple(enc, cr, inputBytes);

            if (modifiedProfile.profile(inputBytes != outputBytes)) {
                string.setTString(fromByteArrayNode.execute(outputBytes, string.encoding.tencoding)); // cr, characterLengthNode.execute(rope)
                return string;
            } else {
                return nil;
            }
        }

        @Specialization(guards = "isComplexCaseMapping(string, caseMappingOptions, singleByteOptimizableNode)")
        protected Object swapcaseMultiByteComplex(RubyString string, int caseMappingOptions,
                @Cached @Shared("bytesNode") BytesNode bytesNode,
                @Cached @Shared("codeRangeNode") CodeRangeNode codeRangeNode,
                @Cached @Shared("fromByteArrayNode") TruffleString.FromByteArrayNode fromByteArrayNode,
                @Cached @Shared("dummyEncodingProfile") ConditionProfile dummyEncodingProfile,
                @Cached @Shared("modifiedProfile") ConditionProfile modifiedProfile) {
            // Taken from org.jruby.RubyString#swapcase_bang19.

            final Rope rope = string.rope;
            final Encoding enc = rope.getEncoding();

            if (dummyEncodingProfile.profile(enc.isDummy())) {
                throw new RaiseException(
                        getContext(),
                        coreExceptions().encodingCompatibilityErrorIncompatibleWithOperation(enc, this));
            }

            final RopeBuilder builder = RopeBuilder.createRopeBuilder(bytesNode.execute(rope), rope.getEncoding());
            final boolean modified = StringSupport
                    .swapCaseMultiByteComplex(enc, codeRangeNode.execute(rope), builder, caseMappingOptions, this);

            if (modifiedProfile.profile(modified)) {
                string.setTString(fromByteArrayNode.execute(builder.getBytes(), string.encoding.tencoding));
                return string;
            } else {
                return nil;
            }
        }
    }

    @CoreMethod(names = "dump")
    @ImportStatic(StringGuards.class)
    public abstract static class DumpNode extends CoreMethodArrayArgumentsNode {

        private static final byte[] FORCE_ENCODING_CALL_BYTES = RopeOperations.encodeAsciiBytes(".force_encoding(\"");

        @TruffleBoundary
        @Specialization(guards = "isAsciiCompatible(libString.getRope(string))")
        protected RubyString dumpAsciiCompatible(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @Cached TruffleString.FromByteArrayNode fromByteArrayNode) {
            ByteArrayBuilder outputBytes = dumpCommon(libString.getRope(string));

            return createString(fromByteArrayNode, outputBytes.getBytes(), libString.getEncoding(string));
        }

        @TruffleBoundary
        @Specialization(guards = "!isAsciiCompatible(libString.getRope(string))")
        protected RubyString dump(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @Cached TruffleString.FromByteArrayNode fromByteArrayNode) {
            ByteArrayBuilder outputBytes = dumpCommon(libString.getRope(string));

            outputBytes.append(FORCE_ENCODING_CALL_BYTES);
            outputBytes.append(libString.getRope(string).getEncoding().getName());
            outputBytes.append((byte) '"');
            outputBytes.append((byte) ')');

            return createString(fromByteArrayNode, outputBytes.getBytes(), Encodings.BINARY);
        }

        // Taken from org.jruby.RubyString#dump
        private ByteArrayBuilder dumpCommon(Rope rope) {
            ByteArrayBuilder buf = null;
            final Encoding enc = rope.getEncoding();
            final CodeRange cr = rope.getCodeRange();

            int p = 0;
            int end = rope.byteLength();
            byte[] bytes = rope.getBytes();

            int len = 2;
            while (p < end) {
                int c = bytes[p++] & 0xff;

                switch (c) {
                    case '"':
                    case '\\':
                    case '\n':
                    case '\r':
                    case '\t':
                    case '\f':
                    case '\013':
                    case '\010':
                    case '\007':
                    case '\033':
                        len += 2;
                        break;
                    case '#':
                        len += p < end && isEVStr(bytes[p] & 0xff) ? 2 : 1;
                        break;
                    default:
                        if (ASCIIEncoding.INSTANCE.isPrint(c)) {
                            len++;
                        } else {
                            if (enc.isUTF8()) {
                                int n = StringSupport.characterLength(enc, cr, bytes, p - 1, end) - 1;
                                if (n > 0) {
                                    if (buf == null) {
                                        buf = new ByteArrayBuilder();
                                    }
                                    int cc = StringSupport.codePoint(enc, rope.getCodeRange(), bytes, p - 1, end, this);
                                    buf.append(StringUtils.formatASCIIBytes("%x", cc));
                                    len += buf.getLength() + 4;
                                    buf.setLength(0);
                                    p += n;
                                    break;
                                }
                            }
                            len += 4;
                        }
                        break;
                }
            }

            if (!enc.isAsciiCompatible()) {
                len += FORCE_ENCODING_CALL_BYTES.length + enc.getName().length + "\")".length();
            }

            RopeBuilder outBytes = new RopeBuilder();
            outBytes.unsafeEnsureSpace(len);
            byte[] out = outBytes.getUnsafeBytes();
            int q = 0;
            p = 0;
            end = rope.byteLength();

            out[q++] = '"';
            while (p < end) {
                int c = bytes[p++] & 0xff;
                if (c == '"' || c == '\\') {
                    out[q++] = '\\';
                    out[q++] = (byte) c;
                } else if (c == '#') {
                    if (p < end && isEVStr(bytes[p] & 0xff)) {
                        out[q++] = '\\';
                    }
                    out[q++] = '#';
                } else if (c == '\n') {
                    out[q++] = '\\';
                    out[q++] = 'n';
                } else if (c == '\r') {
                    out[q++] = '\\';
                    out[q++] = 'r';
                } else if (c == '\t') {
                    out[q++] = '\\';
                    out[q++] = 't';
                } else if (c == '\f') {
                    out[q++] = '\\';
                    out[q++] = 'f';
                } else if (c == '\013') {
                    out[q++] = '\\';
                    out[q++] = 'v';
                } else if (c == '\010') {
                    out[q++] = '\\';
                    out[q++] = 'b';
                } else if (c == '\007') {
                    out[q++] = '\\';
                    out[q++] = 'a';
                } else if (c == '\033') {
                    out[q++] = '\\';
                    out[q++] = 'e';
                } else if (ASCIIEncoding.INSTANCE.isPrint(c)) {
                    out[q++] = (byte) c;
                } else {
                    out[q++] = '\\';
                    if (enc.isUTF8()) {
                        int n = StringSupport.characterLength(enc, cr, bytes, p - 1, end) - 1;
                        if (n > 0) {
                            int cc = StringSupport.codePoint(enc, cr, bytes, p - 1, end, this);
                            p += n;
                            outBytes.setLength(q);
                            outBytes.append(StringUtils.formatASCIIBytes("u%04X", cc));
                            q = outBytes.getLength();
                            continue;
                        }
                    }
                    outBytes.setLength(q);
                    outBytes.append(StringUtils.formatASCIIBytes("x%02X", c));
                    q = outBytes.getLength();
                }
            }
            out[q++] = '"';
            outBytes.setLength(q);
            assert out == outBytes.getUnsafeBytes(); // must not reallocate

            return outBytes;
        }

        private static boolean isEVStr(int c) {
            return c == '$' || c == '@' || c == '{';
        }

    }

    @CoreMethod(names = "undump")
    @ImportStatic(StringGuards.class)
    public abstract static class UndumpNode extends CoreMethodArrayArgumentsNode {
        @Specialization(guards = "isAsciiCompatible(libString.getRope(string))")
        protected RubyString undumpAsciiCompatible(Object string,
                @Cached MakeStringNode makeStringNode,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            // Taken from org.jruby.RubyString#undump
            Pair<RopeBuilder, RubyEncoding> outputBytesResult = StringSupport.undump(
                    libString.getRope(string),
                    libString.getEncoding(string),
                    getContext(),
                    this);
            final RubyEncoding rubyEncoding = outputBytesResult.getRight();
            return makeStringNode.fromBuilder(outputBytesResult.getLeft(), rubyEncoding, CR_UNKNOWN);
        }

        @Specialization(guards = "!isAsciiCompatible(libString.getRope(string))")
        protected RubyString undumpNonAsciiCompatible(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            throw new RaiseException(
                    getContext(),
                    getContext().getCoreExceptions().encodingCompatibilityError(
                            Utils.concat("ASCII incompatible encoding: ", libString.getRope(string).encoding),
                            this));
        }

    }

    @CoreMethod(names = "setbyte", required = 2, raiseIfNotMutableSelf = true, lowerFixnum = { 1, 2 })
    @NodeChild(value = "string", type = RubyNode.class)
    @NodeChild(value = "index", type = RubyBaseNodeWithExecute.class)
    @NodeChild(value = "value", type = RubyBaseNodeWithExecute.class)
    @ImportStatic(StringGuards.class)
    public abstract static class SetByteNode extends CoreMethodNode {

        @Child private CheckIndexNode checkIndexNode = CheckIndexNodeGen.create();

        @CreateCast("index")
        protected ToIntNode coerceIndexToInt(RubyBaseNodeWithExecute index) {
            return ToIntNode.create(index);
        }

        @CreateCast("value")
        protected ToIntNode coerceValueToInt(RubyBaseNodeWithExecute value) {
            return ToIntNode.create(value);
        }

        @Specialization(guards = "tstring.isMutable()")
        protected int mutable(RubyString string, int index, int value,
                @Bind("string.tstring") AbstractTruffleString tstring,
                @Cached MutableTruffleString.WriteByteNode writeByteNode) {
            var tencoding = string.encoding.tencoding;
            final int normalizedIndex = checkIndexNode.executeCheck(index, tstring.byteLength(tencoding));

            writeByteNode.execute((MutableTruffleString) tstring, normalizedIndex, (byte) value, tencoding);
            return value;
        }

        @Specialization(guards = "!tstring.isMutable()")
        protected int immutable(RubyString string, int index, int value,
                @Bind("string.tstring") AbstractTruffleString tstring,
                @Cached MutableTruffleString.AsMutableTruffleStringNode asMutableTruffleStringNode,
                @Cached MutableTruffleString.WriteByteNode writeByteNode) {
            var tencoding = string.encoding.tencoding;
            final int normalizedIndex = checkIndexNode.executeCheck(index, tstring.byteLength(tencoding));

            MutableTruffleString mutableTString = asMutableTruffleStringNode.execute(tstring, tencoding);
            writeByteNode.execute(mutableTString, normalizedIndex, (byte) value, tencoding);
            string.setTString(mutableTString);
            return value;
        }
    }

    public abstract static class CheckIndexNode extends RubyBaseNode {

        public abstract int executeCheck(int index, int length);

        @Specialization
        protected int checkIndex(int index, int length,
                @Cached ConditionProfile negativeIndexProfile,
                @Cached BranchProfile errorProfile) {
            if (index >= length) {
                errorProfile.enter();
                throw new RaiseException(
                        getContext(),
                        getContext().getCoreExceptions().indexErrorOutOfString(index, this));
            }

            if (negativeIndexProfile.profile(index < 0)) {
                index += length;
                if (index < 0) {
                    errorProfile.enter();
                    throw new RaiseException(
                            getContext(),
                            getContext().getCoreExceptions().indexErrorOutOfString(index, this));
                }
            }

            return index;
        }

    }

    public abstract static class NormalizeIndexNode extends RubyBaseNode {

        public abstract int executeNormalize(int index, int length);

        public static NormalizeIndexNode create() {
            return NormalizeIndexNodeGen.create();
        }

        @Specialization
        protected int normalizeIndex(int index, int length,
                @Cached ConditionProfile negativeIndexProfile) {
            if (negativeIndexProfile.profile(index < 0)) {
                return index + length;
            }

            return index;
        }

    }

    @CoreMethod(names = { "size", "length" })
    @ImportStatic(StringGuards.class)
    public abstract static class SizeNode extends CoreMethodArrayArgumentsNode {

        public static SizeNode create() {
            return StringNodesFactory.SizeNodeFactory.create(null);
        }

        public abstract int execute(Object string);

        @Specialization
        protected int size(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @Cached CharacterLengthNode characterLengthNode) {
            return characterLengthNode.execute(libString.getRope(string));
        }

    }

    @CoreMethod(names = "squeeze!", rest = true, raiseIfNotMutableSelf = true)
    @ImportStatic(StringGuards.class)
    public abstract static class SqueezeBangNode extends CoreMethodArrayArgumentsNode {

        @Child private CheckEncodingNode checkEncodingNode;
        private final ConditionProfile singleByteOptimizableProfile = ConditionProfile.create();

        @Specialization(guards = "isEmpty(string.rope)")
        protected Object squeezeBangEmptyString(RubyString string, Object[] args) {
            return nil;
        }

        @TruffleBoundary
        @Specialization(guards = { "!isEmpty(string.rope)", "noArguments(args)" })
        protected Object squeezeBangZeroArgs(RubyString string, Object[] args) {
            // Taken from org.jruby.RubyString#squeeze_bang19.

            final Rope rope = string.rope;
            final RopeBuilder buffer = RopeOperations.toRopeBuilderCopy(rope);

            final boolean[] squeeze = new boolean[StringSupport.TRANS_SIZE];
            for (int i = 0; i < StringSupport.TRANS_SIZE; i++) {
                squeeze[i] = true;
            }

            if (singleByteOptimizableProfile.profile(rope.isSingleByteOptimizable())) {
                if (!StringSupport.singleByteSqueeze(buffer, squeeze)) {
                    return nil;
                } else {
                    string.setRope(RopeOperations.ropeFromRopeBuilder(buffer));
                }
            } else {
                if (!StringSupport
                        .multiByteSqueeze(
                                buffer,
                                rope.getCodeRange(),
                                squeeze,
                                null,
                                string.rope.getEncoding(),
                                false,
                                this)) {
                    return nil;
                } else {
                    string.setRope(RopeOperations.ropeFromRopeBuilder(buffer));
                }
            }

            return string;
        }

        @Specialization(guards = { "!isEmpty(string.rope)", "!noArguments(args)" })
        protected Object squeezeBang(VirtualFrame frame, RubyString string, Object[] args,
                @Cached ToStrNode toStrNode) {
            // Taken from org.jruby.RubyString#squeeze_bang19.

            final Object[] otherStrings = new Object[args.length];

            for (int i = 0; i < args.length; i++) {
                otherStrings[i] = toStrNode.execute(args[i]);
            }

            return performSqueezeBang(string, otherStrings);
        }

        @TruffleBoundary
        private Object performSqueezeBang(RubyString string, Object[] otherStrings) {
            if (checkEncodingNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                checkEncodingNode = insert(CheckEncodingNode.create());
            }

            final Rope rope = string.rope;
            final RopeBuilder buffer = RopeOperations.toRopeBuilderCopy(rope);

            Object otherStr = otherStrings[0];
            var otherRope = RubyStringLibrary.getUncached().getTString(otherStr);
            var otherEncoding = RubyStringLibrary.getUncached().getEncoding(otherStr);
            RubyEncoding enc = checkEncodingNode.executeCheckEncoding(string, otherStr);
            final boolean squeeze[] = new boolean[StringSupport.TRANS_SIZE + 1];

            boolean singlebyte = rope.isSingleByteOptimizable() &&
                    TStringUtils.isSingleByteOptimizable(otherRope, otherEncoding);

            if (singlebyte && otherRope.byteLength(otherEncoding.tencoding) == 1 && otherStrings.length == 1) {
                squeeze[otherRope.readByteUncached(0, otherEncoding.tencoding)] = true;
                if (!StringSupport.singleByteSqueeze(buffer, squeeze)) {
                    return nil;
                } else {
                    string.setRope(RopeOperations.ropeFromRopeBuilder(buffer));
                    return string;
                }
            }

            StringSupport.TrTables tables = StringSupport
                    .trSetupTable(otherRope, otherEncoding, squeeze, null, true, enc.jcoding, this);

            for (int i = 1; i < otherStrings.length; i++) {
                otherStr = otherStrings[i];
                otherRope = RubyStringLibrary.getUncached().getTString(otherStr);
                otherEncoding = RubyStringLibrary.getUncached().getEncoding(otherStr);
                enc = checkEncodingNode.executeCheckEncoding(string, otherStr);
                singlebyte = singlebyte && TStringUtils.isSingleByteOptimizable(otherRope, otherEncoding);
                tables = StringSupport.trSetupTable(otherRope, otherEncoding, squeeze, tables, false, enc.jcoding,
                        this);
            }

            if (singleByteOptimizableProfile.profile(singlebyte)) {
                if (!StringSupport.singleByteSqueeze(buffer, squeeze)) {
                    return nil;
                } else {
                    string.setRope(RopeOperations.ropeFromRopeBuilder(buffer));
                }
            } else {
                if (!StringSupport
                        .multiByteSqueeze(buffer, rope.getCodeRange(), squeeze, tables, enc.jcoding, true, this)) {
                    return nil;
                } else {
                    string.setRope(RopeOperations.ropeFromRopeBuilder(buffer));
                }
            }

            return string;
        }

    }

    @CoreMethod(names = "succ!", raiseIfNotMutableSelf = true)
    public abstract static class SuccBangNode extends CoreMethodArrayArgumentsNode {
        @Specialization
        protected RubyString succBang(RubyString string,
                @Cached TruffleString.FromByteArrayNode fromByteArrayNode) {
            final Rope rope = string.rope;

            if (!rope.isEmpty()) {
                final RopeBuilder succBuilder = StringSupport.succCommon(rope, this);
                string.setTString(fromByteArrayNode.execute(succBuilder.getBytes(), string.encoding.tencoding));
            }

            return string;
        }
    }

    // String#sum is in Java because without OSR we can't warm up the Rubinius implementation

    @CoreMethod(names = "sum", optional = 1)
    public abstract static class SumNode extends CoreMethodArrayArgumentsNode {

        public static SumNode create() {
            return SumNodeFactory.create(null);
        }

        public abstract Object executeSum(Object string, Object bits);

        @Child private DispatchNode addNode = DispatchNode.create();
        private final BytesNode bytesNode = BytesNode.create();

        @Specialization
        protected Object sum(Object string, long bits,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            // Copied from JRuby

            final Rope rope = strings.getRope(string);
            final byte[] bytes = bytesNode.execute(rope);
            int p = 0;
            final int len = rope.byteLength();
            final int end = p + len;

            if (bits >= 8 * 8) { // long size * bits in byte
                Object sum = 0;
                while (p < end) {
                    sum = addNode.call(sum, "+", bytes[p++] & 0xff);
                }
                return sum;
            } else {
                long sum = 0;
                while (p < end) {
                    sum += bytes[p++] & 0xff;
                }
                return bits == 0 ? sum : sum & (1L << bits) - 1L;
            }
        }

        @Specialization
        protected Object sum(Object string, NotProvided bits,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            return sum(string, 16, strings);
        }

        @Specialization(guards = { "!isImplicitLong(bits)", "wasProvided(bits)" })
        protected Object sum(Object string, Object bits,
                @Cached ToLongNode toLongNode,
                @Cached SumNode sumNode) {
            return sumNode.executeSum(string, toLongNode.execute(bits));
        }

    }

    @CoreMethod(names = "to_f")
    public abstract static class ToFNode extends CoreMethodArrayArgumentsNode {

        @Specialization
        @TruffleBoundary
        protected double toF(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            try {
                return convertToDouble(strings.getRope(string));
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        @TruffleBoundary
        private double convertToDouble(Rope rope) {
            return new DoubleConverter().parse(rope, false, true);
        }
    }

    @CoreMethod(names = { "to_s", "to_str" })
    public abstract static class ToSNode extends CoreMethodArrayArgumentsNode {

        @Specialization
        protected ImmutableRubyString toS(ImmutableRubyString string) {
            return string;
        }

        @Specialization(guards = "!isStringSubclass(string)")
        protected RubyString toS(RubyString string) {
            return string;
        }

        @Specialization(guards = "isStringSubclass(string)")
        protected RubyString toSOnSubclass(RubyString string) {
            return createString(string.tstring, string.encoding);
        }

        public boolean isStringSubclass(RubyString string) {
            return string.getLogicalClass() != coreLibrary().stringClass;
        }
    }

    @CoreMethod(names = { "to_sym", "intern" })
    @ImportStatic({ StringCachingGuards.class, StringGuards.class, StringOperations.class })
    public abstract static class ToSymNode extends CoreMethodArrayArgumentsNode {

        @Child CodeRangeNode codeRangeNode = CodeRangeNode.create();

        @Specialization(
                guards = {
                        "!isBrokenCodeRange(strings.getRope(string), codeRangeNode)",
                        "equalNode.execute(strings.getRope(string),cachedRope)",
                        "strings.getEncoding(string) == cachedEncoding" },
                limit = "getDefaultCacheLimit()")
        protected RubySymbol toSymCached(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached("strings.getRope(string)") Rope cachedRope,
                @Cached("strings.getEncoding(string)") RubyEncoding cachedEncoding,
                @Cached("getSymbol(cachedRope, cachedEncoding)") RubySymbol cachedSymbol,
                @Cached RopeNodes.EqualNode equalNode) {
            return cachedSymbol;
        }

        @Specialization(guards = "!isBrokenCodeRange(strings.getRope(string), codeRangeNode)", replaces = "toSymCached")
        protected RubySymbol toSym(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            return getSymbol(strings.getRope(string), strings.getEncoding(string));
        }

        @Specialization(guards = "isBrokenCodeRange(strings.getRope(string), codeRangeNode)")
        protected RubySymbol toSymBroken(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            throw new RaiseException(getContext(), coreExceptions().encodingError("invalid encoding symbol", this));
        }
    }

    @CoreMethod(names = "reverse!", raiseIfNotMutableSelf = true)
    @ImportStatic(StringGuards.class)
    public abstract static class ReverseBangNode extends CoreMethodArrayArgumentsNode {

        @Child
        TruffleString.CodePointLengthNode codePointLengthNode = TruffleString.CodePointLengthNode.create();
        @Child private TruffleString.FromByteArrayNode fromByteArrayNode = TruffleString.FromByteArrayNode.create();

        @Specialization(guards = "reverseIsEqualToSelf(string, codePointLengthNode)")
        protected RubyString reverseNoOp(RubyString string) {
            return string;
        }

        @Specialization(
                guards = {
                        "!reverseIsEqualToSelf(string, codePointLengthNode)",
                        "isSingleByteOptimizable(string, singleByteOptimizableNode)" })
        protected RubyString reverseSingleByteOptimizable(RubyString string,
                @Cached BytesNode bytesNode,
                @Cached SingleByteOptimizableNode singleByteOptimizableNode) {
            final Rope rope = string.rope;
            final byte[] originalBytes = bytesNode.execute(rope);
            final int len = originalBytes.length;
            final byte[] reversedBytes = new byte[len];

            for (int i = 0; i < len; i++) {
                reversedBytes[len - i - 1] = originalBytes[i];
            }

            string.setTString(fromByteArrayNode.execute(reversedBytes, string.encoding.tencoding)); // codeRangeNode.execute(rope), characterLengthNode.execute(rope)
            return string;
        }

        @Specialization(
                guards = {
                        "!reverseIsEqualToSelf(string, codePointLengthNode)",
                        "!isSingleByteOptimizable(string, singleByteOptimizableNode)" })
        protected RubyString reverse(RubyString string,
                @Cached BytesNode bytesNode,
                @Cached CodeRangeNode codeRangeNode,
                @Cached SingleByteOptimizableNode singleByteOptimizableNode) {
            // Taken from org.jruby.RubyString#reverse!

            final Rope rope = string.rope;
            final byte[] originalBytes = bytesNode.execute(rope);
            int p = 0;
            final int len = originalBytes.length;

            final Encoding enc = rope.getEncoding();
            final CodeRange cr = codeRangeNode.execute(rope);
            final int end = p + len;
            int op = len;
            final byte[] reversedBytes = new byte[len];

            while (p < end) {
                int cl = StringSupport.characterLength(enc, cr, originalBytes, p, end, true);
                if (cl > 1 || (originalBytes[p] & 0x80) != 0) {
                    op -= cl;
                    System.arraycopy(originalBytes, p, reversedBytes, op, cl);
                    p += cl;
                } else {
                    reversedBytes[--op] = originalBytes[p++];
                }
            }

            string.setTString(fromByteArrayNode.execute(reversedBytes, string.encoding.tencoding)); // codeRangeNode.execute(rope), characterLengthNode.execute(rope)
            return string;
        }

        public static boolean reverseIsEqualToSelf(RubyString string,
                                                   TruffleString.CodePointLengthNode codePointLengthNode) {
            return codePointLengthNode.execute(string.getTString(), string.encoding.tencoding) <= 1;
        }
    }

    @CoreMethod(names = "tr!", required = 2, raiseIfNotMutableSelf = true)
    @NodeChild(value = "self", type = RubyNode.class)
    @NodeChild(value = "fromStr", type = RubyBaseNodeWithExecute.class)
    @NodeChild(value = "toStr", type = RubyBaseNodeWithExecute.class)
    @ImportStatic(StringGuards.class)
    public abstract static class TrBangNode extends CoreMethodNode {

        @Child private CheckEncodingNode checkEncodingNode;
        @Child private DeleteBangNode deleteBangNode;

        @CreateCast("fromStr")
        protected ToStrNode coerceFromStrToString(RubyBaseNodeWithExecute fromStr) {
            return ToStrNodeGen.create(fromStr);
        }

        @CreateCast("toStr")
        protected ToStrNode coerceToStrToString(RubyBaseNodeWithExecute toStr) {
            return ToStrNodeGen.create(toStr);
        }

        @Specialization(guards = "isEmpty(self.rope)")
        protected Object trBangSelfEmpty(RubyString self, Object fromStr, Object toStr) {
            return nil;
        }

        @Specialization(
                guards = {
                        "!isEmpty(self.rope)",
                        "isEmpty(libToStr.getRope(toStr))" })
        protected Object trBangToEmpty(RubyString self, Object fromStr, Object toStr,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libToStr) {
            if (deleteBangNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                deleteBangNode = insert(DeleteBangNode.create());
            }

            return deleteBangNode.executeDeleteBang(self, new Object[]{ fromStr });
        }

        @Specialization(
                guards = {
                        "libFromStr.isRubyString(fromStr)",
                        "!isEmpty(self.rope)",
                        "!isEmpty(libToStr.getRope(toStr))" })
        protected Object trBangNoEmpty(RubyString self, Object fromStr, Object toStr,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libFromStr,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libToStr) {
            if (checkEncodingNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                checkEncodingNode = insert(CheckEncodingNode.create());
            }

            return StringNodesHelper.trTransHelper(
                    checkEncodingNode,
                    self,
                    self.rope,
                    fromStr,
                    libFromStr.getRope(fromStr),
                    toStr,
                    libToStr.getRope(toStr),
                    false,
                    this);
        }
    }

    @CoreMethod(names = "tr_s!", required = 2, raiseIfNotMutableSelf = true)
    @NodeChild(value = "self", type = RubyNode.class)
    @NodeChild(value = "fromStr", type = RubyBaseNodeWithExecute.class)
    @NodeChild(value = "toStrNode", type = RubyBaseNodeWithExecute.class)
    @ImportStatic(StringGuards.class)
    public abstract static class TrSBangNode extends CoreMethodNode {

        @Child private CheckEncodingNode checkEncodingNode;
        @Child private DeleteBangNode deleteBangNode;

        @CreateCast("fromStr")
        protected ToStrNode coerceFromStrToString(RubyBaseNodeWithExecute fromStr) {
            return ToStrNodeGen.create(fromStr);
        }

        @CreateCast("toStrNode")
        protected ToStrNode coerceToStrToString(RubyBaseNodeWithExecute toStr) {
            return ToStrNodeGen.create(toStr);
        }

        @Specialization(
                guards = { "isEmpty(self.rope)" })
        protected Object trSBangEmpty(RubyString self, Object fromStr, Object toStr) {
            return nil;
        }

        @Specialization(
                guards = { "libFromStr.isRubyString(fromStr)", "libToStr.isRubyString(toStr)", "!isEmpty(self.rope)" })
        protected Object trSBang(RubyString self, Object fromStr, Object toStr,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libFromStr,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libToStr) {
            if (libToStr.getRope(toStr).isEmpty()) {
                if (deleteBangNode == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    deleteBangNode = insert(DeleteBangNode.create());
                }

                return deleteBangNode.executeDeleteBang(self, new Object[]{ fromStr });
            }

            if (checkEncodingNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                checkEncodingNode = insert(CheckEncodingNode.create());
            }

            return StringNodesHelper.trTransHelper(
                    checkEncodingNode,
                    self,
                    self.rope,
                    fromStr,
                    libFromStr.getRope(fromStr),
                    toStr,
                    libToStr.getRope(toStr),
                    true,
                    this);
        }
    }

    @NodeChild(value = "string", type = RubyNode.class)
    @NodeChild(value = "format", type = RubyBaseNodeWithExecute.class)
    @CoreMethod(names = "unpack", required = 1)
    @ImportStatic({ StringCachingGuards.class, StringOperations.class })
    @ReportPolymorphism
    public abstract static class UnpackNode extends CoreMethodNode {

        private final BranchProfile exceptionProfile = BranchProfile.create();

        @CreateCast("format")
        protected ToStrNode coerceFormat(RubyBaseNodeWithExecute format) {
            return ToStrNodeGen.create(format);
        }

        @Specialization(guards = { "equalNode.execute(libFormat.getRope(format), cachedFormat)" })
        protected RubyArray unpackCached(Object string, Object format,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libFormat,
                @Cached("libFormat.getRope(format)") Rope cachedFormat,
                @Cached("create(compileFormat(libFormat.getRope(format)))") DirectCallNode callUnpackNode,
                @Cached BytesNode bytesNode,
                @Cached RopeNodes.EqualNode equalNode,
                @Cached StringGetAssociatedNode stringGetAssociatedNode) {
            final Rope rope = libString.getRope(string);

            final ArrayResult result;

            try {
                result = (ArrayResult) callUnpackNode.call(
                        new Object[]{
                                bytesNode.execute(rope),
                                rope.byteLength(),
                                stringGetAssociatedNode.execute(string) }); // TODO impl associated for ImmutableRubyString
            } catch (FormatException e) {
                exceptionProfile.enter();
                throw FormatExceptionTranslator.translate(getContext(), this, e);
            }

            return finishUnpack(result);
        }

        @Specialization(
                guards = "libFormat.isRubyString(format)",
                replaces = "unpackCached")
        protected RubyArray unpackUncached(Object string, Object format,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libFormat,
                @Cached IndirectCallNode callUnpackNode,
                @Cached BytesNode bytesNode,
                @Cached StringGetAssociatedNode stringGetAssociatedNode) {
            final Rope rope = libString.getRope(string);

            final ArrayResult result;

            try {
                result = (ArrayResult) callUnpackNode.call(
                        compileFormat(libFormat.getRope(format)),
                        new Object[]{
                                bytesNode.execute(rope),
                                rope.byteLength(),
                                stringGetAssociatedNode.execute(string) });
            } catch (FormatException e) {
                exceptionProfile.enter();
                throw FormatExceptionTranslator.translate(getContext(), this, e);
            }

            return finishUnpack(result);
        }

        private RubyArray finishUnpack(ArrayResult result) {
            return createArray(result.getOutput(), result.getOutputLength());
        }

        @TruffleBoundary
        protected RootCallTarget compileFormat(Rope rope) {
            try {
                return new UnpackCompiler(getLanguage(), this).compile(RopeOperations.decodeRope(rope));
            } catch (DeferredRaiseException dre) {
                throw dre.getException(getContext());
            }
        }

        protected int getCacheLimit() {
            return getLanguage().options.UNPACK_CACHE;
        }

    }

    public abstract static class InvertAsciiCaseBytesNode extends RubyBaseNode {

        private final boolean lowerToUpper;
        private final boolean upperToLower;

        public static InvertAsciiCaseBytesNode createLowerToUpper() {
            return InvertAsciiCaseBytesNodeGen.create(true, false);
        }

        public static InvertAsciiCaseBytesNode createUpperToLower() {
            return InvertAsciiCaseBytesNodeGen.create(false, true);
        }

        public static InvertAsciiCaseBytesNode createSwapCase() {
            return InvertAsciiCaseBytesNodeGen.create(true, true);
        }

        protected InvertAsciiCaseBytesNode(boolean lowerToUpper, boolean upperToLower) {
            this.lowerToUpper = lowerToUpper;
            this.upperToLower = upperToLower;
        }

        public abstract byte[] executeInvert(byte[] bytes, int start);

        @Specialization
        protected byte[] invert(byte[] bytes, int start,
                @Cached BranchProfile foundLowerCaseCharProfile,
                @Cached BranchProfile foundUpperCaseCharProfile,
                @Cached LoopConditionProfile loopProfile) {
            byte[] modified = null;

            int i = start;
            try {
                for (; loopProfile.inject(i < bytes.length); i++) {
                    final byte b = bytes[i];

                    if (lowerToUpper && StringSupport.isAsciiLowercase(b)) {
                        foundLowerCaseCharProfile.enter();

                        if (modified == null) {
                            modified = bytes.clone();
                        }

                        // Convert lower-case ASCII char to upper-case.
                        modified[i] ^= 0x20;
                    }

                    if (upperToLower && StringSupport.isAsciiUppercase(b)) {
                        foundUpperCaseCharProfile.enter();

                        if (modified == null) {
                            modified = bytes.clone();
                        }

                        // Convert upper-case ASCII char to lower-case.
                        modified[i] ^= 0x20;
                    }

                    TruffleSafepoint.poll(this);
                }
            } finally {
                profileAndReportLoopCount(loopProfile, i - start);
            }

            return modified;
        }

    }

    public abstract static class InvertAsciiCaseNode extends RubyBaseNode {

        @Child private InvertAsciiCaseBytesNode invertNode;

        public static InvertAsciiCaseNode createLowerToUpper() {
            return InvertAsciiCaseNodeGen.create(InvertAsciiCaseBytesNode.createLowerToUpper());
        }

        public static InvertAsciiCaseNode createUpperToLower() {
            return InvertAsciiCaseNodeGen.create(InvertAsciiCaseBytesNode.createUpperToLower());
        }

        public static InvertAsciiCaseNode createSwapCase() {
            return InvertAsciiCaseNodeGen.create(InvertAsciiCaseBytesNode.createSwapCase());
        }

        public InvertAsciiCaseNode(InvertAsciiCaseBytesNode invertNode) {
            this.invertNode = invertNode;
        }

        public abstract Object executeInvert(RubyString string);

        @Specialization
        protected Object invert(RubyString string,
                @Cached BytesNode bytesNode,
                @Cached TruffleString.FromByteArrayNode fromByteArrayNode,
                @Cached ConditionProfile noopProfile) {
            final Rope rope = string.rope;

            final byte[] bytes = bytesNode.execute(rope);
            byte[] modified = invertNode.executeInvert(bytes, 0);

            if (noopProfile.profile(modified == null)) {
                return nil;
            } else {
                string.setTString(fromByteArrayNode.execute(modified, string.encoding.tencoding)); // codeRangeNode.execute(rope), characterLengthNode.execute(rope)
                return string;
            }
        }

    }

    @Primitive(name = "string_upcase!", raiseIfNotMutable = 0, lowerFixnum = 1)
    @ImportStatic({ StringGuards.class, Config.class })
    public abstract static class StringUpcaseBangPrimitiveNode extends PrimitiveArrayArgumentsNode {

        @Child SingleByteOptimizableNode singleByteOptimizableNode = SingleByteOptimizableNode
                .create();

        @Specialization(guards = { "isSingleByteCaseMapping(string, caseMappingOptions, singleByteOptimizableNode)" })
        protected Object upcaseSingleByte(RubyString string, int caseMappingOptions,
                @Cached("createLowerToUpper()") InvertAsciiCaseNode invertAsciiCaseNode) {
            return invertAsciiCaseNode.executeInvert(string);
        }

        @Specialization(guards = { "isSimpleAsciiCaseMapping(string, caseMappingOptions, singleByteOptimizableNode)" })
        protected Object upcaseMultiByteAsciiSimple(RubyString string, int caseMappingOptions,
                @Cached @Shared("bytesNode") BytesNode bytesNode,
                @Cached CharacterLengthNode characterLengthNode,
                @Cached @Shared("codeRangeNode") CodeRangeNode codeRangeNode,
                @Cached @Shared("fromByteArrayNode") TruffleString.FromByteArrayNode fromByteArrayNode,
                @Cached @Shared("dummyEncodingProfile") ConditionProfile dummyEncodingProfile,
                @Cached @Shared("modifiedProfile") ConditionProfile modifiedProfile) {
            final Rope rope = string.rope;
            final Encoding encoding = rope.getEncoding();

            if (dummyEncodingProfile.profile(encoding.isDummy())) {
                throw new RaiseException(
                        getContext(),
                        coreExceptions().encodingCompatibilityErrorIncompatibleWithOperation(encoding, this));
            }

            final CodeRange cr = codeRangeNode.execute(rope);
            final byte[] inputBytes = bytesNode.execute(rope);
            final byte[] outputBytes = StringSupport.upcaseMultiByteAsciiSimple(encoding, cr, inputBytes);

            if (modifiedProfile.profile(inputBytes != outputBytes)) {
                string.setTString(fromByteArrayNode.execute(outputBytes, string.encoding.tencoding)); // cr, characterLengthNode.execute(rope)
                return string;
            } else {
                return nil;
            }
        }

        @Specialization(guards = { "isComplexCaseMapping(string, caseMappingOptions, singleByteOptimizableNode)" })
        protected Object upcaseMultiByteComplex(RubyString string, int caseMappingOptions,
                @Cached @Shared("bytesNode") BytesNode bytesNode,
                @Cached @Shared("codeRangeNode") CodeRangeNode codeRangeNode,
                @Cached @Shared("fromByteArrayNode") TruffleString.FromByteArrayNode fromByteArrayNode,
                @Cached @Shared("dummyEncodingProfile") ConditionProfile dummyEncodingProfile,
                @Cached @Shared("modifiedProfile") ConditionProfile modifiedProfile) {
            final Rope rope = string.rope;
            final Encoding encoding = rope.getEncoding();

            if (dummyEncodingProfile.profile(encoding.isDummy())) {
                throw new RaiseException(
                        getContext(),
                        coreExceptions().encodingCompatibilityErrorIncompatibleWithOperation(encoding, this));
            }

            final RopeBuilder builder = RopeBuilder.createRopeBuilder(bytesNode.execute(rope), rope.getEncoding());
            final boolean modified = StringSupport
                    .upcaseMultiByteComplex(encoding, codeRangeNode.execute(rope), builder, caseMappingOptions, this);
            if (modifiedProfile.profile(modified)) {
                string.setTString(fromByteArrayNode.execute(builder.getBytes(), string.encoding.tencoding));
                return string;
            } else {
                return nil;
            }
        }

    }

    @CoreMethod(names = "valid_encoding?")
    public abstract static class ValidEncodingQueryNode extends CoreMethodArrayArgumentsNode {

        @Specialization
        protected boolean validEncoding(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @Cached CodeRangeNode codeRangeNode) {
            final CodeRange codeRange = codeRangeNode.execute(libString.getRope(string));

            return codeRange != CR_BROKEN;
        }

    }

    @Primitive(name = "string_capitalize!", raiseIfNotMutable = 0, lowerFixnum = 1)
    @ImportStatic({ StringGuards.class, Config.class })
    public abstract static class StringCapitalizeBangPrimitiveNode extends PrimitiveArrayArgumentsNode {

        @Child private BytesNode bytesNode = BytesNode.create();
        @Child private CodeRangeNode codeRangeNode = CodeRangeNode.create();
        @Child private TruffleString.FromByteArrayNode fromByteArrayNode = TruffleString.FromByteArrayNode.create();
        @Child SingleByteOptimizableNode singleByteOptimizableNode = SingleByteOptimizableNode.create();

        @Specialization(guards = "isSingleByteCaseMapping(string, caseMappingOptions, singleByteOptimizableNode)")
        protected Object capitalizeSingleByte(RubyString string, int caseMappingOptions,
                @Cached("createUpperToLower()") InvertAsciiCaseBytesNode invertAsciiCaseNode,
                @Cached @Shared("emptyStringProfile") ConditionProfile emptyStringProfile,
                @Cached @Exclusive ConditionProfile firstCharIsLowerProfile,
                @Cached @Exclusive ConditionProfile otherCharsAlreadyLowerProfile,
                @Cached @Exclusive ConditionProfile mustCapitalizeFirstCharProfile) {
            final Rope rope = string.rope;

            if (emptyStringProfile.profile(rope.isEmpty())) {
                return nil;
            }

            final byte[] sourceBytes = bytesNode.execute(rope);
            final byte[] finalBytes;

            final byte[] processedBytes = invertAsciiCaseNode.executeInvert(sourceBytes, 1);

            if (otherCharsAlreadyLowerProfile.profile(processedBytes == null)) {
                // Bytes 1..N are either not letters or already lowercased. Time to check the first byte.

                if (firstCharIsLowerProfile.profile(StringSupport.isAsciiLowercase(sourceBytes[0]))) {
                    // The first char requires capitalization, but the remaining bytes in the original string are
                    // already properly cased.
                    finalBytes = sourceBytes.clone();
                } else {
                    // The string is already capitalized.
                    return nil;
                }
            } else {
                // At least one char was lowercased when looking at bytes 1..N. We still must check the first byte.
                finalBytes = processedBytes;
            }

            if (mustCapitalizeFirstCharProfile.profile(StringSupport.isAsciiLowercase(sourceBytes[0]))) {
                finalBytes[0] ^= 0x20;
            }

            string.setTString(fromByteArrayNode.execute(finalBytes, string.encoding.tencoding)); // codeRangeNode.execute(rope), characterLengthNode.execute(rope)
            return string;
        }

        @Specialization(guards = "isSimpleAsciiCaseMapping(string, caseMappingOptions, singleByteOptimizableNode)")
        protected Object capitalizeMultiByteAsciiSimple(RubyString string, int caseMappingOptions,
                @Cached @Shared("dummyEncodingProfile") BranchProfile dummyEncodingProfile,
                @Cached @Shared("emptyStringProfile") ConditionProfile emptyStringProfile,
                @Cached @Shared("modifiedProfile") ConditionProfile modifiedProfile) {
            // Taken from org.jruby.RubyString#capitalize_bang19.

            final Rope rope = string.rope;
            final Encoding enc = rope.getEncoding();

            if (enc.isDummy()) {
                dummyEncodingProfile.enter();
                throw new RaiseException(
                        getContext(),
                        coreExceptions().encodingCompatibilityErrorIncompatibleWithOperation(enc, this));
            }

            if (emptyStringProfile.profile(rope.isEmpty())) {
                return nil;
            }

            final CodeRange cr = codeRangeNode.execute(rope);
            final byte[] inputBytes = bytesNode.execute(rope);
            final byte[] outputBytes = StringSupport.capitalizeMultiByteAsciiSimple(enc, cr, inputBytes);

            if (modifiedProfile.profile(inputBytes != outputBytes)) {
                string.setTString(fromByteArrayNode.execute(outputBytes, string.encoding.tencoding)); // cr, characterLengthNode.execute(rope)
                return string;
            }

            return nil;
        }

        @Specialization(guards = "isComplexCaseMapping(string, caseMappingOptions, singleByteOptimizableNode)")
        protected Object capitalizeMultiByteComplex(RubyString string, int caseMappingOptions,
                @Cached @Shared("dummyEncodingProfile") BranchProfile dummyEncodingProfile,
                @Cached @Shared("emptyStringProfile") ConditionProfile emptyStringProfile,
                @Cached @Shared("modifiedProfile") ConditionProfile modifiedProfile) {
            final Rope rope = string.rope;
            final Encoding enc = rope.getEncoding();

            if (enc.isDummy()) {
                dummyEncodingProfile.enter();
                throw new RaiseException(
                        getContext(),
                        coreExceptions().encodingCompatibilityErrorIncompatibleWithOperation(enc, this));
            }

            if (emptyStringProfile.profile(rope.isEmpty())) {
                return nil;
            }

            final RopeBuilder builder = RopeBuilder.createRopeBuilder(bytesNode.execute(rope), rope.getEncoding());
            final boolean modified = StringSupport
                    .capitalizeMultiByteComplex(enc, codeRangeNode.execute(rope), builder, caseMappingOptions, this);
            if (modifiedProfile.profile(modified)) {
                string.setTString(fromByteArrayNode.execute(builder.getBytes(), string.encoding.tencoding));
                return string;
            } else {
                return nil;
            }
        }

    }

    @CoreMethod(names = "clear", raiseIfNotMutableSelf = true)
    public abstract static class ClearNode extends CoreMethodArrayArgumentsNode {
        @Specialization
        protected RubyString clear(RubyString string) {
            string.setTString(string.encoding.tencoding.getEmpty());
            return string;
        }
    }

    public static class StringNodesHelper {

        @TruffleBoundary
        private static Object trTransHelper(CheckEncodingNode checkEncodingNode, RubyString self, Rope selfRope,
                Object fromStr, Rope fromStrRope,
                Object toStr, Rope toStrRope, boolean sFlag, Node node) {
            final RubyEncoding e1 = checkEncodingNode.executeCheckEncoding(self, fromStr);
            final RubyEncoding e2 = checkEncodingNode.executeCheckEncoding(self, toStr);
            final RubyEncoding enc = e1 == e2 ? e1 : checkEncodingNode.executeCheckEncoding(fromStr, toStr);

            final Rope ret = StringSupport
                    .trTransHelper(selfRope, fromStrRope, toStrRope, e1.jcoding, enc.jcoding, sFlag, node);
            if (ret == null) {
                return Nil.INSTANCE;
            }

            self.setRope(ret, enc);
            return self;
        }
    }

    @Primitive(name = "character_printable_p")
    public abstract static class CharacterPrintablePrimitiveNode extends PrimitiveArrayArgumentsNode {

        @Specialization
        protected boolean isCharacterPrintable(Object character,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached ConditionProfile is7BitProfile,
                @Cached TruffleString.GetByteCodeRangeNode getCodeRangeNode,
                @Cached TruffleString.CodePointAtByteIndexNode getCodePointNode) {
            final RubyEncoding encoding = strings.getEncoding(character);
            final var tString = strings.getTString(character);

            final int codePoint = getCodePointNode.execute(tString, 0, encoding.tencoding);
            final boolean asciiOnly = TStringGuards.is7Bit(tString, encoding, getCodeRangeNode);

            if (is7BitProfile.profile(asciiOnly)) {
                return StringSupport.isAsciiPrintable(codePoint);
            } else {
                return isMBCPrintable(encoding.jcoding, codePoint);
            }
        }

        @TruffleBoundary
        protected boolean isMBCPrintable(Encoding encoding, int codePoint) {
            return encoding.isPrint(codePoint);
        }

    }

    @Primitive(name = "string_append")
    public abstract static class StringAppendPrimitiveNode extends CoreMethodArrayArgumentsNode {

        @Child private StringAppendNode stringAppendNode = StringAppendNode.create();

        public static StringAppendPrimitiveNode create() {
            return StringAppendPrimitiveNodeFactory.create(null);
        }

        public abstract RubyString executeStringAppend(RubyString string, Object other);

        @Specialization
        protected RubyString stringAppend(RubyString string, Object other) {
            final RubyString result = stringAppendNode.executeStringAppend(string, other);
            string.setTString(result.tstring, result.encoding);
            return string;
        }

    }

    @Primitive(name = "string_awk_split", lowerFixnum = 1)
    @ImportStatic(StringGuards.class)
    public abstract static class StringAwkSplitPrimitiveNode extends PrimitiveArrayArgumentsNode {

        @Child private BytesNode bytesNode = BytesNode.create();
        @Child private CallBlockNode yieldNode = CallBlockNode.create();
        @Child CodeRangeNode codeRangeNode = CodeRangeNode.create();

        private static final int SUBSTRING_CREATED = -1;

        @Specialization(guards = "is7Bit(strings.getRope(string), codeRangeNode)")
        protected Object stringAwkSplitSingleByte(Object string, int limit, Object block,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached ConditionProfile executeBlockProfile,
                @Cached ConditionProfile growArrayProfile,
                @Cached ConditionProfile trailingSubstringProfile,
                @Cached ConditionProfile trailingEmptyStringProfile,
                @Cached TruffleString.SubstringByteIndexNode substringNode) {
            Object[] ret = new Object[10];
            int storeIndex = 0;

            final Rope rope = strings.getRope(string);
            var tString = strings.getTString(string);
            final RubyEncoding encoding = strings.getEncoding(string);
            final byte[] bytes = bytesNode.execute(rope);

            int substringStart = 0;
            boolean findingSubstringEnd = false;
            for (int i = 0; i < bytes.length; i++) {
                if (StringSupport.isAsciiSpace(bytes[i])) {
                    if (findingSubstringEnd) {
                        findingSubstringEnd = false;

                        final RubyString substring = createSubString(substringNode, tString, encoding, substringStart,
                                i - substringStart);
                        ret = addSubstring(
                                ret,
                                storeIndex++,
                                substring,
                                block,
                                executeBlockProfile,
                                growArrayProfile);
                        substringStart = SUBSTRING_CREATED;
                    }
                } else {
                    if (!findingSubstringEnd) {
                        substringStart = i;
                        findingSubstringEnd = true;

                        if (storeIndex == limit - 1) {
                            break;
                        }
                    }
                }
            }

            if (trailingSubstringProfile.profile(findingSubstringEnd)) {
                final RubyString substring = createSubString(substringNode, tString, encoding, substringStart,
                        bytes.length - substringStart);
                ret = addSubstring(ret, storeIndex++, substring, block, executeBlockProfile, growArrayProfile);
            }

            if (trailingEmptyStringProfile.profile(limit < 0 && StringSupport.isAsciiSpace(bytes[bytes.length - 1]))) {
                final RubyString substring = createSubString(substringNode, tString, encoding, bytes.length - 1, 0);
                ret = addSubstring(ret, storeIndex++, substring, block, executeBlockProfile, growArrayProfile);
            }

            if (block == nil) {
                return createArray(ret, storeIndex);
            } else {
                return string;
            }
        }

        @TruffleBoundary
        @Specialization(guards = "!is7Bit(strings.getRope(string), codeRangeNode)")
        protected Object stringAwkSplit(Object string, int limit, Object block,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached ConditionProfile executeBlockProfile,
                @Cached ConditionProfile growArrayProfile,
                @Cached ConditionProfile trailingSubstringProfile,
                @Cached TruffleString.SubstringByteIndexNode substringNode,
                @Cached TruffleString.CodePointAtByteIndexNode codePointAtByteIndexNode) {
            Object[] ret = new Object[10];
            int storeIndex = 0;

            final Rope rope = strings.getRope(string);
            var tString = strings.getTString(string);
            final RubyEncoding encoding = strings.getEncoding(string);
            final boolean limitPositive = limit > 0;
            int i = limit > 0 ? 1 : 0;

            final byte[] bytes = bytesNode.execute(rope);
            int p = 0;
            int ptr = p;
            int len = rope.byteLength();
            int end = p + len;
            final Encoding enc = rope.getEncoding();
            final CodeRange cr = rope.getCodeRange();
            boolean skip = true;

            int e = 0, b = 0;
            while (p < end) {
                int c = codePointAtByteIndexNode.execute(tString, p, encoding.tencoding);
                p += StringSupport.characterLength(enc, cr, bytes, p, end, true);

                if (skip) {
                    if (StringSupport.isAsciiSpace(c)) {
                        b = p - ptr;
                    } else {
                        e = p - ptr;
                        skip = false;
                        if (limitPositive && limit <= i) {
                            break;
                        }
                    }
                } else {
                    if (StringSupport.isAsciiSpace(c)) {
                        var substring = createSubString(substringNode, tString, encoding, b, e - b);
                        ret = addSubstring(
                                ret,
                                storeIndex++,
                                substring,
                                block,
                                executeBlockProfile,
                                growArrayProfile);
                        skip = true;
                        b = p - ptr;
                        if (limitPositive) {
                            i++;
                        }
                    } else {
                        e = p - ptr;
                    }
                }
            }

            if (trailingSubstringProfile.profile(len > 0 && (limitPositive || len > b || limit < 0))) {
                var substring = createSubString(substringNode, tString, encoding, b, len - b);
                ret = addSubstring(ret, storeIndex++, substring, block, executeBlockProfile, growArrayProfile);
            }

            if (block == nil) {
                return createArray(ret, storeIndex);
            } else {
                return string;
            }
        }

        private Object[] addSubstring(Object[] store, int index, RubyString substring,
                Object block, ConditionProfile executeBlockProfile, ConditionProfile growArrayProfile) {
            if (executeBlockProfile.profile(block != nil)) {
                yieldNode.yield((RubyProc) block, substring);
            } else {
                if (growArrayProfile.profile(index < store.length)) {
                    store[index] = substring;
                } else {
                    store = ArrayUtils.grow(store, store.length * 2);
                    store[index] = substring;
                }
            }

            return store;
        }

    }

    @Primitive(name = "string_byte_substring", lowerFixnum = { 1, 2 })
    public abstract static class StringByteSubstringPrimitiveNode extends PrimitiveArrayArgumentsNode {

        @Child private NormalizeIndexNode normalizeIndexNode = NormalizeIndexNode.create();

        public static StringByteSubstringPrimitiveNode create() {
            return StringByteSubstringPrimitiveNodeFactory.create(null);
        }

        public abstract Object executeStringByteSubstring(Object string, Object index, Object length);

        @Specialization
        protected Object stringByteSubstring(Object string, int index, NotProvided length,
                @Cached ConditionProfile indexOutOfBoundsProfile,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @Cached TruffleString.SubstringByteIndexNode substringNode) {
            var tString = libString.getTString(string);
            var encoding = libString.getEncoding(string);
            final int stringByteLength = tString.byteLength(encoding.tencoding);
            final int normalizedIndex = normalizeIndexNode.executeNormalize(index, stringByteLength);

            if (indexOutOfBoundsProfile.profile(normalizedIndex < 0 || normalizedIndex >= stringByteLength)) {
                return nil;
            }

            return createSubString(substringNode, tString, encoding, normalizedIndex, 1);
        }

        @Specialization
        protected Object stringByteSubstring(Object string, int index, int length,
                @Cached ConditionProfile negativeLengthProfile,
                @Cached ConditionProfile indexOutOfBoundsProfile,
                @Cached ConditionProfile lengthTooLongProfile,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @Cached TruffleString.SubstringByteIndexNode substringNode) {
            if (negativeLengthProfile.profile(length < 0)) {
                return nil;
            }

            var tString = libString.getTString(string);
            var encoding = libString.getEncoding(string);
            final int stringByteLength = tString.byteLength(encoding.tencoding);
            final int normalizedIndex = normalizeIndexNode.executeNormalize(index, stringByteLength);

            if (indexOutOfBoundsProfile.profile(normalizedIndex < 0 || normalizedIndex > stringByteLength)) {
                return nil;
            }

            if (lengthTooLongProfile.profile(normalizedIndex + length > stringByteLength)) {
                length = stringByteLength - normalizedIndex;
            }

            return createSubString(substringNode, tString, encoding, normalizedIndex, length);
        }

        @Fallback
        protected Object stringByteSubstring(Object string, Object range, Object length) {
            return FAILURE;
        }

    }

    @Primitive(name = "string_chr_at", lowerFixnum = 1)
    @ImportStatic(StringGuards.class)
    public abstract static class StringChrAtPrimitiveNode extends CoreMethodArrayArgumentsNode {

        @Specialization(
                guards = { "indexOutOfBounds(strings.getRope(string), byteIndex)" })
        protected Object stringChrAtOutOfBounds(Object string, int byteIndex,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            return nil;
        }

        @Specialization(
                guards = {
                        "!indexOutOfBounds(strings.getRope(string), byteIndex)",
                        "isSingleByteOptimizable(strings.getRope(string), singleByteOptimizableNode)" })
        protected Object stringChrAtSingleByte(Object string, int byteIndex,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached StringByteSubstringPrimitiveNode stringByteSubstringNode,
                @Cached SingleByteOptimizableNode singleByteOptimizableNode) {
            return stringByteSubstringNode.executeStringByteSubstring(string, byteIndex, 1);
        }

        @Specialization(
                guards = {
                        "!indexOutOfBounds(strings.getRope(string), byteIndex)",
                        "!isSingleByteOptimizable(strings.getRope(string), singleByteOptimizableNode)" })
        protected Object stringChrAt(Object string, int byteIndex,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached GetActualEncodingNode getActualEncodingNode,
                @Cached TruffleString.GetInternalByteArrayNode getInternalByteArrayNode,
                @Cached CalculateCharacterLengthNode calculateCharacterLengthNode,
                @Cached CodeRangeNode codeRangeNode,
                @Cached SingleByteOptimizableNode singleByteOptimizableNode,
                @Cached TruffleString.SubstringByteIndexNode substringByteIndexNode,
                @Cached TruffleString.ForceEncodingNode forceEncodingNode) {
            final Rope rope = strings.getRope(string);
            var originalEncoding = strings.getEncoding(string);
            final RubyEncoding actualEncoding = getActualEncodingNode.execute(rope, originalEncoding);
            var tstring = forceEncodingNode.execute(strings.getTString(string), originalEncoding.tencoding,
                    actualEncoding.tencoding);
            final int end = rope.byteLength();
            var bytes = getInternalByteArrayNode.execute(tstring, actualEncoding.tencoding);
            final int clen = calculateCharacterLengthNode.characterLength(
                    actualEncoding.jcoding,
                    codeRangeNode.execute(rope),
                    Bytes.fromRange(bytes, byteIndex, end));

            if (!StringSupport.MBCLEN_CHARFOUND_P(clen)) {
                return nil;
            }

            if (clen + byteIndex > end) {
                return nil;
            }

            return createSubString(substringByteIndexNode, tstring, actualEncoding, byteIndex, clen);
        }

        protected static boolean indexOutOfBounds(Rope rope, int byteIndex) {
            return ((byteIndex < 0) || (byteIndex >= rope.byteLength()));
        }

    }

    @Primitive(name = "string_escape")
    public abstract static class StringEscapePrimitiveNode extends PrimitiveArrayArgumentsNode {

        @Specialization
        protected RubyString string_escape(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            final Rope rope = rbStrEscape(strings.getRope(string));
            return createString(rope, Encodings.US_ASCII);
        }

        // MRI: rb_str_escape
        @TruffleBoundary
        private static Rope rbStrEscape(Rope str) {
            final Encoding enc = str.getEncoding();
            final byte[] pBytes = str.getBytes();
            final CodeRange cr = str.getCodeRange();

            int p = 0;
            int pend = str.byteLength();
            int prev = p;
            RopeBuilder result = new RopeBuilder();
            boolean unicode_p = enc.isUnicode();
            boolean asciicompat = enc.isAsciiCompatible();

            while (p < pend) {
                int c, cc;
                int n = StringSupport.characterLength(enc, cr, pBytes, p, pend, false);
                if (!MBCLEN_CHARFOUND_P(n)) {
                    if (p > prev) {
                        result.append(pBytes, prev, p - prev);
                    }
                    n = enc.minLength();
                    if (pend < p + n) {
                        n = (pend - p);
                    }
                    while ((n--) > 0) {
                        result.append(
                                String.format("\\x%02X", (long) (pBytes[p] & 0377)).getBytes(
                                        StandardCharsets.US_ASCII));
                        prev = ++p;
                    }
                    continue;
                }
                n = MBCLEN_CHARFOUND_LEN(n);
                c = enc.mbcToCode(pBytes, p, pend);
                p += n;
                switch (c) {
                    case '\n':
                        cc = 'n';
                        break;
                    case '\r':
                        cc = 'r';
                        break;
                    case '\t':
                        cc = 't';
                        break;
                    case '\f':
                        cc = 'f';
                        break;
                    case '\013':
                        cc = 'v';
                        break;
                    case '\010':
                        cc = 'b';
                        break;
                    case '\007':
                        cc = 'a';
                        break;
                    case 033:
                        cc = 'e';
                        break;
                    default:
                        cc = 0;
                        break;
                }
                if (cc != 0) {
                    if (p - n > prev) {
                        result.append(pBytes, prev, p - n - prev);
                    }
                    result.append('\\');
                    result.append((byte) cc);
                    prev = p;
                } else if (asciicompat && Encoding.isAscii(c) && (c < 0x7F && c > 31 /* ISPRINT(c) */)) {
                } else {
                    if (p - n > prev) {
                        result.append(pBytes, prev, p - n - prev);
                    }

                    if (unicode_p && (c & 0xFFFFFFFFL) < 0x7F && Encoding.isAscii(c) &&
                            ASCIIEncoding.INSTANCE.isPrint(c)) {
                        result.append(StringUtils.formatASCIIBytes("%c", (char) (c & 0xFFFFFFFFL)));
                    } else {
                        result.append(StringUtils.formatASCIIBytes(escapedCharFormat(c, unicode_p), c & 0xFFFFFFFFL));
                    }

                    prev = p;
                }
            }
            if (p > prev) {
                result.append(pBytes, prev, p - prev);
            }

            result.setEncoding(USASCIIEncoding.INSTANCE);
            return result.toRope(CodeRange.CR_7BIT);
        }

        private static int MBCLEN_CHARFOUND_LEN(int r) {
            return r;
        }

        // MBCLEN_CHARFOUND_P, ONIGENC_MBCLEN_CHARFOUND_P
        private static boolean MBCLEN_CHARFOUND_P(int r) {
            return 0 < r;
        }

        private static String escapedCharFormat(int c, boolean isUnicode) {
            String format;
            // c comparisons must be unsigned 32-bit
            if (isUnicode) {

                if ((c & 0xFFFFFFFFL) < 0x7F && Encoding.isAscii(c) && ASCIIEncoding.INSTANCE.isPrint(c)) {
                    throw new UnsupportedOperationException();
                } else if (c < 0x10000) {
                    format = "\\u%04X";
                } else {
                    format = "\\u{%X}";
                }
            } else {
                if ((c & 0xFFFFFFFFL) < 0x100) {
                    format = "\\x%02X";
                } else {
                    format = "\\x{%X}";
                }
            }
            return format;
        }

    }

    @Primitive(name = "string_find_character", lowerFixnum = 1)
    @ImportStatic(StringGuards.class)
    public abstract static class StringFindCharacterNode extends CoreMethodArrayArgumentsNode {

        @Specialization(guards = "offset < 0")
        protected Object stringFindCharacterNegativeOffset(Object string, int offset) {
            return nil;
        }

        @Specialization(guards = "offsetTooLarge(strings.getRope(string), offset)")
        protected Object stringFindCharacterOffsetTooLarge(Object string, int offset,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            return nil;
        }

        @Specialization(
                guards = {
                        "offset >= 0",
                        "!offsetTooLarge(strings.getRope(string), offset)",
                        "isSingleByteOptimizable(strings.getRope(string), singleByteOptimizableNode)" })
        protected Object stringFindCharacterSingleByte(Object string, int offset,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached SingleByteOptimizableNode singleByteOptimizableNode,
                @Cached TruffleString.SubstringByteIndexNode substringNode) {
            // Taken from Rubinius's String::find_character.
            return createSubString(substringNode, strings, string, offset, 1);
        }

        @Specialization(
                guards = {
                        "offset >= 0",
                        "!offsetTooLarge(strings.getRope(string), offset)",
                        "!isSingleByteOptimizable(strings.getRope(string), singleByteOptimizableNode)" })
        protected Object stringFindCharacter(Object string, int offset,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached GetBytesObjectNode getBytesObject,
                @Cached CalculateCharacterLengthNode calculateCharacterLengthNode,
                @Cached CodeRangeNode codeRangeNode,
                @Cached SingleByteOptimizableNode singleByteOptimizableNode,
                @Cached TruffleString.SubstringByteIndexNode substringNode) {
            // Taken from Rubinius's String::find_character.

            final Rope rope = strings.getRope(string);
            final Encoding enc = rope.getEncoding();
            final CodeRange cr = codeRangeNode.execute(rope);

            final int clen = calculateCharacterLengthNode
                    .characterLength(enc, cr, getBytesObject.getClamped(rope, offset, enc.maxLength()));

            return createSubString(substringNode, strings, string, offset, clen);
        }

        protected static boolean offsetTooLarge(Rope rope, int offset) {
            return offset >= rope.byteLength();
        }

    }

    @NonStandard
    @CoreMethod(names = "from_codepoint", onSingleton = true, required = 2, lowerFixnum = 1)
    public abstract static class StringFromCodepointPrimitiveNode extends CoreMethodArrayArgumentsNode {

        @Child private StringNodes.MakeStringNode makeStringNode = StringNodes.MakeStringNode.create();

        @Specialization(guards = { "isSimple(code, rubyEncoding)", "isCodepoint(code)" })
        protected RubyString stringFromCodepointSimple(long code, RubyEncoding rubyEncoding,
                @Cached ConditionProfile isUTF8Profile,
                @Cached ConditionProfile isUSAsciiProfile,
                @Cached ConditionProfile isAscii8BitProfile) {
            final int intCode = (int) code; // isSimple() guarantees this is OK
            final Encoding encoding = rubyEncoding.jcoding;
            final Rope rope;

            if (isUTF8Profile.profile(encoding == UTF8Encoding.INSTANCE)) {
                rope = RopeConstants.UTF8_SINGLE_BYTE_ROPES[intCode];
            } else if (isUSAsciiProfile.profile(encoding == USASCIIEncoding.INSTANCE)) {
                rope = RopeConstants.US_ASCII_SINGLE_BYTE_ROPES[intCode];
            } else if (isAscii8BitProfile.profile(encoding == ASCIIEncoding.INSTANCE)) {
                rope = RopeConstants.ASCII_8BIT_SINGLE_BYTE_ROPES[intCode];
            } else {
                rope = RopeOperations.create(new byte[]{ (byte) intCode }, encoding, CodeRange.CR_UNKNOWN);
            }

            return createString(rope, rubyEncoding);
        }

        @Specialization(guards = { "!isSimple(code, rubyEncoding)", "isCodepoint(code)" })
        protected RubyString stringFromCodepoint(long code, RubyEncoding rubyEncoding,
                @Cached CalculateCharacterLengthNode calculateCharacterLengthNode,
                @Cached BranchProfile errorProfile) {
            final Encoding encoding = rubyEncoding.jcoding;

            final int length = StringSupport.codeLength(encoding, (int) code);
            if (length <= 0) {
                errorProfile.enter();
                throw new RaiseException(getContext(), coreExceptions().rangeError(code, rubyEncoding, this));
            }

            final byte[] bytes = new byte[length];
            final int codeToMbc = StringSupport.codeToMbc(encoding, (int) code, bytes, 0);
            if (codeToMbc < 0) {
                errorProfile.enter();
                throw new RaiseException(getContext(), coreExceptions().rangeError(code, rubyEncoding, this));
            }

            final Bytes bytesObject = new Bytes(bytes, 0, length);
            if (calculateCharacterLengthNode.characterLength(encoding, CR_UNKNOWN, bytesObject) != length) {
                errorProfile.enter();
                throw new RaiseException(getContext(), coreExceptions().rangeError(code, rubyEncoding, this));
            }

            return makeStringNode.executeMake(bytes, rubyEncoding, CodeRange.CR_VALID);
        }

        protected boolean isCodepoint(long code) {
            // Fits in an unsigned int
            return code >= 0 && code < (1L << 32);
        }

        protected boolean isSimple(long code, RubyEncoding encoding) {
            final Encoding enc = encoding.jcoding;

            return (enc.isAsciiCompatible() && code >= 0x00 && code < 0x80) ||
                    (enc == ASCIIEncoding.INSTANCE && code >= 0x00 && code <= 0xFF);
        }

    }

    @Primitive(name = "string_to_f")
    public abstract static class StringToFPrimitiveNode extends PrimitiveArrayArgumentsNode {

        @TruffleBoundary
        @Specialization
        protected Object stringToF(Object string,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached FixnumOrBignumNode fixnumOrBignumNode,
                @Cached BytesNode bytesNode) {
            final Rope rope = strings.getRope(string);
            if (rope.isEmpty()) {
                return nil;
            }

            final String javaString = strings.getJavaString(string);
            if (javaString.startsWith("0x")) {
                try {
                    return Double.parseDouble(javaString);
                } catch (NumberFormatException e) {
                    // Try falling back to this implementation if the first fails, neither 100% complete
                    final Object result = ConvertBytes
                            .bytesToInum(
                                    getContext(),
                                    this,
                                    fixnumOrBignumNode,
                                    bytesNode,
                                    rope,
                                    16,
                                    true);
                    if (result instanceof Integer) {
                        return ((Integer) result).doubleValue();
                    } else if (result instanceof Long) {
                        return ((Long) result).doubleValue();
                    } else if (result instanceof Double) {
                        return result;
                    } else {
                        return nil;
                    }
                }
            }
            try {
                return new DoubleConverter().parse(rope, true, true);
            } catch (NumberFormatException e) {
                return nil;
            }
        }

    }

    @Primitive(name = "find_string", lowerFixnum = 2)
    @ImportStatic(StringGuards.class)
    public abstract static class StringIndexPrimitiveNode extends PrimitiveArrayArgumentsNode {

        @Child private CheckEncodingNode checkEncodingNode;
        @Child CodeRangeNode codeRangeNode = CodeRangeNode.create();
        @Child SingleByteOptimizableNode singleByteNode = SingleByteOptimizableNode.create();

        @Specialization(
                guards = "isEmpty(stringsPattern.getRope(pattern))")
        protected int stringIndexEmptyPattern(Object string, Object pattern, int byteOffset,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary stringsPattern) {
            assert byteOffset >= 0;
            return byteOffset;
        }

        @Specialization(
                guards = {
                        "isSingleByteString(libPattern.getRope(pattern))",
                        "!isBrokenCodeRange(libPattern.getRope(pattern), codeRangeNode)",
                        "canMemcmp(libString.getRope(string), libPattern.getRope(pattern), singleByteNode)" })
        protected Object stringIndexSingleBytePattern(Object string, Object pattern, int byteOffset,
                @Cached BytesNode bytesNode,
                @Cached ConditionProfile offsetTooLargeProfile,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libPattern) {
            assert byteOffset >= 0;

            checkEncoding(string, pattern);

            final Rope sourceRope = libString.getRope(string);
            final int end = sourceRope.byteLength();

            if (offsetTooLargeProfile.profile(byteOffset >= end)) {
                return nil;
            }

            final byte[] sourceBytes = bytesNode.execute(sourceRope);
            final byte searchByte = bytesNode.execute(libPattern.getRope(pattern))[0];

            final int index = com.oracle.truffle.api.ArrayUtils.indexOf(sourceBytes, byteOffset, end, searchByte);

            return index == -1 ? nil : index;
        }

        @Specialization(
                guards = {
                        "!isEmpty(libPattern.getRope(pattern))",
                        "!isSingleByteString(libPattern.getRope(pattern))",
                        "!isBrokenCodeRange(libPattern.getRope(pattern), codeRangeNode)",
                        "canMemcmp(libString.getRope(string), libPattern.getRope(pattern), singleByteNode)" })
        protected Object stringIndexMultiBytePattern(Object string, Object pattern, int byteOffset,
                @Cached BytesNode bytesNode,
                @Cached BranchProfile matchFoundProfile,
                @Cached BranchProfile noMatchProfile,
                @Cached LoopConditionProfile loopProfile,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libPattern) {
            assert byteOffset >= 0;

            checkEncoding(string, pattern);

            final Rope sourceRope = libString.getRope(string);
            final byte[] sourceBytes = bytesNode.execute(sourceRope);
            final Rope searchRope = libPattern.getRope(pattern);
            final byte[] searchBytes = bytesNode.execute(searchRope);

            int end = sourceRope.byteLength() - searchRope.byteLength();

            int i = byteOffset;
            try {
                for (; loopProfile.inject(i <= end); i++) {
                    if (sourceBytes[i] == searchBytes[0]) {
                        if (ArrayUtils.regionEquals(sourceBytes, i, searchBytes, 0, searchRope.byteLength())) {
                            matchFoundProfile.enter();
                            return i;
                        }
                    }
                    TruffleSafepoint.poll(this);
                }
            } finally {
                profileAndReportLoopCount(loopProfile, i - byteOffset);
            }

            noMatchProfile.enter();
            return nil;
        }

        @Specialization(
                guards = {
                        "isBrokenCodeRange(stringsPattern.getRope(pattern), codeRangeNode)" })
        protected Object stringIndexBrokenPattern(Object string, Object pattern, int byteOffset,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary stringsPattern) {
            assert byteOffset >= 0;
            return nil;
        }

        @Specialization(
                guards = {
                        "!isBrokenCodeRange(libPattern.getRope(pattern), codeRangeNode)",
                        "!canMemcmp(libString.getRope(string), libPattern.getRope(pattern), singleByteNode)" })
        protected Object stringIndexGeneric(Object string, Object pattern, int byteOffset,
                @Cached ByteIndexFromCharIndexNode byteIndexFromCharIndexNode,
                @Cached StringByteCharacterIndexNode byteIndexToCharIndexNode,
                @Cached NormalizeIndexNode normalizeIndexNode,
                @Cached ConditionProfile badIndexProfile,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libPattern) {
            assert byteOffset >= 0;

            checkEncoding(string, pattern);

            // Rubinius will pass in a byte index for the `start` value, but StringSupport.index requires a character index.
            final int charIndex = byteIndexToCharIndexNode.executeStringByteCharacterIndex(string, byteOffset);

            final Rope stringRope = libString.getRope(string);
            final int index = index(
                    stringRope,
                    libPattern.getRope(pattern),
                    charIndex,
                    stringRope.getEncoding(),
                    normalizeIndexNode,
                    byteIndexFromCharIndexNode);

            if (badIndexProfile.profile(index == -1)) {
                return nil;
            }

            return index;
        }

        @TruffleBoundary
        private int index(Rope source, Rope other, int byteOffset, Encoding enc, NormalizeIndexNode normalizeIndexNode,
                ByteIndexFromCharIndexNode byteIndexFromCharIndexNode) {
            // Taken from org.jruby.util.StringSupport.index.
            assert byteOffset >= 0;

            int sourceLen = source.characterLength();
            int otherLen = other.characterLength();

            byteOffset = normalizeIndexNode.executeNormalize(byteOffset, sourceLen);

            if (sourceLen - byteOffset < otherLen) {
                return -1;
            }
            byte[] bytes = source.getBytes();
            int p = 0;
            final int end = source.byteLength();
            if (byteOffset != 0) {
                if (!source.isSingleByteOptimizable()) {
                    final int pp = byteIndexFromCharIndexNode.execute(source, 0, byteOffset);
                    byteOffset = StringSupport.offset(0, end, pp);
                }
                p += byteOffset;
            }
            if (otherLen == 0) {
                return byteOffset;
            }

            while (true) {
                int pos = indexOf(source, other, p);
                if (pos < 0) {
                    return pos;
                }
                pos -= p;
                int t = enc.rightAdjustCharHead(bytes, p, p + pos, end);
                if (t == p + pos) {
                    return pos + byteOffset;
                }
                if ((sourceLen -= t - p) <= 0) {
                    return -1;
                }
                byteOffset += t - p;
                p = t;
            }
        }

        @TruffleBoundary
        private int indexOf(Rope sourceRope, Rope otherRope, int fromIndex) {
            // Taken from org.jruby.util.ByteList.indexOf.

            final byte[] source = sourceRope.getBytes();
            final int sourceOffset = 0;
            final int sourceCount = sourceRope.byteLength();
            final byte[] target = otherRope.getBytes();
            final int targetOffset = 0;
            final int targetCount = otherRope.byteLength();

            if (fromIndex >= sourceCount) {
                return (targetCount == 0 ? sourceCount : -1);
            }
            if (fromIndex < 0) {
                fromIndex = 0;
            }
            if (targetCount == 0) {
                return fromIndex;
            }

            byte first = target[targetOffset];
            int max = sourceOffset + (sourceCount - targetCount);

            for (int i = sourceOffset + fromIndex; i <= max; i++) {
                if (source[i] != first) {
                    while (++i <= max && source[i] != first) {
                    }
                }

                if (i <= max) {
                    int j = i + 1;
                    int end = j + targetCount - 1;
                    for (int k = targetOffset + 1; j < end && source[j] == target[k]; j++, k++) {
                    }

                    if (j == end) {
                        return i - sourceOffset;
                    }
                }
            }
            return -1;
        }

        private void checkEncoding(Object string, Object pattern) {
            if (checkEncodingNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                checkEncodingNode = insert(CheckEncodingNode.create());
            }

            checkEncodingNode.executeCheckEncoding(string, pattern);
        }

    }

    @Primitive(name = "string_byte_character_index", lowerFixnum = 1)
    @ImportStatic(StringGuards.class)
    public abstract static class StringByteCharacterIndexNode extends PrimitiveArrayArgumentsNode {

        @Child SingleByteOptimizableNode singleByteOptimizableNode = SingleByteOptimizableNode
                .create();

        public abstract int executeStringByteCharacterIndex(Object string, int byteIndex);

        public static StringByteCharacterIndexNode create() {
            return StringByteCharacterIndexNodeFactory.create(null);
        }

        @Specialization(
                guards = {
                        "isSingleByteOptimizable(strings.getRope(string), singleByteOptimizableNode)" })
        protected int singleByte(Object string, int byteIndex,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings) {
            return byteIndex;
        }

        @Specialization(
                guards = {
                        "!isSingleByteOptimizable(libString.getRope(string), singleByteOptimizableNode)",
                        "isFixedWidthEncoding(libString.getRope(string))" })
        protected int fixedWidth(Object string, int byteIndex,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            return byteIndex / libString.getRope(string).getEncoding().minLength();
        }

        @Specialization(
                guards = {
                        "!isSingleByteOptimizable(libString.getRope(string), singleByteOptimizableNode)",
                        "!isFixedWidthEncoding(libString.getRope(string))",
                        "isValidUtf8(libString.getRope(string), codeRangeNode)" })
        protected int validUtf8(Object string, int byteIndex,
                @Cached CodeRangeNode codeRangeNode,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            // Taken from Rubinius's String::find_byte_character_index.
            // TODO (nirvdrum 02-Apr-15) There's a way to optimize this for UTF-8, but porting all that code isn't necessary at the moment.
            return notValidUtf8(string, byteIndex, codeRangeNode, libString);
        }

        @TruffleBoundary
        @Specialization(
                guards = {
                        "!isSingleByteOptimizable(libString.getRope(string), singleByteOptimizableNode)",
                        "!isFixedWidthEncoding(libString.getRope(string))",
                        "!isValidUtf8(libString.getRope(string), codeRangeNode)" })
        protected int notValidUtf8(Object string, int byteIndex,
                @Cached CodeRangeNode codeRangeNode,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            // Taken from Rubinius's String::find_byte_character_index and Encoding::find_byte_character_index.

            final Rope rope = libString.getRope(string);
            final byte[] bytes = rope.getBytes();
            final Encoding encoding = rope.getEncoding();
            final CodeRange codeRange = rope.getCodeRange();
            int p = 0;
            final int end = bytes.length;
            int charIndex = 0;

            while (p < end && byteIndex > 0) {
                final int charLen = StringSupport.characterLength(encoding, codeRange, bytes, p, end, true);
                p += charLen;
                byteIndex -= charLen;
                charIndex++;
            }

            return charIndex;
        }
    }

    /** Search pattern in string starting after offset characters, and return a character index or nil */
    @Primitive(name = "string_character_index", lowerFixnum = 2)
    @NodeChild(value = "string", type = RubyBaseNodeWithExecute.class)
    @NodeChild(value = "pattern", type = RubyBaseNodeWithExecute.class)
    @NodeChild(value = "offset", type = RubyNode.class)
    public abstract static class StringCharacterIndexNode extends PrimitiveNode {

        @Child SingleByteOptimizableNode singleByteOptimizableNode = SingleByteOptimizableNode.create();

        @CreateCast("string")
        protected RubyBaseNodeWithExecute coerceStringToRope(RubyBaseNodeWithExecute string) {
            return ToRopeNodeGen.create(string);
        }

        @CreateCast("pattern")
        protected RubyBaseNodeWithExecute coercePatternToRope(RubyBaseNodeWithExecute pattern) {
            return ToRopeNodeGen.create(pattern);
        }

        @Specialization(
                guards = "singleByteOptimizableNode.execute(stringRope)")
        protected Object singleByteOptimizable(Rope stringRope, Rope patternRope, int offset,
                @Cached @Shared("stringBytesNode") BytesNode stringBytesNode,
                @Cached @Shared("patternBytesNode") BytesNode patternBytesNode,
                @Cached LoopConditionProfile loopProfile) {

            assert offset >= 0;
            assert offset + patternRope.byteLength() <= stringRope
                    .byteLength() : "already checked in the caller, String#index";

            int p = offset;
            final int e = stringRope.byteLength();
            final int pe = patternRope.byteLength();
            final int l = e - pe + 1;

            final byte[] stringBytes = stringBytesNode.execute(stringRope);
            final byte[] patternBytes = patternBytesNode.execute(patternRope);

            try {
                for (; loopProfile.inject(p < l); p++) {
                    if (ArrayUtils.regionEquals(stringBytes, p, patternBytes, 0, pe)) {
                        return p;
                    }
                    TruffleSafepoint.poll(this);
                }
            } finally {
                profileAndReportLoopCount(loopProfile, p - offset);
            }

            return nil;
        }

        @TruffleBoundary
        @Specialization(
                guards = "!singleByteOptimizableNode.execute(stringRope)")
        protected Object multiByte(Rope stringRope, Rope patternRope, int offset,
                @Cached CalculateCharacterLengthNode calculateCharacterLengthNode,
                @Cached @Shared("stringBytesNode") BytesNode stringBytesNode,
                @Cached @Shared("patternBytesNode") BytesNode patternBytesNode) {

            assert offset >= 0;
            assert offset + patternRope.byteLength() <= stringRope
                    .byteLength() : "already checked in the caller, String#index";

            int p = 0;
            final int e = stringRope.byteLength();
            final int pe = patternRope.byteLength();
            final int l = e - pe + 1;

            final byte[] stringBytes = stringBytesNode.execute(stringRope);
            final byte[] patternBytes = patternBytesNode.execute(patternRope);

            final Encoding enc = stringRope.getEncoding();
            final CodeRange cr = stringRope.getCodeRange();
            int c = 0;
            int index = 0;

            while (p < e && index < offset) {
                c = calculateCharacterLengthNode.characterLength(enc, cr, Bytes.fromRange(stringBytes, p, e));
                if (StringSupport.MBCLEN_CHARFOUND_P(c)) {
                    p += c;
                    index++;
                } else {
                    return nil;
                }
            }

            for (; p < l; p += c, ++index) {
                c = calculateCharacterLengthNode.characterLength(enc, cr, Bytes.fromRange(stringBytes, p, e));
                if (!StringSupport.MBCLEN_CHARFOUND_P(c)) {
                    return nil;
                }
                if (ArrayUtils.regionEquals(stringBytes, p, patternBytes, 0, pe)) {
                    return index;
                }
            }

            return nil;
        }
    }

    /** Search pattern in string starting after offset bytes, and return a byte index or nil */
    @Primitive(name = "string_byte_index", lowerFixnum = 2)
    @NodeChild(value = "string", type = RubyBaseNodeWithExecute.class)
    @NodeChild(value = "pattern", type = RubyBaseNodeWithExecute.class)
    @NodeChild(value = "offset", type = RubyNode.class)
    public abstract static class StringByteIndexNode extends PrimitiveNode {

        @Child SingleByteOptimizableNode singleByteOptimizableNode = SingleByteOptimizableNode.create();

        @CreateCast("string")
        protected RubyBaseNodeWithExecute coerceStringToRope(RubyBaseNodeWithExecute string) {
            return ToRopeNodeGen.create(string);
        }

        @CreateCast("pattern")
        protected RubyBaseNodeWithExecute coercePatternToRope(RubyBaseNodeWithExecute pattern) {
            return ToRopeNodeGen.create(pattern);
        }

        @Specialization(guards = "!patternFits(stringRope, patternRope, offset)")
        protected Object patternTooLarge(Rope stringRope, Rope patternRope, int offset) {
            assert offset >= 0;
            return nil;
        }

        @Specialization(
                guards = {
                        "singleByteOptimizableNode.execute(stringRope)",
                        "patternFits(stringRope, patternRope, offset)" })
        protected Object singleByteOptimizable(Rope stringRope, Rope patternRope, int offset,
                @Cached @Shared("stringBytesNode") BytesNode stringBytesNode,
                @Cached @Shared("patternBytesNode") BytesNode patternBytesNode,
                @Cached LoopConditionProfile loopProfile) {

            assert offset >= 0;
            int p = offset;
            final int e = stringRope.byteLength();
            final int pe = patternRope.byteLength();
            final int l = e - pe + 1;

            final byte[] stringBytes = stringBytesNode.execute(stringRope);
            final byte[] patternBytes = patternBytesNode.execute(patternRope);

            try {
                for (; loopProfile.inject(p < l); p++) {
                    if (ArrayUtils.regionEquals(stringBytes, p, patternBytes, 0, pe)) {
                        return p;
                    }
                    TruffleSafepoint.poll(this);
                }
            } finally {
                profileAndReportLoopCount(loopProfile, p - offset);
            }

            return nil;
        }

        @TruffleBoundary
        @Specialization(
                guards = {
                        "!singleByteOptimizableNode.execute(stringRope)",
                        "patternFits(stringRope, patternRope, offset)" })
        protected Object multiByte(Rope stringRope, Rope patternRope, int offset,
                @Cached CalculateCharacterLengthNode calculateCharacterLengthNode,
                @Cached @Shared("stringBytesNode") BytesNode stringBytesNode,
                @Cached @Shared("patternBytesNode") BytesNode patternBytesNode) {

            assert offset >= 0;
            int p = offset;
            final int e = stringRope.byteLength();
            final int pe = patternRope.byteLength();
            final int l = e - pe + 1;

            final byte[] stringBytes = stringBytesNode.execute(stringRope);
            final byte[] patternBytes = patternBytesNode.execute(patternRope);

            final Encoding enc = stringRope.getEncoding();
            final CodeRange cr = stringRope.getCodeRange();
            int c;

            for (; p < l; p += c) {
                c = calculateCharacterLengthNode.characterLength(enc, cr, Bytes.fromRange(stringBytes, p, e));
                if (!StringSupport.MBCLEN_CHARFOUND_P(c)) {
                    return nil;
                }
                if (ArrayUtils.regionEquals(stringBytes, p, patternBytes, 0, pe)) {
                    return p;
                }
            }

            return nil;
        }

        protected boolean patternFits(Rope stringRope, Rope patternRope, int offset) {
            return offset + patternRope.byteLength() <= stringRope.byteLength();
        }
    }

    /** Calculates the byte offset of a character, indicated by a character index, starting from a provided byte offset
     * into the rope. Providing a 0 starting offset simply finds the byte offset for the nth character into the rope,
     * according to the rope's encoding. Providing a non-zero starting byte offset effectively allows for calculating a
     * character's byte offset into a substring of the rope without having to creating a SubstringRope.
     *
     * @rope - The rope/string being indexed.
     * @startByteOffset - Starting position in the rope for the calculation of the character's byte offset.
     * @characterIndex - The character index into the rope, starting from the provided byte offset. */
    @ImportStatic({ RopeGuards.class, StringGuards.class, StringOperations.class })
    public abstract static class ByteIndexFromCharIndexNode extends RubyBaseNode {

        public static ByteIndexFromCharIndexNode create() {
            return ByteIndexFromCharIndexNodeGen.create();
        }

        @Child protected SingleByteOptimizableNode singleByteOptimizableNode = SingleByteOptimizableNode.create();

        public abstract int execute(Rope rope, int startByteOffset, int characterIndex);

        @Specialization(guards = "isSingleByteOptimizable(rope)")
        protected int singleByteOptimizable(Rope rope, int startByteOffset, int characterIndex) {
            return startByteOffset + characterIndex;
        }

        @Specialization(guards = { "!isSingleByteOptimizable(rope)", "isFixedWidthEncoding(rope)" })
        protected int fixedWidthEncoding(Rope rope, int startByteOffset, int characterIndex) {
            final Encoding encoding = rope.getEncoding();
            return startByteOffset + characterIndex * encoding.minLength();
        }

        @Specialization(
                guards = { "!isSingleByteOptimizable(rope)", "!isFixedWidthEncoding(rope)", "characterIndex == 0" })
        protected int multiByteZeroIndex(Rope rope, int startByteOffset, int characterIndex) {
            return startByteOffset;
        }

        @Specialization(guards = { "!isSingleByteOptimizable(rope)", "!isFixedWidthEncoding(rope)" })
        protected int multiBytes(Rope rope, int startByteOffset, int characterIndex,
                @Cached ConditionProfile indexTooLargeProfile,
                @Cached ConditionProfile invalidByteProfile,
                @Cached BytesNode bytesNode,
                @Cached CalculateCharacterLengthNode calculateCharacterLengthNode,
                @Cached CodeRangeNode codeRangeNode) {
            // Taken from Rubinius's String::byte_index.

            final Encoding enc = rope.getEncoding();
            final byte[] bytes = bytesNode.execute(rope);
            final int e = rope.byteLength();
            int p = startByteOffset;

            int i, k = characterIndex;

            for (i = 0; i < k && p < e; i++) {
                final int c = calculateCharacterLengthNode
                        .characterLength(enc, codeRangeNode.execute(rope), Bytes.fromRange(bytes, p, e));

                // TODO (nirvdrum 22-Dec-16): Consider having a specialized version for CR_BROKEN strings to avoid these checks.
                // If it's an invalid byte, just treat it as a single byte
                if (invalidByteProfile.profile(!StringSupport.MBCLEN_CHARFOUND_P(c))) {
                    ++p;
                } else {
                    p += StringSupport.MBCLEN_CHARFOUND_LEN(c);
                }
            }

            // TODO (nirvdrum 22-Dec-16): Since we specialize elsewhere on index being too large, do we need this? Can character boundary search in a CR_BROKEN string cause us to encounter this case?
            if (indexTooLargeProfile.profile(i < k)) {
                return -1;
            } else {
                return p;
            }
        }

        protected boolean isSingleByteOptimizable(Rope rope) {
            return singleByteOptimizableNode.execute(rope);
        }

    }

    // Named 'string_byte_index' in Rubinius.
    @Primitive(name = "string_byte_index_from_char_index", lowerFixnum = 1)
    @ImportStatic({ StringGuards.class, StringOperations.class })
    public abstract static class StringByteIndexFromCharIndexNode extends PrimitiveArrayArgumentsNode {

        @Specialization
        protected Object singleByteOptimizable(Object string, int characterIndex,
                @Cached ByteIndexFromCharIndexNode byteIndexFromCharIndexNode,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            return byteIndexFromCharIndexNode.execute(libString.getRope(string), 0, characterIndex);
        }

    }

    // Port of Rubinius's String::previous_byte_index.
    //
    // This method takes a byte index, finds the corresponding character the byte index belongs to, and then returns
    // the byte index marking the start of the previous character in the string.
    @Primitive(name = "string_previous_byte_index", lowerFixnum = 1)
    @ImportStatic(StringGuards.class)
    public abstract static class StringPreviousByteIndexNode extends PrimitiveArrayArgumentsNode {

        @Specialization(guards = "index < 0")
        protected Object negativeIndex(Object string, int index) {
            throw new RaiseException(getContext(), coreExceptions().argumentError("negative index given", this));
        }

        @Specialization(guards = "index == 0")
        protected Object zeroIndex(Object string, int index) {
            return nil;
        }

        @Specialization(guards = {
                "index > 0",
                "isSingleByteOptimizable(strings.getRope(string), singleByteOptimizableNode)" })
        protected int singleByteOptimizable(Object string, int index,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached SingleByteOptimizableNode singleByteOptimizableNode) {
            return index - 1;
        }

        @Specialization(guards = {
                "index > 0",
                "!isSingleByteOptimizable(strings.getRope(string), singleByteOptimizableNode)",
                "isFixedWidthEncoding(strings.getRope(string))" })
        protected int fixedWidthEncoding(Object string, int index,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached SingleByteOptimizableNode singleByteOptimizableNode,
                @Cached ConditionProfile firstCharacterProfile) {
            final Encoding encoding = strings.getRope(string).getEncoding();

            // TODO (nirvdrum 11-Apr-16) Determine whether we need to be bug-for-bug compatible with Rubinius.
            // Implement a bug in Rubinius. We already special-case the index == 0 by returning nil. For all indices
            // corresponding to a given character, we treat them uniformly. However, for the first character, we only
            // return nil if the index is 0. If any other index into the first character is encountered, we return 0.
            // It seems unlikely this will ever be encountered in practice, but it's here for completeness.
            if (firstCharacterProfile.profile(index < encoding.maxLength())) {
                return 0;
            }

            return (index / encoding.maxLength() - 1) * encoding.maxLength();
        }

        @Specialization(guards = {
                "index > 0",
                "!isSingleByteOptimizable(strings.getRope(string), singleByteOptimizableNode)",
                "!isFixedWidthEncoding(strings.getRope(string))" })
        @TruffleBoundary
        protected Object other(Object string, int index,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary strings,
                @Cached SingleByteOptimizableNode singleByteOptimizableNode) {
            final Rope rope = strings.getRope(string);
            final int p = 0;
            final int end = p + rope.byteLength();

            final int b = rope.getEncoding().prevCharHead(rope.getBytes(), p, p + index, end);

            if (b == -1) {
                return nil;
            }

            return b - p;
        }

    }

    @Primitive(name = "find_string_reverse", lowerFixnum = 2)
    @ImportStatic(StringGuards.class)
    public abstract static class StringRindexPrimitiveNode extends CoreMethodArrayArgumentsNode {

        @Child private CheckEncodingNode checkEncodingNode;
        @Child CodeRangeNode codeRangeNode = CodeRangeNode.create();
        @Child SingleByteOptimizableNode singleByteNode = SingleByteOptimizableNode.create();

        @Specialization(guards = { "isEmpty(stringsPattern.getRope(pattern))" })
        protected Object stringRindexEmptyPattern(Object string, Object pattern, int byteOffset,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary stringsPattern) {
            assert byteOffset >= 0;
            return byteOffset;
        }

        @Specialization(guards = {
                "isSingleByteString(patternRope)",
                "!isBrokenCodeRange(patternRope, codeRangeNode)",
                "canMemcmp(libString.getRope(string), patternRope, singleByteNode)" })
        protected Object stringRindexSingleBytePattern(Object string, Object pattern, int byteOffset,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libPattern,
                @Bind("libPattern.getRope(pattern)") Rope patternRope,
                @Cached BytesNode bytesNode,
                @Cached BranchProfile startTooLargeProfile,
                @Cached BranchProfile matchFoundProfile,
                @Cached BranchProfile noMatchProfile,
                @Cached LoopConditionProfile loopProfile,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            assert byteOffset >= 0;

            checkEncoding(string, pattern);

            final Rope sourceRope = libString.getRope(string);
            final int end = sourceRope.byteLength();
            final byte[] sourceBytes = bytesNode.execute(sourceRope);
            final byte searchByte = bytesNode.execute(patternRope)[0];
            int normalizedStart = byteOffset;

            if (normalizedStart >= end) {
                startTooLargeProfile.enter();
                normalizedStart = end - 1;
            }

            int i = normalizedStart;
            try {
                for (; loopProfile.inject(i >= 0); i--) {
                    if (sourceBytes[i] == searchByte) {
                        matchFoundProfile.enter();
                        return i;
                    }
                    TruffleSafepoint.poll(this);
                }
            } finally {
                profileAndReportLoopCount(loopProfile, normalizedStart - i);
            }

            noMatchProfile.enter();
            return nil;
        }

        @Specialization(guards = {
                "!isEmpty(patternRope)",
                "!isSingleByteString(patternRope)",
                "!isBrokenCodeRange(patternRope, codeRangeNode)",
                "canMemcmp(libString.getRope(string), patternRope, singleByteNode)" })
        protected Object stringRindexMultiBytePattern(Object string, Object pattern, int byteOffset,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libPattern,
                @Bind("libPattern.getRope(pattern)") Rope patternRope,
                @Cached BytesNode bytesNode,
                @Cached BranchProfile startOutOfBoundsProfile,
                @Cached BranchProfile startTooCloseToEndProfile,
                @Cached BranchProfile matchFoundProfile,
                @Cached BranchProfile noMatchProfile,
                @Cached LoopConditionProfile loopProfile,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            assert byteOffset >= 0;

            checkEncoding(string, pattern);

            final Rope sourceRope = libString.getRope(string);
            final int end = sourceRope.byteLength();
            final byte[] sourceBytes = bytesNode.execute(sourceRope);
            final int matchSize = patternRope.byteLength();
            final byte[] searchBytes = bytesNode.execute(patternRope);
            int normalizedStart = byteOffset;

            if (normalizedStart >= end) {
                startOutOfBoundsProfile.enter();
                normalizedStart = end - 1;
            }

            if (end - normalizedStart < matchSize) {
                startTooCloseToEndProfile.enter();
                normalizedStart = end - matchSize;
            }

            int i = normalizedStart;
            try {
                for (; loopProfile.inject(i >= 0); i--) {
                    if (sourceBytes[i] == searchBytes[0]) {
                        if (ArrayUtils.regionEquals(sourceBytes, i, searchBytes, 0, matchSize)) {
                            matchFoundProfile.enter();
                            return i;
                        }
                    }
                    TruffleSafepoint.poll(this);
                }
            } finally {
                profileAndReportLoopCount(loopProfile, normalizedStart - i);
            }

            noMatchProfile.enter();
            return nil;
        }

        @Specialization(guards = { "isBrokenCodeRange(stringsPattern.getRope(pattern), codeRangeNode)" })
        protected Object stringRindexBrokenPattern(Object string, Object pattern, int byteOffset,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary stringsPattern) {
            assert byteOffset >= 0;
            return nil;
        }

        @Specialization(guards = {
                "!isBrokenCodeRange(patternRope, codeRangeNode)",
                "!canMemcmp(libString.getRope(string), patternRope, singleByteNode)" })
        protected Object stringRindex(Object string, Object pattern, int byteOffset,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libPattern,
                @Bind("libPattern.getRope(pattern)") Rope patternRope,
                @Cached BytesNode stringBytes,
                @Cached BytesNode patternBytes,
                @Cached GetByteNode patternGetByteNode,
                @Cached GetByteNode stringGetByteNode,
                @Cached LoopConditionProfile loopProfile,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            // Taken from Rubinius's String::rindex.
            assert byteOffset >= 0;

            int pos = byteOffset;

            final Rope stringRope = libString.getRope(string);
            final int total = stringRope.byteLength();
            final int matchSize = patternRope.byteLength();

            if (pos >= total) {
                pos = total - 1;
            }

            switch (matchSize) {
                case 0: {
                    return byteOffset;
                }

                case 1: {
                    final int matcher = patternGetByteNode.executeGetByte(patternRope, 0);

                    while (pos >= 0) {
                        if (stringGetByteNode.executeGetByte(stringRope, pos) == matcher) {
                            return pos;
                        }

                        pos--;
                    }

                    return nil;
                }

                default: {
                    if (total - pos < matchSize) {
                        pos = total - matchSize;
                    }

                    int cur = pos;

                    try {
                        while (loopProfile.inject(cur >= 0)) {
                            if (ArrayUtils.regionEquals(
                                    stringBytes.execute(stringRope),
                                    cur,
                                    patternBytes.execute(patternRope),
                                    0,
                                    matchSize)) {
                                return cur;
                            }

                            cur--;
                            TruffleSafepoint.poll(this);
                        }
                    } finally {
                        profileAndReportLoopCount(loopProfile, pos - cur);
                    }
                }
            }

            return nil;
        }

        private void checkEncoding(Object string, Object pattern) {
            if (checkEncodingNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                checkEncodingNode = insert(CheckEncodingNode.create());
            }

            checkEncodingNode.executeCheckEncoding(string, pattern);
        }

    }

    @Primitive(name = "string_splice", lowerFixnum = { 2, 3 })
    @ImportStatic(StringGuards.class)
    public abstract static class StringSplicePrimitiveNode extends PrimitiveArrayArgumentsNode {

        @Specialization(guards = { "libOther.isRubyString(other)", "indexAtStartBound(spliceByteIndex)" })
        protected Object splicePrepend(
                RubyString string, Object other, int spliceByteIndex, int byteCountToReplace, RubyEncoding rubyEncoding,
                @Cached TruffleString.SubstringByteIndexNode prependSubstringNode,
                @Cached TruffleString.ConcatNode prependConcatNode,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libOther) {
            var original = string.tstring;
            var originalTEncoding = string.encoding.tencoding;
            var left = libOther.getTString(other);
            var right = prependSubstringNode.execute(original, byteCountToReplace,
                    original.byteLength(originalTEncoding) - byteCountToReplace, originalTEncoding, true);

            var prependResult = prependConcatNode.execute(left, right, rubyEncoding.tencoding, true);
            string.setTString(prependResult, rubyEncoding);

            return string;
        }

        @Specialization(guards = { "libOther.isRubyString(other)", "indexAtEndBound(string, spliceByteIndex)" })
        protected Object spliceAppend(
                RubyString string, Object other, int spliceByteIndex, int byteCountToReplace, RubyEncoding rubyEncoding,
                @Cached TruffleString.ConcatNode appendConcatNode,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libOther) {
            var left = string.tstring;
            var right = libOther.getTString(other);

            var concatResult = appendConcatNode.execute(left, right, rubyEncoding.tencoding, true);
            string.setTString(concatResult, rubyEncoding);

            return string;
        }

        @Specialization(guards = { "libOther.isRubyString(other)", "!indexAtEitherBounds(string, spliceByteIndex)" })
        protected RubyString splice(
                RubyString string, Object other, int spliceByteIndex, int byteCountToReplace, RubyEncoding rubyEncoding,
                @Cached ConditionProfile insertStringIsEmptyProfile,
                @Cached ConditionProfile splitRightIsEmptyProfile,
                @Cached TruffleString.SubstringByteIndexNode leftSubstringNode,
                @Cached TruffleString.SubstringByteIndexNode rightSubstringNode,
                @Cached TruffleString.ConcatNode leftConcatNode,
                @Cached TruffleString.ConcatNode rightConcatNode,
                @Cached TruffleString.ForceEncodingNode forceEncodingNode,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libOther) {
            var sourceTEncoding = string.encoding.tencoding;
            var resultTEncoding = rubyEncoding.tencoding;
            var source = string.tstring;
            var insert = libOther.getTString(other);
            final int rightSideStartingIndex = spliceByteIndex + byteCountToReplace;

            var splitLeft = leftSubstringNode.execute(source, 0, spliceByteIndex, sourceTEncoding, true);
            var splitRight = rightSubstringNode.execute(source, rightSideStartingIndex,
                    source.byteLength(sourceTEncoding) - rightSideStartingIndex, sourceTEncoding, true);

            final TruffleString joinedLeft; // always in resultTEncoding
            if (insertStringIsEmptyProfile.profile(insert.isEmpty())) {
                joinedLeft = forceEncodingNode.execute(splitLeft, sourceTEncoding, resultTEncoding);
            } else {
                joinedLeft = leftConcatNode.execute(splitLeft, insert, resultTEncoding, true);
            }

            final TruffleString joinedRight; // always in resultTEncoding
            if (splitRightIsEmptyProfile.profile(splitRight.isEmpty())) {
                joinedRight = joinedLeft;
            } else {
                joinedRight = rightConcatNode.execute(joinedLeft, splitRight, resultTEncoding, true);
            }

            string.setTString(joinedRight, rubyEncoding);
            return string;
        }

        protected boolean indexAtStartBound(int index) {
            return index == 0;
        }

        protected boolean indexAtEndBound(RubyString string, int index) {
            return index == string.rope.byteLength();
        }

        protected boolean indexAtEitherBounds(RubyString string, int index) {
            return indexAtStartBound(index) || indexAtEndBound(string, index);
        }
    }

    @Primitive(name = "string_to_inum", lowerFixnum = 1)
    @NodeChild(value = "string", type = RubyBaseNodeWithExecute.class)
    @NodeChild(value = "fixBase", type = RubyNode.class)
    @NodeChild(value = "strict", type = RubyNode.class)
    @NodeChild(value = "raiseOnError", type = RubyNode.class)
    public abstract static class StringToInumPrimitiveNode extends PrimitiveNode {

        @Specialization(guards = "base == 10")
        protected Object base10(Object string, int base, boolean strict, boolean raiseOnError,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @Cached TruffleString.ParseLongNode parseLongNode,
                @Cached BranchProfile notLazyLongProfile,
                @Cached FixnumOrBignumNode fixnumOrBignumNode,
                @Cached BytesNode bytesNode,
                @Cached BranchProfile exceptionProfile) {
            var tstring = libString.getTString(string);
            try {
                return parseLongNode.execute(tstring, 10);
            } catch (TruffleString.NumberFormatException e) {
                notLazyLongProfile.enter();
                Rope rope = libString.getRope(string);
                return bytesToInum(rope, base, strict, raiseOnError, fixnumOrBignumNode, bytesNode, exceptionProfile);
            }
        }

        @Specialization(guards = "base == 0")
        protected Object base0(Object string, int base, boolean strict, boolean raiseOnError,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @Cached TruffleString.ParseLongNode parseLongNode,
                @Cached TruffleString.CodePointAtByteIndexNode codePointNode,
                @Cached ConditionProfile notEmptyProfile,
                @Cached BranchProfile notLazyLongProfile,
                @Cached FixnumOrBignumNode fixnumOrBignumNode,
                @Cached BytesNode bytesNode,
                @Cached BranchProfile exceptionProfile) {
            var tstring = libString.getTString(string);
            var enc = libString.getEncoding(string);
            var tenc = enc.tencoding;
            var len = tstring.byteLength(tenc);

            if (notEmptyProfile.profile(enc.jcoding.isAsciiCompatible() && len >= 1)) {
                int first = codePointNode.execute(tstring, 0, tenc);
                int second;
                if ((first >= '1' && first <= '9') ||
                        (len >= 2 && (first == '-' || first == '+') &&
                                (second = codePointNode.execute(tstring, 1, tenc)) >= '1' && second <= '9')) {
                    try {
                        return parseLongNode.execute(tstring, 10);
                    } catch (TruffleString.NumberFormatException e) {
                        notLazyLongProfile.enter();
                    }
                }
            }

            Rope rope = libString.getRope(string);
            return bytesToInum(rope, base, strict, raiseOnError, fixnumOrBignumNode, bytesNode, exceptionProfile);
        }

        @Specialization(guards = { "base != 10", "base != 0" })
        protected Object otherBase(Object string, int base, boolean strict, boolean raiseOnError,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @Cached FixnumOrBignumNode fixnumOrBignumNode,
                @Cached BytesNode bytesNode,
                @Cached BranchProfile exceptionProfile) {
            Rope rope = libString.getRope(string);
            return bytesToInum(rope, base, strict, raiseOnError, fixnumOrBignumNode, bytesNode, exceptionProfile);
        }

        private Object bytesToInum(Rope rope, int base, boolean strict, boolean raiseOnError,
                FixnumOrBignumNode fixnumOrBignumNode,
                BytesNode bytesNode,
                BranchProfile exceptionProfile) {
            try {
                return ConvertBytes.bytesToInum(
                        getContext(),
                        this,
                        fixnumOrBignumNode,
                        bytesNode,
                        rope,
                        base,
                        strict);
            } catch (RaiseException e) {
                exceptionProfile.enter();
                if (!raiseOnError) {
                    return nil;
                }
                throw e;
            }
        }
    }

    @Primitive(name = "string_byte_append")
    public abstract static class StringByteAppendPrimitiveNode extends CoreMethodArrayArgumentsNode {
        @Specialization(guards = "libOther.isRubyString(other)")
        protected RubyString stringByteAppend(RubyString string, Object other,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libOther,
                @Cached TruffleString.ConcatNode concatNode,
                @Cached TruffleString.ForceEncodingNode forceEncodingNode) {
            // The semantics of this primitive are such that the original string's byte[] should be extended without
            // negotiating the encoding.
            var encoding = string.encoding;
            var left = string.tstring;
            var right = forceEncodingNode.execute(libOther.getTString(other), libOther.getEncoding(other).tencoding,
                    encoding.tencoding);
            string.setTString(concatNode.execute(left, right, encoding.tencoding, true), encoding);
            return string;
        }
    }

    @Primitive(name = "string_substring", lowerFixnum = { 1, 2 })
    @ImportStatic(StringGuards.class)
    public abstract static class StringSubstringPrimitiveNode extends CoreMethodArrayArgumentsNode {

        @Child NormalizeIndexNode normalizeIndexNode = NormalizeIndexNode.create();
        @Child TruffleString.CodePointLengthNode codePointLengthNode = TruffleString.CodePointLengthNode.create();
        @Child SingleByteOptimizableNode singleByteOptimizableNode = SingleByteOptimizableNode.create();
        @Child TruffleString.SubstringByteIndexNode substringNode;

        public abstract Object execute(Object string, int index, int length);

        @Specialization(guards = {
                "!indexTriviallyOutOfBounds(libString.getTString(string), codePointLengthNode, " +
                        "libString.getEncoding(string), index, length)",
                "noCharacterSearch(libString.getRope(string), singleByteOptimizableNode)" })
        protected Object stringSubstringSingleByte(Object string, int index, int length,
                @Cached @Shared("negativeIndexProfile") ConditionProfile negativeIndexProfile,
                @Cached @Shared("tooLargeTotalProfile") ConditionProfile tooLargeTotalProfile,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            final Rope rope = libString.getRope(string);
            final RubyEncoding encoding = libString.getEncoding(string);
            var tstring = libString.getTString(string);
            int stringCodePointLength = codePointLengthNode.execute(tstring, encoding.tencoding);
            int normalizedIndex = normalizeIndexNode.executeNormalize(index, stringCodePointLength);
            int characterLength = length;

            if (negativeIndexProfile.profile(normalizedIndex < 0)) {
                return nil;
            }

            if (tooLargeTotalProfile.profile(normalizedIndex + characterLength > stringCodePointLength)) {
                characterLength = stringCodePointLength - normalizedIndex;
            }

            return makeRope(encoding, rope, normalizedIndex, characterLength);
        }

        @Specialization(guards = {
                "!indexTriviallyOutOfBounds(libString.getTString(string), codePointLengthNode, " +
                        "libString.getEncoding(string), index, length)",
                "!noCharacterSearch(libString.getRope(string), singleByteOptimizableNode)" })
        protected Object stringSubstringGeneric(Object string, int index, int length,
                @Cached @Shared("negativeIndexProfile") ConditionProfile negativeIndexProfile,
                @Cached @Shared("tooLargeTotalProfile") ConditionProfile tooLargeTotalProfile,
                @Cached @Exclusive ConditionProfile foundSingleByteOptimizableDescendentProfile,
                @Cached BranchProfile singleByteOptimizableBaseProfile,
                @Cached BranchProfile leafBaseProfile,
                @Cached BranchProfile slowSearchProfile,
                @Cached ByteIndexFromCharIndexNode byteIndexFromCharIndexNode,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            final Rope rope = libString.getRope(string);
            final RubyEncoding encoding = libString.getEncoding(string);
            var tstring = libString.getTString(string);
            int stringCodePointLength = codePointLengthNode.execute(tstring, encoding.tencoding);
            int normalizedIndex = normalizeIndexNode.executeNormalize(index, stringCodePointLength);
            int characterLength = length;

            if (negativeIndexProfile.profile(normalizedIndex < 0)) {
                return nil;
            }

            if (tooLargeTotalProfile.profile(normalizedIndex + characterLength > stringCodePointLength)) {
                characterLength = stringCodePointLength - normalizedIndex;
            }

            final SearchResult searchResult = searchForSingleByteOptimizableDescendant(
                    rope,
                    normalizedIndex,
                    characterLength,
                    singleByteOptimizableBaseProfile,
                    leafBaseProfile,
                    slowSearchProfile);

            if (foundSingleByteOptimizableDescendentProfile
                    .profile(singleByteOptimizableNode.execute(searchResult.rope))) {
                return makeRope(
                        encoding,
                        searchResult.rope,
                        searchResult.index,
                        characterLength);
            }

            return stringSubstringMultiByte(
                    string,
                    libString,
                    normalizedIndex,
                    characterLength,
                    byteIndexFromCharIndexNode);
        }

        @Specialization(guards = {
                "indexTriviallyOutOfBounds(libString.getTString(string), codePointLengthNode, " +
                        "libString.getEncoding(string), index, length)" })
        protected Object stringSubstringNegativeLength(Object string, int index, int length,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            return nil;
        }

        private SearchResult searchForSingleByteOptimizableDescendant(Rope base, int index, int characterLength,
                BranchProfile singleByteOptimizableBaseProfile,
                BranchProfile leafBaseProfile,
                BranchProfile slowSearchProfile) {

            if (singleByteOptimizableNode.execute(base)) {
                singleByteOptimizableBaseProfile.enter();
                return new SearchResult(index, base);
            }

            if (base instanceof LeafRope) {
                leafBaseProfile.enter();
                return new SearchResult(index, base);
            }

            slowSearchProfile.enter();
            return searchForSingleByteOptimizableDescendantSlow(base, index, characterLength);
        }

        @TruffleBoundary
        private SearchResult searchForSingleByteOptimizableDescendantSlow(Rope base, int index, int characterLength) {
            // If we've found something that's single-byte optimizable, we can halt the search. Taking a substring of
            // a single byte optimizable rope is a fast operation.
            if (base.isSingleByteOptimizable()) {
                return new SearchResult(index, base);
            }

            if (base instanceof LeafRope) {
                return new SearchResult(index, base);
            } else if (base instanceof SubstringRope) {
                final SubstringRope substringRope = (SubstringRope) base;
                if (substringRope.isSingleByteOptimizable()) {
                    // the substring byte offset is also a character offset
                    return searchForSingleByteOptimizableDescendantSlow(
                            substringRope.getChild(),
                            index + substringRope.getByteOffset(),
                            characterLength);
                } else {
                    return new SearchResult(index, substringRope);
                }
            } else if (base instanceof NativeRope) {
                final NativeRope nativeRope = (NativeRope) base;
                return new SearchResult(index, nativeRope.toLeafRope());
            } else {
                throw new UnsupportedOperationException(
                        "Don't know how to traverse rope type: " + base.getClass().getName());
            }
        }

        private Object stringSubstringMultiByte(Object string, RubyStringLibrary libString, int beg, int characterLen,
                ByteIndexFromCharIndexNode byteIndexFromCharIndexNode) {
            // Taken from org.jruby.RubyString#substr19 & org.jruby.RubyString#multibyteSubstr19.

            final Rope rope = libString.getRope(string);
            var tstring = libString.getTString(string);
            final RubyEncoding encoding = libString.getEncoding(string);
            final int length = tstring.byteLength(encoding.tencoding);

            int p;
            final int end = length;
            int substringByteLength;

            p = byteIndexFromCharIndexNode.execute(rope, 0, beg);
            if (p == end) {
                substringByteLength = 0;
            } else {
                int pp = byteIndexFromCharIndexNode.execute(rope, p, characterLen);
                substringByteLength = StringSupport.offset(p, end, pp);
            }

            return makeRope(encoding, tstring, p, substringByteLength);
        }

        private RubyString makeRope(RubyEncoding encoding, Rope rope, int beg, int byteLength) {
            return makeRope(encoding, TStringUtils.fromRope(rope, encoding), beg, byteLength);
        }

        private RubyString makeRope(RubyEncoding encoding, AbstractTruffleString tstring, int beg, int byteLength) {
            if (substringNode == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                substringNode = insert(TruffleString.SubstringByteIndexNode.create());
            }

            return createSubString(substringNode, tstring, encoding, beg, byteLength);
        }

        protected static boolean indexTriviallyOutOfBounds(AbstractTruffleString tstring,
                TruffleString.CodePointLengthNode codePointLengthNode, RubyEncoding encoding,
                int index, int length) {
            return (length < 0) ||
                    (index > codePointLengthNode.execute(tstring, encoding.tencoding));
        }

        protected static boolean noCharacterSearch(Rope rope,
                SingleByteOptimizableNode singleByteOptimizableNode) {
            return rope.isEmpty() || singleByteOptimizableNode.execute(rope);
        }

        private static final class SearchResult {
            public final int index;
            public final Rope rope;

            public SearchResult(final int index, final Rope rope) {
                this.index = index;
                this.rope = rope;
            }
        }

    }

    @NonStandard
    @CoreMethod(names = "from_bytearray", onSingleton = true, required = 4, lowerFixnum = { 2, 3 })
    public abstract static class StringFromByteArrayPrimitiveNode extends CoreMethodArrayArgumentsNode {

        @Specialization
        protected RubyString stringFromByteArray(
                RubyByteArray byteArray, int start, int count, RubyEncoding rubyEncoding,
                @Cached StringNodes.MakeStringNode makeStringNode) {
            final byte[] bytes = byteArray.bytes;
            final byte[] array = ArrayUtils.extractRange(bytes, start, start + count);

            return makeStringNode.executeMake(array, rubyEncoding, CR_UNKNOWN);
        }

    }

    public abstract static class StringAppendNode extends RubyBaseNode {

        public static StringAppendNode create() {
            return StringAppendNodeGen.create();
        }

        public abstract RubyString executeStringAppend(Object string, Object other);

        @Specialization(guards = "libOther.isRubyString(other)")
        protected RubyString stringAppend(Object string, Object other,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libOther,
                @Cached EncodingNodes.CheckStringEncodingNode checkEncodingNode,
                @Cached TruffleString.ConcatNode concatNode) {

            var left = libString.getTString(string);
            var leftEncoding = libString.getEncoding(string);
            var right = libOther.getTString(other);
            var rightEncoding = libOther.getEncoding(other);

            final RubyEncoding compatibleEncoding = checkEncodingNode.executeCheckEncoding(left, leftEncoding,
                    right, rightEncoding);

            var result = concatNode.execute(left, right, compatibleEncoding.tencoding, true);
            return createString(result, compatibleEncoding);
        }
    }

    @Primitive(name = "string_to_null_terminated_byte_array")
    public abstract static class StringToNullTerminatedByteArrayNode extends PrimitiveArrayArgumentsNode {

        @Specialization(guards = "libString.isRubyString(string)")
        protected Object stringToNullTerminatedByteArray(Object string,
                @Cached TruffleString.CopyToByteArrayNode copyToByteArrayNode,
                @CachedLibrary(limit = "LIBSTRING_CACHE") RubyStringLibrary libString) {
            final var encoding = libString.getEncoding(string);
            final var tstring = libString.getTString(string);
            final int bytesToCopy = tstring.byteLength(encoding.tencoding);
            final var bytesWithNull = new byte[bytesToCopy + 1];

            // NOTE: we always need one copy here, as native code could modify the passed byte[]
            copyToByteArrayNode.execute(tstring, 0,
                    bytesWithNull, 0, bytesToCopy, encoding.tencoding);

            return getContext().getEnv().asGuestValue(bytesWithNull);
        }

        @Specialization
        protected Object emptyString(Nil string) {
            return getContext().getEnv().asGuestValue(null);
        }

    }

    @Primitive(name = "string_interned?")
    public abstract static class IsInternedNode extends PrimitiveArrayArgumentsNode {
        @Specialization
        protected boolean isInterned(ImmutableRubyString string) {
            return true;
        }

        @Specialization
        protected boolean isInterned(RubyString string) {
            return false;
        }
    }

    @Primitive(name = "string_intern")
    public abstract static class InternNode extends PrimitiveArrayArgumentsNode {
        @Specialization
        protected ImmutableRubyString internString(RubyString string,
                @Cached TruffleString.AsManagedNode asManagedNode) {
            var encoding = string.encoding;
            TruffleString immutableManagedString = asManagedNode.execute(string.tstring, encoding.tencoding);
            return getLanguage().getFrozenStringLiteral(immutableManagedString, encoding);
        }
    }

}
