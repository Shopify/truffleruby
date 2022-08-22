package org.truffleruby.core.klass;

import com.oracle.truffle.api.nodes.Node;
import org.truffleruby.RubyContext;

public class WithSingletonClass implements ClassLike {


    public final RubyContext context;
    public final Node node;

    public final RubyClass logicalClass;

    public WithSingletonClass(RubyContext context, Node node, RubyClass logicalClass){
        this.context = context;
        this.node = node;
        this.logicalClass = logicalClass;
    }

    @Override
    public RubyClass reify(Object object) {
        throw new UnsupportedOperationException();
    }

//    public SourceSection getSourceSection() {
//        return this.node.getEncapsulatingSourceSection();
//    }

}
