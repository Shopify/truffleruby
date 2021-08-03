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

import org.truffleruby.parser.ast.HashParseNode;
import org.truffleruby.parser.ast.KeywordRestArgParseNode;
import org.truffleruby.parser.ast.LocalVarParseNode;
import org.truffleruby.parser.ast.ParseNode;
import org.truffleruby.parser.ast.SymbolParseNode;
import org.truffleruby.parser.ast.types.INameNode;
import org.truffleruby.parser.parser.ParseNodeTuple;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class KeywordArgumentsDescriptor {

    private static final ConcurrentHashMap<KeywordArgumentsDescriptor, KeywordArgumentsDescriptor> CANONICAL_DESCRIPTORS = new ConcurrentHashMap<>();

    public static final KeywordArgumentsDescriptor EMPTY = get(new String[]{}, false);

    private final String[] keywords;
    public final boolean alsoSplat;

    public static KeywordArgumentsDescriptor get(String[] keywords, boolean alsoSplat) {
        final KeywordArgumentsDescriptor descriptor = new KeywordArgumentsDescriptor(keywords, alsoSplat);

        final KeywordArgumentsDescriptor found = CANONICAL_DESCRIPTORS.putIfAbsent(descriptor, descriptor);

        if (found == null) {
            return descriptor;
        } else {
            return found;
        }
    }

    private KeywordArgumentsDescriptor(String[] keywords, boolean alsoSplat) {
        for (int n = 0; n < keywords.length; n++) {
            Objects.requireNonNull(keywords[n]);
        }
        this.keywords = keywords;
        this.alsoSplat = alsoSplat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeywordArgumentsDescriptor that = (KeywordArgumentsDescriptor) o;
        return Arrays.equals(keywords, that.keywords);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(keywords);
    }

    @Override
    public String toString() {
        return "KeywordArgumentsDescriptor{" +
                "keywords=" + Arrays.toString(keywords) +
                '}';
    }

    public static KeywordArgumentsDescriptor getKeywordArgumentsDescriptor(ParseNode[] arguments) {
        KeywordArgumentsDescriptor keywordArgumentsDescriptor = KeywordArgumentsDescriptor.EMPTY;
        boolean alsoSplat = false;
        if ((arguments.length > 0)) {

            // First, check for `HashParseNode` in the argument
            Object hashIndex = null;
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] instanceof HashParseNode) {
                    hashIndex = i;
                }
            }

            if (hashIndex != null) {
                // Alright so this isn't pretty but
                // We want to figure out the number of KW-related nodes
                // because it may be null sometimes and we don't want to count those
                HashParseNode keywordArgHash = (HashParseNode) arguments[(Integer) hashIndex];
                Integer argumentCount = 0;
                for (ParseNodeTuple pair : keywordArgHash.getPairs()) {
                    if (pair instanceof ParseNodeTuple) {
                        if ((pair.getKey() instanceof SymbolParseNode) && ((SymbolParseNode) pair.getKey()).getName() != null) {
                            argumentCount++;
                        } else if ((pair.getKey() instanceof LocalVarParseNode) && ((LocalVarParseNode) pair.getKey()).getName() != null) {
                            argumentCount++;
                        }
                    }
                }

                String[] keywords = new String[argumentCount];
                int countIndex = 0;
                for (ParseNodeTuple pair : keywordArgHash.getPairs()) {
                    if (pair instanceof ParseNodeTuple) {
                        if ((pair.getKey() instanceof SymbolParseNode) && ((SymbolParseNode) pair.getKey()).getName() != null) {
                            keywords[countIndex] = ((SymbolParseNode) pair.getKey()).getName();
                            countIndex++;
                        } else if ((pair.getKey() instanceof LocalVarParseNode) && ((LocalVarParseNode) pair.getKey()).getName() != null) {
                            keywords[countIndex] = ((LocalVarParseNode) pair.getKey()).getName();
                            countIndex++;
                        } else if ((pair.getKey() == null) && (pair.getValue() != null)) { // Indicates a splat kw hash
                            alsoSplat = true;
                        }
                    }
                }
                keywordArgumentsDescriptor = KeywordArgumentsDescriptor.get(keywords, alsoSplat);
            }
        }
        return keywordArgumentsDescriptor;
    }

    public boolean contains(String expected) {
        return Arrays.asList(keywords).contains(expected);
    }

    public String[] getKeywords() {
        return keywords;
    }
}
