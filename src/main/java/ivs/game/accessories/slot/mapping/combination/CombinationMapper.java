package ivs.game.accessories.slot.mapping.combination;

import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.reel.ReelItem;

/**
 * Maps an external combination description to a {@link Combination}.
 *
 * <p>The mapper converts string item identifiers and preserves their
 * configured order.</p>
 *
 * @param <I> the type of items contained in the combination
 */
public interface CombinationMapper<I extends ReelItem> {

    /**
     * Maps one combination description.
     *
     * @param description external combination description
     * @return mapped combination
     * @throws NullPointerException if {@code description} is {@code null}
     * @throws RuntimeException     if an item cannot be mapped or the
     *                              combination cannot be created
     */
    Combination<I> map(CombinationDescription description);
}
