package committee.nova.danmaku.utils;

import java.nio.charset.Charset;

public class Charsets {
    public static Charset toCharset(Charset s) {
        return s == null ? Charset.defaultCharset() : s;
    }

    public static Charset toCharset(final String charset) {
        return charset == null ? Charset.defaultCharset() : Charset.forName(charset);
    }
}
