/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.language.arguments;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.truffleruby.RubyLanguage;
import org.truffleruby.collections.Memo;
import org.truffleruby.core.hash.Entry;
import org.truffleruby.core.hash.RubyHash;
import org.truffleruby.core.hash.library.BucketsHashStore;
import org.truffleruby.core.hash.library.HashStoreLibrary;
import org.truffleruby.core.hash.library.PackedHashStoreLibrary;
import org.truffleruby.language.RubyBaseNode;
import org.truffleruby.language.methods.Arity;

public class OptimizedKeywordArguments {

    /**
     * Normally we pass keyword arguments in a hash that is passed as the last user argument.
     *
     *     arg0, arg1, arg2, ..., kwHash
     *
     * In this optimized version, we pass keyword argument pairs in the argument array, after user argumnents.
     *
     *     arg0, arg1, arg2, ..., key0, value0, key1, value1, key2, value2, ...
     *
     * To mark
     */

    public enum CallingConvention {
        UNOPTIMIZED,
        OPTIMIZED_GENERIC,
        OPTIMIZED_PACKED
    }

    // Logic for saying if the optimization applies

    public static boolean canArityUseOptimizedCallingConvention(Arity arity) {
        return arity.hasKeywords() && !arity.hasKeywordsRest();
    }

    public static boolean doesFrameContainOptimizedKeywordArguments(Frame frame) {
        return RubyArguments.getCallingConvention(frame) != CallingConvention.UNOPTIMIZED;
    }

    // Logic for packing

    public static int numberOfGivenArguments(Frame frame) {
        final int extra = frame.getArguments().length - RubyArguments.RUNTIME_ARGUMENT_COUNT ;

        switch (RubyArguments.getCallingConvention(frame)) {
            case OPTIMIZED_PACKED:
                return extra / 3;

            case OPTIMIZED_GENERIC:
                return extra / 2;

            default:
                return extra;
        }
    }

    public static Object[] packOptimizedArguments(Object[] arguments, Memo<CallingConvention> flattenArgumentsFlagMemo) {
        // Sometimes the argument is empty, and not a RubyHash instance
        if (arguments.length > 0 && arguments[0] instanceof RubyHash) {

            // Flatten the arguments hash and reassign to `arguments`
            RubyHash extractedArguments = (RubyHash) arguments[0];
            Object[] result;
            // There are 2 types of Hashes: Packed hashes (small), Generic hashes
            // Packed hashes does not have `firstInSequence`, while Generic hashes do
            if (extractedArguments.firstInSequence != null) {
                Entry entry = extractedArguments.firstInSequence;
                Object[] flattenedArguments = new Object[extractedArguments.size * 2]; // Twice the size to store both keys & values
                int i = 0;
                while (entry != null) {
                    flattenedArguments[i] = entry.getKey();
                    flattenedArguments[i+1] = entry.getValue();
                    entry = entry.getNextInSequence();
                    i += 2;
                }
                result = flattenedArguments;
            } else {
                final Object[] store = (Object[]) extractedArguments.store;
                final Object[] copied = PackedHashStoreLibrary.createStore();
                System.arraycopy(store, 0, copied, 0, store.length); // store length to fit all the arguments (not sure ifcorrect)
                result = copied;
            }
            arguments = result;

            if (extractedArguments.firstInSequence != null) {
                flattenArgumentsFlagMemo.set(CallingConvention.OPTIMIZED_GENERIC);
            } else {
                flattenArgumentsFlagMemo.set(CallingConvention.OPTIMIZED_PACKED);
            }

            return arguments;
        } else {
            flattenArgumentsFlagMemo.set(CallingConvention.UNOPTIMIZED);
            return arguments;
        }
    }

    // Logic for unpacking

    public static RubyHash unpackOptimizedKeywordArguments(VirtualFrame frame) {
        assert doesFrameContainOptimizedKeywordArguments(frame);
        CallingConvention callingConvention = RubyArguments.getCallingConvention(frame);
        Object[] flattenedArguments = RubyArguments.getArguments(frame);
        switch (callingConvention) {
            case OPTIMIZED_PACKED:
                final Object[] store = PackedHashStoreLibrary.createStore();
                for (int n = 0; n < flattenedArguments.length; n += 3) {
                    // I'm not sure why but sometimes there are null elements
                    // at the end of the `flattenedArguments` array
                    if (flattenedArguments[n] == null) {
                        break;
                    }
                    PackedHashStoreLibrary.setHashedKeyValue(store, n / 3, (Integer) flattenedArguments[n], flattenedArguments[n + 1], flattenedArguments[n + 2]);
                }
                return new RubyHash(
                        RubyLanguage.getCurrentContext().getCoreLibrary().hashClass,
                        RubyLanguage.getCurrentLanguage().hashShape,
                        RubyLanguage.getCurrentContext(),
                        store,
                        flattenedArguments.length / 3,
                        null,
                        null,
                        RubyBaseNode.nil,
                        RubyBaseNode.nil,
                        false);

            case OPTIMIZED_GENERIC:
                HashStoreLibrary hashes = HashStoreLibrary.getUncached();
                BucketsHashStore bucket = new BucketsHashStore(new Entry[flattenedArguments.length / 2 * 4]);
                final RubyHash rubyHash = new RubyHash(
                        RubyLanguage.getCurrentContext().getCoreLibrary().hashClass,
                        RubyLanguage.getCurrentLanguage().hashShape,
                        RubyLanguage.getCurrentContext(),
                        bucket,
                        flattenedArguments.length / 2,
                        null,
                        null,
                        RubyBaseNode.nil,
                        RubyBaseNode.nil,
                        false);
                for (int n = 0; n < flattenedArguments.length; n += 2) {
                    final Object key = flattenedArguments[n];
                    final Object value = flattenedArguments[n + 1];
                    hashes.set(rubyHash.store, rubyHash, key, value, false);
                }
                return rubyHash;

            default:
                throw new UnsupportedOperationException(callingConvention.toString());
        }
    }

    public static void actualNumberOfArguments(VirtualFrame frame, Memo<Integer> givenMemo, Memo<Integer> argumentsCountMemo) {
        switch (RubyArguments.getCallingConvention(frame)) {
            case OPTIMIZED_PACKED:
                givenMemo.set(RubyArguments.getArgumentsCount(frame) / 3);
                argumentsCountMemo.set(givenMemo.get() / 3);
                return;

            case OPTIMIZED_GENERIC:
                argumentsCountMemo.set(RubyArguments.getArgumentsCount(frame) / 2);
                return;

            default:
                argumentsCountMemo.set(givenMemo.get());
                return;
        }
    }

}
