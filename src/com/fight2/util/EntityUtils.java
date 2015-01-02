package com.fight2.util;

import org.andengine.entity.IEntity;

public class EntityUtils {
    public static void topAlignEntity(final IEntity entity, final float y) {
        entity.setY(y - entity.getHeight() * 0.5f);
    }

    public static void bottomAlignEntity(final IEntity entity, final float y) {
        entity.setY(y + entity.getHeight() * 0.5f);
    }

    public static void leftAlignEntity(final IEntity entity, final float x) {
        entity.setX(x + entity.getWidth() * 0.5f);
    }

    public static void rightAlignEntity(final IEntity entity, final float x) {
        entity.setX(x - entity.getWidth() * 0.5f);
    }
}