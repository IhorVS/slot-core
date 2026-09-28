package ivs.game.accessories.slot.matcher.linear;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.field.impl.StandardSlotField;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.policy.AllCombinationMatchPolicy;
import ivs.game.accessories.slot.matcher.policy.LongestCombinationMatchPolicy;
import ivs.game.accessories.slot.reel.ReelItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class LinearCombinationMatcherTest {
    private static final int LINE_ID = 0;

    private static final AllCombinationMatchPolicy<
            LinearCombinationMatch<ReelItem>
            > ALL_POLICY = new AllCombinationMatchPolicy<>();

    private static final ReelItem A = mock();
    private static final ReelItem B = mock();
    private static final ReelItem C = mock();
    private static final ReelItem WILD = mock();

    /*
     * FIELD:
     *          column
     *          0     1     2     3     4
     * row 0 |  A  |  A  |  A  |  A  |  A  |
     * row 1 |  B  |  B  |  B  |  B  |  B  |
     * row 2 |  C  |  C  |  C  |  C  |  C  |
     */
    private static final SlotField<ReelItem> FIELD = new StandardSlotField<>(List.of(
            List.of(A, B, C),
            List.of(A, B, C),
            List.of(A, B, C),
            List.of(A, B, C),
            List.of(A, B, C)
    ));

    /*
     * FIELD_WITH_WILD:
     *          column
     *          0     1     2     3     4
     * row 0 | WILD|  A  |  A  |  A  |  A  |
     * row 1 |  B  |  B  |  B  |  B  |  B  |
     * row 2 |  C  |  C  |  C  |  C  |  C  |
     */
    private static final SlotField<ReelItem> FIELD_WITH_WILD = new StandardSlotField<>(List.of(
            List.of(WILD, B, C),
            List.of(A, B, C),
            List.of(A, B, C),
            List.of(A, B, C),
            List.of(A, B, C)
    ));

    /*
     * FIELD_WITH_MISSING_ITEM:
     *          column
     *          0     1     2     3     4
     * row 0 |  A  |  B  |  A  |  A  |  A  |
     * row 1 |  B  |  A  |  B  |  B  |  B  |
     * row 2 |  C  |  C  |  C  |  C  |  C  |
     */
    private static final SlotField<ReelItem> FIELD_WITH_MISSING_ITEM =
            new StandardSlotField<>(List.of(
                    List.of(A, B, C),
                    List.of(B, A, C),
                    List.of(A, B, C),
                    List.of(A, B, C),
                    List.of(A, B, C)
            ));
    private static final SlotField<ReelItem> FIELD_WITH_WILD_IN_MIDDLE =
            fieldWithTopRow(List.of(A, WILD, A, A, A));
    private static final SlotField<ReelItem> FIELD_WITH_WILD_AT_END =
            fieldWithTopRow(List.of(A, A, A, A, WILD));
    private static final SlotField<ReelItem> FIELD_WITH_MULTIPLE_WILDS =
            fieldWithTopRow(List.of(A, WILD, WILD, A, A));
    private static final SlotField<ReelItem> FIELD_WITH_DIFFERENT_SYMBOLS =
            fieldWithTopRow(List.of(A, B, C, A, A));
    /*
     * FIELD_WITH_SELECTED_LINES:
     *          column
     *          0     1     2     3     4
     * row 0 |  A  |  A  |  A  |  A  |  A  |
     * row 1 |  B  |  B  |  B  |  B  |  B  |  <- selected line
     * row 2 |  C  |  C  |  C  |  C  |  C  |  <- selected line
     */
    private static final SlotField<ReelItem> FIELD_WITH_SELECTED_LINES =
            new StandardSlotField<>(List.of(
                    List.of(A, B, C),
                    List.of(A, B, C),
                    List.of(A, B, C),
                    List.of(A, B, C),
                    List.of(A, B, C)
            ));

    private static final FieldLine LINE = new FieldLine(LINE_ID, List.of(
            new FieldPosition(0, 0),
            new FieldPosition(1, 0),
            new FieldPosition(2, 0),
            new FieldPosition(3, 0),
            new FieldPosition(4, 0)
    ));

    private static final List<FieldLine> LINES = List.of(LINE);
    private static final FieldLine SECOND_LINE = new FieldLine(LINE_ID, List.of(
            new FieldPosition(0, 1),
            new FieldPosition(1, 1),
            new FieldPosition(2, 1),
            new FieldPosition(3, 1),
            new FieldPosition(4, 1)
    ));
    private static final FieldLine THIRD_LINE = new FieldLine(LINE_ID, List.of(
            new FieldPosition(0, 2),
            new FieldPosition(1, 2),
            new FieldPosition(2, 2),
            new FieldPosition(3, 2),
            new FieldPosition(4, 2)
    ));
    private static final List<FieldLine> TWO_LINES = List.of(SECOND_LINE, THIRD_LINE);
    private static final FieldLine SHORT_LINE = new FieldLine(LINE_ID, List.of(
            new FieldPosition(0, 0),
            new FieldPosition(1, 0),
            new FieldPosition(2, 0)
    ));

    private static final Combination<ReelItem> AAA =
            new Combination<>(0, "A", A, A, A);
    private static final Combination<ReelItem> AAAA =
            new Combination<>(1, "A", A, A, A, A);
    private static final Combination<ReelItem> AAAAA =
            new Combination<>(2, "A", A, A, A, A, A);
    private static final Combination<ReelItem> BBBBB =
            new Combination<>(3, "B", B, B, B, B, B);
    private static final Combination<ReelItem> CCC =
            new Combination<>(4, "C", C, C, C);

    private static final List<Combination<ReelItem>> COMBINATIONS =
            List.of(AAA, AAAA, AAAAA);

    private static final Set<ReelItem> EMPTY_WILD_SUBSTITUTES = Set.of();
    private static final Set<ReelItem> WILD_SUBSTITUTES = Set.of(WILD);

    @Test
    void matchReturnsAllConfiguredCombinationsOnSelectedLine() {
        // Input: the selected top line contains five A symbols.
        // Expected: all configured combinations AAA, AAAA, and AAAAA match.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  A  |  A  |  A  |  A  |  A  |  <- selected line
         * row 1 |  B  |  B  |  B  |  B  |  B  |
         * row 2 |  C  |  C  |  C  |  C  |  C  |
         */
        var matches = new LinearCombinationMatcher<>(
                FIELD,
                LINES,
                COMBINATIONS,
                EMPTY_WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(3, matches.size());
        assertEquals(AAA, matches.get(0).combination());
        assertEquals(AAAA, matches.get(1).combination());
        assertEquals(AAAAA, matches.get(2).combination());
        assertEquals(LINE, matches.get(0).line());
        assertEquals(
                List.of(new FieldPosition(0, 0), new FieldPosition(1, 0),
                        new FieldPosition(2, 0)),
                matches.get(0).positions()
        );
    }

    @Test
    void matchAppliesConfiguredPolicy() {
        var matches = new LinearCombinationMatcher<>(
                FIELD,
                LINES,
                COMBINATIONS,
                EMPTY_WILD_SUBSTITUTES,
                new LongestCombinationMatchPolicy<>()
        ).match();

        assertEquals(List.of(AAAAA), matches.stream()
                .map(LinearCombinationMatch::combination)
                .toList());
    }

    @Test
    void matchDoesNotAcceptWildSubstituteAtStart() {
        // Input: the selected line starts with WILD followed by A symbols.
        // Expected: WILD cannot replace the first item of an A combination.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 | WILD|  A  |  A  |  A  |  A  |  <- selected line
         * row 1 |  B  |  B  |  B  |  B  |  B  |
         * row 2 |  C  |  C  |  C  |  C  |  C  |
         */
        var matches = new LinearCombinationMatcher<>(
                FIELD_WITH_WILD,
                LINES,
                COMBINATIONS,
                WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(List.of(), matches);
    }

    @Test
    void matchDoesNotAcceptLineOfWildSubstitutes() {
        // Input: every position on the selected line contains WILD.
        // Expected: no A combination matches because the first item is WILD.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 | WILD| WILD| WILD| WILD| WILD|  <- selected line
         * row 1 |  B  |  B  |  B  |  B  |  B  |
         * row 2 |  C  |  C  |  C  |  C  |  C  |
         */
        var field = fieldWithTopRow(List.of(WILD, WILD, WILD, WILD, WILD));

        var matches = new LinearCombinationMatcher<>(
                field,
                LINES,
                COMBINATIONS,
                WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(List.of(), matches);
    }

    @Test
    void matchReturnsMatchesForEverySelectedLine() {
        // Input: the second line contains BBBBB and the third line contains
        // CCCCC.
        // Expected: BBBBB matches completely and CCC matches the first three
        // positions of the third line.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  A  |  A  |  A  |  A  |  A  |
         * row 1 |  B  |  B  |  B  |  B  |  B  |  <- selected line
         * row 2 |  C  |  C  |  C  |  C  |  C  |  <- selected line
         */
        var matches = new LinearCombinationMatcher<>(
                FIELD_WITH_SELECTED_LINES,
                TWO_LINES,
                List.of(BBBBB, CCC),
                EMPTY_WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(2, matches.size());
        assertEquals(BBBBB, matches.getFirst().combination());
        assertEquals(SECOND_LINE, matches.getFirst().line());
        assertEquals(5, matches.get(0).length());
        assertEquals(SECOND_LINE.positions(), matches.get(0).positions());
        assertEquals(CCC, matches.get(1).combination());
        assertEquals(THIRD_LINE, matches.get(1).line());
        assertEquals(3, matches.get(1).length());
        assertEquals(
                THIRD_LINE.positions().subList(0, 3),
                matches.get(1).positions()
        );
    }

    @Test
    void matchReturnsResultsInLineThenCombinationOrder() {
        // Input: selected lines are checked in the order BBBBB, then CCCCC;
        // configured combinations are BBBBB and CCC.
        // Expected: results are returned by line order, then combination order.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  A  |  A  |  A  |  A  |  A  |
         * row 1 |  B  |  B  |  B  |  B  |  B  |  <- first selected line
         * row 2 |  C  |  C  |  C  |  C  |  C  |  <- second selected line
         */
        var matches = new LinearCombinationMatcher<>(
                FIELD_WITH_SELECTED_LINES,
                TWO_LINES,
                List.of(BBBBB, CCC),
                EMPTY_WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(
                List.of(SECOND_LINE, THIRD_LINE),
                matches.stream().map(LinearCombinationMatch::line).toList()
        );
        assertEquals(
                List.of(BBBBB, CCC),
                matches.stream().map(LinearCombinationMatch::combination).toList()
        );
    }

    @Test
    void matchSupportsCombinationsWithDifferentSymbols() {
        // Input: the selected line starts with A, B, C.
        // Expected: the configured ABC combination matches the first three
        // positions.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  A  |  B  |  C  |  A  |  A  |  <- selected line
         * row 1 |  B  |  B  |  B  |  B  |  B  |
         * row 2 |  C  |  C  |  C  |  C  |  C  |
         */
        var combination = new Combination<>(0, "ABC", A, B, C);
        var matches = new LinearCombinationMatcher<>(
                FIELD_WITH_DIFFERENT_SYMBOLS,
                LINES,
                List.of(combination),
                EMPTY_WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(1, matches.size());
        assertEquals(combination, matches.getFirst().combination());
    }

    @Test
    void matchDoesNotReturnCombinationLongerThanLine() {
        // Input: the selected line contains only three positions.
        // Expected: the four-symbol combination cannot match this line.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  A  |  A  |  A  |  A  |  A  |  <- first three selected
         * row 1 |  B  |  B  |  B  |  B  |  B  |
         * row 2 |  C  |  C  |  C  |  C  |  C  |
         */
        var matches = new LinearCombinationMatcher<>(
                FIELD,
                List.of(SHORT_LINE),
                List.of(AAAA),
                EMPTY_WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(List.of(), matches);
    }

    @Test
    void matchAcceptsWildSubstituteInTheMiddle() {
        // Input: the selected line contains a configured wild substitute in
        // its middle position.
        // Expected: the wild substitute completes AAAAA.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  A  | WILD|  A  |  A  |  A  |  <- selected line
         * row 1 |  B  |  B  |  B  |  B  |  B  |
         * row 2 |  C  |  C  |  C  |  C  |  C  |
         */
        var matches = new LinearCombinationMatcher<>(
                FIELD_WITH_WILD_IN_MIDDLE,
                LINES,
                List.of(AAAAA),
                WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(1, matches.size());
    }

    @Test
    void matchAcceptsWildSubstituteAtTheEnd() {
        // Input: the selected line contains a configured wild substitute in
        // its last position.
        // Expected: the wild substitute completes AAAAA.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  A  |  A  |  A  |  A  | WILD|  <- selected line
         * row 1 |  B  |  B  |  B  |  B  |  B  |
         * row 2 |  C  |  C  |  C  |  C  |  C  |
         */
        var matches = new LinearCombinationMatcher<>(
                FIELD_WITH_WILD_AT_END,
                LINES,
                List.of(AAAAA),
                WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(1, matches.size());
    }

    @Test
    void matchAcceptsTwoWildSubstitutesInThreeItemCombination() {
        // Input: the selected line starts with A, WILD, WILD.
        // Expected: both wild substitutes complete AAA.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  A  | WILD| WILD|  A  |  A  |  <- selected line
         * row 1 |  B  |  B  |  B  |  B  |  B  |
         * row 2 |  C  |  C  |  C  |  C  |  C  |
         */
        var matches = new LinearCombinationMatcher<>(
                FIELD_WITH_MULTIPLE_WILDS,
                LINES,
                List.of(AAA),
                WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(List.of(AAA), matches.stream()
                .map(LinearCombinationMatch::combination)
                .toList());
    }

    @Test
    void matchAcceptsWildSubstituteAtEndOfThreeItemCombination() {
        // Input: the selected line starts with A, A, WILD.
        // Expected: the wild substitute completes AAA.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  A  |  A  | WILD|  A  |  A  |  <- selected line
         * row 1 |  B  |  B  |  B  |  B  |  B  |
         * row 2 |  C  |  C  |  C  |  C  |  C  |
         */
        var field = fieldWithTopRow(List.of(A, A, WILD, A, A));

        var matches = new LinearCombinationMatcher<>(
                field,
                LINES,
                List.of(AAA),
                WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(List.of(AAA), matches.stream()
                .map(LinearCombinationMatch::combination)
                .toList());
    }

    @Test
    void matchDoesNotUseWildSubstituteWhenItIsNotConfigured() {
        // Input: the selected line contains WILD after A, but no wild
        // substitutes are configured.
        // Expected: WILD cannot replace the second A in AAAAA.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  A  | WILD|  A  |  A  |  A  |  <- selected line
         * row 1 |  B  |  B  |  B  |  B  |  B  |
         * row 2 |  C  |  C  |  C  |  C  |  C  |
         */
        var matches = new LinearCombinationMatcher<>(
                FIELD_WITH_WILD_IN_MIDDLE,
                LINES,
                List.of(AAAAA),
                EMPTY_WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(List.of(), matches);
    }

    @Test
    void matchReturnsActualPositionsIncludingWildSubstitutes() {
        // Input: the middle position of the selected line is a configured
        // wild substitute.
        // Expected: the match contains the original line positions, including
        // the Wild position.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  A  | WILD|  A  |  A  |  A  |  <- selected line
         * row 1 |  B  |  B  |  B  |  B  |  B  |
         * row 2 |  C  |  C  |  C  |  C  |  C  |
         */
        var matches = new LinearCombinationMatcher<>(
                FIELD_WITH_WILD_IN_MIDDLE,
                LINES,
                List.of(AAAAA),
                WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(LINE.positions(), matches.getFirst().positions());
    }

    @Test
    void matchReturnsUnmodifiableResults() {
        // Input: a complete AAA match on the selected top line.
        // Expected: the match list and its positions are unmodifiable.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  A  |  A  |  A  |  A  |  A  |  <- selected line
         * row 1 |  B  |  B  |  B  |  B  |  B  |
         * row 2 |  C  |  C  |  C  |  C  |  C  |
         */
        var matches = new LinearCombinationMatcher<>(
                FIELD,
                LINES,
                List.of(AAA),
                EMPTY_WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertThrows(UnsupportedOperationException.class, matches::clear);
        assertThrows(
                UnsupportedOperationException.class,
                () -> matches.getFirst().positions().clear()
        );
    }

    @Test
    void matchDoesNotReturnCombinationWithMissingItem() {
        // Input: the selected top line starts with "A" but contains "B" in the
        // second column.
        // Expected: no A combinations of length 3 to 5 match.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  A  |  B  |  A  |  A  |  A  |  <- selected line
         * row 1 |  B  |  A  |  B  |  B  |  B  |
         * row 2 |  C  |  C  |  C  |  C  |  C  |
         */
        var matches = new LinearCombinationMatcher<>(
                FIELD_WITH_MISSING_ITEM,
                LINES,
                COMBINATIONS,
                EMPTY_WILD_SUBSTITUTES,
                ALL_POLICY
        ).match();

        assertEquals(List.of(), matches);
    }

    private static SlotField<ReelItem> fieldWithTopRow(List<ReelItem> topRow) {
        return new StandardSlotField<>(topRow.stream()
                .map(item -> List.of(item, B, C))
                .toList());
    }
}
