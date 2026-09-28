package ivs.game.accessories.slot.matcher;

import ivs.game.accessories.slot.reel.ReelItem;

import java.util.List;

/**
 * Coordinates combination matchers for one spin.
 *
 * <p>The matchers are created and configured by the caller. The implementation
 * defines how the supplied matchers are executed, while the returned result
 * contains matches from all of them.</p>
 *
 * @param <I> the type of reel items on the field
 */
@FunctionalInterface
public interface CombinationMatcherOrchestrator<I extends ReelItem> {

    /**
     * Runs the supplied matchers.
     *
     * @param matchers the matchers to run, in processing order
     * @return an unmodifiable list of all detected combinations in matcher
     * processing order; never {@code null}
     */
    List<CombinationMatch<I>> match(
            List<? extends CombinationMatcher<
                    I, ? extends CombinationMatch<I>>> matchers
    );
}
