package ivs.game.accessories.slot.engine;

import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.CombinationMatch;
import ivs.game.accessories.slot.matcher.CombinationMatcher;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.linear.FieldLine;
import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.ReelItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Verifies spin coordination and engine-specific validation.
 *
 * <p>Each test starts with a valid configuration and an empty matcher factory.
 * Individual tests override only the components required by their scenario.</p>
 *
 * <p>Configuration validation details are covered by ConfigValidatorTest.
 * This class checks that the engine invokes validation and coordinates
 * field generation, matcher execution, and prize resolution correctly.</p>
 *
 * <p>Mocks are created explicitly without MockitoExtension. Common setup
 * contains stubs that are intentionally unused in early rejection scenarios.</p>
 */
class StandardSlotEngineTest {

    private final SlotEngineConfig<ReelItem> config = mock();
    private final ReelBank<ReelItem> reelBank = mock();
    private final CombinationMatcherFactory<ReelItem> matcherFactory = mock();

    private final CombinationMatcher<ReelItem, CombinationMatch<ReelItem>> firstMatcher = mock();
    private final CombinationMatcher<ReelItem, CombinationMatch<ReelItem>> secondMatcher = mock();

    private final CombinationMatch<ReelItem> firstMatch = mock();
    private final CombinationMatch<ReelItem> secondMatch = mock();

    private final ReelItem a = mock();
    private final ReelItem b = mock();

    private final Combination<ReelItem> aa = new Combination<>(0, "A", a, a);
    private final Combination<ReelItem> bb = new Combination<>(1, "B", b, b);

    private final FieldLine line = new FieldLine(0, List.of(
            new FieldPosition(0, 0),
            new FieldPosition(1, 0)
    ));

    /**
     * Configures two reels with different physical and visible sizes.
     *
     * <p>At position zero, reel 0 shows A, B and reel 1 shows A.
     * AA has two prize identifiers; BB has no configured prizes.</p>
     */
    @BeforeEach
    void setUp() {
        when(reelBank.size()).thenReturn(2);
        when(reelBank.getReelSize(0)).thenReturn(4);
        when(reelBank.getReelSize(1)).thenReturn(3);
        when(reelBank.getItems(0, 0)).thenReturn(List.of(a, b));
        when(reelBank.getItems(1, 0)).thenReturn(List.of(a));

        when(config.getReelBank()).thenReturn(reelBank);
        when(config.getLines()).thenReturn(List.of(line));
        when(config.getWildSubstitutes()).thenReturn(Set.of());
        when(config.getCombinations()).thenReturn(List.of(aa, bb));
        when(config.getPrizesByCombination()).thenReturn(
                Map.of(aa, Set.of("PRIZE_A", "BONUS_A"))
        );

        // doReturn avoids wildcard capture issues in the factory return type.
        doReturn(List.of()).when(matcherFactory).getMatchers(same(config), any());

        when(firstMatcher.match()).thenReturn(List.of(firstMatch));
        when(secondMatcher.match()).thenReturn(List.of(secondMatch));
        when(firstMatch.combination()).thenReturn(aa);
        when(secondMatch.combination()).thenReturn(bb);
    }

    @Test
    void constructorRejectsNullConfig() {
        // Input: no configuration is supplied.
        // Expected: construction fails before the factory is called.
        var exception = assertThrows(
                NullPointerException.class,
                () -> new StandardSlotEngine<>(null, matcherFactory)
        );

        assertEquals("Engine configuration must not be null", exception.getMessage());
        verifyNoInteractions(matcherFactory);
    }

    @Test
    void constructorRejectsNullMatcherFactory() {
        // Input: the configuration is valid, but the factory is null.
        // Expected: construction reports the missing factory.
        var exception = assertThrows(
                NullPointerException.class,
                () -> new StandardSlotEngine<>(config, null)
        );

        assertEquals("Matcher factory must not be null", exception.getMessage());
    }

    @Test
    void constructorValidatesConfig() {
        // Input: the configuration has no reel bank.
        // Expected: construction delegates configuration validation.
        when(config.getReelBank()).thenReturn(null);

        var exception = assertThrows(
                NullPointerException.class,
                () -> new StandardSlotEngine<>(config, matcherFactory)
        );

        assertEquals("Reel bank must not be null", exception.getMessage());
        verifyNoInteractions(matcherFactory);
    }

    @Test
    void constructorDoesNotCreateMatchers() {
        // Input: a valid configuration and factory.
        // Expected: matchers are created during a spin, not construction.
        new StandardSlotEngine<>(config, matcherFactory);

        verifyNoInteractions(matcherFactory);
    }

    @Test
    void spinBuildsFieldUsingSuppliedPositions() {
        // Input: positions 1 and 2 select different visible items.
        // Expected: the result contains those positions and the selected field.
        /*
         *          column
         *          0     1
         * row 0 |  B  |  B  |
         * row 1 |  A  |     |
         */
        when(reelBank.getItems(0, 1)).thenReturn(List.of(b, a));
        when(reelBank.getItems(1, 2)).thenReturn(List.of(b));

        var engine = new StandardSlotEngine<>(config, matcherFactory);
        var result = engine.spin(1, 2);

        assertArrayEquals(new int[]{1, 2}, result.getReelPositions());
        assertEquals(2, result.getField().getColumnCount());
        assertEquals(2, result.getField().getColumnSize(0));
        assertEquals(1, result.getField().getColumnSize(1));
        assertSame(b, result.getField().getItem(0, 0));
        assertSame(a, result.getField().getItem(0, 1));
        assertSame(b, result.getField().getItem(1, 0));

        verify(reelBank).getItems(0, 1);
        verify(reelBank).getItems(1, 2);
    }

    @Test
    void spinPassesConfigAndGeneratedFieldToFactory() {
        // Input: a valid spin.
        // Expected: the factory receives the original config and result field.
        var engine = new StandardSlotEngine<>(config, matcherFactory);
        var result = engine.spin(0, 0);

        verify(matcherFactory).getMatchers(same(config), same(result.getField()));
    }

    @Test
    void spinReturnsNoMatchesForEmptyFactoryResult() {
        // Input: the factory returns an empty list.
        // Expected: the field is generated without matches or resolved prizes.
        var engine = new StandardSlotEngine<>(config, matcherFactory);
        var result = engine.spin(0, 0);

        assertArrayEquals(new int[]{0, 0}, result.getReelPositions());
        assertEquals(List.of(), result.getMatches());
        assertEquals(Map.of(), result.getPrizesByCombination());
    }

    @Test
    void spinRunsMatchersAndRetainsResultsInFactoryOrder() {
        // Input: the factory supplies two matchers.
        // Expected: both execution and result order follow the supplied list.
        doReturn(List.of(firstMatcher, secondMatcher))
                .when(matcherFactory).getMatchers(same(config), any());

        var engine = new StandardSlotEngine<>(config, matcherFactory);
        var result = engine.spin(0, 0);

        assertEquals(List.of(firstMatch, secondMatch), result.getMatches());

        InOrder order = inOrder(matcherFactory, firstMatcher, secondMatcher);
        order.verify(matcherFactory).getMatchers(same(config), same(result.getField()));
        order.verify(firstMatcher).match();
        order.verify(secondMatcher).match();
    }

    @Test
    void spinResolvesPrizesAndRetainsMatchesWithoutPrizes() {
        // Input: AA and BB match, but prizes are configured only for AA.
        // Expected: both matches remain; the prize map contains only AA.
        doReturn(List.of(firstMatcher, secondMatcher))
                .when(matcherFactory).getMatchers(same(config), any());

        var engine = new StandardSlotEngine<>(config, matcherFactory);
        var result = engine.spin(0, 0);

        assertEquals(List.of(firstMatch, secondMatch), result.getMatches());
        assertEquals(
                Map.of(aa, Set.of("PRIZE_A", "BONUS_A")),
                result.getPrizesByCombination()
        );
    }

    @Test
    void spinRetainsRepeatedMatchesWithOnePrizeEntry() {
        // Input: two distinct matches refer to the same configured combination.
        // Expected: both matches remain and share one prize entry.
        when(secondMatch.combination()).thenReturn(aa);
        when(firstMatcher.match()).thenReturn(List.of(firstMatch, secondMatch));
        doReturn(List.of(firstMatcher))
                .when(matcherFactory).getMatchers(same(config), any());

        var engine = new StandardSlotEngine<>(config, matcherFactory);
        var result = engine.spin(0, 0);

        assertEquals(List.of(firstMatch, secondMatch), result.getMatches());
        assertEquals(
                Map.of(aa, Set.of("PRIZE_A", "BONUS_A")),
                result.getPrizesByCombination()
        );
    }

    @Test
    void spinAcceptsLastPhysicalPositionOfEachReel() {
        // Input: positions equal each reel's physical size minus one.
        // Expected: both upper valid boundaries are accepted.
        when(reelBank.getItems(0, 3)).thenReturn(List.of(a, b));
        when(reelBank.getItems(1, 2)).thenReturn(List.of(a));

        var engine = new StandardSlotEngine<>(config, matcherFactory);
        var result = engine.spin(3, 2);

        assertArrayEquals(new int[]{3, 2}, result.getReelPositions());
    }

    @Test
    void spinRejectsNullPositions() {
        // Input: the position array is null.
        // Expected: the factory is not called.
        var engine = new StandardSlotEngine<>(config, matcherFactory);

        var exception = assertThrows(
                NullPointerException.class,
                () -> engine.spin((int[]) null)
        );

        assertEquals("Reel positions must not be null", exception.getMessage());
        verifyNoInteractions(matcherFactory);
    }

    @Test
    void spinRejectsEmptyPositions() {
        // Input: no positions are supplied.
        // Expected: the explicit empty-array check rejects the spin.
        assertInvalidPositions("Reel positions must not be empty");
    }

    @Test
    void spinRejectsTooFewPositions() {
        // Input: one position is supplied for two reels.
        // Expected: missing positions are not generated automatically.
        assertInvalidPositions("Expected 2 reel positions, received 1", 0);
    }

    @Test
    void spinRejectsTooManyPositions() {
        // Input: three positions are supplied for two reels.
        // Expected: excess positions are rejected.
        assertInvalidPositions("Expected 2 reel positions, received 3", 0, 0, 0);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 4})
    void spinRejectsInvalidFirstReelPosition(int position) {
        // Input: reel 0 receives a position outside its valid range 0..3.
        // Expected: the error identifies the position and reel.
        assertInvalidPositions(
                "Invalid position " + position + " for reel 0",
                position,
                0
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 3})
    void spinRejectsInvalidSecondReelPosition(int position) {
        // Input: reel 1 receives a position outside its valid range 0..2.
        // Expected: each reel is checked using its own physical size.
        assertInvalidPositions(
                "Invalid position " + position + " for reel 1",
                0,
                position
        );
    }

    @Test
    void spinValidatesLineRowsBeforeCallingFactory() {
        // Input: the line references row 1 in a column containing only row 0.
        // Expected: field-dependent validation fails before factory invocation.
        /*
         *          column
         *          0     1
         * row 0 |  A  |  A  |
         * row 1 |  B  |  *  |  <- invalid position
         */
        var invalidLine = new FieldLine(1, List.of(new FieldPosition(1, 1)));
        when(config.getLines()).thenReturn(List.of(invalidLine));

        var engine = new StandardSlotEngine<>(config, matcherFactory);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> engine.spin(0, 0)
        );

        assertEquals("Line 1 references invalid row 1 in column 1", exception.getMessage());
        verifyNoInteractions(matcherFactory);
    }

    @Test
    void spinRejectsNullFactoryResult() {
        // Input: the factory returns null instead of a matcher list.
        // Expected: the engine reports a factory contract violation.
        doReturn(null).when(matcherFactory).getMatchers(same(config), any());

        var engine = new StandardSlotEngine<>(config, matcherFactory);

        var exception = assertThrows(
                IllegalStateException.class,
                () -> engine.spin(0, 0)
        );

        assertEquals("Matcher factory must not return null", exception.getMessage());
    }

    @Test
    void spinRejectsNullMatcherBeforeRunningAnyMatcher() {
        // Input: a valid matcher is followed by a null element.
        // Expected: the complete list is validated before any matcher runs.
        doReturn(Arrays.asList(firstMatcher, null))
                .when(matcherFactory).getMatchers(same(config), any());

        var engine = new StandardSlotEngine<>(config, matcherFactory);

        var exception = assertThrows(
                IllegalStateException.class,
                () -> engine.spin(0, 0)
        );

        assertEquals("Matcher factory returned null matcher at index 1", exception.getMessage());
        verifyNoInteractions(firstMatcher, secondMatcher);
    }

    @Test
    void spinPropagatesFactoryException() {
        // Input: the factory fails while creating matchers.
        // Expected: the original exception reaches the caller unchanged.
        var failure = new IllegalStateException("Factory failed");
        when(matcherFactory.getMatchers(same(config), any())).thenThrow(failure);

        var engine = new StandardSlotEngine<>(config, matcherFactory);

        var actual = assertThrows(
                IllegalStateException.class,
                () -> engine.spin(0, 0)
        );

        assertSame(failure, actual);
        verifyNoInteractions(firstMatcher, secondMatcher);
    }

    @Test
    void spinPropagatesMatcherExceptionAndStopsFollowingMatchers() {
        // Input: the first matcher throws an exception.
        // Expected: the exception is propagated and the second matcher does not run.
        var failure = new IllegalStateException("Matcher failed");
        when(firstMatcher.match()).thenThrow(failure);
        doReturn(List.of(firstMatcher, secondMatcher))
                .when(matcherFactory).getMatchers(same(config), any());

        var engine = new StandardSlotEngine<>(config, matcherFactory);

        var actual = assertThrows(
                IllegalStateException.class,
                () -> engine.spin(0, 0)
        );

        assertSame(failure, actual);
        verifyNoInteractions(secondMatcher);
    }

    @Test
    void spinCreatesMatchersForEachInvocation() {
        // Input: two spins are performed on the same engine.
        // Expected: the factory receives a separate generated field for each spin.
        var engine = new StandardSlotEngine<>(config, matcherFactory);

        var firstResult = engine.spin(0, 0);
        var secondResult = engine.spin(0, 0);

        assertNotSame(firstResult.getField(), secondResult.getField());
        verify(matcherFactory).getMatchers(same(config), same(firstResult.getField()));
        verify(matcherFactory).getMatchers(same(config), same(secondResult.getField()));
    }

    @Test
    void spinResultDoesNotExposeMutablePositions() {
        // Input: the caller modifies the input array and an array returned by the getter.
        // Expected: neither modification changes the stored spin positions.
        int[] positions = {0, 0};
        var engine = new StandardSlotEngine<>(config, matcherFactory);
        var result = engine.spin(positions);

        positions[0] = 3;

        int[] returnedPositions = result.getReelPositions();
        returnedPositions[1] = 2;

        assertArrayEquals(new int[]{0, 0}, result.getReelPositions());
    }

    @Test
    void spinReturnsUnmodifiableCollections() {
        // Input: a spin produces a match with configured prizes.
        // Expected: the match list, prize map, and nested prize set cannot be modified.
        doReturn(List.of(firstMatcher))
                .when(matcherFactory).getMatchers(same(config), any());

        var engine = new StandardSlotEngine<>(config, matcherFactory);
        var result = engine.spin(0, 0);

        assertThrows(
                UnsupportedOperationException.class,
                () -> result.getMatches().clear()
        );
        assertThrows(
                UnsupportedOperationException.class,
                () -> result.getPrizesByCombination().clear()
        );
        assertThrows(
                UnsupportedOperationException.class,
                () -> result.getPrizesByCombination().get(aa).clear()
        );
    }

    /**
     * Checks position validation and ensures the factory is not invoked.
     */
    private void assertInvalidPositions(String message, int... positions) {
        var engine = new StandardSlotEngine<>(config, matcherFactory);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> engine.spin(positions)
        );

        assertEquals(message, exception.getMessage());
        verifyNoInteractions(matcherFactory);
    }
}
