package ivs.game.accessories.slot.mapping.line.impl;

import ivs.game.accessories.slot.mapping.line.FieldLineListMapper;
import ivs.game.accessories.slot.mapping.line.FieldLineMapper;
import ivs.game.accessories.slot.matcher.linear.FieldLine;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Maps ordered row descriptions by delegating each description to a configured
 * {@link FieldLineMapper}.
 */
@RequiredArgsConstructor
public final class DefaultFieldLineListMapper implements FieldLineListMapper {

    @NonNull
    private final FieldLineMapper lineMapper;

    /**
     * Maps every line description and uses its source index as the line identifier.
     *
     * @param lines line descriptions containing row indices ordered by field column
     * @return unmodifiable list of mapped field lines
     * @throws NullPointerException     if {@code lines} is {@code null}
     * @throws IllegalArgumentException if {@code lines} contains a {@code null} description
     * @throws RuntimeException         if a line description cannot be mapped
     */
    @Override
    public List<FieldLine> map(@NonNull List<List<Integer>> lines) {
        Validate.noNullElements(lines, "List must not contain null line descriptions");

        return IntStream.range(0, lines.size())
                .mapToObj(lineId -> lineMapper.map(lineId, lines.get(lineId)))
                .toList();
    }
}
