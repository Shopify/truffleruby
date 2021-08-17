package org.truffleruby.language.arguments;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.truffleruby.collections.PEBiFunction;
import org.truffleruby.core.hash.RubyHash;
import org.truffleruby.core.hash.library.HashStoreLibrary;
import org.truffleruby.core.symbol.RubySymbol;
import org.truffleruby.language.RubyContextSourceNode;
import org.truffleruby.language.RubyNode;
import org.truffleruby.language.locals.LocalVariableType;
import org.truffleruby.language.locals.ReadLocalVariableNode;
import org.truffleruby.language.locals.WriteLocalVariableNode;

public class CheckKeywordArgumentNode extends RubyContextSourceNode implements PEBiFunction {

    private final RubySymbol name;

    @Child ReadLocalVariableNode readLocalVariableNode;
    @Child WriteLocalVariableNode writeDefaultNode;
    @Child ReadUserKeywordsHashNode readUserKeywordsHashNode;

    public CheckKeywordArgumentNode(RubySymbol name, FrameSlot slot, RubyNode defaultValue, int minimum) {
        this.name = name;
        readLocalVariableNode = new ReadLocalVariableNode(LocalVariableType.FRAME_LOCAL, slot);
        writeDefaultNode = new WriteLocalVariableNode(slot, defaultValue);
        readUserKeywordsHashNode = new ReadUserKeywordsHashNode(minimum);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        Object value = readLocalVariableNode.execute(frame);

        if (value == getLanguage().symbolTable.getSymbol("missing_default_keyword_argument")) {
            final RubyHash hash = readUserKeywordsHashNode.execute(frame);

            if (hash == null) {
                value = null;
            } else {
                HashStoreLibrary hashes = HashStoreLibrary.getUncached();
                value = hashes.lookupOrDefault(hash.store, frame, hash, name, this);
            }

            if (value == null) {
                writeDefaultNode.execute(frame);
            }
        }

        return null;
    }

    @Override
    public Object accept(Frame frame, Object hash, Object key) {
        return null;
    }
}
