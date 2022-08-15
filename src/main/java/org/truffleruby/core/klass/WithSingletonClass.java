package org.truffleruby.core.klass;

import com.oracle.truffle.api.nodes.Node;
import org.truffleruby.RubyContext;
import org.truffleruby.language.RubyDynamicObject;
import org.truffleruby.language.objects.SingletonClassNode;

public class WithSingletonClass implements ClassLike {


    private final RubyContext context;
    private final Node node;

    public WithSingletonClass(RubyContext context, Node node){
        this.context = context;
        this.node = node;
    }

    @Override
    public RubyClass reify(Object object) {
        return SingletonClassNode.getSingletonClassForInstance(context, (RubyDynamicObject) object, node);
    }

//    public SourceSection getSourceSection() {
//        return this.node.getEncapsulatingSourceSection();
//    }

}
