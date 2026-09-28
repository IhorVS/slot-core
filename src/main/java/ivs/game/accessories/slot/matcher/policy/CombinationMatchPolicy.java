package ivs.game.accessories.slot.matcher.policy;

import ivs.game.accessories.slot.matcher.CombinationMatch;

import java.util.List;

/**
 * Applies a rule to the combinations detected by a matcher.
 *
 * @param <M> the type of detected combination
 */
@FunctionalInterface
public interface CombinationMatchPolicy<M extends CombinationMatch<?>> {

    /**
     * Applies this policy to the supplied matches.
     *
     * @param matches a valid list of matches to process
     * @return the matches retained by this policy
     */
    List<M> apply(List<M> matches);
}
