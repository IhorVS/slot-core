package ivs.game.accessories.slot.mapping.reel.impl;

import ivs.game.accessories.slot.mapping.reel.ReelItemListMapper;
import ivs.game.accessories.slot.mapping.reel.ReelItemMapper;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import java.util.List;

/**
 * Maps a list of string identifiers by delegating each value to a configured
 * {@link ReelItemMapper}.
 *
 * @param <T> reel item type
 */
@RequiredArgsConstructor
public final class DefaultReelItemListMapper<T extends ReelItem> implements ReelItemListMapper<T> {

    @NonNull
    private final ReelItemMapper<T> itemMapper;

    /**
     * Maps every source value while preserving its position in the list.
     *
     * @param values external reel item identifiers
     * @return unmodifiable list of mapped reel items
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws RuntimeException     if a value cannot be mapped
     */
    @Override
    public List<T> map(@NonNull List<String> values) {
        Validate.noNullElements(values, "Reel Items must not contain null elements");

        return values.stream()
                .map(itemMapper::map)
                .toList();
    }
}
