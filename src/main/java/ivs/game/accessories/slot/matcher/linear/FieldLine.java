package ivs.game.accessories.slot.matcher.linear;

import ivs.game.accessories.slot.matcher.FieldPosition;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.function.Consumer;

/**
 * An ordered line on a slot field.
 *
 * <p>Positions are ordered from left to right and there can be no more than
 * one position from the same column.</p>
 *
 * @param id        the line identifier
 * @param positions the line positions
 */
public record FieldLine(int id, List<FieldPosition> positions) {

    public FieldLine {
        Validate.notNull(positions, "Positions must not be null");
        Validate.isTrue(!positions.isEmpty(), "Line must contain at least one position");
        Validate.noNullElements(positions, "Positions must not contain null elements");

        for (int index = 1; index < positions.size(); index++) {
            Validate.isTrue(
                    positions.get(index - 1).column() < positions.get(index).column(),
                    "Line positions must be ordered by unique columns"
            );
        }

        positions = List.copyOf(positions);
    }

    /**
     * Performs the specified action for each position from left to right.
     *
     * @param action the action to perform for each position
     * @throws NullPointerException if {@code action} is {@code null}
     */
    public void forEach(@NonNull Consumer<? super FieldPosition> action) {
        positions.forEach(action);
    }
}
