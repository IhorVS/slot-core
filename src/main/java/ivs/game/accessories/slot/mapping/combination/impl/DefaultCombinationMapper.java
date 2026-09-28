package ivs.game.accessories.slot.mapping.combination.impl;

import ivs.game.accessories.slot.mapping.combination.CombinationDescription;
import ivs.game.accessories.slot.mapping.combination.CombinationMapper;
import ivs.game.accessories.slot.mapping.reel.ReelItemListMapper;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Maps a combination description using a configured reel item list mapper.
 *
 * @param <I> the type of items contained in the combination
 */
@RequiredArgsConstructor
public final class DefaultCombinationMapper<I extends ReelItem> implements CombinationMapper<I> {

    @NonNull
    private final ReelItemListMapper<I> itemListMapper;

    /**
     * Maps item identifiers and creates a combination with the configured
     * identifier and group identifier.
     *
     * @param description external combination description
     * @return mapped combination
     * @throws NullPointerException if {@code description} is {@code null}
     * @throws RuntimeException     if an item cannot be mapped or the
     *                              combination cannot be created
     */
    @Override
    public Combination<I> map(@NonNull CombinationDescription description) {
        try {
            String groupId = Validate.notNull(description.groupId(), "Group ID is null");

            List<String> values = Validate.notNull(
                    description.values(), "The list of combination elements is null");
            Validate.isTrue(!values.isEmpty(), "The list of combination elements is empty");

            IntStream.range(0, values.size())
                    .filter(index -> values.get(index) == null || values.get(index).isBlank())
                    .findFirst()
                    .ifPresent(index -> {
                        throw new IllegalArgumentException(
                                "Combination element at index %d is null or blank".formatted(index)
                        );
                    });

            return new Combination<>(description.id(), groupId, itemListMapper.map(values));

        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Cannot map combination ID=%d".formatted(description.id()), e);
        }
    }
}
