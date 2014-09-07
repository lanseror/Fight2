package com.fight2.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.andengine.util.debug.Debug;

import android.content.Context;

import com.fight2.R;

public class LogUtils {
    private static Context CONTEXT;

    public static void init(final Context context) {
        CONTEXT = context;
    }

    public static void e(final Throwable e) {
        Debug.e(e);

        FileOutputStream output = null;
        try {
            final String outputName = R.string.app_name + ".debug";
            output = CONTEXT.openFileOutput(outputName, Context.MODE_PRIVATE);
            final PrintStream printStream = new PrintStream(output);
            e.printStackTrace(printStream);
            printStream.flush();
        } catch (final FileNotFoundException e1) {
            throw new RuntimeException(e1);
        } finally {
            if (output != null)
                try {
                    output.close();
                } catch (final IOException e1) {
                    throw new RuntimeException(e1);
                }
        }
    }
}
