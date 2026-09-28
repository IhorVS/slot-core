package ivs.game.accessories.slot.field;

import ivs.game.accessories.slot.reel.ReelItem;

import java.util.Objects;

/**
 * Represents the visible area of a slot machine.
 *
 * <p>The field consists of vertical columns. Columns are indexed from left to
 * right, starting at {@code 0}. Items within each column are indexed from top
 * to bottom, also starting at {@code 0}. Both column and item indexes are
 * continuous and have no gaps.</p>
 *
 * <p>Columns may have different sizes. Therefore, the valid row range depends
 * on the selected column and is {@code 0..getColumnSize(column) - 1}.</p>
 *
 * <p>For example, a rectangular field with five columns and three items in
 * each column is indexed as follows:</p>
 *
 * <pre>
 *          column
 *             0   1   2   3   4
 *           ---------------------
 * row 0   |   A   A   A   A   A
 * row 1   |   B   B   B   B   B
 * row 2   |   C   C   C   C   C
 * </pre>
 *
 * <p>The item at column {@code 2} and row {@code 1} is obtained with
 * {@code getItem(2, 1)} and is {@code B} in this example.</p>
 *
 * @param <I> the type of reel items displayed on the field
 */
public interface SlotField<I extends ReelItem> {

    /**
     * Performs the specified action for each item on the field.
     *
     * <p>Items are visited column by column from left to right. Within each
     * column, items are visited from top to bottom. In other words, the
     * {@code false} from the action stops the traversal immediately.</p>
     *
     * <pre>
     * (column 0, row 0), (column 0, row 1), ..., (column 1, row 0), ...
     * </pre>
     *
     * @param action the action to perform for each item; it receives the item,
     *               its column index, and its row index, in that order
     * @throws NullPointerException if {@code action} is {@code null}
     */
    default void forEach(SlotFieldConsumer<? super I> action) {
        Objects.requireNonNull(action, "Action must not be null");

        for (int column = 0; column < getColumnCount(); column++) {
            for (int row = 0; row < getColumnSize(column); row++) {
                if (!action.accept(getItem(column, row), column, row)) {
                    return;
                }
            }
        }
    }

    /**
     * Returns the item at the specified column and row.
     *
     * @param column the zero-based column index, counted from left to right
     * @param row    the zero-based row index within the selected column,
     *               counted from top to bottom
     * @return the item at the specified position
     * @throws IllegalArgumentException if {@code column} or {@code row} is
     *                                  outside the valid range
     */
    I getItem(int column, int row);

    /**
     * Returns the number of columns.
     *
     * @return the number of columns
     */
    int getColumnCount();

    /**
     * Returns the number of items in the column.
     *
     * @param column the zero-based column index, counted from left to right
     * @return the number of items in the column
     * @throws IllegalArgumentException if {@code column} is outside the range
     *                                  {@code 0..getColumnCount() - 1}
     */
    int getColumnSize(int column);
}
