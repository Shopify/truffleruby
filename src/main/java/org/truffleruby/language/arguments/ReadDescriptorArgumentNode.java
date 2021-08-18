package org.truffleruby.language.arguments;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.truffleruby.language.RubyContextSourceNode;
import org.truffleruby.language.locals.WriteLocalVariableNode;
import org.truffleruby.parser.MethodTranslator;

@NodeChild("descriptor")
public abstract class ReadDescriptorArgumentNode extends RubyContextSourceNode {

    @Child private ReadUserKeywordsHashNode readHash;

    private MethodTranslator translator;
    private String[] expected;

    public static ReadDescriptorArgumentNode create(int minimum, MethodTranslator translator, String[] expected) {
        return ReadDescriptorArgumentNodeGen.create(new ReadUserKeywordsHashNode(minimum), translator, expected, new ReadDescriptorNode());
    }

    protected ReadDescriptorArgumentNode(ReadUserKeywordsHashNode readHash, MethodTranslator translator, String[] expected) {
        this.readHash = readHash;
        this.translator = translator;
        this.expected = expected;
    }

    public abstract Object execute(VirtualFrame frame, KeywordArgumentsDescriptor descriptor);

    @Specialization(guards = "descriptor == cachedDescriptor", limit = "4")
    protected Object cached(VirtualFrame frame, KeywordArgumentsDescriptor descriptor,
                         @Cached("descriptor") KeywordArgumentsDescriptor cachedDescriptor) {
        // Do something more intelligent here than use the uncached here... we now statically know the descriptor!
        return uncached(frame, cachedDescriptor);
    }

    @Specialization(replaces = "cached")
    protected Object uncached(VirtualFrame frame, KeywordArgumentsDescriptor descriptor) {
        // This will be a specialisation thing
        final String[] keywords = descriptor.getKeywords();
        if (keywords.length == 0) {
            return null;
        }

        // I have no idea why this is needed... if there is no actual hash don't unload anything...
        if (readHash.execute(frame) == null) {
            return null;
        }

        Object[] values = RubyArguments.getKeywordArgumentsValues(frame);
        int keywordArgValueIndex = values.length - keywords.length;

        for (int n = 0; n < keywords.length; n++) {
            // Optimized way of attaining kw and value
            String keyword = keywords[n];
            Object optimizedValue = values[keywordArgValueIndex];

            keywordArgValueIndex++;

            // Expected means that the callee expects this keyword argument
            if (expected(keyword)) {
                // Store the optimizedValue into the local var with WriteLocalVariableNode
                final FrameSlot slot = translator.getEnvironment().declareVar(keyword);
                final ReadDescriptorArgumentValueNode valueNode = new ReadDescriptorArgumentValueNode(optimizedValue);
                final WriteLocalVariableNode writeNode = new WriteLocalVariableNode(slot, valueNode);
                insert(writeNode);
                writeNode.execute(frame);
            } else {
                // TODO - if there's a kwrest it'll take this value (and it's the job of ReadKeywordRestArgumentNode to handle that)
                // but if there isn't a kwrest, then at this point we need to report the error! This requirement won't
                // become pressing until we stop putting descriptor argument values into the hash as well.
            }
        }

        return null;
    }

    private boolean expected(String keyword) {
        for (String expectedKeyword : expected) {
            if (keyword.equals(expectedKeyword)) {
                return true;
            }
        }

        return false;
    }

}
