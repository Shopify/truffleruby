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

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class KeywordArgumentsDescriptor {

    private static final ConcurrentHashMap<KeywordArgumentsDescriptor, KeywordArgumentsDescriptor> CANONICAL_DESCRIPTORS = new ConcurrentHashMap<>();

    public static final KeywordArgumentsDescriptor EMPTY = get(new String[]{});

    private final String[] keywords;

    public static KeywordArgumentsDescriptor get(String[] keywords) {
        final KeywordArgumentsDescriptor descriptor = new KeywordArgumentsDescriptor(keywords);

        final KeywordArgumentsDescriptor found = CANONICAL_DESCRIPTORS.putIfAbsent(descriptor, descriptor);

        if (found == null) {
            return descriptor;
        } else {
            return found;
        }
    }

    private KeywordArgumentsDescriptor(String[] keywords) {
        this.keywords = keywords;
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

}
