package ivs.game.accessories.slot.matcher.scatter;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.matcher.AbstractCombinationMatcher;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.policy.CombinationMatchPolicy;
import ivs.game.accessories.slot.reel.ReelItem;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Detects scatter combinations anywhere on a slot field.
 *
 * @param <I> the type of reel items on the field
 */
public final class ScatterCombinationMatcher<I extends ReelItem>
        extends AbstractCombinationMatcher<I, ScatterCombinationMatch<I>> {
    private final SlotField<I> field;
    private final List<Combination<I>> combinations;

    /**
     * Creates a matcher for one field snapshot.
     *
     * @param field        the field to inspect
     * @param combinations combinations to detect
     * @param policy       the policy applied to detected combinations
     * @throws NullPointerException     if an argument or combination is null
     * @throws IllegalArgumentException if combinations is empty
     */
    public ScatterCombinationMatcher(
            SlotField<I> field,
            Collection<Combination<I>> combinations,
            CombinationMatchPolicy<ScatterCombinationMatch<I>> policy
    ) {
        super(policy);
        this.field = Validate.notNull(field, "Field must not be null");
        Validate.notNull(combinations, "Combinations must not be null");
        Validate.isTrue(
                !combinations.isEmpty(),
                "Combinations must contain at least one combination"
        );
        Validate.noNullElements(combinations, "Combinations must not contain null elements");
        this.combinations = List.copyOf(combinations);
    }

    /**
     * {@inheritDoc}
     *
     * <p>For each matched combination, positions are returned in the same
     * order as the corresponding items in the combination. The returned list
     * and all position lists in its matches are unmodifiable.</p>
     */
    @Override
    protected List<ScatterCombinationMatch<I>> findMatches() {
        List<ScatterCombinationMatch<I>> matches = new ArrayList<>();
        Map<I, List<FieldPosition>> positionsByItem = indexField();

        for (Combination<I> combination : combinations) {
            // Match repeated symbols to successive unique positions.
            Map<I, Integer> nextPositionByItem = new HashMap<>();
            List<FieldPosition> positions = new ArrayList<>();
            for (I requiredItem : combination.getItems()) {
                List<FieldPosition> availablePositions =
                        positionsByItem.getOrDefault(requiredItem, List.of());
                int nextPosition = nextPositionByItem.getOrDefault(requiredItem, 0);
                if (nextPosition >= availablePositions.size()) {
                    positions.clear();
                    break;
                }
                positions.add(availablePositions.get(nextPosition));
                nextPositionByItem.put(requiredItem, nextPosition + 1);
            }

            if (!positions.isEmpty()) {
                matches.add(new ScatterCombinationMatch<>(
                        combination,
                        positions
                ));
            }
        }

        return List.copyOf(matches);
    }

    private Map<I, List<FieldPosition>> indexField() {
        Map<I, List<FieldPosition>> positionsByItem = new HashMap<>();
        field.forEach((item, column, row) -> {
            positionsByItem
                    .computeIfAbsent(item, ignored -> new ArrayList<>())
                    .add(new FieldPosition(column, row));
            return true;
        });
        return positionsByItem;
    }
}
