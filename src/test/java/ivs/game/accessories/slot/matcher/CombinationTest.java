package ivs.game.accessories.slot.matcher;

import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombinationTest {

    private static final int ID_0 = 0;
    private static final int ID_1 = 1;

    private static final String GROUP_ID = "test";

    /**
     * Verifies that the list constructor stores the supplied items and group.
     */
    @Test
    void listConstructorAcceptsValidItems() {
        List<StandardReelItem> reelItems = List.of(StandardReelItem.A, StandardReelItem.B);
        Combination<StandardReelItem> combination = new Combination<>(ID_0, GROUP_ID, reelItems);

        assertEquals(reelItems, combination.getItems());
        assertEquals(GROUP_ID, combination.getGroupId());
        assertEquals(2, combination.size());
    }

    /**
     * Verifies that the constructor rejects a null group identifier.
     */
    @Test
    void constructorRejectsNullGroupId() {
        assertThrows(
                NullPointerException.class,
                () -> new Combination<>(ID_0, null, List.of(StandardReelItem.A))
        );
    }

    /**
     * Verifies that the constructor rejects a blank group identifier.
     */
    @Test
    void constructorRejectsBlankGroupId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Combination<>(ID_0, " ", List.of(StandardReelItem.A))
        );
    }

    /**
     * Verifies that the list constructor rejects a null item list.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void listConstructorRejectsNullList() {
        assertThrows(
                NullPointerException.class,
                () -> new Combination<>(ID_0, GROUP_ID, (List<StandardReelItem>) null)
        );
    }

    /**
     * Verifies that a combination must contain at least one item.
     */
    @Test
    void listConstructorRejectsEmptyList() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Combination<StandardReelItem>(ID_0, GROUP_ID, List.of())
        );
    }

    /**
     * Verifies that the list constructor rejects a null item.
     */
    @Test
    void listConstructorRejectsNullElement() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Combination<>(ID_0, GROUP_ID, Collections.singletonList((StandardReelItem) null))
        );
    }

    /**
     * Verifies that the varargs constructor stores the supplied items.
     */
    @Test
    void varargsConstructorAcceptsValidItems() {
        Combination<StandardReelItem> combination = new Combination<>(ID_0,
                GROUP_ID,
                StandardReelItem.A,
                StandardReelItem.B);

        assertEquals(
                List.of(StandardReelItem.A, StandardReelItem.B),
                combination.getItems()
        );
        assertEquals(2, combination.size());
    }

    /**
     * Verifies that the varargs constructor rejects a null array.
     */
    @Test
    void varargsConstructorRejectsNullArray() {
        assertThrows(
                NullPointerException.class,
                () -> new Combination<>(ID_0, GROUP_ID, (StandardReelItem[]) null)
        );
    }

    /**
     * Verifies that the varargs constructor rejects an empty array.
     */
    @Test
    void varargsConstructorRejectsEmptyArray() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Combination<StandardReelItem>(ID_0, GROUP_ID)
        );
    }

    /**
     * Verifies that the varargs constructor rejects a null item.
     */
    @Test
    void varargsConstructorRejectsNullElement() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Combination<>(ID_0, GROUP_ID, StandardReelItem.A, null)
        );
    }

    /**
     * Verifies that the stored item list cannot be modified.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void itemsReturnsUnmodifiableList() {
        Combination<StandardReelItem> combination =
                new Combination<>(ID_0, GROUP_ID, StandardReelItem.A);

        assertThrows(
                UnsupportedOperationException.class,
                () -> combination.getItems().add(StandardReelItem.B)
        );
    }

    /**
     * Verifies that equal combinations have equal hash codes.
     */
    @Test
    void equalCombinationsAreEqualAndHaveSameHashCode() {
        Combination<StandardReelItem> first =
                new Combination<>(ID_0, GROUP_ID, StandardReelItem.A, StandardReelItem.B);
        Combination<StandardReelItem> second =
                new Combination<>(ID_0, GROUP_ID, List.of(StandardReelItem.A, StandardReelItem.B));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Verifies that combinations with different items are not equal.
     */
    @Test
    void combinationsWithDifferentItemsAreNotEqual() {
        Combination<StandardReelItem> first =
                new Combination<>(ID_0, GROUP_ID, StandardReelItem.A, StandardReelItem.B);
        Combination<StandardReelItem> second =
                new Combination<>(ID_0, GROUP_ID, StandardReelItem.A, StandardReelItem.C);

        assertNotEquals(first, second);
    }

    /**
     * Verifies that combinations with different identifiers are not equal.
     */
    @Test
    void combinationsWithDifferentIdsAreNotEqual() {
        Combination<StandardReelItem> first =
                new Combination<>(ID_0, GROUP_ID, StandardReelItem.A, StandardReelItem.B);
        Combination<StandardReelItem> second =
                new Combination<>(ID_1, GROUP_ID, StandardReelItem.A, StandardReelItem.B);

        assertNotEquals(first, second);
    }

    /**
     * Verifies that combinations in different groups are not equal.
     */
    @Test
    void combinationsWithDifferentGroupIdsAreNotEqual() {
        Combination<StandardReelItem> first =
                new Combination<>(ID_0, "first", StandardReelItem.A);
        Combination<StandardReelItem> second =
                new Combination<>(ID_0, "second", StandardReelItem.A);

        assertNotEquals(first, second);
    }

    /**
     * Verifies that item order participates in combination equality.
     */
    @Test
    void combinationsWithDifferentOrderAreNotEqual() {
        Combination<StandardReelItem> first =
                new Combination<>(ID_0, GROUP_ID, StandardReelItem.A, StandardReelItem.B);
        Combination<StandardReelItem> second =
                new Combination<>(ID_0, GROUP_ID, StandardReelItem.B, StandardReelItem.A);

        assertNotEquals(first, second);
    }

    /**
     * Verifies that equal combinations have compatible hash codes and can be
     * found in a hash-based collection using an equivalent instance.
     */
    @Test
    void equalCombinationsAreFoundInHashSet() {
        Combination<StandardReelItem> stored =
                new Combination<>(ID_0, GROUP_ID, StandardReelItem.A, StandardReelItem.B);
        Combination<StandardReelItem> equivalent =
                new Combination<>(ID_0, GROUP_ID, List.of(StandardReelItem.A, StandardReelItem.B));

        Set<Combination<StandardReelItem>> combinations = new HashSet<>();
        combinations.add(stored);

        assertTrue(combinations.contains(equivalent));
    }

    /**
     * Verifies that differences in ID, group, or items produce distinct
     * combinations in a hash-based collection.
     */
    @Test
    void differentCombinationsRemainDistinctInHashSet() {
        Combination<StandardReelItem> first =
                new Combination<>(ID_0, GROUP_ID, StandardReelItem.A);
        Combination<StandardReelItem> differentId =
                new Combination<>(ID_1, GROUP_ID, StandardReelItem.A);
        Combination<StandardReelItem> differentGroup =
                new Combination<>(ID_0, "other", StandardReelItem.A);
        Combination<StandardReelItem> differentItems =
                new Combination<>(ID_0, GROUP_ID, StandardReelItem.B);

        Set<Combination<StandardReelItem>> combinations =
                Set.of(first, differentId, differentGroup, differentItems);

        assertEquals(4, combinations.size());
    }
}
