package ivs.game.accessories.slot.reel.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StandardReelItemTest {

    @ParameterizedTest
    @MethodSource("reelItems")
    void getId(StandardReelItem item, char expectedId) {
        assertEquals(expectedId, item.getId());
    }

    @ParameterizedTest
    @MethodSource("reelItems")
    void valueOfIdSuccess(StandardReelItem expectedItem, char id) {
        assertEquals(expectedItem, StandardReelItem.valueOf((int) id));
    }

    @Test
    void valueOfIsFailure() {
        var ex = assertThrows(IllegalArgumentException.class, () -> StandardReelItem.valueOf('='));
        assertEquals("Unknown Reel Item: =", ex.getMessage());
    }

    @ParameterizedTest
    @MethodSource("reelItems")
    void valueOfIdCharacterOk(StandardReelItem expectedItem, char characterId) {
        assertEquals(expectedItem, StandardReelItem.valueOf(characterId));
    }

    @Test
    void valueOfIdCharacterFailure() {
        var ex = assertThrows(IllegalArgumentException.class, () -> StandardReelItem.valueOf('='));
        assertEquals("Unknown Reel Item: =", ex.getMessage());
    }

    private static Stream<Arguments> reelItems() {
        return Stream.of(
                Arguments.of(StandardReelItem.A, 'A'),
                Arguments.of(StandardReelItem.B, 'B'),
                Arguments.of(StandardReelItem.C, 'C'),
                Arguments.of(StandardReelItem.D, 'D'),
                Arguments.of(StandardReelItem.E, 'E'),
                Arguments.of(StandardReelItem.F, 'F'),
                Arguments.of(StandardReelItem.G, 'G'),
                Arguments.of(StandardReelItem.H, 'H'),
                Arguments.of(StandardReelItem.I, 'I'),
                Arguments.of(StandardReelItem.J, 'J'),
                Arguments.of(StandardReelItem.K, 'K'),
                Arguments.of(StandardReelItem.L, 'L'),
                Arguments.of(StandardReelItem.M, 'M'),
                Arguments.of(StandardReelItem.N, 'N'),
                Arguments.of(StandardReelItem.O, 'O'),
                Arguments.of(StandardReelItem.P, 'P'),
                Arguments.of(StandardReelItem.Q, 'Q'),
                Arguments.of(StandardReelItem.R, 'R'),
                Arguments.of(StandardReelItem.S, 'S'),
                Arguments.of(StandardReelItem.T, 'T'),
                Arguments.of(StandardReelItem.U, 'U'),
                Arguments.of(StandardReelItem.V, 'V'),
                Arguments.of(StandardReelItem.W, 'W'),
                Arguments.of(StandardReelItem.X, 'X'),
                Arguments.of(StandardReelItem.Y, 'Y'),
                Arguments.of(StandardReelItem.Z, 'Z'),
                Arguments.of(StandardReelItem.WLD, '?'),
                Arguments.of(StandardReelItem.SCT, '@'),
                Arguments.of(StandardReelItem.BON, '&'),
                Arguments.of(StandardReelItem.JPT, '!'),
                Arguments.of(StandardReelItem.MUL, '*')
        );
    }
}