package org.truffleruby.language.arguments;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.truffleruby.language.RubyContextSourceNode;

public class WriteMissingKeywordArgumentsNode extends RubyContextSourceNode {

    public static final Object MISSING = new Object();

    private final FrameSlot[] frameSlots;

    public WriteMissingKeywordArgumentsNode(FrameSlot[] frameSlots) {
        this.frameSlots = frameSlots;
    }

    @Override
    @ExplodeLoop
    public Object execute(VirtualFrame frame) {
        for (FrameSlot frameSlot : frameSlots) {
            frame.setObject(frameSlot, MISSING);
        }
        return null;
    }

}
