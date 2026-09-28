package ivs.game.accessories.slot.mapping.line;

import ivs.game.accessories.slot.matcher.linear.FieldLine;

import java.util.List;

/**
 * Maps ordered row indices to a field line.
 *
 * <p>The position of each row index in the source list becomes its zero-based
 * column index.</p>
 *
 * <pre>
 * line ID: 5
 * rows:    [0, 1, 2, 1, 0]
 *
 * positions:
 * (0, 0), (1, 1), (2, 2), (3, 1), (4, 0)
 * </pre>
 */
public interface FieldLineMapper {

    /**
     * Maps ordered row indices to a field line.
     *
     * @param lineId the line identifier
     * @param rows   row indices ordered by field column
     * @return mapped field line
     * @throws NullPointerException     if {@code rows} is {@code null}
     * @throws IllegalArgumentException if {@code rows} is empty, contains a
     *                                  {@code null}, or contains a negative index
     */
    FieldLine map(int lineId, List<Integer> rows);
}
