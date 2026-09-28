package ivs.game.accessories.slot.field.impl;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.List;

/**
 * Standard slot field backed by a list of columns.
 *
 * <p>The outer list contains columns from left to right. Each inner list
 * contains the items of one column from top to bottom. Both lists are copied,
 * so subsequent changes to the supplied lists do not affect this field.</p>
 *
 * @param <I> the type of reel items displayed on the field
 */
public final class StandardSlotField<I extends ReelItem> implements SlotField<I> {
    private final List<List<I>> columns;

    /**
     * Creates a slot field from the specified columns.
     *
     * @param columns the columns from left to right, with items in each column
     *                ordered from top to bottom
     * @throws NullPointerException     if {@code columns} or a column is
     *                                  {@code null}
     * @throws IllegalArgumentException if {@code columns} is empty, contains
     *                                  an empty column, or contains a
     *                                  {@code null} item
     */
    public StandardSlotField(@NonNull List<List<I>> columns) {
        Validate.isTrue(!columns.isEmpty(), "Columns must contain at least one column");

        this.columns = columns.stream()
                .map(column -> {
                    Validate.notNull(column, "Null column not allowed");
                    Validate.isTrue(!column.isEmpty(), "Columns must contain at least one item");
                    Validate.noNullElements(column, "Null reel items not allowed");

                    return List.copyOf(column);
                })
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public I getItem(int column, int row) {
        List<I> selectedColumn = getColumn(column);
        Validate.inclusiveBetween(
                0,
                selectedColumn.size() - 1,
                row,
                "Invalid row %d of column %d".formatted(row, column)
        );
        return selectedColumn.get(row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
        return columns.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnSize(int column) {
        return getColumn(column).size();
    }

    private List<I> getColumn(int column) {
        Validate.inclusiveBetween(
                0,
                columns.size() - 1,
                column,
                "Invalid column: %d".formatted(column)
        );
        return columns.get(column);  // Unmodifiable collection
    }
}
