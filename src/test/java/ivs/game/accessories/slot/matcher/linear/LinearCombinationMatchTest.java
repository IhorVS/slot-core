package ivs.game.accessories.slot.matcher.linear;

import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LinearCombinationMatchTest {
    private static final int ID = 0;
    private static final String GROUP_ID = "test";

    @Test
    void constructorRejectsNullCombination() {
        assertThrows(
                NullPointerException.class,
                () -> new LinearCombinationMatch<>(
                        null,
                        List.of(new FieldPosition(0, 0)),
                        line()
                )
        );
    }

    @Test
    void constructorRejectsNullPositions() {
        assertThrows(
                NullPointerException.class,
                () -> new LinearCombinationMatch<>(
                        new Combination<>(ID, GROUP_ID, StandardReelItem.A),
                        null,
                        line()
                )
        );
    }

    @Test
    void constructorRejectsEmptyPositions() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LinearCombinationMatch<>(
                        new Combination<>(ID, GROUP_ID, StandardReelItem.A),
                        List.of(),
                        line()
                )
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void constructorRejectsNullPositionElements() {
        assertThrows(
                NullPointerException.class,
                () -> new LinearCombinationMatch<>(
                        new Combination<>(ID, GROUP_ID, StandardReelItem.A),
                        List.of((FieldPosition) null),
                        line()
                )
        );
    }

    @Test
    void constructorRejectsNullLine() {
        assertThrows(
                NullPointerException.class,
                () -> new LinearCombinationMatch<>(
                        new Combination<>(ID, GROUP_ID, StandardReelItem.A),
                        List.of(new FieldPosition(0, 0)),
                        null
                )
        );
    }

    @Test
    void constructorDefensivelyCopiesPositions() {
        List<FieldPosition> positions = new ArrayList<>();
        positions.add(new FieldPosition(0, 0));

        LinearCombinationMatch<StandardReelItem> match =
                new LinearCombinationMatch<>(
                        new Combination<>(ID, GROUP_ID, StandardReelItem.A),
                        positions,
                        line()
                );

        positions.add(new FieldPosition(1, 0));

        assertEquals(List.of(new FieldPosition(0, 0)), match.positions());
        assertEquals(1, match.length());
    }

    private static FieldLine line() {
        return new FieldLine(0, List.of(new FieldPosition(0, 0)));
    }
}
