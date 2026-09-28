package ivs.game.accessories.slot.mapping.combination.impl;

import ivs.game.accessories.slot.mapping.combination.CombinationDescription;
import ivs.game.accessories.slot.mapping.reel.ReelItemListMapper;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests mapping combination descriptions through a configured item list mapper.
 */
@ExtendWith(MockitoExtension.class)
class DefaultCombinationMapperTest {

    @Mock
    private ReelItemListMapper<StandardReelItem> itemListMapper;

    @InjectMocks
    private DefaultCombinationMapper<StandardReelItem> mapper;

    /**
     * Verifies that the identifier, group, and mapped items are passed to the combination.
     */
    @Test
    void mapsCombinationDescription() {
        List<String> values = List.of("A", "K", "A");
        List<StandardReelItem> items = List.of(
                StandardReelItem.A,
                StandardReelItem.K,
                StandardReelItem.A
        );
        when(itemListMapper.map(values)).thenReturn(items);

        Combination<StandardReelItem> result =
                mapper.map(new CombinationDescription(7, "regular", values));

        assertEquals(7, result.getId());
        assertEquals("regular", result.getGroupId());
        assertEquals(items, result.getItems());
        verify(itemListMapper).map(values);
    }

    /**
     * Verifies that a null description is rejected before item mapping.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullDescription() {
        assertThrows(NullPointerException.class, () -> mapper.map(null));

        verifyNoInteractions(itemListMapper);
    }

    /**
     * Verifies that a null group identifier is reported with the combination ID.
     */
    @Test
    void rejectsNullGroupId() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(new CombinationDescription(3, null, List.of("A")))
        );

        assertEquals("Cannot map combination ID=3", e.getMessage());
        assertInstanceOf(NullPointerException.class, e.getCause());
        assertEquals("Group ID is null", e.getCause().getMessage());
        verifyNoInteractions(itemListMapper);
    }

    /**
     * Verifies that a null element list is rejected before item mapping.
     */
    @Test
    void rejectsNullValues() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(new CombinationDescription(4, "A", null))
        );

        assertEquals("Cannot map combination ID=4", e.getMessage());
        assertInstanceOf(NullPointerException.class, e.getCause());
        assertEquals("The list of combination elements is null", e.getCause().getMessage());
        verifyNoInteractions(itemListMapper);
    }

    /**
     * Verifies that an empty element list is rejected before item mapping.
     */
    @Test
    void rejectsEmptyValues() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(new CombinationDescription(5, "A", List.of()))
        );

        assertEquals("Cannot map combination ID=5", e.getMessage());
        assertInstanceOf(IllegalArgumentException.class, e.getCause());
        assertEquals("The list of combination elements is empty", e.getCause().getMessage());
        verifyNoInteractions(itemListMapper);
    }

    /**
     * Verifies that the index of a null element is included in the cause.
     */
    @Test
    void rejectsNullElement() {
        List<String> values = Arrays.asList("A", null, "A");

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(new CombinationDescription(6, "A", values))
        );

        assertEquals("Cannot map combination ID=6", e.getMessage());
        assertInstanceOf(IllegalArgumentException.class, e.getCause());
        assertEquals("Combination element at index 1 is null or blank", e.getCause().getMessage());
        verifyNoInteractions(itemListMapper);
    }

    /**
     * Verifies that the index of a blank element is included in the cause.
     */
    @Test
    void rejectsBlankElement() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(new CombinationDescription(8, "A", List.of("A", "  ", "A")))
        );

        assertEquals("Cannot map combination ID=8", e.getMessage());
        assertInstanceOf(IllegalArgumentException.class, e.getCause());
        assertEquals("Combination element at index 1 is null or blank", e.getCause().getMessage());
        verifyNoInteractions(itemListMapper);
    }

    /**
     * Verifies that an item mapping failure remains available as the cause.
     */
    @Test
    void preservesItemMappingFailure() {
        List<String> values = List.of("UNKNOWN");
        IllegalArgumentException failure = new IllegalArgumentException("Unknown item");
        when(itemListMapper.map(values)).thenThrow(failure);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(new CombinationDescription(9, "A", values))
        );

        assertEquals("Cannot map combination ID=9", e.getMessage());
        assertSame(failure, e.getCause());
    }

    /**
     * Verifies that a null item list mapper is rejected during construction.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullItemListMapper() {
        assertThrows(NullPointerException.class, () -> new DefaultCombinationMapper<>(null));
    }
}
