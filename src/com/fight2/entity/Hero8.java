package com.fight2.entity;

import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.constant.TiledTextureEnum;
import com.fight2.util.TiledTextureFactory;

public class Hero8 extends AnimatedSprite {
    private final static long SPD = 150;
    private final long[] speed = { SPD, SPD, SPD, SPD, SPD, SPD, SPD, SPD };
    private final static int FPD = 8;

    public Hero8(final float x, final float y, final VertexBufferObjectManager vbom) {
        super(x, y, TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.HERO2), vbom);
    }

    public void onGoing(final Path path, final int waypointIndex) {
        final float[] xs = path.getCoordinatesX();
        final float[] ys = path.getCoordinatesY();
        final float x1 = xs[waypointIndex];
        final float y1 = ys[waypointIndex];
        final float x2 = xs[waypointIndex + 1];
        final float y2 = ys[waypointIndex + 1];
        if (x1 > x2 && y1 < y2) { // left up
            leftUp();
        } else if (x1 == x2 && y1 < y2) { // up
            up();
        } else if (x1 < x2 && y1 < y2) { // right up
            rightUp();
        } else if (x1 > x2 && y1 == y2) {// left
            left();
        } else if (x1 < x2 && y1 == y2) {// right
            right();
        } else if (x1 > x2 && y1 > y2) {// left down
            leftDown();
        } else if (x1 == x2 && y1 > y2) {// down
            down();
        } else if (x1 < x2 && y1 > y2) {// right down
            rightDown();
        }
    }

    private void leftUp() {
        this.animate(speed, 0, FPD - 1, true);
    }

    private void up() {
        this.animate(speed, FPD, FPD * 2 - 1, true);
    }

    private void rightUp() {
        this.animate(speed, FPD * 2, FPD * 3 - 1, true);
    }

    private void left() {
        this.animate(speed, FPD * 3, FPD * 4 - 1, true);
    }

    private void right() {
        this.animate(speed, FPD * 4, FPD * 5 - 1, true);
    }

    private void leftDown() {
        this.animate(speed, FPD * 5, FPD * 6 - 1, true);
    }

    private void down() {
        this.animate(speed, FPD * 6, FPD * 7 - 1, true);
    }

    private void rightDown() {
        this.animate(speed, FPD * 7, FPD * 8 - 1, true);
    }
}
