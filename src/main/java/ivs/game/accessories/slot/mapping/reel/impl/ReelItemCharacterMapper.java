package ivs.game.accessories.slot.mapping.reel.impl;

import ivs.game.accessories.slot.mapping.reel.ReelItemMapper;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.apache.commons.lang3.Validate;

/**
 * Maps single-character identifiers to {@link StandardReelItem} values.
 *
 * <pre>
 * "A" -> StandardReelItem.A
 * "?" -> StandardReelItem.WLD
 * "@" -> StandardReelItem.SCT
 * "&" -> StandardReelItem.BON
 * "!" -> StandardReelItem.JPT
 * "*" -> StandardReelItem.MUL
 * </pre>
 */
public final class ReelItemCharacterMapper implements ReelItemMapper<StandardReelItem> {

    /**
     * Maps a single-character identifier to a standard reel item.
     *
     * @param value single-character reel item identifier
     * @return corresponding standard reel item
     * @throws NullPointerException     if {@code value} is {@code null}
     * @throws IllegalArgumentException if the value does not contain exactly one
     *                                  character or the character is unknown
     */
    @Override
    public StandardReelItem map(String value) {
        Validate.notBlank(value, "Reel item character must not be null or blank");
        Validate.isTrue(value.length() == 1,
                "Reel item character must contain exactly one character: '%s'", value);

        try {
            return StandardReelItem.valueOf(value.charAt(0));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown standard reel item character: '%s'".formatted(value), e
            );
        }
    }
}
