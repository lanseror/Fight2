package com.fight2.util;

import org.json.JSONObject;

public class VersionUtils {
    public static final String CLIENT_VERSION = "0.001";

    public static int checkVersion() {
        final String url = HttpUtils.HOST_URL + "/config/version";
        int status = 0;
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final String version = responseJson.getString("value");
            if (!CLIENT_VERSION.equals(version)) {
                status = 1;
            }
        } catch (final Exception e) {
            status = 2;
        }
        return status;
    }

}