package dev.renner.backend.util;

/**
 * Created by renne on 09.05.2017.
 */
public class BOM {

    public static final String UTF8_BOM = "\uFEFF";

    public static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }
}
