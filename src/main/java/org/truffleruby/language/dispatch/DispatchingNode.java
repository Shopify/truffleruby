/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.language.dispatch;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.nodes.NodeInterface;
import org.truffleruby.language.arguments.keywords.KeywordDescriptor;

public interface DispatchingNode extends NodeInterface {

    public Object call(Object receiver, String method, Object... arguments);

    public Object callWithBlock(Object receiver, String method, Object block, Object... arguments);

    public Object callWithBlockUsingKwd(Object instance, String initialize, KeywordDescriptor kwd, Object block, Object[] args);

    public Object dispatch(Frame frame, Object receiver, String methodName, Object block, Object[] arguments);

}
