package org.truffleruby.language.arguments;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;
import org.truffleruby.core.hash.RubyHash;
import org.truffleruby.core.hash.library.HashStoreLibrary;
import org.truffleruby.core.symbol.RubySymbol;
import org.truffleruby.language.RubyContextSourceNode;
import org.truffleruby.language.RubyNode;
import org.truffleruby.language.locals.WriteLocalVariableNode;

public class CheckKeywordArgumentNode extends RubyContextSourceNode {

    private final FrameSlot slot;
    private final int minimum;
    private final RubySymbol name;

    protected @Child ReadUserKeywordsHashNode readUserKeywordsHashNode;
    protected @Child HashStoreLibrary hashStoreLibrary;
    protected @Child WriteLocalVariableNode writeDefaultNode;

    private final BranchProfile foundHashProfile = BranchProfile.create();

    public CheckKeywordArgumentNode(FrameSlot slot, int minimum, RubyNode defaultValue, RubySymbol name) {
        this.slot = slot;
        this.minimum = minimum;
        this.name = name;
        writeDefaultNode = new WriteLocalVariableNode(slot, defaultValue);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        // Get the current value of the parameter.

        final Object value = frame.getValue(slot);

        // If it has a value, then we're done.

        if (value != WriteMissingKeywordArgumentsNode.MISSING) {
            return null;
        }

        // If it doesn't have a value, then we should try looking up in the full keyword argument hash, if there is one.

        if (readUserKeywordsHashNode == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            readUserKeywordsHashNode = insert(new ReadUserKeywordsHashNode(minimum));
        }

        final RubyHash hash = readUserKeywordsHashNode.execute(frame);

        if (hash != null) {
            if (hashStoreLibrary == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                hashStoreLibrary = insert(HashStoreLibrary.createDispatched());
            }

            final Object valueFromHash = hashStoreLibrary.lookupOrDefault(hash.store, frame, hash, name, (f, h, k) -> null);

            if (valueFromHash != null) {
                // If we found a value in the hash, then store it in the local and we're done.

                foundHashProfile.enter();
                frame.setObject(slot, valueFromHash);
                return null;
            }
        }

        // We didn't find a value in the local or we didn't have a hash or we didn't find a value in the hash - run
        // the default expression, which may either assign a value to the local, or it may raise an exception in
        // the case of a required keyword argument.

        writeDefaultNode.execute(frame);
        return null;
    }

}
