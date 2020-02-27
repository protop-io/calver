package io.protop.calver;

import java.util.Objects;

class StringUtils {

    static boolean isNullOrEmpty(final String string) {
        if (Objects.isNull(string)) {
            return true;
        } else if (string.trim().length() == 0) {
            return true;
        }

        return false;
    }
}
