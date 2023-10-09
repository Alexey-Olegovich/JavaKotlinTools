package alexey.tools.common.connections;

import alexey.tools.common.converters.ByteBufferIO;

import java.nio.ByteBuffer;

public class StringSerialization implements Serialization<String, String> {

    private final ByteBufferIO temp = new ByteBufferIO();



    @Override
    public void write(Connection<String, String> connection, ByteBuffer output, String source) {
        ByteBufferIO.writeUTF8(output, source);
    }

    @Override
    public String read(Connection<String, String> connection, ByteBuffer input) {
        temp.setByteBuffer(input);
        return temp.readString();
    }
}
