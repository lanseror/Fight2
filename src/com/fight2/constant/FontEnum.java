package com.fight2.constant;

public enum FontEnum {
    Main("AdobeBoldFaceStdR.otf"),
    Battle("AdobeBoldFaceStdR.otf"),
    BoldFace("AdobeBoldFaceStdR.otf");

    private final String fontUrl;

    private FontEnum(final String fontUrl) {
        this.fontUrl = fontUrl;
    }

    public String getFontUrl() {
        return fontUrl;
    }

}
