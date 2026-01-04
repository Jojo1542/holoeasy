package org.holoeasy.line.composite;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract base class for display elements in a CompositeDisplayLine.
 * Provides common display entity properties and packet handling.
 */
public abstract class AbstractDisplayElement<T, SELF extends AbstractDisplayElement<T, SELF>> implements CompositeElement {

    protected static final AtomicInteger IDs_COUNTER = new AtomicInteger(10000 + new Random().nextInt(1000));

    // Metadata indices for Display base entity (1.21.10+)
    protected static final int INDEX_INTERPOLATION_DELAY = 8;
    protected static final int INDEX_TRANSFORMATION_INTERPOLATION_DURATION = 9;
    protected static final int INDEX_POSITION_ROTATION_INTERPOLATION_DURATION = 10;
    protected static final int INDEX_TRANSLATION = 11;
    protected static final int INDEX_SCALE = 12;
    protected static final int INDEX_ROTATION_LEFT = 13;
    protected static final int INDEX_ROTATION_RIGHT = 14;
    protected static final int INDEX_BILLBOARD = 15;
    protected static final int INDEX_BRIGHTNESS = 16;
    protected static final int INDEX_VIEW_RANGE = 17;
    protected static final int INDEX_SHADOW_RADIUS = 18;
    protected static final int INDEX_SHADOW_STRENGTH = 19;
    protected static final int INDEX_DISPLAY_WIDTH = 20;
    protected static final int INDEX_DISPLAY_HEIGHT = 21;
    protected static final int INDEX_GLOW_COLOR_OVERRIDE = 22;

    protected final Set<Integer> modifiedFields = new HashSet<>();

    protected final EntityType entityType;
    protected final int entityId;

    // Element width for positioning
    protected float width = 0.5f;

    // Display base properties
    protected int interpolationDelay = 0;
    protected int transformationInterpolationDuration = 0;
    protected int positionRotationInterpolationDuration = 0;
    protected Vector3f baseTranslation = new Vector3f(0.0f, 0.0f, 0.0f);
    protected Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
    protected Quaternion4f rotationLeft = new Quaternion4f(0.0f, 0.0f, 0.0f, 1.0f);
    protected Quaternion4f rotationRight = new Quaternion4f(0.0f, 0.0f, 0.0f, 1.0f);
    protected byte billboard = 3; // CENTER by default for composite elements
    protected int brightness = -1;
    protected float viewRange = 1.0f;
    protected float shadowRadius = 0.0f;
    protected float shadowStrength = 1.0f;
    protected float displayWidth = 0.0f;
    protected float displayHeight = 0.0f;
    protected int glowColorOverride = -1;

    // Location storage
    protected Location spawnLocation;

    protected AbstractDisplayElement(EntityType entityType) {
        this.entityType = entityType;
        this.entityId = IDs_COUNTER.getAndIncrement();
    }

    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    /**
     * Set the width this element occupies.
     */
    public SELF width(float width) {
        this.width = width;
        return self();
    }

    @Override
    public void spawn(@NotNull Player player, float x, float y, float z) {
        Location loc = spawnLocation;
        if (loc == null) return;

        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
                entityId,
                UUID.randomUUID(),
                entityType,
                SpigotConversionUtil.fromBukkitLocation(loc),
                loc.getYaw(),
                0,
                null
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);

        // Immediately update with metadata including translation
        update(player, x, y, z);
    }

    @Override
    public void despawn(@NotNull Player player) {
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(entityId);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

    @Override
    public void teleport(@NotNull Player player, @NotNull Location location) {
        this.spawnLocation = location;
        WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(
                entityId,
                SpigotConversionUtil.fromBukkitLocation(location),
                false
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

    /**
     * Set spawn location (called by CompositeDisplayLine).
     */
    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
    }

    /**
     * Add Display base entity metadata to the list.
     * Translation is computed from base translation + offset.
     */
    protected void addDisplayBaseMetadata(List<EntityData<?>> entityData, float offsetX, float offsetY, float offsetZ) {
        // Translation is always sent (combines base + offset)
        Vector3f finalTranslation = new Vector3f(
                baseTranslation.getX() + offsetX,
                baseTranslation.getY() + offsetY,
                baseTranslation.getZ() + offsetZ
        );
        entityData.add(new EntityData<>(INDEX_TRANSLATION, EntityDataTypes.VECTOR3F, finalTranslation));

        if (modifiedFields.contains(INDEX_INTERPOLATION_DELAY)) {
            entityData.add(new EntityData<>(INDEX_INTERPOLATION_DELAY, EntityDataTypes.INT, interpolationDelay));
        }
        if (modifiedFields.contains(INDEX_TRANSFORMATION_INTERPOLATION_DURATION)) {
            entityData.add(new EntityData<>(INDEX_TRANSFORMATION_INTERPOLATION_DURATION, EntityDataTypes.INT, transformationInterpolationDuration));
        }
        if (modifiedFields.contains(INDEX_POSITION_ROTATION_INTERPOLATION_DURATION)) {
            entityData.add(new EntityData<>(INDEX_POSITION_ROTATION_INTERPOLATION_DURATION, EntityDataTypes.INT, positionRotationInterpolationDuration));
        }
        if (modifiedFields.contains(INDEX_SCALE)) {
            entityData.add(new EntityData<>(INDEX_SCALE, EntityDataTypes.VECTOR3F, scale));
        }
        if (modifiedFields.contains(INDEX_ROTATION_LEFT)) {
            entityData.add(new EntityData<>(INDEX_ROTATION_LEFT, EntityDataTypes.QUATERNION, rotationLeft));
        }
        if (modifiedFields.contains(INDEX_ROTATION_RIGHT)) {
            entityData.add(new EntityData<>(INDEX_ROTATION_RIGHT, EntityDataTypes.QUATERNION, rotationRight));
        }
        if (modifiedFields.contains(INDEX_BILLBOARD)) {
            entityData.add(new EntityData<>(INDEX_BILLBOARD, EntityDataTypes.BYTE, billboard));
        }
        if (modifiedFields.contains(INDEX_BRIGHTNESS)) {
            entityData.add(new EntityData<>(INDEX_BRIGHTNESS, EntityDataTypes.INT, brightness));
        }
        if (modifiedFields.contains(INDEX_VIEW_RANGE)) {
            entityData.add(new EntityData<>(INDEX_VIEW_RANGE, EntityDataTypes.FLOAT, viewRange));
        }
        if (modifiedFields.contains(INDEX_SHADOW_RADIUS)) {
            entityData.add(new EntityData<>(INDEX_SHADOW_RADIUS, EntityDataTypes.FLOAT, shadowRadius));
        }
        if (modifiedFields.contains(INDEX_SHADOW_STRENGTH)) {
            entityData.add(new EntityData<>(INDEX_SHADOW_STRENGTH, EntityDataTypes.FLOAT, shadowStrength));
        }
        if (modifiedFields.contains(INDEX_DISPLAY_WIDTH)) {
            entityData.add(new EntityData<>(INDEX_DISPLAY_WIDTH, EntityDataTypes.FLOAT, displayWidth));
        }
        if (modifiedFields.contains(INDEX_DISPLAY_HEIGHT)) {
            entityData.add(new EntityData<>(INDEX_DISPLAY_HEIGHT, EntityDataTypes.FLOAT, displayHeight));
        }
        if (modifiedFields.contains(INDEX_GLOW_COLOR_OVERRIDE)) {
            entityData.add(new EntityData<>(INDEX_GLOW_COLOR_OVERRIDE, EntityDataTypes.INT, glowColorOverride));
        }
    }

    // ==================== Builder Methods ====================

    public SELF billboard(byte billboard) {
        this.billboard = billboard;
        modifiedFields.add(INDEX_BILLBOARD);
        return self();
    }

    public SELF interpolationDelay(int delay) {
        this.interpolationDelay = delay;
        modifiedFields.add(INDEX_INTERPOLATION_DELAY);
        return self();
    }

    public SELF translation(float x, float y, float z) {
        this.baseTranslation = new Vector3f(x, y, z);
        return self();
    }

    public SELF scale(float scale) {
        this.scale = new Vector3f(scale, scale, scale);
        modifiedFields.add(INDEX_SCALE);
        return self();
    }

    public SELF scale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
        modifiedFields.add(INDEX_SCALE);
        return self();
    }

    public SELF rotationLeft(float x, float y, float z, float w) {
        this.rotationLeft = new Quaternion4f(x, y, z, w);
        modifiedFields.add(INDEX_ROTATION_LEFT);
        return self();
    }

    public SELF rotationRight(float x, float y, float z, float w) {
        this.rotationRight = new Quaternion4f(x, y, z, w);
        modifiedFields.add(INDEX_ROTATION_RIGHT);
        return self();
    }

    public SELF transformationInterpolationDuration(int duration) {
        this.transformationInterpolationDuration = duration;
        modifiedFields.add(INDEX_TRANSFORMATION_INTERPOLATION_DURATION);
        return self();
    }

    public SELF brightness(int blockLight, int skyLight) {
        this.brightness = (blockLight << 4) | (skyLight << 20);
        modifiedFields.add(INDEX_BRIGHTNESS);
        return self();
    }

    public SELF viewRange(float range) {
        this.viewRange = range;
        modifiedFields.add(INDEX_VIEW_RANGE);
        return self();
    }

    public SELF shadowRadius(float radius) {
        this.shadowRadius = radius;
        modifiedFields.add(INDEX_SHADOW_RADIUS);
        return self();
    }

    public SELF shadowStrength(float strength) {
        this.shadowStrength = strength;
        modifiedFields.add(INDEX_SHADOW_STRENGTH);
        return self();
    }

    public SELF glowColorOverride(int color) {
        this.glowColorOverride = color;
        modifiedFields.add(INDEX_GLOW_COLOR_OVERRIDE);
        return self();
    }
}
