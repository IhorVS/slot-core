package ivs.game.accessories.slot.mapping.line.impl;

import ivs.game.accessories.slot.mapping.line.FieldLineMapper;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.linear.FieldLine;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Maps ordered row indices to field positions and creates a {@link FieldLine}.
 */
public final class DefaultFieldLineMapper implements FieldLineMapper {

    /**
     * Maps every row index to a position whose column is determined by the
     * index of that value in the source list.
     *
     * @param lineId the line identifier
     * @param rows   row indices ordered by field column
     * @return mapped field line
     * @throws NullPointerException     if {@code rows} is {@code null}
     * @throws IllegalArgumentException if {@code rows} is empty, contains a
     *                                  {@code null}, or contains a negative index
     */
    @Override
    public FieldLine map(int lineId, @NonNull List<Integer> rows) {
        Validate.noNullElements(rows, "Row indices must not contain null elements");

        List<FieldPosition> positions = IntStream.range(0, rows.size())
                .mapToObj(column -> createPosition(lineId, column, rows.get(column)))
                .toList();

        return new FieldLine(lineId, positions);
    }

    /*
     * Validates one row index and creates its field position.
     */
    private FieldPosition createPosition(int lineId, int column, int row) {
        Validate.isTrue(row >= 0,
                "Line %d, column %d: row index must be non-negative", lineId, column);

        return new FieldPosition(column, row);
    }
}
