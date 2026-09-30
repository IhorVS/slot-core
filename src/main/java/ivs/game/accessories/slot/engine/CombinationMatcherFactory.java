package ivs.game.accessories.slot.engine;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.matcher.CombinationMatch;
import ivs.game.accessories.slot.matcher.CombinationMatcher;
import ivs.game.accessories.slot.reel.ReelItem;

import java.util.List;

/**
 * Creates combination matchers for one spin.
 *
 * <p>The factory may return matchers producing different match types,
 * such as linear and scatter matches. All produced matches share the
 * {@link CombinationMatch} contract.</p>
 *
 * @param <I> the type of reel items
 */
@FunctionalInterface
public interface CombinationMatcherFactory<I extends ReelItem> {

    /**
     * Creates matchers using the engine configuration and current field.
     *
     * <p>The returned list determines matcher execution order.
     * Each matcher must be configured to inspect the supplied field.</p>
     *
     * @param config the slot engine configuration
     * @param field  the field built for the current spin
     * @return matchers in execution order; never null and without null elements
     */
    List<? extends CombinationMatcher<I, ? extends CombinationMatch<I>>> getMatchers(
            SlotEngineConfig<I> config,
            SlotField<I> field
    );
}
