package ivs.game.accessories.slot.matcher.orchestration;

import ivs.game.accessories.slot.matcher.CombinationMatch;
import ivs.game.accessories.slot.matcher.CombinationMatcher;
import ivs.game.accessories.slot.matcher.CombinationMatcherOrchestrator;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.List;

/**
 * Runs combination matchers sequentially in the order in which they are
 * supplied.
 *
 * @param <I> the type of reel items on the field
 */
public final class SequentialCombinationMatcherOrchestrator<
        I extends ReelItem
        > implements CombinationMatcherOrchestrator<I> {

    /**
     * {@inheritDoc}
     *
     * <p>Results from each matcher are appended to the results of the
     * preceding matcher. The returned list is unmodifiable.</p>
     */
    @Override
    public List<CombinationMatch<I>> match(
            @NonNull List<? extends CombinationMatcher<
                    I, ? extends CombinationMatch<I>>> matchers
    ) {
        Validate.noNullElements(matchers, "Matchers must not contain null elements");

        return matchers.stream()
                .<CombinationMatch<I>>flatMap(matcher -> matcher.match().stream())
                .toList();
    }
}
