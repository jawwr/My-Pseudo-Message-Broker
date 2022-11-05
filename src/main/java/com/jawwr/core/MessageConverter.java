package com.jawwr.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageConverter {

    public static Object deserializeFromJson(byte[] message, Class<?> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(message, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("Not a deserializable object");
        }
    }

    public static <T> byte[] serializeToJson(T obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(obj);
    }
}
