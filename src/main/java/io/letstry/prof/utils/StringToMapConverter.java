package io.letstry.prof.utils;

import java.io.IOException;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class StringToMapConverter implements Converter<String, Map<Integer, String>> {

    @Override
    public Map<Integer, String> convert(String source) {
        try {
            return new ObjectMapper().readValue(source, new TypeReference<Map<Integer, String>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}