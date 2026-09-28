package ivs.game.accessories.slot.field.impl;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class SlotFieldContractTest {

    protected abstract SlotField<StandardReelItem> createField();

    @Test
    void forEachVisitsItemsByColumnAndRowOrder() {
        List<String> visited = new ArrayList<>();

        createField().forEach((item, column, row) -> {
            visited.add("%s:%d:%d".formatted(item, column, row));
            return true;
        });

        assertEquals(
                List.of(
                        "A:0:0",
                        "B:0:1",
                        "C:0:2",
                        "D:1:0",
                        "E:1:1",
                        "F:2:0"
                ),
                visited
        );
    }

    @Test
    void forEachStopsWhenActionReturnsFalse() {
        List<StandardReelItem> visited = new ArrayList<>();

        createField().forEach((item, column, row) -> {
            visited.add(item);
            return item != StandardReelItem.D;
        });

        assertEquals(
                List.of(
                        StandardReelItem.A,
                        StandardReelItem.B,
                        StandardReelItem.C,
                        StandardReelItem.D
                ),
                visited
        );
    }

    @Test
    void forEachRejectsNullAction() {
        assertThrows(NullPointerException.class, () -> createField().forEach(null));
    }
}
