package ivs.game.accessories.slot.matcher;

import ivs.game.accessories.slot.reel.ReelItem;

import java.util.List;

/**
 * Describes one combination detected by a matcher.
 *
 * @param <I> the type of reel items on the field
 */
public interface CombinationMatch<I extends ReelItem> {

    /**
     * Returns the combination detected by the matcher.
     *
     * @return the detected combination
     */
    Combination<I> combination();

    /**
     * Returns the positions participating in the combination.
     *
     * @return the matched positions
     */
    List<FieldPosition> positions();

    /**
     * Returns the number of positions participating in the combination.
     *
     * @return the combination length
     */
    default int length() {
        return positions().size();
    }
}
