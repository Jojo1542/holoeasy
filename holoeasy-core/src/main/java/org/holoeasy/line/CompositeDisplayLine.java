package org.holoeasy.line;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.holoeasy.hologram.Hologram;
import org.holoeasy.line.composite.AbstractDisplayElement;
import org.holoeasy.line.composite.CompositeElement;
import org.holoeasy.line.composite.SpacerElement;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A composite line that can contain multiple display elements arranged horizontally.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * new CompositeDisplayLine(hologram)
 *     .add(new ItemDisplayElement(diamondSword).scale(0.5f))
 *     .addSpacer(0.3f)
 *     .add(new TextDisplayElement("x5"))
 *     .addSpacer(0.3f)
 *     .add(new ItemDisplayElement(emerald).scale(0.5f))
 * }</pre>
 *
 * <p>This displays: [item] [space] [text] [space] [item] horizontally centered.</p>
 */
@ApiStatus.Experimental
public class CompositeDisplayLine extends Line<Void> {

    private final List<CompositeElement> elements = new ArrayList<>();

    // Alignment options
    private Alignment alignment = Alignment.CENTER;

    // Y translation for all elements
    private float yTranslation = 0.0f;

    public CompositeDisplayLine(Hologram hologram) {
        // We use a dummy entity type since we don't spawn a single entity
        super(hologram, EntityTypes.MARKER, player -> null);
    }

    @Override
    public @NotNull Type getType() {
        return Type.COMPOSITE_LINE;
    }

    /**
     * Add a display element to the composite line.
     */
    public CompositeDisplayLine add(@NotNull CompositeElement element) {
        elements.add(element);
        return this;
    }

    /**
     * Add a spacer with the specified width.
     * @param width the horizontal space in blocks
     */
    public CompositeDisplayLine addSpacer(float width) {
        elements.add(new SpacerElement(width));
        return this;
    }

    /**
     * Set horizontal alignment for the composite elements.
     */
    public CompositeDisplayLine alignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    /**
     * Set Y offset for all elements (use Line.yOffset for hologram-level offset).
     */
    public CompositeDisplayLine yTranslation(float y) {
        this.yTranslation = y;
        return this;
    }

    /**
     * Set Y offset for positioning in the hologram.
     */
    public CompositeDisplayLine yOffset(double yOffset) {
        super.setYOffset(yOffset);
        return this;
    }

    @Override
    public void show(@NotNull Player player) {
        Location loc = getLocation();
        if (loc == null) return;

        float[] offsets = calculateOffsets();

        for (int i = 0; i < elements.size(); i++) {
            CompositeElement element = elements.get(i);
            if (element.hasEntity()) {
                if (element instanceof AbstractDisplayElement) {
                    ((AbstractDisplayElement<?, ?>) element).setSpawnLocation(loc);
                }
                element.spawn(player, offsets[i], yTranslation, 0);
            }
        }
    }

    @Override
    public void hide(@NotNull Player player) {
        for (CompositeElement element : elements) {
            if (element.hasEntity()) {
                element.despawn(player);
            }
        }
    }

    @Override
    public void update(@NotNull Player player) {
        float[] offsets = calculateOffsets();

        for (int i = 0; i < elements.size(); i++) {
            CompositeElement element = elements.get(i);
            if (element.hasEntity()) {
                element.update(player, offsets[i], yTranslation, 0);
            }
        }
    }

    /**
     * Calculate X offsets for each element based on alignment.
     */
    private float[] calculateOffsets() {
        float[] offsets = new float[elements.size()];
        float totalWidth = 0;

        // Calculate total width
        for (CompositeElement element : elements) {
            totalWidth += element.getWidth();
        }

        // Calculate starting position based on alignment
        float startX;
        switch (alignment) {
            case LEFT:
                startX = 0;
                break;
            case RIGHT:
                startX = -totalWidth;
                break;
            case CENTER:
            default:
                startX = -totalWidth / 2;
                break;
        }

        // Calculate offset for each element (center of each element)
        float currentX = startX;
        for (int i = 0; i < elements.size(); i++) {
            float elementWidth = elements.get(i).getWidth();
            offsets[i] = currentX + (elementWidth / 2);
            currentX += elementWidth;
        }

        return offsets;
    }

    /**
     * Get the list of elements in this composite.
     */
    public List<CompositeElement> getElements() {
        return new ArrayList<>(elements);
    }

    /**
     * Get the total width of all elements combined.
     */
    public float getTotalWidth() {
        float total = 0;
        for (CompositeElement element : elements) {
            total += element.getWidth();
        }
        return total;
    }

    /**
     * Horizontal alignment options for composite elements.
     */
    public enum Alignment {
        /** Elements start from the left of the spawn point */
        LEFT,
        /** Elements are centered on the spawn point */
        CENTER,
        /** Elements end at the spawn point */
        RIGHT
    }
}
