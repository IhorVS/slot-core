package ivs.game.accessories.slot.engine;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.field.impl.ReelBankFieldBuilder;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.CombinationMatch;
import ivs.game.accessories.slot.matcher.CombinationMatcher;
import ivs.game.accessories.slot.matcher.orchestration.SequentialCombinationMatcherOrchestrator;
import ivs.game.accessories.slot.prize.StandardPrizeResolver;
import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.ReelItem;
import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Coordinates field generation, combination matching, and prize resolution.
 *
 * <p>Each spin uses explicitly supplied reel positions. The matcher factory
 * creates matchers for the generated field, and the engine runs them
 * sequentially in the returned list order.</p>
 *
 * <p>The configuration and factory are retained by reference. Configuration
 * components and collections must remain unchanged while the engine is in use.</p>
 *
 * @param <I> the type of reel items
 */
public final class StandardSlotEngine<I extends ReelItem> {

    private final SlotEngineConfig<I> config;
    private final CombinationMatcherFactory<I> matcherFactory;
    private final ReelBankFieldBuilder<I> fieldBuilder;
    private final SequentialCombinationMatcherOrchestrator<I> orchestrator;
    private final StandardPrizeResolver<I, String> prizeResolver;

    /**
     * Creates an engine using the supplied configuration and matcher factory.
     *
     * <p>Empty line, combination, wild, and prize collections are allowed.
     * The factory determines which components its matchers require.</p>
     *
     * @param config         the static slot configuration
     * @param matcherFactory the factory creating matchers for each spin
     * @throws NullPointerException     if a required argument or configuration
     *                                  component is null
     * @throws IllegalArgumentException if the configuration contains null
     *                                  elements, duplicate identifiers,
     *                                  invalid reel sizes or line columns,
     *                                  unknown prize combinations, or blank
     *                                  prize identifiers
     */
    public StandardSlotEngine(
            SlotEngineConfig<I> config,
            CombinationMatcherFactory<I> matcherFactory
    ) {
        Validate.notNull(config, "Engine configuration must not be null");
        Validate.notNull(matcherFactory, "Matcher factory must not be null");

        ConfigValidator.validate(config);

        this.config = config;
        this.matcherFactory = matcherFactory;
        this.fieldBuilder = new ReelBankFieldBuilder<>(config.getReelBank());
        this.orchestrator = new SequentialCombinationMatcherOrchestrator<>();
        this.prizeResolver = new StandardPrizeResolver<>(config.getPrizesByCombination());
    }

    /**
     * Performs a spin using one physical position for each reel.
     *
     * <p>Positions are supplied from left to right. No positions are generated
     * automatically. Factory results are checked before any matcher runs.</p>
     *
     * @param positions physical reel positions
     * @return the generated field, reel positions, matches, and configured prizes
     * @throws NullPointerException     if positions are null
     * @throws IllegalArgumentException if positions are invalid or a configured
     *                                  line references a row outside the field
     * @throws IllegalStateException    if the factory returns null or a list
     *                                  containing null matchers
     */
    public SpinResult<I> spin(int... positions) {
        Validate.notNull(positions, "Reel positions must not be null");
        Validate.isTrue(positions.length > 0, "Reel positions must not be empty");
        int[] actualPositions = positions.clone();

        validatePositions(actualPositions);

        SlotField<I> field = fieldBuilder.build(actualPositions);
        ConfigValidator.validateLineRows(config, field);

        var matchers = matcherFactory.getMatchers(config, field);
        validateMatchers(matchers);

        List<CombinationMatch<I>> matches = orchestrator.match(matchers);

        return new SpinResult<>(
                actualPositions.clone(),
                field,
                matches,
                Collections.unmodifiableMap(createPrizes(matches))
        );
    }

    private static <I extends ReelItem> void validateMatchers(List<? extends CombinationMatcher<I, ? extends CombinationMatch<I>>> matchers) {
        Validate.validState(matchers != null, "Matcher factory must not return null");
        for (int index = 0; index < matchers.size(); index++) {
            Validate.validState(
                    matchers.get(index) != null,
                    "Matcher factory returned null matcher at index %d",
                    index
            );
        }
    }

    private Map<Combination<I>, Set<String>> createPrizes(List<CombinationMatch<I>> matches) {
        Map<Combination<I>, Set<String>> prizes = new HashMap<>();
        prizeResolver.resolve(matches).forEach((combination, identifiers) ->
                prizes.put(combination, Set.copyOf(identifiers))
        );
        return prizes;
    }

    private void validatePositions(int[] positions) {
        ReelBank<I> reelBank = config.getReelBank();
        Validate.isTrue(
                positions.length == reelBank.size(),
                "Expected %d reel positions, received %d",
                reelBank.size(),
                positions.length
        );

        for (int reelId = 0; reelId < positions.length; reelId++) {
            Validate.isTrue(
                    positions[reelId] >= 0 && positions[reelId] < reelBank.getReelSize(reelId),
                    "Invalid position %d for reel %d",
                    positions[reelId],
                    reelId
            );
        }
    }
}
