package com.art1001.supply.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LongToDeteSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        String output = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.of("UTC+8")));
        gen.writeString(output);
    }


}
