package org.truffleruby.language.arguments;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.truffleruby.language.RubyContextSourceNode;

public class WriteMissingKeywordArgumentsNode extends RubyContextSourceNode {

    private final FrameSlot[] frameSlots;
    private final Object missingValue;

    public WriteMissingKeywordArgumentsNode(FrameSlot[] frameSlots, Object missingValue) {
        this.frameSlots = frameSlots;
        this.missingValue = missingValue;
    }

    @Override
    @ExplodeLoop
    public Object execute(VirtualFrame frame) {
        for (FrameSlot frameSlot : frameSlots) {
            frame.setObject(frameSlot, missingValue);
        }
        return null;
    }

}
