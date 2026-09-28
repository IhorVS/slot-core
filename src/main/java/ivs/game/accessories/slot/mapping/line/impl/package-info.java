/**
 * Provides default implementations of line mapping components.
 *
 * <p>{@link DefaultFieldLineMapper} converts ordered row indices to field
 * positions and creates a field line. Each source value becomes a row index,
 * while its position in the source list becomes the column index.</p>
 *
 * <p>{@link DefaultFieldLineListMapper} maps multiple line descriptions,
 * preserves their order, and uses each description index as its line
 * identifier.</p>
 *
 * <pre>
 * List&lt;List&lt;Integer&gt;&gt;
 *          |
 *          v
 * DefaultFieldLineListMapper
 *          |
 *          v
 * DefaultFieldLineMapper
 *          |
 *          v
 * List&lt;FieldLine&gt;
 * </pre>
 */
package ivs.game.accessories.slot.mapping.line.impl;
