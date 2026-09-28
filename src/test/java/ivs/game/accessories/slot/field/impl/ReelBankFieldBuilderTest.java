package ivs.game.accessories.slot.field.impl;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.ReelItem;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReelBankFieldBuilderTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    void constructorRejectsNullReelBank() {
        assertThrows(
                NullPointerException.class,
                () -> new ReelBankFieldBuilder<StandardReelItem>(null)
        );
    }

    @Test
    void buildRejectsNullPositions() {
        ReelBank<StandardReelItem> reelBank = mock();
        ReelBankFieldBuilder<StandardReelItem> builder = new ReelBankFieldBuilder<>(reelBank);

        assertThrows(NullPointerException.class, () -> builder.build((int[]) null));
    }

    @Test
    void buildRejectsPositionsCountDifferentFromReelBankSize() {
        ReelBank<StandardReelItem> reelBank = mock();
        when(reelBank.size()).thenReturn(2);
        ReelBankFieldBuilder<StandardReelItem> builder = new ReelBankFieldBuilder<>(reelBank);

        assertThrows(IllegalArgumentException.class, () -> builder.build(0));
        verify(reelBank).size();
    }

    @Test
    void buildCreatesFieldUsingPositionsForCorrespondingReels() {
        ReelBank<StandardReelItem> reelBank = mock();
        when(reelBank.size()).thenReturn(3);
        when(reelBank.getItems(0, 2)).thenReturn(List.of(StandardReelItem.A, StandardReelItem.B));
        when(reelBank.getItems(1, 1)).thenReturn(List.of(StandardReelItem.C));
        when(reelBank.getItems(2, 0)).thenReturn(List.of(StandardReelItem.D, StandardReelItem.E, StandardReelItem.F));

        ReelBankFieldBuilder<StandardReelItem> builder = new ReelBankFieldBuilder<>(reelBank);
        SlotField<StandardReelItem> field = builder.build(2, 1, 0);

        assertEquals(3, field.getColumnCount());
        assertEquals(List.of(StandardReelItem.A, StandardReelItem.B), itemsOf(field, 0));
        assertEquals(List.of(StandardReelItem.C), itemsOf(field, 1));
        assertEquals(
                List.of(StandardReelItem.D, StandardReelItem.E, StandardReelItem.F),
                itemsOf(field, 2)
        );
        verify(reelBank).getItems(0, 2);
        verify(reelBank).getItems(1, 1);
        verify(reelBank).getItems(2, 0);
    }

    @Test
    void buildPropagatesInvalidPositionFromReelBank() {
        ReelBank<StandardReelItem> reelBank = mock();
        when(reelBank.size()).thenReturn(1);
        when(reelBank.getItems(0, -1)).thenThrow(IllegalArgumentException.class);

        ReelBankFieldBuilder<StandardReelItem> builder = new ReelBankFieldBuilder<>(reelBank);

        assertThrows(IllegalArgumentException.class, () -> builder.build(-1));
    }

    private static <I extends ReelItem> List<I> itemsOf(
            SlotField<I> field,
            int column
    ) {
        return IntStream.range(0, field.getColumnSize(column))
                .mapToObj(row -> field.getItem(column, row))
                .toList();
    }
}
