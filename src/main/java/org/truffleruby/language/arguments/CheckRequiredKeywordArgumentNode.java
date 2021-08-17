package org.truffleruby.language.arguments;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;
import org.truffleruby.language.RubyContextSourceNode;
import org.truffleruby.language.RubyNode;
import org.truffleruby.language.locals.LocalVariableType;
import org.truffleruby.language.locals.ReadLocalVariableNode;
import org.truffleruby.language.locals.WriteLocalVariableNode;

public class CheckRequiredKeywordArgumentNode extends RubyContextSourceNode {

    @Child ReadLocalVariableNode readLocalVariableNode;
    @Child WriteLocalVariableNode writeLocalVariableNode;
    private final BranchProfile missingProfile = BranchProfile.create();

    public CheckRequiredKeywordArgumentNode(FrameSlot slot, RubyNode defaultValue) {
        readLocalVariableNode = new ReadLocalVariableNode(LocalVariableType.FRAME_LOCAL, slot);
        writeLocalVariableNode = new WriteLocalVariableNode(slot, defaultValue);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        final Object currentValue = readLocalVariableNode.execute(frame);

        if (currentValue == getLanguage().symbolTable.getSymbol("missing_default_keyword_argument")) {
            missingProfile.enter();
            writeLocalVariableNode.execute(frame);
        }

        return null;
    }

}
