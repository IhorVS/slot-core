package ivs.game.accessories.slot.matcher.linear;

import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.CombinationMatch;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.reel.ReelItem;
import org.apache.commons.lang3.Validate;

import java.util.List;

/**
 * A combination detected on a specific slot line.
 *
 * @param <I>         the type of reel items on the field
 * @param combination the combination detected by the matcher
 * @param positions   positions participating in the combination
 * @param line        the line on which the combination was detected
 */
public record LinearCombinationMatch<I extends ReelItem>(
        Combination<I> combination,
        List<FieldPosition> positions,
        FieldLine line
) implements CombinationMatch<I> {

    public LinearCombinationMatch {
        Validate.notNull(combination, "Combination must not be null");
        Validate.notNull(positions, "Positions must not be null");
        Validate.noNullElements(positions, "Positions must not contain null elements");
        Validate.isTrue(!positions.isEmpty(), "Match must contain at least one position");
        Validate.notNull(line, "Line must not be null");

        positions = List.copyOf(positions);
    }
}
