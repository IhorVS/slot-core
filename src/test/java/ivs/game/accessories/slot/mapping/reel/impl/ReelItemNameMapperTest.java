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
 * Tests mapping enum constant names to standard reel items.
 */
class ReelItemNameMapperTest {

    private static final ReelItemNameMapper MAPPER = new ReelItemNameMapper();

    /**
     * Verifies that every {@link StandardReelItem} can be mapped by its exact
     * enum constant name.
     *
     * @param expectedItem expected standard reel item
     */
    @ParameterizedTest
    @EnumSource(StandardReelItem.class)
    void mapsEnumConstantName(StandardReelItem expectedItem) {
        StandardReelItem result = MAPPER.map(expectedItem.name());

        assertEquals(expectedItem, result);
    }

    /**
     * Verifies that mapping is case-sensitive.
     */
    @Test
    void rejectsLowercaseName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> MAPPER.map("wld"));

        assertEquals("Unknown standard reel item name: 'wld'", e.getMessage());
        assertInstanceOf(IllegalArgumentException.class, e.getCause());
    }

    /**
     * Verifies that {@code null}, empty, and blank reel item names are rejected.
     *
     * @param value null or blank reel item name
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\r", "\n"})
    void rejectsNullOrBlankName(String value) {
        Class<? extends RuntimeException> expectedType =
                value == null
                        ? NullPointerException.class
                        : IllegalArgumentException.class;

        RuntimeException e = assertThrows(expectedType, () -> MAPPER.map(value));

        assertEquals("Reel item name must not be null or blank", e.getMessage());
    }

    /**
     * Verifies that unknown names, character identifiers, and names containing
     * leading or trailing whitespace are rejected.
     *
     * @param value invalid reel item name
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "UNKNOWN",
            "?",
            "@",
            "A ",
            " A",
            " WLD "
    })
    void rejectsUnknownName(String value) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> MAPPER.map(value));

        assertEquals("Unknown standard reel item name: '%s'".formatted(value), e.getMessage());
        assertInstanceOf(IllegalArgumentException.class, e.getCause());
    }
}
