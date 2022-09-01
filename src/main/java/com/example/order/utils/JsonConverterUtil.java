package com.example.order.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachineContext;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.apache.logging.log4j.util.Strings.EMPTY;


@Slf4j
@UtilityClass
public class JsonConverterUtil {

    public static String serialize(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (IOException e) {
            log.error("serializeObject -> Can not convert object: {}", object);
        }

        return EMPTY;
    }

    public static <T> T deserialize(String serializedObject, Class<T> valueType) {
        try {
            return getObjectMapper().readValue(serializedObject, valueType);
        } catch (IOException e) {
            log.error("deserializeObject -> Can not convert object: {}", serializedObject);
        }
        return null;
    }

    public static <T> List<T> deserializeList(String serializedObject, Class<T> valueType) {
        try {
            final ObjectMapper objectMapper = getObjectMapper();
            return objectMapper.readValue(serializedObject, objectMapper.getTypeFactory().constructCollectionType(List.class, valueType));
        } catch (IOException e) {
            log.error("deserializeObjectList -> Can not convert object: {}", serializedObject);
        }
        return Collections.emptyList();
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        return mapper;
    }
}
