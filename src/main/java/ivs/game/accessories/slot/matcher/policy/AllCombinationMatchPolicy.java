package ivs.game.accessories.slot.matcher.policy;

import ivs.game.accessories.slot.matcher.CombinationMatch;

import java.util.List;

/**
 * Retains every detected combination.
 *
 * @param <M> the type of detected combination
 */
public final class AllCombinationMatchPolicy<M extends CombinationMatch<?>>
        implements CombinationMatchPolicy<M> {

    /**
     * {@inheritDoc}
     *
     * <p>The exact same list instance is returned unchanged.</p>
     */
    @Override
    public List<M> apply(List<M> matches) {
        return matches;
    }
}
