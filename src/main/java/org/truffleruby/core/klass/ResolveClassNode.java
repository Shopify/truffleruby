/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.core.klass;

import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import org.truffleruby.language.RubyBaseNode;

@GenerateUncached
public abstract class ResolveClassNode extends RubyBaseNode {

    public static ResolveClassNode create() {
        return ResolveClassNodeGen.create();
    }

    public static ResolveClassNode getUncached() {
        return ResolveClassNodeGen.getUncached();
    }

    public abstract RubyClass executeResolve(RubyClass classLike);

    public RubyClass resolveMetaClass(RubyClass classLike) {
        return executeResolve(classLike);
    }

    public RubyClass resolveLogicalClass(RubyClass classLike) {
        return executeResolve(classLike).nonSingletonClass;
    }

    @Specialization
    protected RubyClass resolve(RubyClass klass) {
        return klass;
    }

}
