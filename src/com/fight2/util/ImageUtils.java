package com.fight2.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.fight2.GameActivity;

public class ImageUtils {

    public static String getLocalString(final String webUrl, final GameActivity activity) throws IOException {

        final Map<String, String> imageDatas = TextureFactory.getInstance().getImageDatas();
        String localString = imageDatas.get(webUrl);

        if (localString == null || localString.equals("")) {
            localString = downloadAndSave(webUrl, activity);
            final ImageOpenHelper dbHelper = activity.getDbHelper();
            final SQLiteDatabase database = dbHelper.getWritableDatabase();
            final ContentValues updateValues = new ContentValues();
            updateValues.put(ImageOpenHelper.STATUS, 1);
            final int updatedRow = database.update(ImageOpenHelper.TABLE_NAME, updateValues, "web=?", new String[] { webUrl });
            if (updatedRow == 0) {
                final ContentValues insertValues = new ContentValues();
                insertValues.put(ImageOpenHelper.KEY, webUrl);
                insertValues.put(ImageOpenHelper.VALUE, localString);
                insertValues.put(ImageOpenHelper.STATUS, 1);
                database.insert(ImageOpenHelper.TABLE_NAME, null, insertValues);
            }

            database.close();
            imageDatas.put(webUrl, localString);
        }

        return localString;
    }

    public static boolean isCached(final String webUrl) {
        final Map<String, String> imageDatas = TextureFactory.getInstance().getImageDatas();
        return imageDatas.containsKey(webUrl);
    }

    public static void invalidImage(final String webUrl, final GameActivity activity) throws IOException {
        if (webUrl != null) {
            final ContentValues values = new ContentValues();
            values.put(ImageOpenHelper.STATUS, 0);
            final ImageOpenHelper dbHelper = activity.getDbHelper();
            final SQLiteDatabase database = dbHelper.getWritableDatabase();
            database.update(ImageOpenHelper.TABLE_NAME, values, "web=?", new String[] { webUrl });
            database.close();
        }
    }

    public static String downloadAndSave(final String webUrl, final Context context) throws IOException {
        final URL url = new URL(HttpUtils.HOST_URL + webUrl);

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