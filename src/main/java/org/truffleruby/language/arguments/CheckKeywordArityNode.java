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
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import org.truffleruby.RubyContext;
import org.truffleruby.RubyLanguage;
import org.truffleruby.core.array.RubyArray;
import org.truffleruby.core.hash.HashOperations;
import org.truffleruby.core.hash.RubyHash;
import org.truffleruby.core.hash.library.HashStoreLibrary;
import org.truffleruby.core.hash.library.HashStoreLibrary.EachEntryCallback;
import org.truffleruby.core.symbol.RubySymbol;
import org.truffleruby.debug.DebugHelpers;
import org.truffleruby.language.RubyBaseNode;
import org.truffleruby.language.RubyContextNode;
import org.truffleruby.language.control.RaiseException;
import org.truffleruby.language.methods.Arity;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.profiles.ConditionProfile;

public class CheckKeywordArityNode extends RubyBaseNode {

    @Child private ReadUserKeywordsHashNode readUserKeywordsHashNode;
    @Child private CheckKeywordArgumentsNode checkKeywordArgumentsNode;
    @Child private HashStoreLibrary hashes;

    private final BranchProfile receivedKeywordsProfile = BranchProfile.create();

    public CheckKeywordArityNode(Arity arity) {
        this.readUserKeywordsHashNode = new ReadUserKeywordsHashNode(arity.getRequired());
    }

    public void checkArity(VirtualFrame frame, Arity arity,
            BranchProfile basicArityCheckFailedProfile,
            RubyLanguage language,
            ContextReference<RubyContext> contextRef) {
        CompilerAsserts.partialEvaluationConstant(arity);

        final RubyHash keywordArguments = readUserKeywordsHashNode.execute(frame);

        final int argumentsCount = RubyArguments.getArgumentsCount(frame);
        int given = argumentsCount;

        if (keywordArguments != null) {
            receivedKeywordsProfile.enter();
            given -= 1;
        }

        if (!arity.basicCheck(given)) {
            basicArityCheckFailedProfile.enter();
            throw new RaiseException(
                    contextRef.get(),
                    contextRef.get().getCoreExceptions().argumentError(given, arity.getRequired(), this));
        }

        if (!arity.hasKeywordsRest() && keywordArguments != null) {
            checkKeywordArguments(argumentsCount, keywordArguments, arity, language, RubyArguments.getKeywordArgumentsValues(frame), RubyArguments.getKeywordArgumentsDescriptor(frame));
        }

        if (keywordArguments != null) {
            checkEverythingInDescriptorIsInHash(language, keywordArguments, RubyArguments.getKeywordArgumentsDescriptor(frame));
        }
    }

    @CompilerDirectives.TruffleBoundary
    private void checkEverythingInDescriptorIsInHash(RubyLanguage language, RubyHash keywordArguments, KeywordArgumentsDescriptor argumentsDescriptor) {
        assert keywordArguments != null;
        assert argumentsDescriptor != null;

        // Don't bother checking if it's a splatted keyword?
        if (argumentsDescriptor.alsoSplat) {
            return;
        }
        for (String described : argumentsDescriptor.getKeywords()) {
            if (!((boolean) RubyContext.send(keywordArguments, "has_key?", language.getSymbol(described)))) {
                throw new RuntimeException("descriptor says kw statically there, but not in hash! did not contain " + described);
            }
        }
    }

    void checkKeywordArguments(int argumentsCount, RubyHash keywordArguments, Arity arity, RubyLanguage language, Object[] keywordArgumentsValues, KeywordArgumentsDescriptor keywordArgumentsDescriptor) {
        if (hashes == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            hashes = insert(HashStoreLibrary.createDispatched());
        }
        //if (checkKeywordArgumentsNode == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
        // TODO this lambda node needs to know about the flattened keyword argument values, so it needs to be created per call
            checkKeywordArgumentsNode = insert(new CheckKeywordArgumentsNode(language, arity, keywordArgumentsValues) {

                @Override
                protected KeywordArgumentsDescriptor getDescriptor() {
                    return keywordArgumentsDescriptor;
                }

            });
        //}
        hashes.eachEntry(keywordArguments.store, keywordArguments, checkKeywordArgumentsNode, argumentsCount);
    }

    private abstract static class CheckKeywordArgumentsNode extends RubyContextNode implements EachEntryCallback {

        private final boolean doesNotAcceptExtraArguments;
        private final int required;
        @CompilationFinal(dimensions = 1) private final RubySymbol[] allowedKeywords;
        private final Object[] keywordArgumentsValues;

        private final ConditionProfile isSymbolProfile = ConditionProfile.create();
        private final BranchProfile tooManyKeywordsProfile = BranchProfile.create();
        private final BranchProfile unknownKeywordProfile = BranchProfile.create();

        public CheckKeywordArgumentsNode(RubyLanguage language, Arity arity, Object[] keywordArgumentsValues) {
            assert !arity.hasKeywordsRest();
            doesNotAcceptExtraArguments = !arity.hasRest() && arity.getOptional() == 0;
            required = arity.getRequired();
            this.keywordArgumentsValues = keywordArgumentsValues;
            allowedKeywords = keywordsAsSymbols(language, arity);
        }

        @Override
        public void accept(int index, Object key, Object value, Object argumentsCount) {
            if (isSymbolProfile.profile(key instanceof RubySymbol)) {
                if (!keywordAllowed(key)) {
                    unknownKeywordProfile.enter();
                    throw new RaiseException(
                            getContext(),
                            coreExceptions().argumentErrorUnknownKeyword((RubySymbol) key, this));
                }
                checkValueIsInArgumentsValueArray(key, index, value);
            } else {
                // the Hash would be split and a reject Hash be created to hold non-Symbols when there is no **kwrest parameter,
                // so we need to check if an extra argument is allowed
                final int given = (int) argumentsCount; // -1 for keyword hash, +1 for reject Hash with non-Symbol keys
                if (doesNotAcceptExtraArguments && given > required) {
                    tooManyKeywordsProfile.enter();
                    throw new RaiseException(getContext(), coreExceptions().argumentError(given, required, this));
                }
            }

        }

        protected abstract KeywordArgumentsDescriptor getDescriptor();

        @CompilerDirectives.TruffleBoundary
        private void checkValueIsInArgumentsValueArray(Object key, int index, Object value) {
            // We only pack symbol keys
            if (!(key instanceof RubySymbol)) {
                return;
            }

            // We're only considering keys in the descriptor
            final String keyword = ((RubySymbol) key).getString();
            int packedIndex = getDescriptor().indexOf(keyword);
            if (packedIndex == -1) {
                return;
            }

            // Check the packed value is the same as the value from the hash
            if (keywordArgumentsValues[packedIndex] != value) {
                throw new IllegalArgumentException("optimised " + keywordArgumentsValues[index] + " original " + value);
            }
        }

        @ExplodeLoop
        private boolean keywordAllowed(Object keyword) {
            for (int i = 0; i < allowedKeywords.length; i++) {
                if (allowedKeywords[i] == keyword) {
                    return true;
                }
            }

            return false;
        }
    }

    static RubySymbol[] keywordsAsSymbols(RubyLanguage language, Arity arity) {
        final String[] names = arity.getKeywordArguments();
        final RubySymbol[] symbols = new RubySymbol[names.length];
        for (int i = 0; i < names.length; i++) {
            symbols[i] = language.getSymbol(names[i]);
        }
        return symbols;
    }
}
