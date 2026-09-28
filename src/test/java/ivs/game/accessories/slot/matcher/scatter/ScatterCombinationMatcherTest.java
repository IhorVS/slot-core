package ivs.game.accessories.slot.matcher.scatter;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.field.impl.StandardSlotField;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.policy.AllCombinationMatchPolicy;
import ivs.game.accessories.slot.matcher.policy.LongestCombinationMatchPolicy;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScatterCombinationMatcherTest {

    private static final int ID = 0;

    private static final AllCombinationMatchPolicy<ScatterCombinationMatch<StandardReelItem>> ALL_POLICY =
            new AllCombinationMatchPolicy<>();

    @Test
    void constructorRejectsNullField() {
        assertThrows(
                NullPointerException.class,
                () -> new ScatterCombinationMatcher<>(
                        null,
                        List.of(new Combination<>(ID,
                                "SCT",
                                StandardReelItem.SCT,
                                StandardReelItem.SCT)),
                        ALL_POLICY
                )
        );
    }

    @Test
    void constructorRejectsNullCombinations() {
        assertThrows(
                NullPointerException.class,
                () -> new ScatterCombinationMatcher<>(field(), null, ALL_POLICY)
        );
    }

    @Test
    void constructorRejectsEmptyCombinations() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ScatterCombinationMatcher<>(field(), List.of(), ALL_POLICY)
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void constructorRejectsNullCombination() {
        assertThrows(
                NullPointerException.class,
                () -> new ScatterCombinationMatcher<>(
                        field(),
                        List.of((Combination<StandardReelItem>) null),
                        ALL_POLICY
                )
        );
    }

    @Test
    void matchFindsCombinationRegardlessOfPosition() {
        // Input: three SCT symbols at arbitrary field positions.
        // Expected: one match containing all three SCT positions.
        /*
         *          column
         *          0     1     2
         * row 0 | SCT |  B  | SCT |
         * row 1 |  A  |     | SCT |
         */
        SlotField<StandardReelItem> field = new StandardSlotField<>(List.of(
                List.of(StandardReelItem.SCT, StandardReelItem.A),
                List.of(StandardReelItem.B),
                List.of(StandardReelItem.SCT, StandardReelItem.SCT)
        ));

        var combination = new Combination<>(ID,
                "SCT",
                StandardReelItem.SCT,
                StandardReelItem.SCT,
                StandardReelItem.SCT);
        var matcher = new ScatterCombinationMatcher<>(
                field,
                List.of(combination),
                ALL_POLICY
        );

        var matches = matcher.match();

        assertEquals(1, matches.size());
        assertEquals(combination, matches.getFirst().combination());
        assertEquals(
                List.of(
                        new FieldPosition(0, 0),
                        new FieldPosition(2, 0),
                        new FieldPosition(2, 1)
                ),
                matches.getFirst().positions()
        );
    }

    @Test
    void matchReturnsAllConfiguredCombinationLengths() {
        // Input: five A symbols in different rows across five columns.
        // Expected: all configured combinations AAA, AAAA, and AAAAA.
        /*
         *          column
         *          0     1     2     3     4
         * row 0 |  B  |  A  |  B  |  C  |  A  |
         * row 1 |  C  |  B  |  A  |  B  |  C  |
         * row 2 |  A  |  C  |  B  |  A  |  B  |
         */
        SlotField<StandardReelItem> field = new StandardSlotField<>(List.of(
                List.of(StandardReelItem.B, StandardReelItem.C, StandardReelItem.A),
                List.of(StandardReelItem.A, StandardReelItem.B, StandardReelItem.C),
                List.of(StandardReelItem.B, StandardReelItem.A, StandardReelItem.B),
                List.of(StandardReelItem.C, StandardReelItem.B, StandardReelItem.A),
                List.of(StandardReelItem.A, StandardReelItem.C, StandardReelItem.B)
        ));
        var combinations = List.of(
                new Combination<>(ID,
                        "A",
                        StandardReelItem.A,
                        StandardReelItem.A,
                        StandardReelItem.A),
                new Combination<>(ID,
                        "A",
                        StandardReelItem.A,
                        StandardReelItem.A,
                        StandardReelItem.A,
                        StandardReelItem.A),
                new Combination<>(ID,
                        "A",
                        StandardReelItem.A,
                        StandardReelItem.A,
                        StandardReelItem.A,
                        StandardReelItem.A,
                        StandardReelItem.A)
        );
        var matcher = new ScatterCombinationMatcher<>(
                field,
                combinations,
                ALL_POLICY
        );

        var matches = matcher.match();

        assertEquals(3, matches.size());
        assertEquals(combinations.get(0), matches.get(0).combination());
        assertEquals(combinations.get(1), matches.get(1).combination());
        assertEquals(combinations.get(2), matches.get(2).combination());
        assertEquals(3, matches.get(0).length());
        assertEquals(4, matches.get(1).length());
        assertEquals(5, matches.get(2).length());
    }

    @Test
    void matchAppliesConfiguredPolicy() {
        SlotField<StandardReelItem> field = new StandardSlotField<>(List.of(
                List.of(StandardReelItem.A),
                List.of(StandardReelItem.A),
                List.of(StandardReelItem.A),
                List.of(StandardReelItem.A),
                List.of(StandardReelItem.A)
        ));
        var combinations = List.of(
                new Combination<>(ID, "A", StandardReelItem.A,
                        StandardReelItem.A, StandardReelItem.A),
                new Combination<>(ID, "A", StandardReelItem.A,
                        StandardReelItem.A, StandardReelItem.A, StandardReelItem.A),
                new Combination<>(ID, "A", StandardReelItem.A,
                        StandardReelItem.A, StandardReelItem.A,
                        StandardReelItem.A, StandardReelItem.A)
        );

        var matches = new ScatterCombinationMatcher<>(
                field,
                combinations,
                new LongestCombinationMatchPolicy<>()
        ).match();

        assertEquals(1, matches.size());
        assertEquals(5, matches.getFirst().length());
    }

    @SuppressWarnings("ExtractMethodRecommender")
    @Test
    void matchSupportsDifferentSymbolsInOneCombination() {
        // Input: combination [C, A, B] and a field containing A, B, and C.
        // Expected: one match, regardless of the combination item order.
        /*
         *          column
         *          0     1     2
         * row 0 |  A  |  B  |  C  |
         */
        SlotField<StandardReelItem> field = new StandardSlotField<>(List.of(
                List.of(StandardReelItem.A),
                List.of(StandardReelItem.B),
                List.of(StandardReelItem.C)
        ));

        var matcher = new ScatterCombinationMatcher<>(
                field,
                List.of(new Combination<>(ID,
                        "CAB",
                        StandardReelItem.C,
                        StandardReelItem.A,
                        StandardReelItem.B)),
                ALL_POLICY
        );

        assertEquals(
                List.of(
                        new FieldPosition(2, 0),
                        new FieldPosition(0, 0),
                        new FieldPosition(1, 0)
                ),
                matcher.match().getFirst().positions()
        );
    }

    @Test
    void matchDoesNotReturnCombinationWithMissingSymbol() {
        // Input: combination [A, A] and a field containing only one A.
        // Expected: no matches.
        /*
         *          column
         *          0     1
         * row 0 |  A  |  B  |
         */
        SlotField<StandardReelItem> field = new StandardSlotField<>(List.of(
                List.of(StandardReelItem.A),
                List.of(StandardReelItem.B)
        ));

        var matcher = new ScatterCombinationMatcher<>(
                field,
                List.of(new Combination<>(ID,
                        "A",
                        StandardReelItem.A,
                        StandardReelItem.A)),
                ALL_POLICY
        );

        assertEquals(List.of(), matcher.match());
    }

    @Test
    void matchReturnsNoMatchesForEmptyField() {
        SlotField<StandardReelItem> emptyField = new SlotField<>() {
            @Override
            public StandardReelItem getItem(int column, int row) {
                throw new IllegalArgumentException("Empty field has no items");
            }

            @Override
            public int getColumnCount() {
                return 0;
            }

            @Override
            public int getColumnSize(int column) {
                throw new IllegalArgumentException("Empty field has no columns");
            }
        };

        var matcher = new ScatterCombinationMatcher<>(
                emptyField,
                List.of(new Combination<>(ID, "SCT", StandardReelItem.SCT)),
                ALL_POLICY
        );

        assertEquals(List.of(), matcher.match());
    }

    private static SlotField<StandardReelItem> field() {
        return new StandardSlotField<>(List.of(List.of(StandardReelItem.SCT)));
    }
}
