package ivs.game.accessories.slot.mapping.combination;

import java.util.List;

/**
 * Describes one combination using external string item identifiers.
 *
 * <p>Item identifiers are stored in their configured order. The description
 * also specifies the combination identifier and the group to which it belongs.</p>
 *
 * <p>The class does not determine the reel item type. This decision is
 * delegated to the configured {@link CombinationMapper}.</p>
 *
 * @param id      identifier of the combination
 * @param groupId identifier of the combination group
 * @param values  external reel item identifiers in their configured order
 */
public record CombinationDescription(
        int id,
        String groupId,
        List<String> values
) {
}
