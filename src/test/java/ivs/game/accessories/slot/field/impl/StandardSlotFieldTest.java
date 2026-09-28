package ivs.game.accessories.slot.field.impl;

import ivs.game.accessories.slot.reel.ReelItem;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StandardSlotFieldTest extends SlotFieldContractTest {

    private static final List<List<StandardReelItem>> COLUMNS = List.of(
            List.of(StandardReelItem.A, StandardReelItem.B, StandardReelItem.C),
            List.of(StandardReelItem.D, StandardReelItem.E),
            List.of(StandardReelItem.F)
    );
    private static final StandardSlotField<StandardReelItem> FIELD =
            new StandardSlotField<>(COLUMNS);

    @Override
    protected StandardSlotField<StandardReelItem> createField() {
        return FIELD;
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void constructorRejectsNullColumns() {
        assertThrows(NullPointerException.class, () -> new StandardSlotField<>(null));
    }

    @Test
    void constructorRejectsEmptyColumns() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new StandardSlotField<>(List.<List<StandardReelItem>>of())
        );
    }

    @Test
    void constructorRejectsNullColumn() {
        List<List<StandardReelItem>> columns = Arrays.asList(List.of(StandardReelItem.A), null);

        assertThrows(
                NullPointerException.class,
                () -> new StandardSlotField<>(columns)
        );
    }

    @Test
    void constructorRejectsEmptyColumn() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new StandardSlotField<>(List.of(List.<StandardReelItem>of()))
        );
    }

    @Test
    void constructorRejectsNullReelItem() {
        List<StandardReelItem> column = Arrays.asList(StandardReelItem.A, null);

        assertThrows(
                IllegalArgumentException.class,
                () -> new StandardSlotField<>(List.of(column))
        );
    }

    @Test
    void getColumnCountReturnsNumberOfColumns() {
        assertEquals(COLUMNS.size(), FIELD.getColumnCount());
    }

    @Test
    void getColumnSizeReturnsNumberOfItemsInSelectedColumn() {
        assertEquals(3, FIELD.getColumnSize(0));
        assertEquals(2, FIELD.getColumnSize(1));
        assertEquals(1, FIELD.getColumnSize(2));
    }

    @Test
    void getItemRejectsColumnBeforeField() {
        assertThrows(IllegalArgumentException.class, () -> FIELD.getItem(-1, 0));
    }

    @Test
    void getItemRejectsColumnAfterField() {
        assertThrows(IllegalArgumentException.class, () -> FIELD.getItem(FIELD.getColumnCount(), 0));
    }

    @Test
    void getItemRejectsRowBeforeColumn() {
        assertThrows(IllegalArgumentException.class, () -> FIELD.getItem(1, -1));
    }

    @Test
    void getItemRejectsRowAfterColumn() {
        assertThrows(IllegalArgumentException.class, () -> FIELD.getItem(1, FIELD.getColumnSize(1)));
    }

    @ParameterizedTest
    @MethodSource("getCoordinates")
    void getItemSuccessful(int column, int row, ReelItem expectedItem) {
        assertEquals(expectedItem, FIELD.getItem(column, row));
    }

    private static Stream<Arguments> getCoordinates() {
        return Stream.of(
                Arguments.of(0, 0, StandardReelItem.A),
                Arguments.of(0, 1, StandardReelItem.B),
                Arguments.of(0, 2, StandardReelItem.C),
                Arguments.of(1, 0, StandardReelItem.D),
                Arguments.of(1, 1, StandardReelItem.E),
                Arguments.of(2, 0, StandardReelItem.F)
        );
    }
}