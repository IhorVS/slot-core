package ivs.game.accessories.slot.engine;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.linear.FieldLine;
import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validates slot configuration and line positions within generated fields.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ConfigValidator {

    /**
     * Validates the static slot configuration.
     *
     * @param config the configuration to validate
     * @param <I>    the type of reel items
     */
    static <I extends ReelItem> void validate(SlotEngineConfig<I> config) {
        Validate.notNull(config, "Engine configuration must not be null");

        validateReelBank(config.getReelBank());
        validateLines(config);
        validateWildSubstitutes(config);
        validateCombinations(config);
        validatePrizes(config);
    }

    /**
     * Validates configured line rows against the generated field.
     *
     * @param config the previously validated configuration
     * @param field  the generated field
     * @param <I>    the type of reel items
     */
    static <I extends ReelItem> void validateLineRows(
            SlotEngineConfig<I> config,
            SlotField<I> field
    ) {
        for (FieldLine line : config.getLines()) {
            for (FieldPosition position : line.positions()) {
                Validate.isTrue(
                        position.row() >= 0 && position.row() < field.getColumnSize(position.column()),
                        "Line %d references invalid row %d in column %d",
                        line.id(),
                        position.row(),
                        position.column()
                );
            }
        }
    }

    private static <I extends ReelItem> void validateReelBank(ReelBank<I> reelBank) {
        Validate.notNull(reelBank, "Reel bank must not be null");
        Validate.isTrue(reelBank.size() > 0, "Reel bank must contain at least one reel");

        for (int reelId = 0; reelId < reelBank.size(); reelId++) {
            Validate.isTrue(
                    reelBank.getReelSize(reelId) > 0,
                    "Reel %d must contain at least one physical position",
                    reelId
            );
        }
    }

    private static <I extends ReelItem> void validateLines(SlotEngineConfig<I> config) {
        Validate.notNull(config.getLines(), "Lines must not be null");
        Validate.noNullElements(config.getLines(), "Lines must not contain null elements");

        Set<Integer> lineIds = new HashSet<>();
        for (FieldLine line : config.getLines()) {
            Validate.isTrue(lineIds.add(line.id()), "Duplicate line ID: %d", line.id());

            for (FieldPosition position : line.positions()) {
                Validate.isTrue(
                        position.column() >= 0 && position.column() < config.getReelBank().size(),
                        "Line %d references invalid column %d",
                        line.id(),
                        position.column()
                );
            }
        }
    }

    private static <I extends ReelItem> void validateWildSubstitutes(SlotEngineConfig<I> config) {
        Validate.notNull(config.getWildSubstitutes(), "Wild substitutes must not be null");
        Validate.noNullElements(
                config.getWildSubstitutes(),
                "Wild substitutes must not contain null elements"
        );
    }

    /*
     * Validates combination identifiers and item sequences.
     *
     * <p>Identifiers must be unique across the configuration. Combinations
     * with different identifiers must not contain identical item sequences,
     * regardless of their group identifiers. Sequence equality requires
     * the same length and equal items in the same left-to-right order.</p>
     *
     * @param config the configuration containing the combinations
     * @param <I>    the type of reel items
     * @throws NullPointerException     if the combination collection is null
     * @throws IllegalArgumentException if the collection contains null elements,
     *                                  duplicate identifiers, or identical item sequences
     */
    private static <I extends ReelItem> void validateCombinations(SlotEngineConfig<I> config) {
        Validate.notNull(config.getCombinations(), "Combinations must not be null");
        Validate.noNullElements(
                config.getCombinations(),
                "Combinations must not contain null elements"
        );

        Set<Integer> combinationIds = new HashSet<>();
        Map<List<I>, Integer> idsByItems = new HashMap<>();

        for (Combination<I> combination : config.getCombinations()) {
            Validate.isTrue(
                    combinationIds.add(combination.getId()),
                    "Duplicate combination ID: %d",
                    combination.getId()
            );

            Integer existingId = idsByItems.putIfAbsent(combination.getItems(), combination.getId());
            Validate.isTrue(
                    existingId == null,
                    "Combinations %s and %d have identical item sequences",
                    existingId,
                    combination.getId()
            );
        }
    }

    private static <I extends ReelItem> void validatePrizes(SlotEngineConfig<I> config) {
        Validate.notNull(config.getPrizesByCombination(), "Prizes by combination must not be null");

        Set<Combination<I>> combinations = new HashSet<>(config.getCombinations());

        config.getPrizesByCombination().forEach((combination, prizes) -> {
            Validate.notNull(combination, "Prize configuration must not contain null keys");
            Validate.isTrue(
                    combinations.contains(combination),
                    "Prizes reference unknown combination ID: %d",
                    combination.getId()
            );
            Validate.notNull(prizes, "Prize configuration must not contain null values");
            Validate.noNullElements(prizes, "Prize identifiers must not contain null elements");

            for (String prize : prizes) {
                Validate.notBlank(prize, "Prize identifier must not be blank");
            }
        });
    }
}
