package ivs.game.accessories.slot.mapping.reel.impl;

import ivs.game.accessories.slot.mapping.reel.ReelDescription;
import ivs.game.accessories.slot.mapping.reel.ReelItemMapper;
import ivs.game.accessories.slot.mapping.reel.ReelMapper;
import ivs.game.accessories.slot.reel.Reel;
import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.impl.StandardReelBank;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static ivs.game.accessories.slot.reel.impl.StandardReelItem.A;
import static ivs.game.accessories.slot.reel.impl.StandardReelItem.K;
import static ivs.game.accessories.slot.reel.impl.StandardReelItem.SCT;
import static ivs.game.accessories.slot.reel.impl.StandardReelItem.WLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests assembly of standard reel bank mapping components.
 */
@ExtendWith(MockitoExtension.class)
class StandardReelBankMapperTest {

    @Mock
    private ReelMapper<StandardReelItem> reelMapper;

    @Mock
    private Reel<StandardReelItem> reel;

    /**
     * Verifies that names are mapped through the complete standard chain.
     */
    @Test
    void mapsReelItemNames() {
        ReelBank<StandardReelItem> reelBank = StandardReelBankMapper.forNames().map(
                List.of(
                        List.of("A", "K", "WLD", "SCT"),
                        List.of("K", "A", "SCT", "WLD")
                ),
                3
        );

        assertInstanceOf(StandardReelBank.class, reelBank);
        assertEquals(2, reelBank.size());
        assertEquals(List.of(A, K, WLD), reelBank.getItems(0, 0));
        assertEquals(List.of(K, A, SCT), reelBank.getItems(1, 0));
    }

    /**
     * Verifies that characters and individual visible sizes are supported.
     */
    @Test
    void mapsReelItemCharacters() {
        ReelBank<StandardReelItem> reelBank = StandardReelBankMapper.forCharacters().map(
                List.of(
                        new ReelDescription(List.of("A", "K", "?", "@"), 3),
                        new ReelDescription(List.of("@", "A", "K"), 2)
                )
        );

        assertInstanceOf(StandardReelBank.class, reelBank);
        assertEquals(2, reelBank.size());
        assertEquals(List.of(A, K, WLD), reelBank.getItems(0, 0));
        assertEquals(List.of(SCT, A), reelBank.getItems(1, 0));
    }

    /**
     * Verifies that a supplied item mapper is used by the complete standard chain.
     */
    @Test
    void usesSuppliedItemMapper() {
        ReelItemMapper<StandardReelItem> itemMapper = value -> switch (value) {
            case "first" -> A;
            case "second" -> K;
            default -> throw new IllegalArgumentException("Unknown item: " + value);
        };

        ReelBank<StandardReelItem> reelBank = StandardReelBankMapper.forItemMapper(itemMapper).map(
                List.of(new ReelDescription(List.of("first", "second", "first"), 2))
        );

        assertInstanceOf(StandardReelBank.class, reelBank);
        assertEquals(List.of(A, K), reelBank.getItems(0, 0));
    }

    /**
     * Verifies that a supplied reel mapper controls reel creation.
     */
    @Test
    void usesSuppliedReelMapper() {
        ReelDescription description = new ReelDescription(List.of("custom"), 1);
        when(reelMapper.map(description.values(), description.visibleSize())).thenReturn(reel);
        when(reel.size()).thenReturn(1);
        when(reel.getItems(0)).thenReturn(List.of(A));

        ReelBank<StandardReelItem> reelBank =
                StandardReelBankMapper.forReelMapper(reelMapper).map(List.of(description));

        assertInstanceOf(StandardReelBank.class, reelBank);
        assertEquals(1, reelBank.size());
        assertEquals(List.of(A), reelBank.getItems(0, 0));
        verify(reelMapper).map(description.values(), description.visibleSize());
    }

    /**
     * Verifies that a null item mapper is rejected by its factory.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullItemMapper() {
        assertThrows(
                NullPointerException.class,
                () -> StandardReelBankMapper.forItemMapper(null)
        );
    }

    /**
     * Verifies that a null reel mapper is rejected by its factory.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullReelMapper() {
        assertThrows(
                NullPointerException.class,
                () -> StandardReelBankMapper.forReelMapper(null)
        );
    }

    /**
     * Verifies that mapping rejects a null description list.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullDescriptions() {
        StandardReelBankMapper<StandardReelItem> mapper =
                StandardReelBankMapper.forNames();

        assertThrows(NullPointerException.class, () -> mapper.map(null));
    }
}
