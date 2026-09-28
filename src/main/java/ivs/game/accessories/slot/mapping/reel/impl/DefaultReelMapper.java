package ivs.game.accessories.slot.mapping.reel.impl;

import ivs.game.accessories.slot.mapping.reel.ReelItemListMapper;
import ivs.game.accessories.slot.mapping.reel.ReelMapper;
import ivs.game.accessories.slot.reel.Reel;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Maps string item identifiers to a reel using configured item and reel
 * mapping components.
 *
 * <p>The item list mapper converts source values to reel items. The reel
 * creator then creates the required {@link Reel} implementation from the
 * mapped items and visible size.</p>
 *
 * @param <I> the type of items contained in the reel
 */
@RequiredArgsConstructor
public final class DefaultReelMapper<I extends ReelItem> implements ReelMapper<I> {

    @NonNull
    private final ReelItemListMapper<I> itemListMapper;

    @NonNull
    private final BiFunction<List<I>, Integer, ? extends Reel<I>> reelCreator;

    /**
     * Maps string item identifiers and creates a reel.
     *
     * @param values      external reel item identifiers in their physical order
     * @param visibleSize the number of items visible on the reel
     * @return mapped reel
     * @throws NullPointerException if {@code values} is {@code null}, or the
     *                              reel creator returns {@code null}
     * @throws RuntimeException     if an item cannot be mapped or the reel cannot
     *                              be created
     */
    @Override
    public Reel<I> map(@NonNull List<String> values, int visibleSize) {
        Validate.noNullElements(values, "List must not contain null elements");

        List<I> items = itemListMapper.map(values);
        Reel<I> reel = reelCreator.apply(items, visibleSize);
        return Validate.notNull(reel, "Reel items list must not be null");
    }
}
