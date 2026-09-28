package ivs.game.accessories.slot.mapping.reel;

import java.util.List;

/**
 * Describes one reel using external string item identifiers.
 *
 * <p>Item identifiers are stored in their physical order on the reel. The
 * description also specifies how many items must be visible when a reel is
 * created.</p>
 *
 * <p>The class does not determine the reel item type or reel implementation.
 * These decisions are delegated to the configured {@link ReelMapper}.</p>
 *
 * @param values      external reel item identifiers in their physical order
 * @param visibleSize the number of items visible on the reel
 */
public record ReelDescription(
        List<String> values,
        int visibleSize
) {
}
