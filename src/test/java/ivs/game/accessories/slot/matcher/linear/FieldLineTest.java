package ivs.game.accessories.slot.matcher.linear;

import ivs.game.accessories.slot.matcher.FieldPosition;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldLineTest {
    private static final int LINE_ID = 0;

    private static final FieldPosition POS_00 = new FieldPosition(0, 0);
    private static final FieldPosition POS_01 = new FieldPosition(0, 1);
    public static final FieldPosition POS_10 = new FieldPosition(1, 0);

    @Test
    void constructorRejectsNullPositions() {
        assertThrows(NullPointerException.class, () -> new FieldLine(LINE_ID, null));
    }

    @Test
    void constructorRejectsEmptyPositions() {
        assertThrows(IllegalArgumentException.class, () -> new FieldLine(LINE_ID, List.of()));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void constructorRejectsNullPositionElements() {
        assertThrows(
                NullPointerException.class,
                () -> new FieldLine(LINE_ID, List.of(POS_00, null))
        );
    }

    @Test
    void constructorRejectsPositionsWithDuplicateColumns() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FieldLine(LINE_ID, List.of(
                        POS_00,
                        POS_01)
                )
        );
    }

    @Test
    void constructorRejectsPositionsWithDecreasingColumns() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FieldLine(LINE_ID, List.of(
                        POS_10,
                        POS_01
                ))
        );
    }

    @Test
    void constructorDefensivelyCopiesPositions() {
        List<FieldPosition> positions = new ArrayList<>();
        positions.add(POS_00);
        FieldLine line = new FieldLine(LINE_ID, positions);

        positions.add(POS_10);

        assertEquals(List.of(POS_00), line.positions());
    }

    @Test
    void forEachVisitsPositionsFromLeftToRight() {
        FieldLine line = new FieldLine(LINE_ID, List.of(
                new FieldPosition(0, 1),
                new FieldPosition(2, 0),
                new FieldPosition(4, 2)
        ));

        List<FieldPosition> visited = new ArrayList<>();
        line.forEach(visited::add);
        assertEquals(line.positions(), visited);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void forEachRejectsNullAction() {
        FieldLine line = new FieldLine(LINE_ID, List.of(POS_00));

        assertThrows(NullPointerException.class, () -> line.forEach(null));
    }
}
