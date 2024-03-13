package Communication.Common.Serializers;

public interface IMessageSerializer<T> {
    public byte[] serialize(T object);
    public T deserialize(byte[] bytes);
}
