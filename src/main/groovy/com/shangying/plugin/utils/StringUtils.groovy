package com.shangying.plugin.utils

/**
 * Created by jinshubao on 2017/1/23.
 */
class StringUtils {

    static String uppercaseFirst(String s) {
        if (s) {
            def t = s.substring(0, 1)
            def f = t.toUpperCase()
            return s.replaceFirst(t, f)
        }
        return s
    }

    static String lowercaseFirst(String s) {
        if (s) {
            def t = s.substring(0, 1)
            def f = t.toLowerCase()
            return s.replaceFirst(t, f)
        }
        return s
    }
}
