package org.holoeasy.line.composite;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A BlockDisplay element for use in CompositeDisplayLine.
 * Displays a block model at a position within the composite.
 */
public class BlockDisplayElement extends AbstractDisplayElement<BlockData, BlockDisplayElement> {

    // Metadata index for BlockDisplay specific (1.21.10+)
    protected static final int INDEX_BLOCK_STATE = 23;

    private final Function<Player, BlockData> blockSupplier;

    /**
     * Create a BlockDisplay element with a static material.
     */
    public BlockDisplayElement(@NotNull Material material) {
        this(player -> material.createBlockData());
    }

    /**
     * Create a BlockDisplay element with static block data.
     */
    public BlockDisplayElement(@NotNull BlockData blockData) {
        this(player -> blockData);
    }

    /**
     * Create a BlockDisplay element with a player-specific block data supplier.
     */
    public BlockDisplayElement(@NotNull Function<Player, BlockData> blockSupplier) {
        super(EntityTypes.BLOCK_DISPLAY);
        this.blockSupplier = blockSupplier;
    }

    @Override
    public void update(@NotNull Player player, float x, float y, float z) {
        BlockData bukkitBlockData = blockSupplier.apply(player);
        WrappedBlockState blockState = SpigotConversionUtil.fromBukkitBlockData(bukkitBlockData);

        List<EntityData<?>> entityData = new ArrayList<>();

        addDisplayBaseMetadata(entityData, x, y, z);

        entityData.add(new EntityData<>(INDEX_BLOCK_STATE, EntityDataTypes.BLOCK_STATE, blockState.getGlobalId()));

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(entityId, entityData);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }
}
