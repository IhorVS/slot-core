package ivs.game.accessories.slot.reel.impl;

import ivs.game.accessories.slot.reel.Reel;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Standard cyclic reel backed by a fixed sequence of items.
 *
 * <p>Each call to {@link #getItems(int)} returns a new unmodifiable list
 * containing the configured number of visible items.</p>
 *
 * @param <I> the type of items contained in the reel
 */
public class StandardReel<I extends ReelItem> implements Reel<I> {
    private final List<I> items;
    private final int visibleSize;

    /**
     * Creates a reel with the specified items and visible item count.
     *
     * @param items       the items in the reel
     * @param visibleSize the number of items returned by {@link #getItems(int)}
     * @throws NullPointerException     if {@code items} is {@code null}
     * @throws IllegalArgumentException if {@code items} is empty, contains
     *                                  {@code null}, or {@code visibleSize}
     *                                  is not positive
     */
    public StandardReel(@NonNull List<I> items, int visibleSize) {
        Validate.isTrue(!items.isEmpty(), "Reel items must contain at least one item");
        Validate.noNullElements(items, "Null reel items not allowed");
        this.items = List.copyOf(items);

        Validate.isTrue(visibleSize > 0, "Visible size must be greater than zero");
        this.visibleSize = visibleSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<I> getItems(int position) {
        int reelSize = items.size();
        Validate.inclusiveBetween(0, reelSize - 1, position, "Position %d out of reel".formatted(position));

        if (position + visibleSize <= reelSize) {
            return items.subList(position, position + visibleSize)
                    .stream()
                    .toList();
        }

        return IntStream.range(0, visibleSize)
                .mapToObj(offset -> items.get((position + offset) % reelSize))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return items.size();
    }
}
