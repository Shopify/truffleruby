package org.truffleruby.language.arguments;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.truffleruby.RubyLanguage;
import org.truffleruby.collections.PEBiFunction;
import org.truffleruby.core.hash.RubyHash;
import org.truffleruby.core.hash.library.EmptyHashStore;
import org.truffleruby.core.hash.library.HashStoreLibrary;
import org.truffleruby.core.symbol.RubySymbol;
import org.truffleruby.language.RubyContextSourceNode;
import org.truffleruby.language.locals.WriteLocalVariableNode;
import org.truffleruby.parser.MethodTranslator;
import org.truffleruby.parser.ast.ArgsParseNode;

public abstract class ReadDescriptorArgumentNode extends RubyContextSourceNode implements PEBiFunction {

    @Child private ReadUserKeywordsHashNode readHash;
    private MethodTranslator translator;

    public static ReadDescriptorArgumentNode create(int minimum, MethodTranslator translator) {
        return ReadDescriptorArgumentNodeGen.create(new ReadUserKeywordsHashNode(minimum), translator);
    }

    protected ReadDescriptorArgumentNode(ReadUserKeywordsHashNode readHash, MethodTranslator translator) {
        this.readHash = readHash;
        this.translator = translator;
    }

    public abstract Object execute(VirtualFrame frame);

    @Specialization
    protected Object lookupKeywordInDescriptor(VirtualFrame frame) {
        // This will be a specialisation thing
        final String[] keywords = RubyArguments.getKeywordArgumentsDescriptor(frame).getKeywords();
        if (keywords.length == 0) {
            return null;
        }

        // This will go away when we stop comparing against the old way of doing keyword arguments
        final RubyHash hash = readHash.execute(frame);
        if (hash == null) {
            return null;
        }

        Object[] values = RubyArguments.getKeywordArgumentsValues(frame);
        int keywordArgValueIndex = values.length - keywords.length;

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

        return null;
    }

    static RubySymbol keywordAsSymbols(RubyLanguage language, String keyword) {
        final RubySymbol[] symbols = new RubySymbol[1];
        symbols[0] = language.getSymbol(keyword);
        return symbols[0];
    }

    // This is required to use the #lookupOrDefault function to check the
    // actual keyword argument value
    @Override
    public Object accept(Frame frame, Object hash, Object key) {
        return null;
    }
}
