/*
 * Copyright (c) 2015, 2023 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.interop;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import org.truffleruby.language.RubyBaseNode;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import org.truffleruby.language.control.RaiseException;

@GenerateUncached
public abstract class ToJavaStringNode extends RubyBaseNode {

    public abstract String execute(Object name);

    @Specialization(guards = "interopLibrary.isString(value)", limit = "getLimit()")
    protected String interopString(Object value,
            @CachedLibrary("value") InteropLibrary interopLibrary,
            @Cached TranslateInteropExceptionNode translateInteropException) {
        try {
            return interopLibrary.asString(value);
        } catch (UnsupportedMessageException e) {
            throw translateInteropException.execute(e);
        }
    }

    @Specialization(guards = "!interopLibrary.isString(value)", limit = "getLimit()")
    protected String notInteropString(Object value,
            @CachedLibrary("value") InteropLibrary interopLibrary) {
        throw new RaiseException(
                getContext(),
                coreExceptions()
                        .typeError("This interop message requires a String or Symbol for the member name", this));
    }

    protected int getLimit() {
        return getLanguage().options.INTEROP_CONVERT_CACHE;
    }
}
