package pl.fejzu.persistence.communication;

public interface MessageCodec<T> {

    byte[] encode(T message);

    T decode(byte[] data);

    String getType();

    Class<T> getMessageType();
}
