/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.core.string;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.strings.AbstractTruffleString;
import org.jcodings.Encoding;
import org.truffleruby.core.encoding.RubyEncoding;
import org.truffleruby.core.encoding.TStringUtils;
import org.truffleruby.core.klass.RubyClass;
import org.truffleruby.core.rope.Rope;
import org.truffleruby.core.rope.RopeOperations;
import org.truffleruby.interop.ToJavaStringNode;
import org.truffleruby.language.RubyDynamicObject;
import org.truffleruby.language.library.RubyLibrary;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.Shape;
import org.truffleruby.language.library.RubyStringLibrary;

@ExportLibrary(RubyLibrary.class)
@ExportLibrary(InteropLibrary.class)
@ExportLibrary(RubyStringLibrary.class)
public final class RubyString extends RubyDynamicObject {

    public boolean frozen;
    public boolean locked = false;
    public Rope rope;
    public AbstractTruffleString tstring;
    public RubyEncoding encoding;

    public RubyString(
            RubyClass rubyClass,
            Shape shape,
            boolean frozen,
            AbstractTruffleString tstring,
            RubyEncoding rubyEncoding) {
        super(rubyClass, shape);
        assert tstring.isCompatibleTo(rubyEncoding.tencoding);
        this.frozen = frozen;
        this.tstring = tstring;
        this.rope = TStringUtils.toRope(tstring, rubyEncoding);
        assert rope.encoding == rubyEncoding.jcoding;
        this.encoding = rubyEncoding;
    }

    @Deprecated
    public RubyString(RubyClass rubyClass, Shape shape, boolean frozen, Rope rope, RubyEncoding rubyEncoding) {
        super(rubyClass, shape);
        assert rope.encoding == rubyEncoding.jcoding;
        this.frozen = frozen;
        this.rope = rope;
        this.tstring = TStringUtils.fromRope(rope, rubyEncoding);
        this.encoding = rubyEncoding;
    }

    public void setRope(Rope rope) {
        assert rope.encoding == encoding.jcoding : rope.encoding + " does not equal " + encoding.jcoding;
        this.rope = rope;
        this.tstring = TStringUtils.fromRope(rope, encoding);
    }

    public void setRope(Rope rope, RubyEncoding encoding) {
        assert rope.encoding == encoding.jcoding;
        this.rope = rope;
        this.tstring = TStringUtils.fromRope(rope, encoding);
        this.encoding = encoding;
    }

    public void setTString(AbstractTruffleString tstring) {
        assert tstring.isCompatibleTo(encoding.tencoding);
        this.rope = TStringUtils.toRope(tstring, encoding);
        this.tstring = tstring;
    }

    public void setTString(AbstractTruffleString tstring, RubyEncoding encoding) {
        assert tstring.isCompatibleTo(encoding.tencoding);
        this.rope = TStringUtils.toRope(tstring, encoding);
        this.tstring = tstring;
        this.encoding = encoding;
    }

    public int byteLength() {
        return tstring.byteLength(encoding.tencoding);
    }

    /** should only be used for debugging */
    @Override
    public String toString() {
        return rope.toString();
    }

    public Encoding getJCoding() {
        assert encoding.jcoding == rope.encoding;
        return encoding.jcoding;
    }

    // region RubyStringLibrary messages
    @ExportMessage
    public RubyEncoding getEncoding() {
        return encoding;
    }

    @ExportMessage
    protected boolean isRubyString() {
        return true;
    }

    @ExportMessage
    protected Rope getRope() {
        return rope;
    }

    @ExportMessage
    protected AbstractTruffleString getTString() {
        return tstring;
    }

    @ExportMessage
    protected String getJavaString() {
        return RopeOperations.decodeRope(rope);
    }
    // endregion

    // region RubyLibrary messages
    @ExportMessage
    public void freeze() {
        frozen = true;
    }

    @ExportMessage
    public boolean isFrozen() {
        return frozen;
    }
    // endregion

    // region String messages
    @ExportMessage
    protected boolean isString() {
        return true;
    }

    @ExportMessage
    protected String asString(
            @Cached ToJavaStringNode toJavaStringNode) {
        return toJavaStringNode.executeToJavaString(this);
    }
    // endregion

}
