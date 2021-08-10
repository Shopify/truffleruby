package org.truffleruby.language.arguments;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import org.truffleruby.RubyLanguage;
import org.truffleruby.collections.PEBiFunction;
import org.truffleruby.core.hash.RubyHash;
import org.truffleruby.core.hash.library.EmptyHashStore;
import org.truffleruby.core.hash.library.HashStoreLibrary;
import org.truffleruby.core.symbol.RubySymbol;
import org.truffleruby.language.RubyContextSourceNode;

public abstract class ReadDescriptorArgumentNode extends RubyContextSourceNode implements PEBiFunction {

    @Child private ReadUserKeywordsHashNode readHash;
    public static ReadDescriptorArgumentNode create(int minimum) {
        return ReadDescriptorArgumentNodeGen.create(new ReadUserKeywordsHashNode(minimum));
    }

    protected ReadDescriptorArgumentNode(ReadUserKeywordsHashNode readHash) {
        this.readHash = readHash;
    }

    @Override
    public final Object execute(VirtualFrame frame) {
        return execute(frame, readHash.execute(frame));
    }

    public abstract Object execute(VirtualFrame frame, RubyHash hash);

    @Specialization(guards = "hash == null")
    protected Object nullHash(VirtualFrame frame, RubyHash hash) {
        return null;
    }

    @Specialization(guards = "hash != null", limit = "3")
    protected Object lookupKeywordInDescriptor(VirtualFrame frame, RubyHash hash,
                                         @CachedLibrary("getHashStore(hash)") HashStoreLibrary hashes) {
        final String[] keywords = RubyArguments.getKeywordArgumentsDescriptor(frame).getKeywords();
        Object[] values = RubyArguments.getKeywordArgumentsValues(frame);
        int keywordArgValueIndex = values.length - keywords.length;

        for (int n = 0; n < keywords.length; n++) {

            // Optimized way of attaining kw and value
            String keyword = keywords[n];
            Object optimizedValue = values[keywordArgValueIndex];

            // Old way of attaining value
            Object actualValue = hashes.lookupOrDefault(hash.store, frame, hash, keywordAsSymbols(getLanguage(), keyword), this);

            // Compare the two values
            assert optimizedValue == actualValue : "the optimizedValue: " + optimizedValue + " actualValue: " + actualValue;
            keywordArgValueIndex++;

            // TODO: Later, we'll store the optimizedValue into the local var with WriteLocalVariableNode
        }

        // TODO: In the future we'll then also want to store default values for keywords not in the descriptor at this point
        return null;
    }

    static RubySymbol keywordAsSymbols(RubyLanguage language, String keyword) {
        final RubySymbol[] symbols = new RubySymbol[1];
        symbols[0] = language.getSymbol(keyword);
        return symbols[0];
    }

    // TODO: Copied from `ReadKeywordArgumentNode`...
    // Workaround for Truffle where the library expression is tried before the guard, resulting in a NPE if
    // hash is null. The guard will fail afterwards anyway, so return a valid store in that case.
    protected Object getHashStore(RubyHash hash) {
        return hash == null ? EmptyHashStore.NULL_HASH_STORE : hash.store;
    }

    // TODO: We need to check if the value from the current implementation
    //   matches with what we get from the descriptor and flatten array values
    protected static void checkDescriptorMatchesRubyHash(Object expectedValue) {
        return;
    }

    // This is required to use the #lookupOrDefault function to check the
    // actual keyword argument value
    @Override
    public Object accept(Frame frame, Object hash, Object key) {
        return null;
    }
}
