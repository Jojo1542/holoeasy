package org.holoeasy.line.composite;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.holoeasy.line.DisplayTextLine;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A TextDisplay element for use in CompositeDisplayLine.
 * Displays text at a position within the composite.
 */
public class TextDisplayElement extends AbstractDisplayElement<Component, TextDisplayElement> {

    // Text options bit mask flags
    public static final byte FLAG_HAS_SHADOW = 0x01;
    public static final byte FLAG_IS_SEE_THROUGH = 0x02;
    public static final byte FLAG_USE_DEFAULT_BACKGROUND = 0x04;

    // Metadata indices for TextDisplay specific (1.21.10+)
    protected static final int INDEX_TEXT = 23;
    protected static final int INDEX_LINE_WIDTH = 24;
    protected static final int INDEX_BACKGROUND_COLOR = 25;
    protected static final int INDEX_TEXT_OPACITY = 26;
    protected static final int INDEX_TEXT_OPTIONS = 27;

    private final Function<Player, Component> textSupplier;

    // Text Display specific properties
    private int lineWidth = 200;
    private int backgroundColor = 0x40000000;
    private byte textOpacity = -1;
    private byte textOptions = 0;

    /**
     * Create a TextDisplay element with static text.
     */
    public TextDisplayElement(@NotNull Component text) {
        this(player -> text);
    }

    /**
     * Create a TextDisplay element with static text string.
     */
    public TextDisplayElement(@NotNull String text) {
        this(player -> Component.text(text));
    }

    /**
     * Create a TextDisplay element with a player-specific text supplier.
     */
    public TextDisplayElement(@NotNull Function<Player, Component> textSupplier) {
        super(EntityTypes.TEXT_DISPLAY);
        this.textSupplier = textSupplier;
    }

    @Override
    public void update(@NotNull Player player, float x, float y, float z) {
        Component textComponent = textSupplier.apply(player);

        List<EntityData<?>> entityData = new ArrayList<>();

        addDisplayBaseMetadata(entityData, x, y, z);

        entityData.add(new EntityData<>(INDEX_TEXT, EntityDataTypes.ADV_COMPONENT, textComponent));

        if (modifiedFields.contains(INDEX_LINE_WIDTH)) {
            entityData.add(new EntityData<>(INDEX_LINE_WIDTH, EntityDataTypes.INT, lineWidth));
        }
        if (modifiedFields.contains(INDEX_BACKGROUND_COLOR)) {
            entityData.add(new EntityData<>(INDEX_BACKGROUND_COLOR, EntityDataTypes.INT, backgroundColor));
        }
        if (modifiedFields.contains(INDEX_TEXT_OPACITY)) {
            entityData.add(new EntityData<>(INDEX_TEXT_OPACITY, EntityDataTypes.BYTE, textOpacity));
        }
        if (modifiedFields.contains(INDEX_TEXT_OPTIONS)) {
            entityData.add(new EntityData<>(INDEX_TEXT_OPTIONS, EntityDataTypes.BYTE, textOptions));
        }

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entityId, entityData);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

    public TextDisplayElement lineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        modifiedFields.add(INDEX_LINE_WIDTH);
        return this;
    }

    public TextDisplayElement backgroundColor(@NotNull Color backgroundColor) {
        this.backgroundColor = backgroundColor.asRGB();
        modifiedFields.add(INDEX_BACKGROUND_COLOR);
        return this;
    }

    public TextDisplayElement backgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        modifiedFields.add(INDEX_BACKGROUND_COLOR);
        return this;
    }

    public TextDisplayElement textOpacity(byte textOpacity) {
        this.textOpacity = textOpacity;
        modifiedFields.add(INDEX_TEXT_OPACITY);
        return this;
    }

    public TextDisplayElement shadow(boolean enabled) {
        if (enabled) {
            this.textOptions |= FLAG_HAS_SHADOW;
        } else {
            this.textOptions &= ~FLAG_HAS_SHADOW;
        }
        modifiedFields.add(INDEX_TEXT_OPTIONS);
        return this;
    }

    public TextDisplayElement seeThrough(boolean enabled) {
        if (enabled) {
            this.textOptions |= FLAG_IS_SEE_THROUGH;
        } else {
            this.textOptions &= ~FLAG_IS_SEE_THROUGH;
        }
        modifiedFields.add(INDEX_TEXT_OPTIONS);
        return this;
    }

    public TextDisplayElement useDefaultBackground(boolean enabled) {
        if (enabled) {
            this.textOptions |= FLAG_USE_DEFAULT_BACKGROUND;
        } else {
            this.textOptions &= ~FLAG_USE_DEFAULT_BACKGROUND;
        }
        modifiedFields.add(INDEX_BACKGROUND_COLOR);
        return this;
    }

    public TextDisplayElement alignment(DisplayTextLine.TextAlignment alignment) {
        this.textOptions = (byte) ((this.textOptions & 0x07) | (alignment.getValue() << 3));
        modifiedFields.add(INDEX_TEXT_OPTIONS);
        return this;
    }
}
