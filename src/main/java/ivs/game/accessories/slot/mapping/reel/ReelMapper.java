package ivs.game.accessories.slot.mapping.reel;

import ivs.game.accessories.slot.reel.Reel;
import ivs.game.accessories.slot.reel.ReelItem;

import java.util.List;

/**
 * Maps an external description of one reel to a {@link Reel}.
 *
 * <p>The mapper converts string item identifiers, preserves their configured
 * order, and creates a reel with the specified number of visible items.</p>
 *
 * <p>The particular reel implementation is selected by the mapper
 * implementation.</p>
 *
 * @param <I> the type of items contained in the reel
 */
public interface ReelMapper<I extends ReelItem> {

    /**
     * Maps string item identifiers to a reel.
     *
     * @param values      external reel item identifiers in their physical order
     * @param visibleSize the number of items visible on the reel
     * @return mapped reel
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws RuntimeException     if an item cannot be mapped or the reel cannot
     *                              be created
     */
    Reel<I> map(List<String> values, int visibleSize);
}
