package ivs.game.accessories.slot.mapping.reel;

import ivs.game.accessories.slot.reel.ReelItem;

import java.util.List;

/**
 * Maps a list of external string identifiers to reel items.
 *
 * <p>The order of source values must be preserved in the resulting list.</p>
 *
 * @param <T> reel item type
 */
public interface ReelItemListMapper<T extends ReelItem> {

    /**
     * Maps external string identifiers to reel items.
     *
     * @param values external reel item identifiers
     * @return mapped reel items in their original order
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws RuntimeException     if a value cannot be mapped
     */
    List<T> map(List<String> values);
}
