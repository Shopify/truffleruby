/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.core.kernel;

import org.truffleruby.language.RubyContextSourceNode;
import org.truffleruby.language.dispatch.NewDispatchHeadNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

import static org.truffleruby.language.dispatch.DispatchConfiguration.PRIVATE;

public class KernelGetsNode extends RubyContextSourceNode {

    @Child private NewDispatchHeadNode callGetsNode;

    @Override
    public Object execute(VirtualFrame frame) {
        if (callGetsNode == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            callGetsNode = insert(NewDispatchHeadNode.create(PRIVATE));
        }
        return callGetsNode.dispatch(frame, coreLibrary().kernelModule, "gets", null, EMPTY_ARGUMENTS);
    }

}
