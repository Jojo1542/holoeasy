package org.holoeasy.line;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.holoeasy.hologram.Hologram;
import org.holoeasy.util.VersionUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@ApiStatus.Experimental
public class DisplayItemLine extends AbstractDisplayLine<ItemStack, DisplayItemLine> {

    // Item Display specific properties
    private byte itemDisplayType = ItemDisplayType.NONE.value;

    public DisplayItemLine(Hologram hologram, Function<Player, ItemStack> valueSupplier) {
        super(hologram, EntityTypes.ITEM_DISPLAY, valueSupplier);
    }

    @Override
    public @NotNull Type getType() {
        return Type.DISPLAY_ITEM_LINE;
    }

    @Override
    public void show(@NotNull Player player) {
        spawn(player);
        this.update(player);
    }

    @Override
    public void hide(@NotNull Player player) {
        destroy(player);
    }

    @Override
    public void update(@NotNull Player player) {
        ItemStack bukkitItem = getValue(player);
        com.github.retrooper.packetevents.protocol.item.ItemStack item = SpigotConversionUtil.fromBukkitItemStack(bukkitItem);

        List<EntityData<?>> entityData = new ArrayList<>();

        addDisplayBaseMetadata(entityData);

        switch (VersionUtil.CLEAN_VERSION) {
            case V1_8:
            case V1_9:
            case V1_10:
            case V1_11:
            case V1_12:
            case V1_13:
            case V1_14:
            case V1_15:
            case V1_16:
            case V1_17:
            case V1_18:
                throw new RuntimeException("DisplayItemLine is available since 1.19.4");
            case V1_19:
                entityData.add(new EntityData<>(22, EntityDataTypes.ITEMSTACK, item));
                entityData.add(new EntityData<>(23, EntityDataTypes.BYTE, itemDisplayType));
                break;
            case V1_21:
            case V1_21_10:
            case V1_21_11:
            default:
                entityData.add(new EntityData<>(23, EntityDataTypes.ITEMSTACK, item));
                entityData.add(new EntityData<>(24, EntityDataTypes.BYTE, itemDisplayType));
                break;
        }

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entityID, entityData);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

    /**
     * Set item display type.
     * @param displayType 0=NONE, 1=THIRD_PERSON_LEFT_HAND, 2=THIRD_PERSON_RIGHT_HAND,
     *                    3=FIRST_PERSON_LEFT_HAND, 4=FIRST_PERSON_RIGHT_HAND,
     *                    5=HEAD, 6=GUI, 7=GROUND, 8=FIXED
     */
    public DisplayItemLine itemDisplayType(byte displayType) {
        this.itemDisplayType = displayType;
        return this;
    }

    /**
     * Set item display type using enum.
     */
    public DisplayItemLine itemDisplayType(ItemDisplayType displayType) {
        this.itemDisplayType = displayType.getValue();
        return this;
    }

    /**
     * Item display type options
     */
    public enum ItemDisplayType {
        NONE(0),
        THIRD_PERSON_LEFT_HAND(1),
        THIRD_PERSON_RIGHT_HAND(2),
        FIRST_PERSON_LEFT_HAND(3),
        FIRST_PERSON_RIGHT_HAND(4),
        HEAD(5),
        GUI(6),
        GROUND(7),
        FIXED(8);

        private final byte value;

        ItemDisplayType(int value) {
            this.value = (byte) value;
        }

        public byte getValue() {
            return value;
        }
    }
}
