package com.rbkmoney.mg.event.sink.converter;

public interface BinaryConverter<T> {

    T convert(byte[] bin, Class<T> clazz);

}
