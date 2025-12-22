package org.holoeasy.line;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3f;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.holoeasy.hologram.Hologram;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

/**
 * Abstract base class for Display entities (TextDisplay, BlockDisplay, ItemDisplay).
 * Contains common Display entity properties available in 1.21.10+
 */
@ApiStatus.Experimental
public abstract class AbstractDisplayLine<T, SELF extends AbstractDisplayLine<T, SELF>> extends Line<T> {

    // Display base properties
    protected int interpolationDelay = 0;
    protected int transformationInterpolationDuration = 0;
    protected int positionRotationInterpolationDuration = 0;
    protected Vector3f translation = new Vector3f(0.0f, 0.0f, 0.0f);
    protected Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
    protected Quaternion4f rotationLeft = new Quaternion4f(0.0f, 0.0f, 0.0f, 1.0f);
    protected Quaternion4f rotationRight = new Quaternion4f(0.0f, 0.0f, 0.0f, 1.0f);
    protected byte billboard = 0;
    protected int brightness = -1; // -1 = no override
    protected float viewRange = 1.0f;
    protected float shadowRadius = 0.0f;
    protected float shadowStrength = 1.0f;
    protected float displayWidth = 0.0f;
    protected float displayHeight = 0.0f;
    protected int glowColorOverride = -1; // -1 = no override

    public AbstractDisplayLine(Hologram hologram, EntityType entityType, Function<Player, T> valueSupplier) {
        super(hologram, entityType, valueSupplier);
    }

    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }

    /**
     * Add Display base entity data for 1.21.10+ to the provided list.
     * Subclasses should call this in their update() method.
     */
    protected void addDisplayBaseMetadata(List<EntityData<?>> entityData) {
        entityData.add(new EntityData<>(8, EntityDataTypes.INT, interpolationDelay));
        entityData.add(new EntityData<>(9, EntityDataTypes.INT, transformationInterpolationDuration));
        entityData.add(new EntityData<>(10, EntityDataTypes.INT, positionRotationInterpolationDuration));
        entityData.add(new EntityData<>(11, EntityDataTypes.VECTOR3F, translation));
        entityData.add(new EntityData<>(12, EntityDataTypes.VECTOR3F, scale));
        entityData.add(new EntityData<>(13, EntityDataTypes.QUATERNION, rotationLeft));
        entityData.add(new EntityData<>(14, EntityDataTypes.QUATERNION, rotationRight));
        entityData.add(new EntityData<>(15, EntityDataTypes.BYTE, billboard));
        entityData.add(new EntityData<>(16, EntityDataTypes.INT, brightness));
        entityData.add(new EntityData<>(17, EntityDataTypes.FLOAT, viewRange));
        entityData.add(new EntityData<>(18, EntityDataTypes.FLOAT, shadowRadius));
        entityData.add(new EntityData<>(19, EntityDataTypes.FLOAT, shadowStrength));
        entityData.add(new EntityData<>(20, EntityDataTypes.FLOAT, displayWidth));
        entityData.add(new EntityData<>(21, EntityDataTypes.FLOAT, displayHeight));
        entityData.add(new EntityData<>(22, EntityDataTypes.INT, glowColorOverride));
    }

    // ==================== Builder Methods ====================

    public SELF yOffset(double yOffset) {
        super.setYOffset(yOffset);
        return self();
    }

    /**
     * Set billboard constraint (rotation behavior)
     * @param billboard 0=FIXED, 1=VERTICAL, 2=HORIZONTAL, 3=CENTER
     */
    public SELF billboard(byte billboard) {
        this.billboard = billboard;
        return self();
    }

    /**
     * Set interpolation delay in ticks.
     */
    public SELF interpolationDelay(int delay) {
        this.interpolationDelay = delay;
        return self();
    }

    /**
     * Set translation offset.
     */
    public SELF translation(float x, float y, float z) {
        this.translation = new Vector3f(x, y, z);
        return self();
    }

    /**
     * Set translation offset.
     */
    public SELF translation(@NotNull Vector3f translation) {
        this.translation = translation;
        return self();
    }

    /**
     * Set translation X offset.
     */
    public SELF translationX(float x) {
        this.translation = new Vector3f(x, translation.getY(), translation.getZ());
        return self();
    }

    /**
     * Set translation Y offset.
     */
    public SELF translationY(float y) {
        this.translation = new Vector3f(translation.getX(), y, translation.getZ());
        return self();
    }

    /**
     * Set translation Z offset.
     */
    public SELF translationZ(float z) {
        this.translation = new Vector3f(translation.getX(), translation.getY(), z);
        return self();
    }

    /**
     * Set scale (size multiplier).
     * @param scale uniform scale for all axes
     */
    public SELF scale(float scale) {
        this.scale = new Vector3f(scale, scale, scale);
        return self();
    }

    /**
     * Set scale (size multiplier) for each axis.
     */
    public SELF scale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
        return self();
    }

    /**
     * Set scale (size multiplier).
     */
    public SELF scale(@NotNull Vector3f scale) {
        this.scale = scale;
        return self();
    }

    /**
     * Set scale X (size multiplier).
     */
    public SELF scaleX(float x) {
        this.scale = new Vector3f(x, scale.getY(), scale.getZ());
        return self();
    }

    /**
     * Set scale Y (size multiplier).
     */
    public SELF scaleY(float y) {
        this.scale = new Vector3f(scale.getX(), y, scale.getZ());
        return self();
    }

    /**
     * Set scale Z (size multiplier).
     */
    public SELF scaleZ(float z) {
        this.scale = new Vector3f(scale.getX(), scale.getY(), z);
        return self();
    }

    /**
     * Set left rotation (applied before scale).
     */
    public SELF rotationLeft(float x, float y, float z, float w) {
        this.rotationLeft = new Quaternion4f(x, y, z, w);
        return self();
    }

    /**
     * Set left rotation (applied before scale).
     */
    public SELF rotationLeft(@NotNull Quaternion4f rotation) {
        this.rotationLeft = rotation;
        return self();
    }

    /**
     * Set right rotation (applied after scale).
     */
    public SELF rotationRight(float x, float y, float z, float w) {
        this.rotationRight = new Quaternion4f(x, y, z, w);
        return self();
    }

    /**
     * Set right rotation (applied after scale).
     */
    public SELF rotationRight(@NotNull Quaternion4f rotation) {
        this.rotationRight = rotation;
        return self();
    }

    /**
     * Set transformation interpolation duration in ticks.
     */
    public SELF transformationInterpolationDuration(int duration) {
        this.transformationInterpolationDuration = duration;
        return self();
    }

    /**
     * Set position/rotation interpolation duration in ticks.
     */
    public SELF positionRotationInterpolationDuration(int duration) {
        this.positionRotationInterpolationDuration = duration;
        return self();
    }

    /**
     * Set brightness override.
     * @param blockLight 0-15
     * @param skyLight 0-15
     */
    public SELF brightness(int blockLight, int skyLight) {
        this.brightness = (blockLight << 4) | (skyLight << 20);
        return self();
    }

    /**
     * Disable brightness override (use natural lighting).
     */
    public SELF disableBrightnessOverride() {
        this.brightness = -1;
        return self();
    }

    /**
     * Set view range multiplier.
     * @param range default is 1.0
     */
    public SELF viewRange(float range) {
        this.viewRange = range;
        return self();
    }

    /**
     * Set shadow radius.
     */
    public SELF shadowRadius(float radius) {
        this.shadowRadius = radius;
        return self();
    }

    /**
     * Set shadow strength.
     * @param strength default is 1.0
     */
    public SELF shadowStrength(float strength) {
        this.shadowStrength = strength;
        return self();
    }

    /**
     * Set display width.
     */
    public SELF displayWidth(float width) {
        this.displayWidth = width;
        return self();
    }

    /**
     * Set display height.
     */
    public SELF displayHeight(float height) {
        this.displayHeight = height;
        return self();
    }

    /**
     * Set glow color override.
     * @param color ARGB color value, or -1 to disable
     */
    public SELF glowColorOverride(int color) {
        this.glowColorOverride = color;
        return self();
    }

    /**
     * Set glow color override using Bukkit Color.
     */
    public SELF glowColorOverride(@NotNull Color color) {
        this.glowColorOverride = color.asRGB();
        return self();
    }
}
