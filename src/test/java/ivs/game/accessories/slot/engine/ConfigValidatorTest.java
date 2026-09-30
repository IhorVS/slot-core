package ivs.game.accessories.slot.engine;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.linear.FieldLine;
import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.ReelItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Verifies static configuration validation and line row validation.
 *
 * <p>Each test starts with a valid configuration. Negative tests override
 * only the component involved in the invalid scenario and verify both
 * the exception type and its message.</p>
 *
 * <p>JUnit's default lifecycle creates a new test instance for each test
 * invocation, so mocks and their overrides are not shared between tests.</p>
 *
 * <p>MockitoExtension is not used: mocks are created explicitly, and common
 * setup stubs are intentionally unused when validation fails early.
 * Strict unused-stubbing checks would require additional leniency settings.</p>
 */
class ConfigValidatorTest {

    private final SlotEngineConfig<ReelItem> config = mock();
    private final ReelBank<ReelItem> reelBank = mock();
    private final SlotField<ReelItem> field = mock();

    private final ReelItem a = mock();
    private final ReelItem b = mock();
    private final ReelItem wild = mock();

    private final Combination<ReelItem> combination = new Combination<>(0, "A", a, a, a);

    private final FieldLine line = new FieldLine(0, List.of(new FieldPosition(0, 0), new FieldPosition(1, 2), new FieldPosition(2, 1)));

    /**
     * Provides valid defaults before each test invocation.
     *
     * <p>The bank has three reels with positive physical sizes.
     * The configuration contains one line, one combination, one wild item,
     * and one prize identifier associated with the configured combination.</p>
     *
     * <p>The generated field has column sizes 1, 3, and 2.
     * The configured line uses the last valid row of each column.</p>
     */
    @BeforeEach
    void setUp() {
        when(reelBank.size()).thenReturn(3);
        when(reelBank.getReelSize(0)).thenReturn(10);
        when(reelBank.getReelSize(1)).thenReturn(12);
        when(reelBank.getReelSize(2)).thenReturn(8);

        when(config.getReelBank()).thenReturn(reelBank);
        when(config.getLines()).thenReturn(List.of(line));
        when(config.getWildSubstitutes()).thenReturn(Set.of(wild));
        when(config.getCombinations()).thenReturn(List.of(combination));
        when(config.getPrizesByCombination()).thenReturn(Map.of(combination, Set.of("PRIZE_A")));

        when(field.getColumnSize(0)).thenReturn(1);
        when(field.getColumnSize(1)).thenReturn(3);
        when(field.getColumnSize(2)).thenReturn(2);
    }

    @Test
    void validateAcceptsValidConfig() {
        // Input: all configuration components use valid defaults.
        // Expected: validation completes without an exception.
        assertDoesNotThrow(() -> ConfigValidator.validate(config));
    }

    @Test
    void validateAcceptsEmptyOptionalCollections() {
        // Input: the reel bank is valid and all other collections are empty.
        // Expected: the validator does not impose matcher-specific requirements.
        when(config.getLines()).thenReturn(List.of());
        when(config.getWildSubstitutes()).thenReturn(Set.of());
        when(config.getCombinations()).thenReturn(List.of());
        when(config.getPrizesByCombination()).thenReturn(Map.of());

        assertDoesNotThrow(() -> ConfigValidator.validate(config));
    }

    @Test
    void validateRejectsNullConfig() {
        // Input: no configuration object is supplied.
        // Expected: validation reports the missing engine configuration.
        var exception = assertThrows(NullPointerException.class, () -> ConfigValidator.validate(null));

        assertEquals("Engine configuration must not be null", exception.getMessage());
    }

    @Test
    void validateRejectsNullReelBank() {
        // Input: the reel bank is null; other components remain valid.
        // Expected: validation reports the missing reel bank.
        when(config.getReelBank()).thenReturn(null);

        assertMissingComponent("Reel bank must not be null");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void validateRejectsNonPositiveReelCount(int size) {
        // Input: the bank reports zero or a negative number of reels.
        // Expected: validation requires at least one reel.
        when(reelBank.size()).thenReturn(size);

        assertInvalidConfig("Reel bank must contain at least one reel");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void validateRejectsNonPositiveReelSize(int size) {
        // Input: the second reel reports zero or a negative physical size.
        // Expected: validation checks each reel and reports reel 1.
        when(reelBank.getReelSize(1)).thenReturn(size);

        assertInvalidConfig("Reel 1 must contain at least one physical position");
    }

    @Test
    void validateRejectsNullLines() {
        // Input: the line collection is null.
        // Expected: validation distinguishes a missing collection from an empty one.
        when(config.getLines()).thenReturn(null);

        assertMissingComponent("Lines must not be null");
    }

    @Test
    void validateRejectsNullLineElement() {
        // Input: the collection contains a valid line followed by null.
        // Expected: validation rejects the null element.
        when(config.getLines()).thenReturn(Arrays.asList(line, null));

        assertInvalidConfig("Lines must not contain null elements");
    }

    @Test
    void validateRejectsDuplicateLineIds() {
        // Input: two different lines have the same identifier.
        // Expected: validation rejects the duplicate ID regardless of positions.
        var anotherLine = new FieldLine(0, List.of(new FieldPosition(1, 0)));
        when(config.getLines()).thenReturn(List.of(line, anotherLine));

        assertInvalidConfig("Duplicate line ID: 0");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 3})
    void validateRejectsLineColumnOutsideReelBank(int column) {
        // Input: a line references a column outside the valid range 0..2.
        // Expected: validation rejects both the negative and upper boundary cases.
        var invalidLine = new FieldLine(1, List.of(new FieldPosition(column, 0)));
        when(config.getLines()).thenReturn(List.of(invalidLine));

        assertInvalidConfig("Line 1 references invalid column " + column);
    }

    @Test
    void validateRejectsNullWildSubstitutes() {
        // Input: the wild substitute set is null.
        // Expected: validation reports the missing set.
        when(config.getWildSubstitutes()).thenReturn(null);

        assertMissingComponent("Wild substitutes must not be null");
    }

    @Test
    void validateRejectsNullWildSubstituteElement() {
        // Input: the set contains a configured wild item and null.
        // Expected: validation rejects the null element.
        Set<ReelItem> substitutes = new HashSet<>(Arrays.asList(wild, null));
        when(config.getWildSubstitutes()).thenReturn(substitutes);

        assertInvalidConfig("Wild substitutes must not contain null elements");
    }

    @Test
    void validateRejectsNullCombinations() {
        // Input: the combination collection is null.
        // Expected: validation reports the missing collection.
        when(config.getCombinations()).thenReturn(null);

        assertMissingComponent("Combinations must not be null");
    }

    @Test
    void validateRejectsNullCombinationElement() {
        // Input: the collection contains a configured combination and null.
        // Expected: validation rejects the null element.
        when(config.getCombinations()).thenReturn(Arrays.asList(combination, null));

        assertInvalidConfig("Combinations must not contain null elements");
    }

    @Test
    void validateRejectsDuplicateCombinationIds() {
        // Input: combinations from different groups share the same identifier.
        // Expected: combination identifiers must be unique across the configuration.
        var anotherCombination = new Combination<>(0, "B", b, b, b);
        when(config.getCombinations()).thenReturn(List.of(combination, anotherCombination));

        assertInvalidConfig("Duplicate combination ID: 0");
    }

    @Test
    void validateRejectsNullPrizeMap() {
        // Input: the prize map is null.
        // Expected: validation reports the missing map.
        when(config.getPrizesByCombination()).thenReturn(null);

        assertMissingComponent("Prizes by combination must not be null");
    }

    @Test
    void validateRejectsNullPrizeMapKey() {
        // Input: the prize map contains a null combination key.
        // Expected: validation rejects the key before checking combination membership.
        Map<Combination<ReelItem>, Set<String>> prizes = new HashMap<>();
        prizes.put(null, Set.of("PRIZE_A"));
        when(config.getPrizesByCombination()).thenReturn(prizes);

        assertMissingComponent("Prize configuration must not contain null keys");
    }

    @Test
    void validateRejectsUnknownPrizeCombination() {
        // Input: a prize references a combination absent from the configuration.
        // Expected: validation reports the unknown combination identifier.
        var unknownCombination = new Combination<>(1, "B", b, b, b);
        when(config.getPrizesByCombination()).thenReturn(Map.of(unknownCombination, Set.of("PRIZE_B")));

        assertInvalidConfig("Prizes reference unknown combination ID: 1");
    }

    @Test
    void validateRejectsNullPrizeSet() {
        // Input: a configured combination maps to a null prize set.
        // Expected: validation reports the null map value.
        Map<Combination<ReelItem>, Set<String>> prizes = new HashMap<>();
        prizes.put(combination, null);
        when(config.getPrizesByCombination()).thenReturn(prizes);

        assertMissingComponent("Prize configuration must not contain null values");
    }

    @Test
    void validateRejectsNullPrizeIdentifier() {
        // Input: a prize set contains a valid identifier and null.
        // Expected: validation rejects the null identifier.
        Set<String> identifiers = new HashSet<>(Arrays.asList("PRIZE_A", null));
        when(config.getPrizesByCombination()).thenReturn(Map.of(combination, identifiers));

        assertInvalidConfig("Prize identifiers must not contain null elements");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    void validateRejectsBlankPrizeIdentifier(String identifier) {
        // Input: a prize identifier is empty or consists only of whitespace.
        // Expected: validation rejects the blank identifier.
        when(config.getPrizesByCombination()).thenReturn(Map.of(combination, Set.of(identifier)));

        assertInvalidConfig("Prize identifier must not be blank");
    }

    @Test
    void validateAcceptsCombinationWithoutPrizes() {
        // Input: a combination is configured, but the prize map is empty.
        // Expected: combinations without configured prizes are allowed.
        when(config.getPrizesByCombination()).thenReturn(Map.of());

        assertDoesNotThrow(() -> ConfigValidator.validate(config));
    }

    @Test
    void validateAcceptsEmptyPrizeSet() {
        // Input: a configured combination maps to an empty prize set.
        // Expected: an empty set is valid and means no configured prizes.
        when(config.getPrizesByCombination()).thenReturn(Map.of(combination, Set.of()));

        assertDoesNotThrow(() -> ConfigValidator.validate(config));
    }

    @Test
    void validateLineRowsAcceptsDifferentColumnSizes() {
        // Input: the line uses the last valid row of each column.
        // Expected: all positions are valid despite different column sizes.
        /*
         *          column
         *          0     1     2
         * row 0 |  *  |  -  |  -  |
         * row 1 |     |  -  |  *  |
         * row 2 |     |  *  |     |
         */
        assertDoesNotThrow(() -> ConfigValidator.validateLineRows(config, field));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 3})
    void validateLineRowsRejectsRowOutsideColumn(int row) {
        // Input: a line references a row outside column 1's valid range 0..2.
        // Expected: validation rejects negative rows and rows at the column size.
        var invalidLine = new FieldLine(1, List.of(new FieldPosition(1, row)));
        when(config.getLines()).thenReturn(List.of(invalidLine));

        var exception = assertThrows(IllegalArgumentException.class,
                () -> ConfigValidator.validateLineRows(config, field));

        assertEquals("Line 1 references invalid row " + row + " in column 1", exception.getMessage());
    }

    @Test
    void validateLineRowsUsesSizeOfReferencedColumn() {
        // Input: row 2 exists in column 1 but does not exist in column 2.
        // Expected: the position in column 2 is rejected.
        /*
         *          column
         *          0     1     2
         * row 0 |  -  |  -  |  -  |
         * row 1 |     |  -  |  -  |
         * row 2 |     |  -  |  *  |  <- invalid position
         */
        var invalidLine = new FieldLine(1, List.of(new FieldPosition(2, 2)));
        when(config.getLines()).thenReturn(List.of(invalidLine));

        var exception = assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validateLineRows(config, field));

        assertEquals("Line 1 references invalid row 2 in column 2", exception.getMessage());
    }

    @Test
    void validateLineRowsAcceptsEmptyLines() {
        // Input: no lines are configured.
        // Expected: row validation completes without inspecting field columns.
        when(config.getLines()).thenReturn(List.of());

        assertDoesNotThrow(() -> ConfigValidator.validateLineRows(config, field));
    }

    /**
     * Verifies the exception type and message for a missing component.
     */
    private void assertMissingComponent(String message) {
        var exception = assertThrows(NullPointerException.class, () -> ConfigValidator.validate(config));

        assertEquals(message, exception.getMessage());
    }

    /**
     * Verifies the exception type and message for invalid configuration data.
     */
    private void assertInvalidConfig(String message) {
        var exception = assertThrows(IllegalArgumentException.class, () -> ConfigValidator.validate(config));

        assertEquals(message, exception.getMessage());
    }

    @Test
    void validateRejectsIdenticalSequencesWithDifferentCombinationIds() {
        // Input: combinations from different groups have different IDs
        // but contain the same sequence AAA.
        // Expected: sequence uniqueness is checked regardless of ID or group.
        var anotherCombination = new Combination<>(1, "ANOTHER_GROUP", a, a, a);
        when(config.getCombinations()).thenReturn(List.of(combination, anotherCombination));

        assertInvalidConfig("Combinations 0 and 1 have identical item sequences");
    }

    @Test
    void validateAcceptsSequencesWithDifferentLengths() {
        // Input: combinations AAA and AAAA have different IDs.
        // Expected: sequences with different lengths are not considered identical.
        var longerCombination = new Combination<>(1, "A", a, a, a, a);
        when(config.getCombinations()).thenReturn(List.of(combination, longerCombination));

        assertDoesNotThrow(() -> ConfigValidator.validate(config));
    }

    @Test
    void validateAcceptsSequencesWithDifferentItemOrder() {
        // Input: combinations AAB and ABA have the same length and symbol counts.
        // Expected: different left-to-right order makes the sequences distinct.
        var firstCombination = new Combination<>(1, "FIRST", a, a, b);
        var secondCombination = new Combination<>(2, "SECOND", a, b, a);
        when(config.getCombinations()).thenReturn(
                List.of(combination, firstCombination, secondCombination)
        );

        assertDoesNotThrow(() -> ConfigValidator.validate(config));
    }
}
