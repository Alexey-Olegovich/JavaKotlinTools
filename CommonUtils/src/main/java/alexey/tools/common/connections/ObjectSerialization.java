package alexey.tools.common.connections;

import alexey.tools.common.collections.ObjectStorage;
import alexey.tools.common.identity.SimpleTypeFactory;
import alexey.tools.common.identity.TypeProperties;
import alexey.tools.common.converters.ByteBufferIO;
import org.jetbrains.annotations.NotNull;
import java.nio.ByteBuffer;

public class ObjectSerialization implements Serialization<Object, Object> {

    protected final ObjectStorage<EncoderRegistration> encoders = new ObjectStorage<>();
    protected final SimpleTypeFactory encoderTypes = new SimpleTypeFactory();

    protected final ObjectStorage<DecoderRegistration> decoders = new ObjectStorage<>();
    protected final SimpleTypeFactory decoderTypes = new SimpleTypeFactory();



    @SuppressWarnings("unchecked")
    public <T> EncoderRegistration<T> registerEncoder(Class<T> encoderClass, Encoder<? super T> encoder) {
        TypeProperties<T> typeProperties = encoderTypes.obtain(encoderClass);
        final int index = typeProperties.id;
        encoders.ensureSpace(index);
        EncoderRegistration<T> registration = encoders.get(index);
        if (registration != null) return registration;
        registration = new EncoderRegistration<>(typeProperties, encoder);
        encoders.justSet(index, registration);
        return registration;
    }

    @SuppressWarnings("unchecked")
    public <T> DecoderRegistration<T> registerDecoder(Class<T> decoderClass, Decoder<T> decoder) {
        TypeProperties<T> typeProperties = decoderTypes.obtain(decoderClass);
        final int index = typeProperties.id;
        decoders.ensureSpace(index);
        DecoderRegistration<T> registration = decoders.get(index);
        if (registration != null) return registration;
        registration = new DecoderRegistration<>(typeProperties, decoder);
        decoders.justSet(index, registration);
        return registration;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public DecoderRegistration<Object> getDecoderRegistration(final int id) {
        if (id < 0) throw new IllegalStateException("Bad id (" + id + ")!");
        if (id >= decoders.size())
            throw new IllegalStateException("Unregistered id (" + id + ")");
        final DecoderRegistration registration = decoders.get(id);
        if (registration == null)
            throw new IllegalStateException("Unregistered id (" + id + ")");
        return registration;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <T> EncoderRegistration<T> getEncoderRegistration(final Class<T> type) {
        final TypeProperties<?> typeProperties = encoderTypes.get(type);
        if (typeProperties == null)
            throw new IllegalStateException("Unregistered class (" + type.getName() + ")!");
        final EncoderRegistration<T> registration = encoders.get(typeProperties.id);
        if (registration == null)
            throw new IllegalStateException("Encoder isn't registered (for " + type.getName() + ")!");
        return registration;
    }

    public int registeredEncoderTypes() {
        return encoderTypes.size();
    }

    public <T> TypeProperties<T> registerEncoderClass(Class<T> encoderClass) {
        return encoderTypes.obtain(encoderClass);
    }

    public int registeredDecoderTypes() {
        return decoderTypes.size();
    }

    public <T> TypeProperties<T> registerDecoderClass(Class<T> decoderClass) {
        return decoderTypes.obtain(decoderClass);
    }



    @Override
    @SuppressWarnings("unchecked")
    public void write(final Connection<Object, Object> connection,
                      final ByteBuffer output, @NotNull final Object source) {
        EncoderRegistration registration = getEncoderRegistration(source.getClass());
        ByteBufferIO.writeInt(output, registration.typeProperties.id, true);
        registration.encoder.encode(this, connection, output, source);
    }

    @Override
    public Object read(final Connection<Object, Object> connection, final ByteBuffer input) {
        return getDecoderRegistration(ByteBufferIO.readInt(input, true)).decode(this, connection, input);
    }



    public static class EncoderRegistration<T> {
        final public TypeProperties<T> typeProperties;
        final public Encoder<? super T> encoder;

        public EncoderRegistration(final TypeProperties<T> typeProperties, final Encoder<? super T> encoder) {
            this.typeProperties = typeProperties;
            this.encoder = encoder;
        }
    }

    public static class DecoderRegistration<T> {
        final public Decoder<T> decoder;
        final public TypeProperties<T> typeProperties;

        public DecoderRegistration(final TypeProperties<T> typeProperties, final Decoder<T> decoder) {
            this.typeProperties = typeProperties;
            this.decoder = decoder;
        }

        public T decode(ObjectSerialization serialization,
                        Connection          connection,
                        ByteBuffer          source) {
            return decoder.decode(serialization, connection, source, typeProperties);
        }
    }

    public interface Encoder <T> {
        void encode(ObjectSerialization serialization,
                    Connection          connection,
                    ByteBuffer          destination,
                    T                   source);
    }

    public interface Decoder <T> {
        T decode(ObjectSerialization serialization,
                 Connection          connection,
                 ByteBuffer          source,
                 TypeProperties<T>   typeProperties);
    }
}
