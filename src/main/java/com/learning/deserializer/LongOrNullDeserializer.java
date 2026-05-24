package com.learning.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * 自定义反序列化器：将字符串ID转换为Long，如果无法转换则返回null
 * 用于处理前端发送的"new_xxx"格式的临时ID
 */
public class LongOrNullDeserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if (value == null || value.isEmpty()) {
            return null;
        }
        // 如果是"new_"开头的临时ID，返回null
        if (value.startsWith("new_")) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            // 如果无法转换为Long，返回null
            return null;
        }
    }
}
