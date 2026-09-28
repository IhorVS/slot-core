package ivs.game.accessories.slot.mapping.combination.impl;

import ivs.game.accessories.slot.mapping.combination.CombinationDescription;
import ivs.game.accessories.slot.mapping.combination.CombinationListMapper;
import ivs.game.accessories.slot.mapping.combination.CombinationMapper;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps combination descriptions by delegating each one to a configured
 * {@link CombinationMapper}.
 *
 * @param <I> the type of items contained in the combinations
 */
@RequiredArgsConstructor
public final class DefaultCombinationListMapper<I extends ReelItem> implements CombinationListMapper<I> {

    @NonNull
    private final CombinationMapper<I> combinationMapper;

    /**
     * Maps descriptions in their configured order and indexes combinations
     * by their identifiers.
     *
     * @param descriptions combination descriptions in their configured order
     * @return unmodifiable map of combination IDs to mapped combinations
     * @throws NullPointerException     if {@code descriptions} is {@code null}
     * @throws IllegalArgumentException if a description is {@code null} or an ID is duplicated
     * @throws RuntimeException         if a combination cannot be mapped
     */
    @Override
    public Map<Integer, Combination<I>> map(@NonNull List<CombinationDescription> descriptions) {
        Validate.noNullElements(descriptions, "List must not contain null combination descriptions");

        Map<Integer, Combination<I>> combinations = new LinkedHashMap<>();

        for (CombinationDescription description : descriptions) {
            Combination<I> combination = Validate.notNull(
                    combinationMapper.map(description),
                    "Combination mapper must not return null"
            );

            int id = combination.getId();
            Validate.isTrue(!combinations.containsKey(id), "Duplicate combination ID: %d", id);

            combinations.put(id, combination);
        }

        return Collections.unmodifiableMap(combinations);
    }
}
