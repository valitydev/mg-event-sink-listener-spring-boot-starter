package dev.vality.mg.event.sink.converter;

public interface BinaryConverter<T> {

    T convert(byte[] bin, Class<T> clazz);

}
