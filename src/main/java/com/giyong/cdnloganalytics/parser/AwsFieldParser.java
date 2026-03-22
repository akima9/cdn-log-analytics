package com.giyong.cdnloganalytics.parser;

import java.util.HashMap;
import java.util.Map;

public class AwsFieldParser {

    public static Map<String, Integer> parse(String line) {
        if (!line.startsWith("#Fields:")) {
            throw new IllegalArgumentException("Invalid fields line");
        }

        String[] fields = line.replace("#Fields: ", "").split(" ");

        Map<String, Integer> map = new HashMap<>();

        for (int i = 0; i < fields.length; i++) {
            map.put(fields[i], i);
        }

        return map;
    }

}
