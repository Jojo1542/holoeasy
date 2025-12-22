package org.holoeasy.line.composite;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.holoeasy.line.DisplayItemLine;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * An ItemDisplay element for use in CompositeDisplayLine.
 * Displays an item model at a position within the composite.
 */
public class ItemDisplayElement extends AbstractDisplayElement<ItemStack, ItemDisplayElement> {

    // Metadata indices for ItemDisplay specific (1.21.10+)
    protected static final int INDEX_ITEM = 23;
    protected static final int INDEX_ITEM_DISPLAY_TYPE = 24;

    private final Function<Player, ItemStack> itemSupplier;
    private byte itemDisplayType = DisplayItemLine.ItemDisplayType.FIXED.getValue();

    /**
     * Create an ItemDisplay element with a static item.
     */
    public ItemDisplayElement(@NotNull ItemStack item) {
        this(player -> item);
    }

    /**
     * Create an ItemDisplay element with a player-specific item supplier.
     */
    public ItemDisplayElement(@NotNull Function<Player, ItemStack> itemSupplier) {
        super(EntityTypes.ITEM_DISPLAY);
        this.itemSupplier = itemSupplier;
    }

    @Override
    public void update(@NotNull Player player, float x, float y, float z) {
        ItemStack bukkitItem = itemSupplier.apply(player);
        com.github.retrooper.packetevents.protocol.item.ItemStack item = SpigotConversionUtil.fromBukkitItemStack(bukkitItem);

        List<EntityData<?>> entityData = new ArrayList<>();

        addDisplayBaseMetadata(entityData, x, y, z);

        entityData.add(new EntityData<>(INDEX_ITEM, EntityDataTypes.ITEMSTACK, item));
        if (modifiedFields.contains(INDEX_ITEM_DISPLAY_TYPE)) {
            entityData.add(new EntityData<>(INDEX_ITEM_DISPLAY_TYPE, EntityDataTypes.BYTE, itemDisplayType));
        }

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entityId, entityData);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

    /**
     * Set the item display type (how the item is rendered).
     */
    public ItemDisplayElement itemDisplayType(DisplayItemLine.ItemDisplayType displayType) {
        this.itemDisplayType = displayType.getValue();
        modifiedFields.add(INDEX_ITEM_DISPLAY_TYPE);
        return this;
    }

    /**
     * Set the item display type using raw byte value.
     */
    public ItemDisplayElement itemDisplayType(byte displayType) {
        this.itemDisplayType = displayType;
        modifiedFields.add(INDEX_ITEM_DISPLAY_TYPE);
        return this;
    }
}
