package ivs.game.accessories.slot.engine;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.CombinationMatch;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains the result of one slot spin.
 *
 * <p>Instances returned by the standard engine contain unmodifiable
 * collections, including nested prize sets. Reel positions are copied
 * on construction and on access.</p>
 *
 * <p>Repeated matches of one combination remain separate in the match list
 * and share one entry in the prize map. Matches without configured prizes
 * remain in the match list.</p>
 *
 * @param <I> the type of reel items
 */
@Getter
public final class SpinResult<I extends ReelItem> {

    /**
     * Physical reel positions, ordered from left to right.
     */
    @Getter(AccessLevel.NONE)
    private final int[] reelPositions;

    /**
     * The field generated for this spin.
     */
    private final SlotField<I> field;

    /**
     * Detected matches in matcher execution order.
     */
    private final List<CombinationMatch<I>> matches;

    /**
     * Configured prize identifiers for the detected combinations.
     */
    private final Map<Combination<I>, Set<String>> prizesByCombination;

    /**
     * Creates a spin result, copying the supplied reel positions.
     *
     * <p>The field, match list, and prize map are retained by reference.</p>
     *
     * @param reelPositions physical reel positions
     * @param field         the generated field
     * @param matches       detected matches in execution order
     * @param prizesByCombination prize identifiers by combination
     * @throws NullPointerException if reel positions are null
     */
    public SpinResult(
            int[] reelPositions,
            SlotField<I> field,
            List<CombinationMatch<I>> matches,
            Map<Combination<I>, Set<String>> prizesByCombination
    ) {
        this.reelPositions = reelPositions.clone();
        this.field = field;
        this.matches = matches;
        this.prizesByCombination = prizesByCombination;
    }

    /**
     * Returns a copy of the physical reel positions.
     *
     * @return reel positions ordered from left to right
     */
    public int[] getReelPositions() {
        return reelPositions.clone();
    }
}
