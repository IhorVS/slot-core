package ivs.game.accessories.slot.reel.impl;

import ivs.game.accessories.slot.reel.Reel;
import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.List;

/**
 * Standard reel bank backed by a fixed list of reels.
 *
 * <p>The bank preserves the order of the supplied reels: reel {@code 0} is
 * the leftmost reel. Each request is delegated to the selected reel after
 * validating the reel identifier and position.</p>
 *
 * @param <I> the type of items contained in the reels
 */
public class StandardReelBank<I extends ReelItem> implements ReelBank<I> {
    private final List<Reel<I>> reels;

    /**
     * Creates a reel bank with the specified reels.
     *
     * <p>The supplied list is copied, so subsequent changes to the list do not
     * affect this bank.</p>
     *
     * @param reels the reels in left-to-right order
     * @throws NullPointerException     if {@code reels} is {@code null}, or
     *                                  contains {@code null}
     * @throws IllegalArgumentException if {@code reels} is empty
     */
    public StandardReelBank(@NonNull List<Reel<I>> reels) {
        Validate.isTrue(!reels.isEmpty(), "Reels list must contain at least one reel");
        Validate.noNullElements(reels, "Null reels not allowed");

        this.reels = List.copyOf(reels);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The position is validated against the size of the selected reel.
     * The returned items are copied into a new unmodifiable list, so the bank
     * does not depend on the mutability or list ownership of the selected
     * reel. The list must contain at least one item.</p>
     *
     * @throws IllegalArgumentException if {@code reelId} is outside the range
     *                                  {@code 0..size() - 1}, if
     *                                  {@code position} is outside the range
     *                                  {@code 0..reel.size() - 1}, or if the
     *                                  selected reel returns an empty list
     * @throws NullPointerException     if the selected reel returns {@code null}
     */
    @Override
    public List<I> getItems(int reelId, int position) {
        Validate.inclusiveBetween(0, reels.size() - 1, reelId, "Invalid reel id: %d".formatted(reelId));
        Reel<I> reel = reels.get(reelId);
        Validate.inclusiveBetween(0, reel.size() - 1, position, "Invalid position %d of reel %d".formatted(position, reelId));

        List<I> items = Validate.notNull(reel.getItems(position), "Reel %d is null".formatted(reelId));
        Validate.isTrue(!items.isEmpty(), "Items must contain at least one item");

        return List.copyOf(items);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return reels.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getReelSize(int reelId) {
        Validate.inclusiveBetween(0, reels.size() - 1, reelId, "Invalid reel id: %d".formatted(reelId));

        return reels.get(reelId).size();
    }
}
