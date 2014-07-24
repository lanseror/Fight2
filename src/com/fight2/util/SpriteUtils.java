package com.fight2.util;

import org.andengine.entity.IEntity;

public class SpriteUtils {
    private SpriteUtils() {

    }

    public static float toContainerOuterX(final IEntity entry) {
        final IEntity container = entry.getParent();
        final float outerX = entry.getX() + container.getX() - container.getWidth() * 0.5f;
        return outerX;
    }

    public static float toContainerOuterY(final IEntity entry) {
        final IEntity container = entry.getParent();
        final float outerY = entry.getY() + container.getY() - container.getHeight() * 0.5f;
        return outerY;
    }
}
