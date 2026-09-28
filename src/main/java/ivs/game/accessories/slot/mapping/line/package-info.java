/**
 * Provides mappings from external line descriptions to slot field lines.
 *
 * <p>A line is represented by an ordered list of row indices. The position of
 * each row index in the list determines its column:</p>
 *
 * <pre>
 * row indices: [0, 1, 2, 1, 0]
 *               |  |  |  |  |
 * columns:      0  1  2  3  4
 *
 * field positions:
 * (0, 0), (1, 1), (2, 2), (3, 1), (4, 0)
 * </pre>
 *
 * <p>{@link FieldLineMapper} maps one row description to a {@link FieldLine}.
 * {@link FieldLineListMapper} maps multiple descriptions and assigns line
 * identifiers according to their positions in the source list.</p>
 *
 * <pre>
 * List&lt;Integer&gt;
 *       |
 *       v
 * FieldLineMapper
 *       |
 *       v
 * FieldLine
 *
 * List&lt;List&lt;Integer&gt;&gt;
 *       |
 *       v
 * FieldLineListMapper
 *       |
 *       v
 * List&lt;FieldLine&gt;
 * </pre>
 *
 * <p>The mappers operate on already extracted values and do not depend on a
 * particular configuration format or serialization library.</p>
 */
package ivs.game.accessories.slot.mapping.line;

import ivs.game.accessories.slot.matcher.linear.FieldLine;
