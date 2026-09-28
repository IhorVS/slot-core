package ivs.game.accessories.slot.matcher;

import ivs.game.accessories.slot.matcher.policy.CombinationMatchPolicy;
import ivs.game.accessories.slot.reel.ReelItem;
import org.apache.commons.lang3.Validate;

import java.util.List;

/**
 * Base implementation that applies a match policy to raw matcher results.
 *
 * @param <I> the type of reel items on the field
 * @param <M> the type of detected combination
 */
public abstract class AbstractCombinationMatcher<
        I extends ReelItem,
        M extends CombinationMatch<I>
        > implements CombinationMatcher<I, M> {

    private final CombinationMatchPolicy<M> policy;

    /**
     * Creates a matcher with the supplied result policy.
     *
     * @param policy the policy applied to raw matches
     */
    protected AbstractCombinationMatcher(CombinationMatchPolicy<M> policy) {
        this.policy = Validate.notNull(policy, "Policy must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<M> match() {
        return policy.apply(findMatches());
    }

    /**
     * Finds all raw combinations before policy processing.
     *
     * @return an unmodifiable list of raw matches; never {@code null}
     */
    protected abstract List<M> findMatches();
}
