package org.holoeasy.line.composite;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Base interface for elements that can be placed in a CompositeDisplayLine.
 * Each element represents a display entity with horizontal positioning.
 */
public interface CompositeElement {

    /**
     * Get the width this element occupies in the composite line.
     * Used for calculating positions of subsequent elements.
     */
    float getWidth();

    /**
     * Get the entity ID for this element.
     * Returns -1 for spacers (no entity).
     */
    int getEntityId();

    /**
     * Called to spawn the entity for a player.
     * @param player the player to show the element to
     * @param x the X translation offset for horizontal positioning
     * @param y the Y translation offset
     * @param z the Z translation offset
     */
    void spawn(@NotNull Player player, float x, float y, float z);

    /**
     * Called to despawn the entity for a player.
     */
    void despawn(@NotNull Player player);

    /**
     * Called to update the entity metadata for a player.
     * @param player the player to update
     * @param x the X translation offset for horizontal positioning
     * @param y the Y translation offset
     * @param z the Z translation offset
     */
    void update(@NotNull Player player, float x, float y, float z);

    /**
     * Called to teleport the entity to a new location.
     * @param player the player to send the teleport packet to
     * @param location the new location
     */
    void teleport(@NotNull Player player, @NotNull org.bukkit.Location location);

    /**
     * Check if this element renders an entity (spacers don't).
     */
    default boolean hasEntity() {
        return getEntityId() != -1;
    }
}
