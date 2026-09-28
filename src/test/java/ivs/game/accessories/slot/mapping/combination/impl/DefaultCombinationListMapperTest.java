package ivs.game.accessories.slot.mapping.combination.impl;

import ivs.game.accessories.slot.mapping.combination.CombinationDescription;
import ivs.game.accessories.slot.mapping.combination.CombinationMapper;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests mapping combination descriptions to a map indexed by combination ID.
 */
@ExtendWith(MockitoExtension.class)
class DefaultCombinationListMapperTest {

    @Mock
    private CombinationMapper<StandardReelItem> combinationMapper;

    @InjectMocks
    private DefaultCombinationListMapper<StandardReelItem> mapper;

    /**
     * Verifies that descriptions are mapped in order and indexed by their IDs.
     */
    @Test
    void mapsDescriptionsInOriginalOrder() {
        CombinationDescription firstDescription =
                new CombinationDescription(5, "A", List.of("A", "A"));
        CombinationDescription secondDescription =
                new CombinationDescription(2, "K", List.of("K", "K"));

        Combination<StandardReelItem> first =
                new Combination<>(5, "A", StandardReelItem.A, StandardReelItem.A);
        Combination<StandardReelItem> second =
                new Combination<>(2, "K", StandardReelItem.K, StandardReelItem.K);

        when(combinationMapper.map(firstDescription)).thenReturn(first);
        when(combinationMapper.map(secondDescription)).thenReturn(second);

        Map<Integer, Combination<StandardReelItem>> result =
                mapper.map(List.of(firstDescription, secondDescription));

        assertEquals(List.of(5, 2), List.copyOf(result.keySet()));
        assertSame(first, result.get(5));
        assertSame(second, result.get(2));

        var order = inOrder(combinationMapper);
        order.verify(combinationMapper).map(firstDescription);
        order.verify(combinationMapper).map(secondDescription);
    }

    /**
     * Verifies that an empty input produces an empty map.
     */
    @Test
    void mapsEmptyList() {
        assertTrue(mapper.map(List.of()).isEmpty());

        verifyNoInteractions(combinationMapper);
    }

    /**
     * Verifies that the returned map cannot be modified.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void returnsUnmodifiableMap() {
        CombinationDescription description =
                new CombinationDescription(1, "A", List.of("A"));
        Combination<StandardReelItem> combination =
                new Combination<>(1, "A", StandardReelItem.A);
        when(combinationMapper.map(description)).thenReturn(combination);

        Map<Integer, Combination<StandardReelItem>> result =
                mapper.map(List.of(description));

        assertThrows(UnsupportedOperationException.class, () -> result.put(2, combination));
    }

    /**
     * Verifies that a null description list is rejected before mapping.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullList() {
        assertThrows(NullPointerException.class, () -> mapper.map(null));

        verifyNoInteractions(combinationMapper);
    }

    /**
     * Verifies that a null description is rejected before mapping any item.
     */
    @Test
    void rejectsNullDescription() {
        List<CombinationDescription> descriptions = Arrays.asList(
                new CombinationDescription(1, "A", List.of("A")),
                null
        );

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(descriptions)
        );

        assertEquals("List must not contain null combination descriptions", e.getMessage());
        verifyNoInteractions(combinationMapper);
    }

    /**
     * Verifies that two mapped combinations cannot have the same ID.
     */
    @Test
    void rejectsDuplicateId() {
        CombinationDescription firstDescription =
                new CombinationDescription(3, "A", List.of("A"));
        CombinationDescription secondDescription =
                new CombinationDescription(3, "K", List.of("K"));

        when(combinationMapper.map(firstDescription))
                .thenReturn(new Combination<>(3, "A", StandardReelItem.A));
        when(combinationMapper.map(secondDescription))
                .thenReturn(new Combination<>(3, "K", StandardReelItem.K));

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(List.of(firstDescription, secondDescription))
        );

        assertEquals("Duplicate combination ID: 3", e.getMessage());
    }

    /**
     * Verifies that a null result from the single combination mapper is rejected.
     */
    @Test
    void rejectsNullMappedCombination() {
        CombinationDescription description =
                new CombinationDescription(4, "A", List.of("A"));

        NullPointerException e = assertThrows(
                NullPointerException.class,
                () -> mapper.map(List.of(description))
        );

        assertEquals("Combination mapper must not return null", e.getMessage());
    }

    /**
     * Verifies that an exception from the single combination mapper is propagated.
     */
    @Test
    void propagatesCombinationMappingFailure() {
        CombinationDescription description =
                new CombinationDescription(6, "A", List.of("UNKNOWN"));
        IllegalArgumentException failure =
                new IllegalArgumentException("Cannot map combination ID=6");

        when(combinationMapper.map(description)).thenThrow(failure);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(List.of(description))
        );

        assertSame(failure, e);
    }

    /**
     * Verifies that a null single combination mapper is rejected at construction.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullCombinationMapper() {
        assertThrows(NullPointerException.class, () -> new DefaultCombinationListMapper<>(null));
    }
}
