package ivs.game.accessories.slot.matcher;

import ivs.game.accessories.slot.reel.ReelItem;

import java.util.List;

/**
 * Detects combinations for one particular spin.
 *
 * <p>A matcher is initialized with the field and all matcher-specific
 * configuration in its constructor. Implementations should keep that state
 * unchanged while matching, so the same matcher always produces the same
 * result.</p>
 *
 * @param <I> the type of reel items on the field
 * @param <M> the type of detected combination
 */
@FunctionalInterface
public interface CombinationMatcher<
        I extends ReelItem,
        M extends CombinationMatch<I>
        > {

    /**
     * Finds all combinations for the state captured by this matcher.
     *
     * @return an unmodifiable list of detected combinations; never {@code null}
     */
    List<M> match();
}
