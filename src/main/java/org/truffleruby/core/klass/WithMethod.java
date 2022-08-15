package org.truffleruby.core.klass;

import com.oracle.truffle.api.nodes.Node;
import org.truffleruby.RubyContext;
import org.truffleruby.language.Visibility;
import org.truffleruby.language.methods.InternalMethod;

public class WithMethod implements ClassLike{

    public final ClassLike underlying;

    public final InternalMethod method;
    public final RubyContext context;
    public final Visibility visibility;
    public final Node node;

    public WithMethod(ClassLike underlying, InternalMethod method, RubyContext context, Visibility visibility, Node node) {
        this.underlying = underlying;
        this.method = method;
        this.context = context;
        this.visibility = visibility;
        this.node = node;
    }

    @Override
    public RubyClass reify(Object object) {
        throw new UnsupportedOperationException();
    }
}
