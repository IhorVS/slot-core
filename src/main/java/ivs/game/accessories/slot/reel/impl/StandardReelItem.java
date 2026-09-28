package ivs.game.accessories.slot.reel.impl;

import ivs.game.accessories.slot.reel.ReelItem;

import java.util.Arrays;

/**
 * Standard set of items that can appear on a slot machine reel.
 *
 * <p>Alphabetic items use their character code as an identifier. Special
 * items use the following identifiers: {@code ?} for a wild item,
 * {@code @} for a scatter item, {@code &} for a bonus item, and {@code !}
 * for a jackpot item.</p>
 */
public enum StandardReelItem implements ReelItem {
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,

    /**
     * Wild
     */
    WLD,

    /**
     * Scatter
     */
    SCT,

    /**
     * Bonus
     */
    BON,

    /**
     * Jackpot
     */
    JPT,

    /**
     * Multiplier
     */
    MUL;

    @Override
    public int getId() {
        return switch (this) {
            case A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z -> this.name().charAt(0);
            case WLD -> '?';
            case SCT -> '@';
            case BON -> '&';
            case JPT -> '!';
            case MUL -> '*';
        };
    }

    /**
     * Returns the reel item associated with the specified identifier.
     *
     * @param id the item identifier
     * @return the matching reel item
     * @throws IllegalArgumentException if no reel item has the specified
     *                                  identifier
     */
    public static StandardReelItem valueOf(int id) {
        return Arrays.stream(values())
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Reel Item: " + id));
    }

    /**
     * Returns the reel item associated with the specified character.
     *
     * @param itemCharacter the item character
     * @return the matching reel item
     * @throws IllegalArgumentException if no reel item has the specified
     *                                  character
     */
    public static StandardReelItem valueOf(char itemCharacter) {
        return Arrays.stream(values())
                .filter(item -> item.getId() == itemCharacter)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Reel Item: " + itemCharacter));
    }
}
