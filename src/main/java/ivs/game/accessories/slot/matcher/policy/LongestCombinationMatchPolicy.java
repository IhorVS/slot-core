package ivs.game.accessories.slot.matcher.policy;

import ivs.game.accessories.slot.matcher.CombinationMatch;

import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Retains all detected combinations with the longest length in each group.
 *
 * @param <M> the type of detected combination
 */
public final class LongestCombinationMatchPolicy<M extends CombinationMatch<?>>
        implements CombinationMatchPolicy<M> {

    /**
     * {@inheritDoc}
     *
     * <p>An empty input produces an empty result and the input order is
     * preserved.</p>
     */
    @Override
    public List<M> apply(List<M> matches) {
        // There is nothing to select from an empty input.
        if (matches.isEmpty()) {
            return List.of();
        }

        // Find the longest match length for every combination group.
        Map<String, Integer> longestLengthByGroup = matches.stream()
                .collect(Collectors.toMap(
                        match -> match.combination().getGroupId(),
                        CombinationMatch::length,
                        BinaryOperator.maxBy(Integer::compareTo)
                ));

        // Keep all matches at the group maximum while preserving input order.
        return matches.stream()
                .filter(match -> match.length() == longestLengthByGroup.get(
                        match.combination().getGroupId()
                ))
                .toList();
    }
}
