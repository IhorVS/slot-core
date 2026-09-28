package ivs.game.accessories.slot.reel.impl;

import ivs.game.accessories.slot.reel.Reel;
import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.ReelItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class StandardReelBankTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    void constructorRejectsNullReels() {
        assertThrows(NullPointerException.class, () -> new StandardReelBank<>(null));
    }

    @Test
    void constructorRejectsEmptyReels() {
        assertThrows(IllegalArgumentException.class, () -> new StandardReelBank<>(List.of()));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void constructorRejectsNullReel() {
        assertThrows(NullPointerException.class, () -> new StandardReelBank<>(List.of((Reel<?>) null)));
    }

    @Test
    void getItemsRejectsReelIdBeforeBank() {
        Reel<?> reel = mock();
        StandardReelBank<?> bank = new StandardReelBank<>(List.of(reel));

        assertThrows(IllegalArgumentException.class, () -> bank.getItems(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> bank.getItems(1, 0));
        verifyNoInteractions(reel);
    }

    @Test
    void getItemsRejectsPositionBeforeReel() {
        Reel<?> reel = mock();
        when(reel.size()).thenReturn(3);
        StandardReelBank<?> bank = new StandardReelBank<>(List.of(reel));

        assertThrows(IllegalArgumentException.class, () -> bank.getItems(0, -1));
        verify(reel, never()).getItems(-1);
    }

    @Test
    void getItemsRejectsPositionAfterReel() {
        Reel<?> reel = mock();
        when(reel.size()).thenReturn(3);
        StandardReelBank<?> bank = new StandardReelBank<>(List.of(reel));

        assertThrows(IllegalArgumentException.class, () -> bank.getItems(0, 3));
    }

    @Test
    void getItemsRejectsNullReturnedByReel() {
        Reel<?> reel = mock();
        when(reel.size()).thenReturn(1);
        when(reel.getItems(0)).thenReturn(null);
        StandardReelBank<?> bank = new StandardReelBank<>(List.of(reel));

        assertThrows(NullPointerException.class, () -> bank.getItems(0, 0));
    }

    @Test
    void getItemsRejectsEmptyListReturnedByReel() {
        Reel<?> reel = mock();
        when(reel.size()).thenReturn(1);
        when(reel.getItems(0)).thenReturn(List.of());
        StandardReelBank<?> bank = new StandardReelBank<>(List.of(reel));

        assertThrows(IllegalArgumentException.class, () -> bank.getItems(0, 0));
    }

    @Test
    void getItemsReturnsItemsFromSelectedReel() {
        Reel<ReelItem> reel = mock();
        List<ReelItem> expectedItems = List.of(StandardReelItem.A, StandardReelItem.B);
        when(reel.size()).thenReturn(3);
        when(reel.getItems(1)).thenReturn(expectedItems);
        ReelBank<ReelItem> bank = new StandardReelBank<>(List.of(reel));

        assertEquals(expectedItems, bank.getItems(0, 1));
        verify(reel).getItems(1);
    }

    @Test
    void sizeReturnsNumberOfReels() {
        StandardReelBank<?> bank = new StandardReelBank<>(List.of(mock(), mock()));

        assertEquals(2, bank.size());
    }

    @Test
    void getReelSizeReturnsSizeOfSelectedReel() {
        Reel<ReelItem> first = mock();
        Reel<ReelItem> second = mock();
        when(first.size()).thenReturn(4);
        when(second.size()).thenReturn(7);

        ReelBank<ReelItem> bank = new StandardReelBank<>(List.of(first, second));

        assertEquals(4, bank.getReelSize(0));
        assertEquals(7, bank.getReelSize(1));

        verify(first).size();
        verify(second).size();
    }

    @Test
    void getReelSizeRejectsInvalidReelId() {
        Reel<?> reel = mock();
        ReelBank<?> bank = new StandardReelBank<>(List.of(reel));

        assertThrows(IllegalArgumentException.class, () -> bank.getReelSize(-1));
        assertThrows(IllegalArgumentException.class, () -> bank.getReelSize(1));
        verifyNoInteractions(reel);
    }
}
