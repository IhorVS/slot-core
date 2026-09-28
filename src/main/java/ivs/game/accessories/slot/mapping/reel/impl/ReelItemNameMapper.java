package ivs.game.accessories.slot.mapping.reel.impl;

import ivs.game.accessories.slot.mapping.reel.ReelItemMapper;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.apache.commons.lang3.Validate;

/**
 * Maps enum constant names to {@link StandardReelItem} values.
 *
 * <pre>
 * "A"   -> StandardReelItem.A
 * "WLD" -> StandardReelItem.WLD
 * "SCT" -> StandardReelItem.SCT
 * </pre>
 */
public final class ReelItemNameMapper implements ReelItemMapper<StandardReelItem> {

    /**
     * Maps an enum constant name to a standard reel item.
     *
     * @param value exact enum constant name
     * @return corresponding standard reel item
     * @throws NullPointerException     if {@code value} is {@code null}
     * @throws IllegalArgumentException if the value does not identify a standard reel item
     */
    @Override
    public StandardReelItem map(String value) {
        Validate.notBlank(value, "Reel item name must not be null or blank");

        try {
            return StandardReelItem.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown standard reel item name: '%s'".formatted(value), e);
        }
    }
}

