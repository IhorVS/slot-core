package ivs.game.accessories.slot.matcher.linear;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.matcher.AbstractCombinationMatcher;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.policy.CombinationMatchPolicy;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Detects combinations on selected lines of a slot field.
 *
 * <p>Each line is checked from left to right, starting at its first position.
 * Every configured combination is checked independently against the
 * corresponding prefix of the line. A field item matches a combination item
 * when it is equal to that item or is one of the configured wild substitutes.</p>
 *
 * <p>The matcher first detects all matching combinations. The configured
 * policy then determines which matches are returned.</p>
 *
 * @param <I> the type of reel items on the field
 */
public final class LinearCombinationMatcher<I extends ReelItem>
        extends AbstractCombinationMatcher<I, LinearCombinationMatch<I>> {
    private final SlotField<I> field;
    private final List<FieldLine> lines;
    private final List<Combination<I>> combinations;
    private final Set<I> wildSubstitutes;

    /**
     * Creates a matcher for one field snapshot.
     *
     * @param field           the field to inspect
     * @param lines           the selected lines to inspect
     * @param combinations    the combinations to detect
     * @param wildSubstitutes the field items that substitute combination items
     * @param policy          the policy applied to detected combinations
     * @throws NullPointerException     if an argument or its element is null
     * @throws IllegalArgumentException if {@code lines} or
     *                                  {@code combinations} is empty
     */
    public LinearCombinationMatcher(
            @NonNull SlotField<I> field,
            @NonNull Collection<FieldLine> lines,
            @NonNull Collection<Combination<I>> combinations,
            @NonNull Set<I> wildSubstitutes,
            @NonNull CombinationMatchPolicy<LinearCombinationMatch<I>> policy
    ) {
        super(policy);
        this.field = field;

        Validate.isTrue(!lines.isEmpty(), "Lines must contain at least one line");
        Validate.noNullElements(lines, "Lines must not contain null elements");
        this.lines = List.copyOf(lines);

        Validate.isTrue(
                !combinations.isEmpty(),
                "Combinations must contain at least one combination"
        );
        Validate.noNullElements(combinations, "Combinations must not contain null elements");
        this.combinations = List.copyOf(combinations);

        Validate.noNullElements(
                wildSubstitutes,
                "Wild substitutes must not contain null elements"
        );
        this.wildSubstitutes = Set.copyOf(wildSubstitutes);
    }

    /**
     * Finds raw matches before applying the configured policy.
     *
     * <p>For every selected line, combinations are checked in the order in
     * which they were provided.</p>
     */
    @Override
    protected List<LinearCombinationMatch<I>> findMatches() {
        List<LinearCombinationMatch<I>> matches = new ArrayList<>();

        for (FieldLine line : lines) {
            for (Combination<I> combination : combinations) {
                if (combinationMatches(line, combination)) {
                    matches.add(new LinearCombinationMatch<>(
                            combination,
                            combinationPositions(line, combination),
                            line
                    ));
                }
            }
        }

        return List.copyOf(matches);
    }

    private boolean combinationMatches(
            FieldLine line,
            Combination<I> combination
    ) {
        if (combination.size() > line.positions().size()) {
            return false;
        }

        for (int index = 0; index < combination.size(); index++) {
            I actualItem = field.getItem(
                    line.positions().get(index).column(),
                    line.positions().get(index).row()
            );
            I requiredItem = combination.getItems().get(index);
            if (!actualItem.equals(requiredItem) && !wildSubstitutes.contains(actualItem)) {
                return false;
            }
        }

        return true;
    }

    private List<FieldPosition> combinationPositions(
            FieldLine line,
            Combination<I> combination
    ) {
        return List.copyOf(line.positions().subList(0, combination.size()));
    }
}
