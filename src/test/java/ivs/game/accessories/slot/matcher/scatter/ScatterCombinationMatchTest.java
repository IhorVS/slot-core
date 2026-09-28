package ivs.game.accessories.slot.matcher.scatter;

import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScatterCombinationMatchTest {
    private static final int ID = 0;
    private static final String GROUP_ID = "test";

    @Test
    void constructorRejectsNullCombination() {
        assertThrows(
                NullPointerException.class,
                () -> new ScatterCombinationMatch<>(
                        null,
                        List.of(new FieldPosition(0, 0))
                )
        );
    }

    @Test
    void constructorRejectsNullPositions() {
        assertThrows(
                NullPointerException.class,
                () -> new ScatterCombinationMatch<>(
                        new Combination<>(ID, GROUP_ID, StandardReelItem.A),
                        null
                )
        );
    }

    @Test
    void constructorRejectsEmptyPositions() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ScatterCombinationMatch<>(
                        new Combination<>(ID, GROUP_ID, StandardReelItem.A),
                        List.of()
                )
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void constructorRejectsNullPositionElements() {
        assertThrows(
                NullPointerException.class,
                () -> new ScatterCombinationMatch<>(
                        new Combination<>(ID, GROUP_ID, StandardReelItem.A),
                        List.of((FieldPosition) null)
                )
        );
    }

    @Test
    void constructorCopiesPositions() {
        List<FieldPosition> positions = new ArrayList<>();
        positions.add(new FieldPosition(0, 0));

        ScatterCombinationMatch<StandardReelItem> match =
                new ScatterCombinationMatch<>(
                        new Combination<>(ID, GROUP_ID, StandardReelItem.A),
                        positions
                );

        positions.add(new FieldPosition(1, 0));

        assertEquals(List.of(new FieldPosition(0, 0)), match.positions());
        assertEquals(1, match.length());
    }
}
