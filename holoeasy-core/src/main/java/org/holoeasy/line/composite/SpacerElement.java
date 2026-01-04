package org.holoeasy.line.composite;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A spacer element that takes up horizontal space but doesn't render anything.
 * Used to add gaps between display elements in a CompositeDisplayLine.
 */
public class SpacerElement implements CompositeElement {

    private final float width;

    /**
     * Create a spacer with the specified width.
     * @param width the horizontal space in blocks
     */
    public SpacerElement(float width) {
        this.width = width;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public int getEntityId() {
        return -1; // No entity
    }

    @Override
    public void spawn(@NotNull Player player, float x, float y, float z) {
        // No-op: spacers don't spawn entities
    }

    @Override
    public void despawn(@NotNull Player player) {
        // No-op: spacers don't have entities
    }

    @Override
    public void update(@NotNull Player player, float x, float y, float z) {
        // No-op: spacers don't have entities
    }

    @Override
    public void teleport(@NotNull Player player, @NotNull org.bukkit.Location location) {
        // No-op: spacers don't have entities
    }

    @Override
    public boolean hasEntity() {
        return false;
    }
}
