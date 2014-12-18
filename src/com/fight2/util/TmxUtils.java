package com.fight2.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;

import com.fight2.entity.Hero;

public class TmxUtils {
    private static int GID = 0;
    private final Hero hero;
    private final TMXTiledMap tmxTiledMap;

    public TmxUtils(final Hero hero, final TMXTiledMap tmxTiledMap) {
        this.hero = hero;
        this.tmxTiledMap = tmxTiledMap;
    }

    public Path findPath(final TMXTile startTile, final TMXTile desTile, final TMXLayer tmxLayer) {
        TMXTilePoint currentPoint = new TMXTilePoint(startTile, null);
        final Queue<TMXTilePoint> queue = new LinkedList<TMXTilePoint>();
        queue.add(currentPoint);
        final Set<TMXTile> visitedTiles = new HashSet<TMXTile>();
        visitedTiles.add(startTile);
        while (!queue.isEmpty()) {
            currentPoint = queue.poll();
            final TMXTile pointTMXTile = currentPoint.getTmxTile();
            final int row = pointTMXTile.getTileRow();
            final int col = pointTMXTile.getTileColumn();
            if (pointTMXTile == desTile) {
                break;
            }

            final TMXTile upTile = tmxLayer.getTMXTile(col, row - 1);
            if (upTile != null && upTile.getGlobalTileID() == GID && !visitedTiles.contains(upTile)) {/* up */
                visit(upTile, currentPoint, queue);
                visitedTiles.add(upTile);
            }
            final TMXTile leftTile = tmxLayer.getTMXTile(col - 1, row);
            if (leftTile != null && leftTile.getGlobalTileID() == GID && !visitedTiles.contains(leftTile)) {/* left */
                visit(leftTile, currentPoint, queue);
                visitedTiles.add(leftTile);
            }
            final TMXTile rightTile = tmxLayer.getTMXTile(col + 1, row);
            if (rightTile != null && rightTile.getGlobalTileID() == GID && !visitedTiles.contains(rightTile)) { /* right */
                visit(rightTile, currentPoint, queue);
                visitedTiles.add(rightTile);
            }
            final TMXTile downTile = tmxLayer.getTMXTile(col, row + 1);
            if (downTile != null && downTile.getGlobalTileID() == GID && !visitedTiles.contains(downTile)) { /* down */
                visit(downTile, currentPoint, queue);
                visitedTiles.add(downTile);
            }
            final TMXTile leftUpTile = tmxLayer.getTMXTile(col - 1, row - 1);
            if (leftUpTile != null && leftUpTile.getGlobalTileID() == GID && !visitedTiles.contains(leftUpTile)) {/* left up */
                visit(leftUpTile, currentPoint, queue);
                visitedTiles.add(leftUpTile);
            }
            final TMXTile rightUpTile = tmxLayer.getTMXTile(col + 1, row - 1);
            if (rightUpTile != null && rightUpTile.getGlobalTileID() == GID && !visitedTiles.contains(rightUpTile)) {/* right up */
                visit(rightUpTile, currentPoint, queue);
                visitedTiles.add(rightUpTile);
            }

            final TMXTile leftDownTile = tmxLayer.getTMXTile(col - 1, row + 1);
            if (leftDownTile != null && leftDownTile.getGlobalTileID() == GID && !visitedTiles.contains(leftDownTile)) { /* left down */
                visit(leftDownTile, currentPoint, queue);
                visitedTiles.add(leftDownTile);
            }

            final TMXTile rightDownTile = tmxLayer.getTMXTile(col + 1, row + 1);
            if (rightDownTile != null && rightDownTile.getGlobalTileID() == GID && !visitedTiles.contains(rightDownTile)) { /* right down */
                visit(rightDownTile, currentPoint, queue);
                visitedTiles.add(rightDownTile);
            }

        }

        final Stack<TMXTile> stack = new Stack<TMXTile>();
        stack.push(currentPoint.getTmxTile());
        while (currentPoint.getPredecessor() != null) {
            currentPoint = currentPoint.getPredecessor();
            stack.push(currentPoint.getTmxTile());
            if (currentPoint.getTmxTile() == startTile) {
                break;
            }
        }

        final Path path = new Path(stack.size());
        while (!stack.isEmpty()) {
            final TMXTile pathTMXTile = stack.pop();
            path.to(tmxLayer.getTileX(pathTMXTile.getTileColumn()) + 0.5f * tmxTiledMap.getTileWidth(),
                    tmxLayer.getTileY(pathTMXTile.getTileRow()) + hero.getHeight() * 0.5f);
        }
        return path;
    }

    private void visit(final TMXTile pointTmxTile, final TMXTilePoint predecessor, final Queue<TMXTilePoint> queue) {
        final TMXTilePoint visitPoint = new TMXTilePoint(pointTmxTile, predecessor);
        queue.add(visitPoint);
    }

    private static class TMXTilePoint {
        private final TMXTile tmxTile;
        private final TMXTilePoint predecessor;

        public TMXTilePoint(final TMXTile tmxTile, final TMXTilePoint predecessor) {
            super();
            this.tmxTile = tmxTile;
            this.predecessor = predecessor;
        }

        public TMXTile getTmxTile() {
            return tmxTile;
        }

        public TMXTilePoint getPredecessor() {
            return predecessor;
        }

    }
}