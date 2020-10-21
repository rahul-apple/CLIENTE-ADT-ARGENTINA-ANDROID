package com.zendesk.adtapp;

public final class StringUtils {
    static boolean hasLength(String string){
        if (string == null){
            return false;
        }
        return !string.isEmpty();
    }
}
