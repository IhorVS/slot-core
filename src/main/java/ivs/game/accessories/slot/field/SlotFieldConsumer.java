package ivs.game.accessories.slot.field;

import ivs.game.accessories.slot.reel.ReelItem;

/**
 * An action accepting an item and its position on a slot field and deciding
 * whether field traversal should continue.
 *
 * @param <I> the type of reel item
 */
@FunctionalInterface
public interface SlotFieldConsumer<I extends ReelItem> {

    /**
     * Performs this action.
     *
     * @param item   the item on the field
     * @param column the zero-based column index
     * @param row    the zero-based row index within the column
     * @return {@code true} to continue traversal, or {@code false} to stop it
     */
    boolean accept(I item, int column, int row);
}
