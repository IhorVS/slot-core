package ivs.game.accessories.slot.reel;

import java.util.List;

/**
 * Represents a cyclic slot machine reel containing a sequence of items.
 *
 * @param <I> the type of items contained in the reel
 */
public interface Reel<I extends ReelItem> {

    /**
     * Returns the items visible on the reel starting at the specified position.
     *
     * <p>Since the reel is cyclic, the returned sequence continues from the
     * beginning when it reaches the end of the reel. The number of returned
     * items is determined by the reel implementation and may, for example,
     * be configured through its constructor.</p>
     *
     * @param position the zero-based position at which to start reading items
     * @return the sequence of items starting at the specified position
     */
    List<I> getItems(int position);

    /**
     * Returns the total number of items in the reel.
     *
     * @return the number of items in the reel
     */
    int size();

}
