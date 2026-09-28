package ivs.game.accessories.slot.mapping.reel.impl;

import ivs.game.accessories.slot.mapping.reel.ReelItemListMapper;
import ivs.game.accessories.slot.reel.Reel;
import ivs.game.accessories.slot.reel.ReelItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests mapping string item identifiers to a reel.
 */
@ExtendWith(MockitoExtension.class)
class DefaultReelMapperTest {

    @Mock
    private ReelItemListMapper<ReelItem> itemListMapper;

    @Mock
    private BiFunction<List<ReelItem>, Integer, Reel<ReelItem>> reelCreator;

    @Mock
    private ReelItem firstItem;

    @Mock
    private ReelItem secondItem;

    @Mock
    private Reel<ReelItem> reel;

    @InjectMocks
    private DefaultReelMapper<ReelItem> mapper;

    /**
     * Verifies that source values are mapped before the configured reel creator
     * receives the mapped items and visible size.
     */
    @Test
    void mapsValuesAndCreatesReel() {
        List<String> values = List.of("A", "B");
        List<ReelItem> items = List.of(firstItem, secondItem);

        when(itemListMapper.map(values)).thenReturn(items);
        when(reelCreator.apply(items, 3)).thenReturn(reel);

        Reel<ReelItem> result = mapper.map(values, 3);

        assertSame(reel, result);

        InOrder inOrder = inOrder(itemListMapper, reelCreator);
        inOrder.verify(itemListMapper).map(values);
        inOrder.verify(reelCreator).apply(items, 3);
    }

    /**
     * Verifies that a {@code null} source list is rejected before mapping.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullValues() {
        assertThrows(NullPointerException.class, () -> mapper.map(null, 3));

        verifyNoInteractions(itemListMapper, reelCreator);
    }

    /**
     * Verifies that a source list containing a {@code null} element is rejected
     * before mapping.
     */
    @Test
    void rejectsNullElement() {
        List<String> values = Arrays.asList("A", null, "B");

        IllegalArgumentException e =
                assertThrows(IllegalArgumentException.class, () -> mapper.map(values, 3));

        assertEquals("List must not contain null elements", e.getMessage());
        verifyNoInteractions(itemListMapper, reelCreator);
    }

    /**
     * Verifies that an exception produced while mapping items is propagated and
     * the reel creator is not invoked.
     */
    @Test
    void propagatesItemMappingFailure() {
        List<String> values = List.of("A", "UNKNOWN");
        IllegalArgumentException expectedException = new IllegalArgumentException("Unknown reel item");

        when(itemListMapper.map(values)).thenThrow(expectedException);

        IllegalArgumentException actualException =
                assertThrows(IllegalArgumentException.class, () -> mapper.map(values, 3)
                );

        assertSame(expectedException, actualException);
        verifyNoInteractions(reelCreator);
    }

    /**
     * Verifies that an exception produced by the reel creator is propagated.
     */
    @Test
    void propagatesReelCreationFailure() {
        List<String> values = List.of("A", "B");
        List<ReelItem> items = List.of(firstItem, secondItem);
        IllegalArgumentException expectedException = new IllegalArgumentException("Invalid visible size");

        when(itemListMapper.map(values)).thenReturn(items);
        when(reelCreator.apply(items, 0)).thenThrow(expectedException);

        IllegalArgumentException actualException = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(values, 0)
        );

        assertSame(expectedException, actualException);
    }

    /**
     * Verifies that a {@code null} value returned by the reel creator is rejected.
     */
    @Test
    void rejectsNullReel() {
        List<String> values = List.of("A", "B");
        List<ReelItem> items = List.of(firstItem, secondItem);

        when(itemListMapper.map(values)).thenReturn(items);
        when(reelCreator.apply(items, 3)).thenReturn(null);

        NullPointerException e = assertThrows(NullPointerException.class, () -> mapper.map(values, 3));

        assertEquals("Reel items list must not be null", e.getMessage());
    }

    /**
     * Verifies that a {@code null} item list mapper is rejected during construction.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullItemListMapper() {
        assertThrows(NullPointerException.class, () -> new DefaultReelMapper<>(null, reelCreator));
    }

    /**
     * Verifies that a {@code null} reel creator is rejected during construction.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullReelCreator() {
        assertThrows(NullPointerException.class, () -> new DefaultReelMapper<>(itemListMapper, null));
    }
}