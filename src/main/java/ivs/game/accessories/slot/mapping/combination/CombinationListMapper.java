package ivs.game.accessories.slot.mapping.combination;

import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.reel.ReelItem;

import java.util.List;
import java.util.Map;

/**
 * Maps external combination descriptions to combinations indexed by ID.
 *
 * <p>Each description is mapped independently. The mapper does not distinguish
 * between linear and scatter combinations.</p>
 *
 * @param <I> the type of items contained in the combinations
 */
public interface CombinationListMapper<I extends ReelItem> {

    /**
     * Maps combination descriptions and indexes the results by combination ID.
     *
     * @param descriptions combination descriptions in their configured order
     * @return unmodifiable map of combination IDs to mapped combinations
     * @throws NullPointerException     if {@code descriptions} is {@code null}
     * @throws IllegalArgumentException if descriptions contain duplicate IDs
     * @throws RuntimeException         if a combination description cannot be mapped
     */
    Map<Integer, Combination<I>> map(List<CombinationDescription> descriptions);
}
