package ivs.game.accessories.slot.reel.impl;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StandardReelTest {

    private static final List<StandardReelItem> ITEMS = List.of(
            StandardReelItem.A,
            StandardReelItem.B,
            StandardReelItem.C,
            StandardReelItem.D
    );

    @Test
    void constructorRejectsNullItems() {
        //noinspection DataFlowIssue
        assertThrows(NullPointerException.class, () -> new StandardReel<>(null, 1));
    }

    @Test
    void constructorRejectsEmptyItems() {
        assertThrows(IllegalArgumentException.class, () -> new StandardReel<>(List.of(), 1));
    }

    @Test
    void constructorRejectsNullReelItem() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new StandardReel<>(Arrays.asList(StandardReelItem.A, null), 1)
        );
    }

    @Test
    void constructorRejectsNonPositiveVisibleSize() {
        assertThrows(IllegalArgumentException.class, () -> new StandardReel<>(ITEMS, 0));
        assertThrows(IllegalArgumentException.class, () -> new StandardReel<>(ITEMS, -1));
    }

    @Test
    void getItemsReturnsItemsStartingAtPosition() {
        StandardReel<StandardReelItem> reel = new StandardReel<>(ITEMS, 2);

        assertEquals(
                List.of(StandardReelItem.B, StandardReelItem.C),
                reel.getItems(1)
        );
    }

    @Test
    void getItemsReturnsOneItemForSingleVisibleItemReel() {
        StandardReel<StandardReelItem> reel = new StandardReel<>(ITEMS, 1);

        assertEquals(List.of(StandardReelItem.A), reel.getItems(0));
        assertEquals(List.of(StandardReelItem.B), reel.getItems(1));
        assertEquals(List.of(StandardReelItem.C), reel.getItems(2));
        assertEquals(List.of(StandardReelItem.D), reel.getItems(3));
    }

    @Test
    void getItemsWrapsAroundTheReel() {
        StandardReel<StandardReelItem> reel = new StandardReel<>(ITEMS, 3);

        assertEquals(
                List.of(StandardReelItem.C, StandardReelItem.D, StandardReelItem.A),
                reel.getItems(2)
        );
    }

    @Test
    void getItemsSupportsMoreThanOneReelCycle() {
        StandardReel<StandardReelItem> reel = new StandardReel<>(ITEMS, 6);

        assertEquals(
                List.of(
                        StandardReelItem.D,
                        StandardReelItem.A,
                        StandardReelItem.B,
                        StandardReelItem.C,
                        StandardReelItem.D,
                        StandardReelItem.A
                ),
                reel.getItems(3)
        );
    }

    @Test
    void getItemsRejectsPositionBeforeReel() {
        StandardReel<StandardReelItem> reel = new StandardReel<>(ITEMS, 1);

        assertThrows(IllegalArgumentException.class, () -> reel.getItems(-1));
    }

    @Test
    void getItemsRejectsPositionAfterReel() {
        StandardReel<StandardReelItem> reel = new StandardReel<>(ITEMS, 1);

        assertThrows(IllegalArgumentException.class, () -> reel.getItems(ITEMS.size()));
    }

    @Test
    void getItemsReturnsUnmodifiableList() {
        StandardReel<StandardReelItem> reel = new StandardReel<>(ITEMS, 2);

        List<StandardReelItem> result = reel.getItems(0);

        assertThrows(UnsupportedOperationException.class, () -> result.add(StandardReelItem.C));
    }

    @Test
    void sizeReturnsNumberOfReelItems() {
        StandardReel<StandardReelItem> reel = new StandardReel<>(ITEMS, 2);

        assertEquals(ITEMS.size(), reel.size());
    }
}