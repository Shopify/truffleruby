package org.truffleruby.parser;

import com.oracle.truffle.api.strings.TruffleString;

/** Simple struct to hold method stubs until their fully defined. */
public record MethodStubHolder(TruffleString name, TruffleString currentArg) {
};
