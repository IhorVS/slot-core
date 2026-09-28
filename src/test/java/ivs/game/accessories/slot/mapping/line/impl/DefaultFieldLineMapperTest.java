package ivs.game.accessories.slot.mapping.line.impl;

import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.linear.FieldLine;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests mapping ordered row indices to field lines.
 */
class DefaultFieldLineMapperTest {

    private final DefaultFieldLineMapper mapper = new DefaultFieldLineMapper();

    /**
     * Verifies that the mapper preserves the line identifier and converts each
     * row index to a position using its list index as the column.
     */
    @Test
    void mapsRowIndicesToFieldLine() {
        FieldLine result = mapper.map(5, List.of(0, 1, 2, 1, 0));

        assertEquals(5, result.id());
        assertEquals(
                List.of(
                        new FieldPosition(0, 0),
                        new FieldPosition(1, 1),
                        new FieldPosition(2, 2),
                        new FieldPosition(3, 1),
                        new FieldPosition(4, 0)
                ),
                result.positions()
        );
    }

    /**
     * Verifies that repeated row indices are allowed because different values
     * may point to the same row in different columns.
     */
    @Test
    void mapsRepeatedRowIndices() {
        FieldLine result = mapper.map(0, List.of(1, 1, 1));

        assertEquals(
                List.of(
                        new FieldPosition(0, 1),
                        new FieldPosition(1, 1),
                        new FieldPosition(2, 1)
                ),
                result.positions()
        );
    }

    /**
     * Verifies that a {@code null} row index list is rejected before mapping.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullRowIndexList() {
        assertThrows(NullPointerException.class, () -> mapper.map(0, null));
    }

    /**
     * Verifies that an empty row index list is rejected because a field line
     * must contain at least one position.
     */
    @Test
    void rejectsEmptyRowIndexList() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> mapper.map(0, List.of()));

        assertEquals("Line must contain at least one position", e.getMessage());
    }

    /**
     * Verifies that a {@code null} row index is rejected before positions are created.
     */
    @Test
    void rejectsNullRowIndex() {
        List<Integer> rows = Arrays.asList(0, null, 2);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> mapper.map(0, rows));

        assertEquals("Row indices must not contain null elements", e.getMessage());
    }

    /**
     * Verifies that a negative row index is rejected and that the error identifies
     * the affected line and column.
     */
    @Test
    void rejectsNegativeRowIndex() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(7, List.of(0, 1, -1, 2))
        );

        assertEquals("Line 7, column 2: row index must be non-negative", e.getMessage());
    }
}
