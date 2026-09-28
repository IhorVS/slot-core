package ivs.game.accessories.slot.field.impl;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.stream.IntStream;

/**
 * Builder for creating a field from a reel bank.
 *
 * @param <I> the type of reel items
 */
public final class ReelBankFieldBuilder<I extends ReelItem> {

    private final ReelBank<I> reelBank;

    /**
     * Creates a builder for the specified reel bank.
     *
     * @param reelBank the source reel bank
     * @throws NullPointerException if {@code reelBank} is {@code null}
     */
    public ReelBankFieldBuilder(@NonNull ReelBank<I> reelBank) {
        this.reelBank = reelBank;
    }

    /**
     * Builds a field using the specified position for each reel.
     *
     * <p>The position at index {@code reelId} is used for the reel with the
     * same index. The resulting field contains one column per reel, in the
     * same left-to-right order as the reel bank.</p>
     *
     * @param positions the positions of the reels
     * @return a field containing the visible items at the specified positions
     * @throws NullPointerException     if {@code positions} is {@code null}
     * @throws IllegalArgumentException if the number of positions does not
     *                                  match the number of reels, or a position
     *                                  is invalid for its reel
     */
    public SlotField<I> build(int... positions) {
        Validate.notNull(positions, "Positions must not be null");
        Validate.isTrue(
                positions.length == reelBank.size(),
                "Positions count must match reel bank size"
        );

        return new StandardSlotField<>(
                IntStream.range(0, positions.length)
                        .mapToObj(reelId -> reelBank.getItems(reelId, positions[reelId]))
                        .toList()
        );
    }
}
