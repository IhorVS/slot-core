package ivs.game.accessories.slot.reel;

import java.util.List;

/**
 * Represents a set of slot machine reels.
 *
 * <p>Reels are indexed from left to right, starting with index {@code 0}.</p>
 *
 * @param <I> the type of items contained in the reels
 */
public interface ReelBank<I extends ReelItem> {

    /**
     * Returns the items currently visible on the specified reel and position.
     *
     * @param reelId   the zero-based index of the reel, counted from left to right
     * @param position the zero-based position of the first visible item on the reel
     * @return the visible items on the specified reel
     * @throws IllegalArgumentException if {@code reelId} is outside the range
     *                                  {@code 0..size() - 1}, or if
     *                                  {@code position} is outside the range
     *                                  {@code 0..reel.size() - 1} for the
     *                                  selected reel
     */
    List<I> getItems(int reelId, int position);

    /**
     * Returns the number of reels in this set.
     *
     * @return the size of this reel bank
     */
    int size();

    /**
     * Returns the total number of physical positions on the specified reel.
     *
     * @param reelId the zero-based index of the reel
     * @return the number of positions on the reel
     * @throws IllegalArgumentException if {@code reelId} is outside the range
     *                                  {@code 0..size() - 1}
     */
    int getReelSize(int reelId);
}
