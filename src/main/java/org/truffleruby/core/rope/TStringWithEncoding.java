/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.core.rope;

import java.util.Objects;

import com.oracle.truffle.api.strings.AbstractTruffleString;
import org.truffleruby.core.encoding.RubyEncoding;

public final class TStringWithEncoding {

    public final AbstractTruffleString tstring;
    public final RubyEncoding encoding;

    public TStringWithEncoding(AbstractTruffleString tstring, RubyEncoding encoding) {
        assert tstring.isCompatibleTo(encoding.tencoding);
        this.tstring = tstring;
        this.encoding = encoding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TStringWithEncoding)) {
            return false;
        }
        TStringWithEncoding that = (TStringWithEncoding) o;
        return tstring.equals(that.tstring) && encoding == that.encoding;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tstring, encoding);
    }

}
