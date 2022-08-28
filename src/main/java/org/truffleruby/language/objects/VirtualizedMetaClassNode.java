/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.language.objects;

import com.oracle.truffle.api.HostCompilerDirectives.InliningCutoff;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import org.truffleruby.core.encoding.RubyEncoding;
import org.truffleruby.core.klass.ClassLike;
import org.truffleruby.core.klass.ClassLike;
import org.truffleruby.core.klass.ConcreteClass;
import org.truffleruby.core.numeric.RubyBignum;
import org.truffleruby.core.range.RubyIntOrLongRange;
import org.truffleruby.core.regexp.RubyRegexp;
import org.truffleruby.core.string.ImmutableRubyString;
import org.truffleruby.core.symbol.RubySymbol;
import org.truffleruby.language.Nil;
import org.truffleruby.language.NoImplicitCastsToLong;
import org.truffleruby.language.RubyBaseNode;
import org.truffleruby.language.RubyDynamicObject;

@GenerateUncached
@TypeSystemReference(NoImplicitCastsToLong.class)
public abstract class VirtualizedMetaClassNode extends RubyBaseNode {

    public static VirtualizedMetaClassNode create() {
        return VirtualizedMetaClassNodeGen.create();
    }

    public static VirtualizedMetaClassNode getUncached() {
        return VirtualizedMetaClassNodeGen.getUncached();
    }

    public abstract ClassLike execute(Object value);

    // Cover all primitives, nil and symbols

    @Specialization(guards = "value")
    protected ClassLike metaClassTrue(boolean value) {
        return new ConcreteClass( coreLibrary().trueClass);
    }

    @Specialization(guards = "!value")
    protected ClassLike metaClassFalse(boolean value) {
        return new ConcreteClass( coreLibrary().falseClass);
    }

    @Specialization
    protected ClassLike metaClassInt(int value) {
        return  new ConcreteClass(coreLibrary().integerClass );
    }

    @Specialization
    protected ClassLike metaClassLong(long value) {
        return new ConcreteClass( coreLibrary().integerClass );
    }

    @Specialization
    protected ClassLike metaClassBignum(RubyBignum value) {
        return new ConcreteClass( coreLibrary().integerClass );
    }

    @Specialization
    protected ClassLike metaClassDouble(double value) {
        return  new ConcreteClass(coreLibrary().floatClass);
    }

    @Specialization
    protected ClassLike metaClassNil(Nil value) {
        return new ConcreteClass(coreLibrary().nilClass);
    }

    @Specialization
    protected ClassLike metaClassSymbol(RubySymbol value) {
        return  new ConcreteClass(coreLibrary().symbolClass );
    }

    @Specialization
    protected ClassLike metaClassEncoding(RubyEncoding value) {
        return  new ConcreteClass(coreLibrary().encodingClass );
    }

    @Specialization
    protected ClassLike metaClassImmutableString(ImmutableRubyString value) {
        return  new ConcreteClass(coreLibrary().stringClass );
    }

    @Specialization
    protected ClassLike metaClassRegexp(RubyRegexp value) {
        return  new ConcreteClass(coreLibrary().regexpClass );
    }

    @Specialization
    protected ClassLike metaClassIntRange(RubyIntOrLongRange value) {
        return  new ConcreteClass(coreLibrary().rangeClass);
     }

    // Cover all RubyDynamicObject cases with cached and uncached

    @Specialization(
            guards = { "object == cachedObject"/*, "metaClass.isSingleton"*/ },
            limit = "getIdentityCacheContextLimit()")
    protected ClassLike singletonClassCached(RubyDynamicObject object,
            @Cached("object") RubyDynamicObject cachedObject,
            @Cached("object.metaClass") ClassLike metaClass) {
        return metaClass;
    }

    @Specialization(replaces = "singletonClassCached")
    protected ClassLike metaClassObject(RubyDynamicObject object) {
        return object.metaClass;
    }

    // Foreign object
    @InliningCutoff
    @Specialization(guards = "isForeignObject(object)")
    protected ClassLike metaClassForeign(Object object,
            @Cached ForeignClassNode foreignClassNode) {
        return new ConcreteClass(foreignClassNode.execute(object));
    }

    protected int getCacheLimit() {
        return getLanguage().options.CLASS_CACHE;
    }

    protected int getIdentityCacheContextLimit() {
        return getLanguage().options.CONTEXT_SPECIFIC_IDENTITY_CACHE;
    }
}
