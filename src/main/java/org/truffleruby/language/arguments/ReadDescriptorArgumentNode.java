package org.truffleruby.language.arguments;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import org.truffleruby.RubyLanguage;
import org.truffleruby.collections.PEBiFunction;
import org.truffleruby.core.hash.RubyHash;
import org.truffleruby.core.hash.library.EmptyHashStore;
import org.truffleruby.core.hash.library.HashStoreLibrary;
import org.truffleruby.core.symbol.RubySymbol;
import org.truffleruby.language.RubyContextSourceNode;
import org.truffleruby.language.RubyNode;
import org.truffleruby.language.locals.WriteLocalVariableNode;
import org.truffleruby.parser.MethodTranslator;
import org.truffleruby.parser.ast.ArgsParseNode;
import org.truffleruby.parser.ast.OptArgParseNode;
import org.truffleruby.parser.ast.ParseNode;

public abstract class ReadDescriptorArgumentNode extends RubyContextSourceNode implements PEBiFunction {

    private ArgsParseNode argsNode;
    @Child private ReadUserKeywordsHashNode readHash;
    private MethodTranslator translator;

    public static ReadDescriptorArgumentNode create(ArgsParseNode argsNode, int minimum, MethodTranslator translator) {
        return ReadDescriptorArgumentNodeGen.create(argsNode, new ReadUserKeywordsHashNode(minimum), translator);
    }

    protected ReadDescriptorArgumentNode(ArgsParseNode argsNode, ReadUserKeywordsHashNode readHash, MethodTranslator translator) {
        this.argsNode = argsNode;
        this.readHash = readHash;
        this.translator = translator;
    }

    public abstract Object execute(VirtualFrame frame);

    @Specialization
    protected Object lookupKeywordInDescriptor(VirtualFrame frame) {
        CompilerDirectives.transferToInterpreter();

        final String[] keywords = RubyArguments.getKeywordArgumentsDescriptor(frame).getKeywords();
        Object[] values = RubyArguments.getKeywordArgumentsValues(frame);
        int keywordArgValueIndex = values.length - keywords.length;

        if (keywords.length > 0) {
            // Only read the hash if there are keywords arguments
            final RubyHash hash = readHash.execute(frame);

            // Previously this was its own specialization
            // We want to keep it simple
            if (hash == null) {
                return null;
            }

            for (int n = 0; n < keywords.length; n++) {

                // Optimized way of attaining kw and value
                String keyword = keywords[n];
                Object optimizedValue = values[keywordArgValueIndex];

                // Old way of attaining value
                Object actualValue = HashStoreLibrary.getUncached().lookupOrDefault(hash.store, frame, hash, keywordAsSymbols(getLanguage(), keyword), this);

                // Compare the two values
                assert optimizedValue == actualValue : "the optimizedValue: " + optimizedValue + " actualValue: " + actualValue;
                keywordArgValueIndex++;

                // Store the optimizedValue into the local var with WriteLocalVariableNode
                final FrameSlot slot = translator.getEnvironment().declareVar(keyword);
                final ReadDescriptorArgumentValueNode valueNode = new ReadDescriptorArgumentValueNode(optimizedValue);
                final WriteLocalVariableNode writeNode = new WriteLocalVariableNode(slot, valueNode);
                insert(writeNode);
                writeNode.execute(frame);
            }
        }
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
