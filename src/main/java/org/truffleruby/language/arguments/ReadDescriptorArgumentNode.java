package org.truffleruby.language.arguments;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.truffleruby.language.RubyContextSourceNode;

import java.util.Map;

@NodeChild("descriptor")
public abstract class ReadDescriptorArgumentNode extends RubyContextSourceNode {

    @Child private ReadUserKeywordsHashNode readHash;

    private Map<String, FrameSlot> expected;

    public static ReadDescriptorArgumentNode create(int minimum, Map<String, FrameSlot> expected) {
        return ReadDescriptorArgumentNodeGen.create(
                new ReadUserKeywordsHashNode(minimum),
                expected,
                new ReadDescriptorNode());
    }

    protected ReadDescriptorArgumentNode(ReadUserKeywordsHashNode readHash, Map<String, FrameSlot> expected) {
        this.readHash = readHash;
        this.expected = expected;
    }

    public abstract Object execute(VirtualFrame frame, KeywordArgumentsDescriptor descriptor);

    @ExplodeLoop
    @Specialization(guards = "descriptor == cachedDescriptor", limit = "4")
    protected Object cached(VirtualFrame frame, KeywordArgumentsDescriptor descriptor,
                            @Cached("descriptor") KeywordArgumentsDescriptor cachedDescriptor,
                            @Cached(value = "getSlots(cachedDescriptor)", dimensions = 1) FrameSlot[] descriptorSlots) {
        //checkArity(frame, cachedDescriptor);

        // Quick exit for an empty descriptor.

        if (cachedDescriptor == KeywordArgumentsDescriptor.EMPTY) {
            return null;
        }

        // I have no idea why this is needed... if there is no actual hash don't unload anything...

        if (readHash.execute(frame) == null) {
            return null;
        }

        // For each keyword in the descriptor, store its value in a local, as long as it's expected.

        for (int n = 0; n < cachedDescriptor.getLength(); n++) {
            final FrameSlot frameSlot = descriptorSlots[n];

            if (frameSlot != null) {
                frame.setObject(frameSlot, RubyArguments.getKeywordArgumentsValue(frame, n));
            } else {
                // TODO - if there's a kwrest it'll take this value (and it's the job of ReadKeywordRestArgumentNode to handle that)
                // but if there isn't a kwrest, then at this point we need to report the error! This requirement won't
                // become pressing until we stop putting descriptor argument values into the hash as well.
            }
        }

        return null;
    }

    @Specialization(replaces = "cached")
    protected Object uncached(VirtualFrame frame, KeywordArgumentsDescriptor descriptor) {
        //checkArity(frame, descriptor);

        // Quick exit for an empty descriptor.

        if (descriptor == KeywordArgumentsDescriptor.EMPTY) {
            return null;
        }

        // I have no idea why this is needed... if there is no actual hash don't unload anything...

        if (readHash.execute(frame) == null) {
            return null;
        }

        // For each keyword in the descriptor, store its value in a local, as long as it's expected.

        for (int n = 0; n < descriptor.getLength(); n++) {
            final String keyword = descriptor.getKeyword(n);
            final FrameSlot frameSlot = expected(keyword);

            if (frameSlot != null) {
                frame.setObject(frameSlot, RubyArguments.getKeywordArgumentsValue(frame, n));
            } else {
                // TODO - if there's a kwrest it'll take this value (and it's the job of ReadKeywordRestArgumentNode to handle that)
                // but if there isn't a kwrest, then at this point we need to report the error! This requirement won't
                // become pressing until we stop putting descriptor argument values into the hash as well.
            }
        }

        return null;
    }

    protected FrameSlot[] getSlots(KeywordArgumentsDescriptor descriptor) {
        FrameSlot[] slots = new FrameSlot[descriptor.getLength()];
        for (int n = 0; n < descriptor.getLength(); n++) {
            slots[n] = expected(descriptor.getKeyword(n));
        }

        return slots;
    }

    @TruffleBoundary
    private FrameSlot expected(String keyword) {
        return expected.get(keyword);
    }

}
