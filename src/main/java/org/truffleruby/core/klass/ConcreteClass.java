package org.truffleruby.core.klass;

public class ConcreteClass implements ClassLike {

    private final RubyClass concreteClass;

    public ConcreteClass(RubyClass concreteClass) {
        this.concreteClass = concreteClass;
    }

    @Override
    public RubyClass reify(Object object) {
        throw new UnsupportedOperationException();
    }

    public RubyClass getConcrete() {
        return concreteClass;
    }

}
