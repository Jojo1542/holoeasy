package org.holoeasy.line;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.adventure.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.holoeasy.hologram.Hologram;
import org.holoeasy.util.VersionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DisplayTextLine extends AbstractDisplayLine<Component, DisplayTextLine> {
    private static final GsonComponentSerializer SERIALIZER = GsonComponentSerializer.builder().build();

    // Text options bit mask flags (for 1.21.10+)
    public static final byte FLAG_HAS_SHADOW = 0x01;
    public static final byte FLAG_IS_SEE_THROUGH = 0x02;
    public static final byte FLAG_USE_DEFAULT_BACKGROUND = 0x04;

    // Text Display specific properties
    private int lineWidth = 200;
    private int backgroundColor = 0x40000000;
    private byte textOpacity = -1;
    private byte textOptions = 0; // Bit mask: shadow, see-through, default bg, alignment

    public DisplayTextLine(Hologram hologram, Function<Player, Component> valueSupplier) {
        super(hologram, EntityTypes.TEXT_DISPLAY, valueSupplier);
    }

    @Override
    public @NotNull Type getType() {
        return Type.DISPLAY_TEXT_LINE;
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
        List<EntityData<?>> entityData = new ArrayList<>();

        Component textComponent = getValue(player);

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
                throw new RuntimeException("DisplayTextLine is available since 1.19.4");
            case V1_19:
                entityData.add(new EntityData<>(15, EntityDataTypes.BYTE, billboard));
                entityData.add(new EntityData<>(22, EntityDataTypes.ADV_COMPONENT, textComponent));
                entityData.add(new EntityData<>(23, EntityDataTypes.INT, lineWidth));
                entityData.add(new EntityData<>(24, EntityDataTypes.INT, backgroundColor));
                entityData.add(new EntityData<>(25, EntityDataTypes.BYTE, textOpacity));
                break;
            case V1_21:
                entityData.add(new EntityData<>(15, EntityDataTypes.BYTE, billboard));
                entityData.add(new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, textComponent));
                entityData.add(new EntityData<>(24, EntityDataTypes.INT, lineWidth));
                entityData.add(new EntityData<>(25, EntityDataTypes.INT, backgroundColor));
                entityData.add(new EntityData<>(26, EntityDataTypes.BYTE, textOpacity));
                break;
            case V1_21_10:
            case V1_21_11:
            default:
                // Text Display specific (1.21.10+)
                entityData.add(new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, textComponent));
                entityData.add(new EntityData<>(24, EntityDataTypes.INT, lineWidth));
                entityData.add(new EntityData<>(25, EntityDataTypes.INT, backgroundColor));
                entityData.add(new EntityData<>(26, EntityDataTypes.BYTE, textOpacity));
                entityData.add(new EntityData<>(27, EntityDataTypes.BYTE, textOptions));
                break;
        }

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entityID, entityData);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

    public DisplayTextLine lineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    public DisplayTextLine backgroundColor(@NotNull Color backgroundColor) {
        this.backgroundColor = backgroundColor.asRGB();
        return this;
    }

    public DisplayTextLine backgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public DisplayTextLine textOpacity(byte textOpacity) {
        this.textOpacity = textOpacity;
        return this;
    }

    /**
     * Enable or disable text shadow.
     */
    public DisplayTextLine shadow(boolean enabled) {
        if (enabled) {
            this.textOptions |= FLAG_HAS_SHADOW;
        } else {
            this.textOptions &= ~FLAG_HAS_SHADOW;
        }
        return this;
    }

    /**
     * Enable or disable see-through mode (text visible through blocks).
     */
    public DisplayTextLine seeThrough(boolean enabled) {
        if (enabled) {
            this.textOptions |= FLAG_IS_SEE_THROUGH;
        } else {
            this.textOptions &= ~FLAG_IS_SEE_THROUGH;
        }
        return this;
    }

    /**
     * Enable or disable using default background color.
     * When enabled, the backgroundColor field is ignored.
     */
    public DisplayTextLine useDefaultBackground(boolean enabled) {
        if (enabled) {
            this.textOptions |= FLAG_USE_DEFAULT_BACKGROUND;
        } else {
            this.textOptions &= ~FLAG_USE_DEFAULT_BACKGROUND;
        }
        return this;
    }

    /**
     * Set text alignment.
     * @param alignment CENTER=0, LEFT=1, RIGHT=2
     */
    public DisplayTextLine alignment(TextAlignment alignment) {
        // Clear alignment bits (bits 3-4) and set new value
        this.textOptions = (byte) ((this.textOptions & 0x07) | (alignment.getValue() << 3));
        return this;
    }

    /**
     * Set the raw text options bitmask directly.
     * @param textOptions bit mask: 0x01=shadow, 0x02=see-through, 0x04=default bg, bits 3-4=alignment
     */
    public DisplayTextLine textOptions(byte textOptions) {
        this.textOptions = textOptions;
        return this;
    }

    /**
     * Text alignment options for Text Display
     */
    public enum TextAlignment {
        CENTER(0),
        LEFT(1),
        RIGHT(2);

        private final int value;

        TextAlignment(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
