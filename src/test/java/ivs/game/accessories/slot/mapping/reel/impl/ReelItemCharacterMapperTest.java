package ivs.game.accessories.slot.mapping.reel.impl;

import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests mapping single-character identifiers to standard reel items.
 */
class ReelItemCharacterMapperTest {

    private static final ReelItemCharacterMapper MAPPER = new ReelItemCharacterMapper();

    /**
     * Verifies that every {@link StandardReelItem} can be mapped by the character
     * stored in its identifier.
     *
     * @param expectedItem expected standard reel item
     */
    @ParameterizedTest
    @EnumSource(StandardReelItem.class)
    void mapsCharacterIdentifier(StandardReelItem expectedItem) {
        String value = Character.toString(expectedItem.getId());

        StandardReelItem result = MAPPER.map(value);

        assertEquals(expectedItem, result);
    }

    /**
     * Verifies that {@code null}, empty, and blank reel item characters are rejected.
     *
     * @param value null or blank reel item character
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\r", "\n"})
    void rejectsNullOrBlankCharacter(String value) {
        Class<? extends RuntimeException> expectedType =
                value == null
                        ? NullPointerException.class
                        : IllegalArgumentException.class;

        RuntimeException e = assertThrows(expectedType, () -> MAPPER.map(value));

        assertEquals("Reel item character must not be null or blank", e.getMessage());
    }

    /**
     * Verifies that values containing more than one character are rejected.
     *
     * @param value invalid multi-character value
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "AB",
            "WLD",
            "SCT",
            "A ",
            " A"
    })
    void rejectsMultiCharacterValue(String value) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> MAPPER.map(value));

        assertEquals(
                "Reel item character must contain exactly one character: '%s'".formatted(value),
                e.getMessage()
        );
    }

    /**
     * Verifies that an unknown single-character identifier is rejected.
     */
    @Test
    void rejectsUnknownCharacter() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> MAPPER.map("#"));

        assertEquals("Unknown standard reel item character: '#'", exception.getMessage());
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }

    /**
     * Verifies that a {@code null} character identifier is rejected.
     */
    @Test
    void rejectsNullCharacter() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> MAPPER.map(null));

        assertEquals("Reel item character must not be null or blank", e.getMessage());
    }
}