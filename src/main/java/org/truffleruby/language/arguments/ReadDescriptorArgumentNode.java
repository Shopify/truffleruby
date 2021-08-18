package org.truffleruby.language.arguments;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.truffleruby.language.RubyContextSourceNode;
import org.truffleruby.parser.MethodTranslator;

import java.util.Map;

@NodeChild("descriptor")
public abstract class ReadDescriptorArgumentNode extends RubyContextSourceNode {

    @Child private ReadUserKeywordsHashNode readHash;

    private MethodTranslator translator;
    private Map<String, FrameSlot> expected;

    public static ReadDescriptorArgumentNode create(int minimum, MethodTranslator translator, Map<String, FrameSlot> expected) {
        return ReadDescriptorArgumentNodeGen.create(new ReadUserKeywordsHashNode(minimum), translator, expected, new ReadDescriptorNode());
    }

    protected ReadDescriptorArgumentNode(ReadUserKeywordsHashNode readHash, MethodTranslator translator, Map<String, FrameSlot> expected) {
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
            FrameSlot frameSlot = expected.get(keyword);
            if (frameSlot != null) {
                // Store the optimizedValue into the local var
                frame.setObject(frameSlot, optimizedValue);
            } else {
                // TODO - if there's a kwrest it'll take this value (and it's the job of ReadKeywordRestArgumentNode to handle that)
                // but if there isn't a kwrest, then at this point we need to report the error! This requirement won't
                // become pressing until we stop putting descriptor argument values into the hash as well.
            }
        }

        return null;
    }

}
