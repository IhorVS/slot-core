package ivs.game.accessories.slot.mapping.reel;

import ivs.game.accessories.slot.reel.ReelItem;

/**
 * Maps an external string identifier to a reel item.
 *
 * <p>The mapper does not define how reel items are stored or created. An
 * implementation may resolve an enum constant, retrieve an item from a registry,
 * or construct a custom reel item.</p>
 *
 * <p>For an enum-based reel item implementation, the mapper can be created with
 * a method reference:</p>
 *
 * <pre>
 * ReelItemMapper&lt;StandardReelItem&gt; mapper = StandardReelItem::valueOf;
 * </pre>
 *
 * @param <T> reel item type
 */
@FunctionalInterface
public interface ReelItemMapper<T extends ReelItem> {

    /**
     * Maps an external string identifier to a reel item.
     *
     * @param value external reel item identifier
     * @return mapped reel item
     * @throws RuntimeException if the value cannot be mapped
     */
    T map(String value);
}
