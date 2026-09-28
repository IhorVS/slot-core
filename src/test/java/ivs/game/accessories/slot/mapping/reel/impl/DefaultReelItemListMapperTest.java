package ivs.game.accessories.slot.mapping.reel.impl;

import ivs.game.accessories.slot.mapping.reel.ReelItemMapper;
import ivs.game.accessories.slot.reel.ReelItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests list mapping through a configured reel item mapper.
 */
@ExtendWith(MockitoExtension.class)
class DefaultReelItemListMapperTest {

    @Mock
    private ReelItem firstItem;

    @Mock
    private ReelItem secondItem;

    @Mock
    private ReelItemMapper<ReelItem> itemMapper;

    @InjectMocks
    private DefaultReelItemListMapper<ReelItem> mapper;

    /**
     * Verifies that every source value is delegated to the configured mapper
     * and that the original element order is preserved.
     */
    @Test
    void mapsValuesInOriginalOrder() {
        when(itemMapper.map("A")).thenReturn(firstItem);
        when(itemMapper.map("B")).thenReturn(secondItem);

        List<ReelItem> result = mapper.map(List.of("A", "B"));

        assertEquals(List.of(firstItem, secondItem), result);

        InOrder inOrder = inOrder(itemMapper);
        inOrder.verify(itemMapper).map("A");
        inOrder.verify(itemMapper).map("B");
    }

    /**
     * Verifies that mapping an empty source list produces an empty result
     * without invoking the item mapper.
     */
    @Test
    void mapsEmptyList() {
        List<ReelItem> result = mapper.map(List.of());

        assertTrue(result.isEmpty());
        verifyNoInteractions(itemMapper);
    }

    /**
     * Verifies that the resulting list cannot be modified.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void returnsUnmodifiableList() {
        when(itemMapper.map("A")).thenReturn(firstItem);

        List<ReelItem> result = mapper.map(List.of("A"));

        assertThrows(UnsupportedOperationException.class, () -> result.add(secondItem));
    }

    /**
     * Verifies that a {@code null} source list is rejected before mapping.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullList() {
        assertThrows(NullPointerException.class, () -> mapper.map(null));
        verifyNoInteractions(itemMapper);
    }

    /**
     * Verifies that a source list containing a {@code null} element is rejected
     * before any values are mapped.
     */
    @Test
    void rejectsNullElement() {
        List<String> values = Arrays.asList("A", null, "B");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> mapper.map(values));

        assertEquals("Reel Items must not contain null elements", e.getMessage());
        verifyNoInteractions(itemMapper);
    }

    /**
     * Verifies that a {@code null} item mapper is rejected during construction.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullItemMapper() {
        assertThrows(NullPointerException.class, () -> new DefaultReelItemListMapper<>(null));
    }

    /**
     * Verifies that an exception produced by the item mapper is propagated and
     * subsequent values are not processed.
     */
    @Test
    void propagatesItemMappingFailure() {
        IllegalArgumentException expectedException = new IllegalArgumentException("Unknown reel item");

        when(itemMapper.map("A")).thenReturn(firstItem);
        when(itemMapper.map("UNKNOWN")).thenThrow(expectedException);

        IllegalArgumentException actualException = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(List.of("A", "UNKNOWN", "B"))
        );

        assertSame(expectedException, actualException);
        verify(itemMapper, never()).map("B");
    }
}
