package com.fight2.util;

public class ChatFontPool {
    private static ChatFontPool INSTANCE = new ChatFontPool();

    private ChatFontPool() {
        // Private the constructor;
    }

    public static ChatFontPool getInstance() {
        return INSTANCE;
    }

}
