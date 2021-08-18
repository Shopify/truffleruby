package org.truffleruby.language.arguments;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.truffleruby.language.RubyContextSourceNode;

public class ReadDescriptorNode extends RubyContextSourceNode {
    @Override
    public Object execute(VirtualFrame frame) {
        return RubyArguments.getKeywordArgumentsDescriptor(frame);
    }
}
