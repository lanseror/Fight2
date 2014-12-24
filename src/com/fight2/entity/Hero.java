package com.fight2.entity;

import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.constant.TiledTextureEnum;
import com.fight2.util.TiledTextureFactory;

public class Hero extends AnimatedSprite {
    private final static long SPD = 45;
    private final long[] speed = { SPD, SPD, SPD, SPD, SPD, SPD, SPD, SPD, SPD, SPD, SPD, SPD, SPD, SPD, SPD, SPD };

    public Hero(final float x, final float y, final VertexBufferObjectManager vbom) {
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
        this.animate(speed, 0, 15, true);
    }

    private void up() {
        this.animate(speed, 16, 31, true);
    }

    private void rightUp() {
        this.animate(speed, 32, 47, true);
    }

    private void left() {
        this.animate(speed, 48, 63, true);
    }

    private void right() {
        this.animate(speed, 64, 79, true);
    }

    private void leftDown() {
        this.animate(speed, 80, 95, true);
    }

    private void down() {
        this.animate(speed, 96, 111, true);
    }

    private void rightDown() {
        this.animate(speed, 112, 127, true);
    }
}
