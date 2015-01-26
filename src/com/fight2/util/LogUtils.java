package com.fight2.util;

import android.content.Context;

public class LogUtils {

    public static void init(final Context context) {
    }

    public static void e(final Throwable e) {
        throw new RuntimeException(e);

    }
}
