package org.truffleruby.core.format.write.bytes;

//package org.truffleruby.core.format.write.bytes;

import org.truffleruby.core.format.FormatNode;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeChild("value")
public abstract class WriteTruncatedBytesNode extends FormatNode {
//    private final Integer size;

//    public WriteTruncatedBytesNode(Integer size) {
//        this.size = size;
//    }

    @Specialization
    protected Object write(VirtualFrame frame, byte[] bytes) {
        writeBytes(frame, bytes);
        return null;
    }
//
//    private Object write(VirtualFrame frame, byte[] bytes) {
//        writeBytes(frame, bytes);

//        for (int n = 0; n < size; n++) {
//            writeByte(frame, bytes[n]);
//        }
//
//        return null;
//    }

}
