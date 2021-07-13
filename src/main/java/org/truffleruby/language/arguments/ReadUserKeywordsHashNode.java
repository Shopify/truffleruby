/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.language.arguments;

import org.truffleruby.RubyLanguage;
import org.truffleruby.core.hash.Entry;
import org.truffleruby.core.hash.RubyHash;
import org.truffleruby.core.hash.library.BucketsHashStore;
import org.truffleruby.core.hash.library.HashStoreLibrary;
import org.truffleruby.core.hash.library.PackedHashStoreLibrary;
import org.truffleruby.language.RubyBaseNode;
import org.truffleruby.language.RubyGuards;
import org.truffleruby.language.dispatch.DispatchNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;
import org.truffleruby.language.dispatch.InternalRespondToNode;

public final class ReadUserKeywordsHashNode extends RubyBaseNode {

    private final int minArgumentCount;

    @Child private InternalRespondToNode respondToToHashNode;
    @Child private DispatchNode callToHashNode;

    private final ConditionProfile notEnoughArgumentsProfile = ConditionProfile.create();
    private final ConditionProfile lastArgumentIsHashProfile = ConditionProfile.create();
    private final ConditionProfile respondsToToHashProfile = ConditionProfile.create();
    private final ConditionProfile convertedIsHashProfile = ConditionProfile.create();

    public ReadUserKeywordsHashNode(int minArgumentCount) {
        this.minArgumentCount = minArgumentCount;
    }

    public RubyHash execute(VirtualFrame frame) {
        final int argumentCount = RubyArguments.getArgumentsCount(frame);

        if (notEnoughArgumentsProfile.profile(argumentCount <= minArgumentCount)) {
            return null;
        }

        Object lastArgument = RubyArguments.getArgument(frame, argumentCount - 1);

        // If we're reading a flattened hash, reconstruct the hash
        if (RubyArguments.isKeyWordArgsOptimizable(frame)) {
            Object[] flattenedArguments = RubyArguments.getArguments(frame);
            String hashType = RubyArguments.flattenedHashType(frame);
            lastArgument = reconstructArgumentHash(hashType, flattenedArguments);
        }

        if (lastArgumentIsHashProfile.profile(RubyGuards.isRubyHash(lastArgument))) {
            return (RubyHash) lastArgument;
        } else {
            return tryConvertToHash(frame, argumentCount, lastArgument);
        }
    }

    private RubyHash reconstructArgumentHash(String hashType, Object[] flattenedArguments) {
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
                    nil,
                    nil,
                    false);
        }

        if (hashType == "GenericHash") {
            HashStoreLibrary hashes = insert(HashStoreLibrary.createDispatched());
            BucketsHashStore bucket = new BucketsHashStore(new Entry[flattenedArguments.length / 2 * 4]);
            final RubyHash rubyHash = new RubyHash(
                    RubyLanguage.getCurrentContext().getCoreLibrary().hashClass,
                    RubyLanguage.getCurrentLanguage().hashShape,
                    RubyLanguage.getCurrentContext(),
                    bucket,
                    flattenedArguments.length / 2,
                    null,
                    null,
                    nil,
                    nil,
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

    private RubyHash tryConvertToHash(VirtualFrame frame, int argumentCount, Object lastArgument) {
        if (respondsToToHashProfile.profile(respondToToHash(frame, lastArgument))) {
            final Object converted = callToHash(frame, lastArgument);

            if (convertedIsHashProfile.profile(RubyGuards.isRubyHash(converted))) {
                RubyArguments.setArgument(frame, argumentCount - 1, converted);
                return (RubyHash) converted;
            }
        }

        return null;
    }

    private boolean respondToToHash(VirtualFrame frame, Object lastArgument) {
        if (respondToToHashNode == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            respondToToHashNode = insert(InternalRespondToNode.create());
        }
        return respondToToHashNode.execute(frame, lastArgument, "to_hash");
    }

    private Object callToHash(VirtualFrame frame, Object lastArgument) {
        if (callToHashNode == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            callToHashNode = insert(DispatchNode.create());
        }
        return callToHashNode.call(lastArgument, "to_hash");
    }

}
