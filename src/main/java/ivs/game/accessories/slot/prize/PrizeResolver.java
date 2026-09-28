package ivs.game.accessories.slot.prize;

import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.CombinationMatch;
import ivs.game.accessories.slot.reel.ReelItem;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Resolves configured prize identifiers for detected combinations.
 *
 * @param <I> the type of reel items in combinations
 * @param <P> the type of prize identifier
 */
@FunctionalInterface
public interface PrizeResolver<I extends ReelItem, P> {

    /**
     * Resolves prizes for the supplied detected combinations.
     *
     * <p>Combinations without configured prizes are omitted from the result.
     * The result is keyed by the combination itself, so repeated matches of
     * one combination share one prize entry.</p>
     *
     * @param matches detected combinations
     * @return prize identifiers by combination
     */
    Map<Combination<I>, Set<P>> resolve(
            List<? extends CombinationMatch<I>> matches
    );
}
