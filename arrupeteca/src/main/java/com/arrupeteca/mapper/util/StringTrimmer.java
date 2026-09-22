package com.arrupeteca.mapper.util;

import org.springframework.stereotype.Component;

@Component
public class StringTrimmer {

    public String trim(String value) {
        return value == null ? null : value.trim();
    }
}
