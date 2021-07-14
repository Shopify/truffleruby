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

    public static boolean isKeywordArgumentOptimizable(Arity arity) {
        return arity.hasKeywords() && !arity.hasKeywordsRest();
    }

    public static boolean isKeyWordArgsOptimizable(Frame frame) {
        if (flattenedHashType(frame).isEmpty()) {
            return false;
        }
        return true;
    }

    public static String flattenedHashType(Frame frame) {
        return (String) frame.getArguments()[RubyArguments.ArgumentIndicies.KW_ARGS_OPTIMIZABLE_FLAG.ordinal()];
    }

    public static Object[] flattenArguments(RubyHash arguments) {
        // There are 2 types of Hashes: Packed hashes (small), Generic hashes
        // Packed hashes does not have `firstInSequence`, while Generic hashes do
        if (arguments.firstInSequence != null) {
            Entry entry = arguments.firstInSequence;
            Object[] flattenedArguments = new Object[arguments.size * 2]; // Twice the size to store both keys & values
            int i = 0;
            while (entry != null) {
                flattenedArguments[i] = entry.getKey();
                flattenedArguments[i+1] = entry.getValue();
                entry = entry.getNextInSequence();
                i += 2;
            }
            return flattenedArguments;
        } else {
            final Object[] store = (Object[]) arguments.store;
            final Object[] copied = PackedHashStoreLibrary.createStore();
            System.arraycopy(store, 0, copied, 0, store.length); // store length to fit all the arguments (not sure ifcorrect)
            return copied;
        }
    }

    static RubyHash reconstructArgumentHash(String hashType, Object[] flattenedArguments) {
        assert !hashType.isEmpty();

        if (hashType == "PackedHash") {
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
        }

        if (hashType == "GenericHash") {
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
        } else {
            return null;
        }
    }

    public static int getOptimizedArgumentsCount(Frame frame) {
        return (frame.getArguments().length - RubyArguments.RUNTIME_ARGUMENT_COUNT) / 3;
    }

    public static Object reconstructHash(VirtualFrame frame) {
        Object lastArgument;
        Object[] flattenedArguments = RubyArguments.getArguments(frame);
        String hashType = flattenedHashType(frame);
        lastArgument = reconstructArgumentHash(hashType, flattenedArguments);
        return lastArgument;
    }

    static Object[] packOptimizedArguments(Object[] arguments, Memo<String> flattenArgumentsFlagMemo) {
        // Sometimes the argument is empty, and not a RubyHash instance
        if (arguments.length > 0 && arguments[0] instanceof RubyHash) {

            // Flatten the arguments hash and reassign to `arguments`
            RubyHash extractedArguments = (RubyHash) arguments[0];
            arguments = flattenArguments(extractedArguments);

            if (extractedArguments.firstInSequence != null) {
                flattenArgumentsFlagMemo.set("GenericHash");
            } else {
                flattenArgumentsFlagMemo.set("PackedHash");
            }

            return arguments;
        } else {
            flattenArgumentsFlagMemo.set("");
            return arguments;
        }
    }

    static void actualNumberOfArguments(VirtualFrame frame, Memo<Integer> givenMemo, Memo<Integer> argumentsCountMemo) {
        if (flattenedHashType(frame) == "PackedHash") {
            givenMemo.set(RubyArguments.getArgumentsCount(frame) / 3);
            argumentsCountMemo.set(givenMemo.get() / 3);
        } else if (flattenedHashType(frame) == "GenericHash") {
            argumentsCountMemo.set(RubyArguments.getArgumentsCount(frame) / 2);
        }
        argumentsCountMemo.set(givenMemo.get());
    }
}
