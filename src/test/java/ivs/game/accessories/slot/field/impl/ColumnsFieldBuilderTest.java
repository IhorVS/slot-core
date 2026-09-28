package ivs.game.accessories.slot.field.impl;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.reel.ReelItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ColumnsFieldBuilderTest {

    @Mock(name = "A")
    private ReelItem itemA;
    @Mock(name = "B")
    private ReelItem itemB;
    @Mock(name = "C")
    private ReelItem itemC;
    @Mock(name = "D")
    private ReelItem itemD;
    @Mock(name = "E")
    private ReelItem itemE;
    @Mock(name = "F")
    private ReelItem itemF;

    @Test
    void addColumnRejectsNullVarargs() {
        ColumnsFieldBuilder<ReelItem> builder = new ColumnsFieldBuilder<>();

        assertThrows(
                NullPointerException.class,
                () -> builder.addColumn((ReelItem[]) null)
        );
    }

    @Test
    void addColumnRejectsEmptyVarargs() {
        ColumnsFieldBuilder<ReelItem> builder = new ColumnsFieldBuilder<>();

        assertThrows(IllegalArgumentException.class, builder::addColumn);
    }

    @Test
    void addColumnRejectsNullVarargItem() {
        ColumnsFieldBuilder<ReelItem> builder = new ColumnsFieldBuilder<>();

        assertThrows(
                IllegalArgumentException.class,
                () -> builder.addColumn(itemA, null)
        );
    }

    @Test
    void addColumnRejectsNullList() {
        ColumnsFieldBuilder<ReelItem> builder = new ColumnsFieldBuilder<>();

        assertThrows(
                NullPointerException.class,
                () -> builder.addColumn((List<ReelItem>) null)
        );
    }

    @Test
    void addColumnRejectsEmptyList() {
        ColumnsFieldBuilder<ReelItem> builder = new ColumnsFieldBuilder<>();

        assertThrows(
                IllegalArgumentException.class,
                () -> builder.addColumn(List.of())
        );
    }

    @Test
    void addColumnRejectsNullListItem() {
        ColumnsFieldBuilder<ReelItem> builder = new ColumnsFieldBuilder<>();
        List<ReelItem> column = new ArrayList<>();
        column.add(itemA);
        column.add(null);

        assertThrows(IllegalArgumentException.class, () -> builder.addColumn(column));
    }

    @Test
    void addColumnReturnsSameBuilder() {
        ColumnsFieldBuilder<ReelItem> builder = new ColumnsFieldBuilder<>();

        assertSame(builder, builder.addColumn(itemA));
    }

    @Test
    void buildRejectsEmptyField() {
        ColumnsFieldBuilder<ReelItem> builder = new ColumnsFieldBuilder<>();

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void buildCreatesFieldFromVarargsColumns() {
        SlotField<ReelItem> field = new ColumnsFieldBuilder<>()
                .addColumn(itemA, itemB, itemC)
                .addColumn(itemD, itemE)
                .addColumn(itemF)
                .build();

        assertEquals(
                List.of(
                        List.of(itemA, itemB, itemC),
                        List.of(itemD, itemE),
                        List.of(itemF)
                ),
                columnsOf(field)
        );
    }

    @Test
    void buildCreatesFieldFromListColumn() {
        SlotField<ReelItem> field = new ColumnsFieldBuilder<>()
                .addColumn(List.of(itemA, itemB))
                .build();

        assertEquals(
                List.of(List.of(itemA, itemB)),
                columnsOf(field)
        );
    }

    @Test
    void addColumnCopiesListContents() {
        List<ReelItem> source = new ArrayList<>(List.of(itemA));
        ColumnsFieldBuilder<ReelItem> builder = new ColumnsFieldBuilder<>();
        builder.addColumn(source);

        source.set(0, itemB);
        source.add(itemC);

        SlotField<ReelItem> field = builder.build();

        assertEquals(List.of(List.of(itemA)), columnsOf(field));
    }

    @Test
    void addColumnCopiesVarargsContents() {
        ReelItem[] source = {itemA};
        ColumnsFieldBuilder<ReelItem> builder = new ColumnsFieldBuilder<>();
        builder.addColumn(source);

        source[0] = itemB;

        SlotField<ReelItem> field = builder.build();

        assertEquals(List.of(List.of(itemA)), columnsOf(field));
    }

    private static <I extends ReelItem> List<List<I>> columnsOf(SlotField<I> field) {
        return IntStream.range(0, field.getColumnCount())
                .mapToObj(column -> IntStream.range(0, field.getColumnSize(column))
                        .mapToObj(row -> field.getItem(column, row))
                        .toList())
                .toList();
    }
}
