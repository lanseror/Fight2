package com.fight2.util;

import java.util.HashMap;
import java.util.Map;

import com.fight2.constant.ConfigEnum;

public class ConfigHelper {
    private static ConfigHelper INSTANCE = new ConfigHelper();
    private final Map<ConfigEnum, Object> datas = new HashMap<ConfigEnum, Object>();

    private ConfigHelper() {
        // Private the constructor;
    }

    public static ConfigHelper getInstance() {
        return INSTANCE;
    }

    public void setConfig(final ConfigEnum key, final Object value) {
        datas.put(key, value);
    }

    public int getInt(final ConfigEnum key) {
        final Integer intObj = (Integer) this.datas.get(key);
        return intObj.intValue();
    }

    public String getString(final ConfigEnum key) {
        return (String) this.datas.get(key);
    }

    public float getFloat(final ConfigEnum key) {
        final Float floatObj = (Float) this.datas.get(key);
        return floatObj.floatValue();
    }
}
