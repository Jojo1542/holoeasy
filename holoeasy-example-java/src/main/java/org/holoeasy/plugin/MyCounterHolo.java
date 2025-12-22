package org.holoeasy.plugin;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.holoeasy.HoloEasy;
import org.holoeasy.hologram.Hologram;
import org.holoeasy.line.*;
import org.holoeasy.line.composite.ItemDisplayElement;
import org.holoeasy.line.composite.TextDisplayElement;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class MyCounterHolo extends Hologram {

    private int clickCount = 0;
    private int diamondCount = 5;
    private int emeraldCount = 12;
    private int goldCount = 3;
    private final Map<UUID, Integer> playerClickCounts = new java.util.HashMap<>();

    // Composite line with items
    private final CompositeDisplayLine itemsRow = compositeLine()
            .add(new ItemDisplayElement(new ItemStack(Material.DIAMOND))
                    .scale(0.6f)
                    .width(0.5f))
            .addSpacer(0.3f)
            .add(new ItemDisplayElement(new ItemStack(Material.EMERALD))
                    .scale(0.6f)
                    .width(0.5f))
            .addSpacer(0.3f)
            .add(new ItemDisplayElement(new ItemStack(Material.GOLD_INGOT))
                    .scale(0.6f)
                    .width(0.5f))
            .yOffset(4.5f);

    // Composite line with counts below items
    private final CompositeDisplayLine countsRow = compositeLine()
            .add(new TextDisplayElement(player -> Component.text("x" + diamondCount, NamedTextColor.AQUA))
                    .scale(0.8f)
                    .width(0.5f))
            .addSpacer(0.3f)
            .add(new TextDisplayElement(player -> Component.text("x" + emeraldCount, NamedTextColor.GREEN))
                    .scale(0.8f)
                    .width(0.5f))
            .addSpacer(0.3f)
            .add(new TextDisplayElement(player -> Component.text("x" + goldCount, NamedTextColor.GOLD))
                    .scale(0.8f)
                    .width(0.5f))
            .yOffset(4.0f);

    private final DisplayTextLine global_counter = displayTextLine(player -> LegacyComponentSerializer.legacyAmpersand().deserialize("Clicked " + clickCount + " times"))
            .shadow(true)
            .backgroundColor(0x80FF0000)
            .scale(2.5f)
            .yOffset(1.55f);

    private final TextLine player_counter = textLine(player ->
            "Clicked " + playerClickCounts.getOrDefault(player.getUniqueId(), 0) + " times by " + player.getName())
            .yOffset(-1f);

    private final Line<?> interactionLine = interactionLine()
            .height(1.0f)
            .yOffset(1f);


    public void onClick(@NotNull Player player) {
        clickCount++;
        playerClickCounts.compute(player.getUniqueId(), (uuid, count) -> count == null ? 1 : count + 1);

        global_counter.updateAll();
        player_counter.update(player);
    }

    public MyCounterHolo(@NotNull HoloEasy lib, @NotNull Location location) {
        super(lib, location);
    }

}
