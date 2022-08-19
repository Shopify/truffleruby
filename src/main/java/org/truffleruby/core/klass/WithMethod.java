package org.truffleruby.core.klass;

import com.oracle.truffle.api.nodes.Node;
import org.truffleruby.RubyContext;
import org.truffleruby.core.module.RubyModule;
import org.truffleruby.language.Visibility;
import org.truffleruby.language.methods.InternalMethod;

import java.util.function.Function;
import java.util.function.Supplier;

public class WithMethod implements ClassLike{

    public final ClassLike underlying;

    public final Function<RubyModule, InternalMethod> method;
    public final RubyContext context;
    public final Visibility visibility;
    public final Node node;

    public WithMethod(ClassLike underlying, Function<RubyModule, InternalMethod> method, RubyContext context, Visibility visibility, Node node) {
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
