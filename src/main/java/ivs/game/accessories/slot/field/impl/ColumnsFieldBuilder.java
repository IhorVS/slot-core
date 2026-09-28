package ivs.game.accessories.slot.field.impl;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.reel.ReelItem;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder for creating a field from columns of reel items.
 *
 * @param <I> the type of reel items
 */
public final class ColumnsFieldBuilder<I extends ReelItem> {

    private final List<List<I>> columns = new ArrayList<>();

    /**
     * Adds the next column to the field.
     *
     * @param items the items in the column, ordered from top to bottom
     * @return this builder
     * @throws NullPointerException     if {@code items} is {@code null}
     * @throws IllegalArgumentException if the column is empty or contains a
     *                                  {@code null} item
     */
    @SafeVarargs
    public final ColumnsFieldBuilder<I> addColumn(I... items) {
        Validate.notNull(items, "Column items must not be null");
        return addColumn(Arrays.asList(items));
    }

    /**
     * Adds the next column to the field.
     *
     * @param items the items in the column, ordered from top to bottom
     * @return this builder
     * @throws NullPointerException     if {@code items} is {@code null}
     * @throws IllegalArgumentException if the column is empty or contains a
     *                                  {@code null} item
     */
    public ColumnsFieldBuilder<I> addColumn(List<? extends I> items) {
        Validate.notNull(items, "Column items must not be null");
        Validate.isTrue(!items.isEmpty(), "Column must contain at least one item");
        Validate.noNullElements(items, "Null reel items not allowed");

        List<I> column = List.copyOf(items);
        columns.add(column);
        return this;
    }

    /**
     * Builds a field from the columns added to this builder.
     *
     * @return a field containing the added columns
     * @throws IllegalStateException if no columns have been added
     */
    public SlotField<I> build() {
        Validate.validState(!columns.isEmpty(), "Field must contain at least one column");
        return new StandardSlotField<>(columns);
    }
}
