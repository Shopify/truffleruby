/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.language.objects.shared;

import org.truffleruby.language.RubyBaseNode;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;

@GenerateUncached
public abstract class PropagateSharingNode extends RubyBaseNode {

    public static PropagateSharingNode create() {
        return PropagateSharingNodeGen.create();
    }

    public abstract void executePropagate(DynamicObject source, Object value);

    @Specialization(guards = "!isSharedNode.executeIsShared(source)", limit = "1")
    public void propagateNotShared(DynamicObject source, Object value,
            @Cached @Shared("isSharedNode") IsSharedNode isSharedNode) {
        // do nothing
    }

    @Specialization(guards = "isSharedNode.executeIsShared(source)", limit = "1")
    public void propagateShared(DynamicObject source, Object value,
            @Cached @Shared("isSharedNode") IsSharedNode isSharedNode,
            @Cached WriteBarrierNode writeBarrierNode) {
        writeBarrierNode.executeWriteBarrier(value);
    }
}
