package com.fight2.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fight2.GameActivity;

public class ImageUtils {

    public static String getLocalString(final String webUrl, final GameActivity activity) throws IOException {
        String localString = null;

        final ImageOpenHelper dbHelper = activity.getDbHelper();
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final String[] columns = { ImageOpenHelper.VALUE };
        final String[] args = { webUrl };
        final Cursor cursor = database.query(ImageOpenHelper.TABLE_NAME, columns, ImageOpenHelper.KEY + "=?", args, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            localString = cursor.getString(0);
        }
        if (localString == null || localString.endsWith("")) {
            localString = downloadAndSave(webUrl, activity);
            final ContentValues values = new ContentValues();
            values.put(ImageOpenHelper.KEY, webUrl);
            values.put(ImageOpenHelper.VALUE, localString);
            database.insert(ImageOpenHelper.TABLE_NAME, null, values);
        }
        database.close();

        return localString;
    }

    public static String downloadAndSave(final String webUrl, final Context context) throws IOException {
        final URL url = new URL(AccountUtils.HOST_URL + webUrl);

        InputStream input = null;
        FileOutputStream output = null;

        try {

            final String outputName = webUrl.replaceAll(".*/", "CardTexture_").replaceAll("\\..*", "");

            input = url.openConnection().getInputStream();
            output = context.openFileOutput(outputName, Context.MODE_PRIVATE);

            int read;
            final byte[] data = new byte[1024];
            while ((read = input.read(data)) != -1)
                output.write(data, 0, read);

            return outputName;

        } finally {
            if (output != null)
                output.close();
            if (input != null)
                input.close();
        }
    }
}