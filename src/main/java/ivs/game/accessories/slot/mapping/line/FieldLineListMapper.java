package ivs.game.accessories.slot.mapping.line;

import ivs.game.accessories.slot.matcher.linear.FieldLine;

import java.util.List;

/**
 * Maps ordered row descriptions to field lines.
 *
 * <p>The index of each description in the source list becomes the identifier
 * of the resulting field line.</p>
 *
 * <pre>
 * source index   row indices       field line ID
 *      0         [0, 0, 0]    ->         0
 *      1         [1, 1, 1]    ->         1
 *      2         [0, 1, 0]    ->         2
 * </pre>
 */
public interface FieldLineListMapper {

    /**
     * Maps row descriptions to field lines while preserving their order.
     *
     * @param lines line descriptions containing row indices ordered by field column
     * @return unmodifiable list of mapped field lines
     * @throws NullPointerException     if {@code lines} is {@code null}
     * @throws IllegalArgumentException if {@code lines} contains a {@code null} description
     * @throws RuntimeException         if a line description cannot be mapped
     */
    List<FieldLine> map(List<List<Integer>> lines);
}
