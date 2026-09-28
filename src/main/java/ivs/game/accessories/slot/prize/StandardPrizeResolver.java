package ivs.game.accessories.slot.prize;

import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.CombinationMatch;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Resolves prize identifiers from a combination-to-prizes map.
 *
 * @param <I> the type of reel items in combinations
 * @param <P> the type of prize identifier
 */
public final class StandardPrizeResolver<I extends ReelItem, P>
        implements PrizeResolver<I, P> {
    private final Map<Combination<I>, Set<P>> prizesByCombination;

    /**
     * Creates a resolver from the supplied prize configuration.
     *
     * @param prizesByCombination prize identifiers by combination
     * @throws NullPointerException if the map, a key, a value, or an element
     *                              is null
     */
    public StandardPrizeResolver(
            @NonNull Map<Combination<I>, ? extends Set<P>> prizesByCombination
    ) {
        Map<Combination<I>, Set<P>> copy = new HashMap<>();
        prizesByCombination.forEach((combination, prizes) -> {
            Validate.notNull(
                    combination,
                    "Prizes by combination must not contain null keys"
            );
            Validate.notNull(
                    prizes,
                    "Prizes by combination must not contain null values"
            );
            Validate.noNullElements(
                    prizes,
                    "Prize identifiers must not contain null elements"
            );
            copy.put(combination, new HashSet<>(prizes));
        });
        this.prizesByCombination = copy;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The returned map and prize sets are independent from the resolver's
     * configuration.</p>
     */
    @Override
    public Map<Combination<I>, Set<P>> resolve(
            @NonNull List<? extends CombinationMatch<I>> matches
    ) {
        Map<Combination<I>, Set<P>> resolved = new HashMap<>();
        for (CombinationMatch<I> match : matches) {
            Set<P> prizes = prizesByCombination.getOrDefault(
                    match.combination(),
                    Collections.emptySet()
            );
            if (!prizes.isEmpty()) {
                resolved.put(match.combination(), new HashSet<>(prizes));
            }
        }
        return resolved;
    }
}
