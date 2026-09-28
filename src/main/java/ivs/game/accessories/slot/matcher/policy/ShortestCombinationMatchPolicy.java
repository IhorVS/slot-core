package ivs.game.accessories.slot.matcher.policy;

import ivs.game.accessories.slot.matcher.CombinationMatch;

import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Retains all detected combinations with the shortest length in each group.
 *
 * @param <M> the type of detected combination
 */
public final class ShortestCombinationMatchPolicy<M extends CombinationMatch<?>>
        implements CombinationMatchPolicy<M> {

    /**
     * {@inheritDoc}
     *
     * <p>An empty input produces an empty result and the input order is
     * preserved.</p>
     */
    @Override
    public List<M> apply(List<M> matches) {
        if (matches.isEmpty()) {
            return List.of();
        }

        Map<String, Integer> shortestLengthByGroup = matches.stream()
                .collect(Collectors.toMap(
                        match -> match.combination().getGroupId(),
                        CombinationMatch::length,
                        BinaryOperator.minBy(Integer::compareTo)
                ));

        return matches.stream()
                .filter(match -> match.length() == shortestLengthByGroup.get(
                        match.combination().getGroupId()
                ))
                .toList();
    }
}
