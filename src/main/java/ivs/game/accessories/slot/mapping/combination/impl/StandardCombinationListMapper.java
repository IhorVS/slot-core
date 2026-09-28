package ivs.game.accessories.slot.mapping.combination.impl;

import ivs.game.accessories.slot.mapping.combination.CombinationDescription;
import ivs.game.accessories.slot.mapping.combination.CombinationListMapper;
import ivs.game.accessories.slot.mapping.combination.CombinationMapper;
import ivs.game.accessories.slot.mapping.reel.ReelItemMapper;
import ivs.game.accessories.slot.mapping.reel.impl.DefaultReelItemListMapper;
import ivs.game.accessories.slot.mapping.reel.impl.ReelItemCharacterMapper;
import ivs.game.accessories.slot.mapping.reel.impl.ReelItemNameMapper;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.reel.ReelItem;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Map;

/**
 * Assembles standard components for mapping combination descriptions.
 *
 * @param <I> the type of items contained in the combinations
 */
public final class StandardCombinationListMapper<I extends ReelItem> implements CombinationListMapper<I> {

    private final CombinationListMapper<I> listMapper;

    private StandardCombinationListMapper(CombinationMapper<I> combinationMapper) {
        Validate.notNull(combinationMapper, "Combination mapper must not be null");
        listMapper = new DefaultCombinationListMapper<>(combinationMapper);
    }

    /**
     * Creates a mapper for standard reel item names.
     *
     * <pre>
     * "A"   -> StandardReelItem.A
     * "K"   -> StandardReelItem.K
     * "WLD" -> StandardReelItem.WLD
     * "SCT" -> StandardReelItem.SCT
     * "BON" -> StandardReelItem.BON
     * "JPT" -> StandardReelItem.JPT
     * "MUL" -> StandardReelItem.MUL
     * </pre>
     *
     * @return mapper using reel item names
     */
    public static StandardCombinationListMapper<StandardReelItem> forNames() {
        return forItemMapper(new ReelItemNameMapper());
    }

    /**
     * Creates a mapper for standard reel item characters.
     *
     * <pre>
     * "A" -> StandardReelItem.A
     * "K" -> StandardReelItem.K
     * "?" -> StandardReelItem.WLD
     * "@" -> StandardReelItem.SCT
     * "&" -> StandardReelItem.BON
     * "!" -> StandardReelItem.JPT
     * "*" -> StandardReelItem.MUL
     * </pre>
     *
     * @return mapper using reel item characters
     */
    public static StandardCombinationListMapper<StandardReelItem> forCharacters() {
        return forItemMapper(new ReelItemCharacterMapper());
    }

    /**
     * Creates a complete mapping chain using the supplied reel item mapper.
     *
     * @param itemMapper mapper used to convert string identifiers to reel items
     * @param <I>        the type of items contained in the combinations
     * @return mapper using the supplied reel item mapper
     * @throws NullPointerException if {@code itemMapper} is {@code null}
     */
    public static <I extends ReelItem> StandardCombinationListMapper<I> forItemMapper(
            @NonNull ReelItemMapper<I> itemMapper) {

        return forCombinationMapper(new DefaultCombinationMapper<>(
                new DefaultReelItemListMapper<>(itemMapper)
        ));
    }

    /**
     * Creates a list mapper using the supplied combination mapper.
     *
     * @param combinationMapper mapper used to create individual combinations
     * @param <I>               the type of items contained in the combinations
     * @return mapper using the supplied combination mapper
     * @throws NullPointerException if {@code combinationMapper} is {@code null}
     */
    public static <I extends ReelItem> StandardCombinationListMapper<I> forCombinationMapper(
            @NonNull CombinationMapper<I> combinationMapper) {

        return new StandardCombinationListMapper<>(combinationMapper);
    }

    /**
     * Maps descriptions to combinations indexed by their identifiers.
     *
     * @param descriptions combination descriptions in their configured order
     * @return unmodifiable map of combination IDs to mapped combinations
     */
    @Override
    public Map<Integer, Combination<I>> map(@NonNull List<CombinationDescription> descriptions) {
        return listMapper.map(descriptions);
    }
}
