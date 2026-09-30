package ivs.game.accessories.slot.engine;

import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.linear.FieldLine;
import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Static configuration shared by the slot engine and its matcher factory.
 *
 * <p>The configuration does not assign combination types or matching policies.
 * These decisions belong to the matcher factory.</p>
 *
 * <p>All components are retained by reference. Validation is performed by
 * the slot engine. Supplied components and collections must remain unchanged
 * while the configuration is in use.</p>
 *
 * @param <I> the type of reel items
 */
@Data
public final class SlotEngineConfig<I extends ReelItem> {

    /**
     * The configured bank of reels, each defining its own visible window size.
     */
    private final ReelBank<I> reelBank;

    /**
     * The available field lines in their configured order.
     */
    private final List<FieldLine> lines;

    /**
     * The items available for wild substitution.
     */
    private final Set<I> wildSubstitutes;

    /**
     * The available combinations in their configured order.
     */
    private final List<Combination<I>> combinations;

    /**
     * String prize identifiers by combination.
     */
    private final Map<Combination<I>, Set<String>> prizesByCombination;
}
