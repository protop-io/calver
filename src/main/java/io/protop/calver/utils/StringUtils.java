package io.protop.calver.utils;

import java.util.Objects;

public class StringUtils {

    public static boolean isNullOrEmpty(final String string) {
        if (Objects.isNull(string)) {
            return true;
        } else if (string.trim().length() == 0) {
            return true;
        }

        return false;
    }
}
