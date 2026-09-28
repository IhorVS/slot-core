package ivs.game.accessories.slot.mapping.reel.impl;

import ivs.game.accessories.slot.mapping.reel.ReelBankMapper;
import ivs.game.accessories.slot.mapping.reel.ReelDescription;
import ivs.game.accessories.slot.mapping.reel.ReelItemListMapper;
import ivs.game.accessories.slot.mapping.reel.ReelItemMapper;
import ivs.game.accessories.slot.mapping.reel.ReelMapper;
import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.ReelItem;
import ivs.game.accessories.slot.reel.impl.StandardReel;
import ivs.game.accessories.slot.reel.impl.StandardReelBank;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.List;

/**
 * Assembles standard components for mapping reel descriptions to a
 * {@link StandardReelBank}.
 *
 * <p>An item mapper creates the complete mapping chain. A supplied reel mapper
 * replaces the item and reel mapping layers while retaining the standard reel
 * bank layer.</p>
 *
 * @param <I> the type of items contained in the reels
 */
public final class StandardReelBankMapper<I extends ReelItem> implements ReelBankMapper<I> {

    private final ReelBankMapper<I> reelBankMapper;

    private StandardReelBankMapper(ReelMapper<I> reelMapper) {
        Validate.notNull(reelMapper, "Reel mapper must not be null");
        reelBankMapper = new DefaultReelBankMapper<>(reelMapper, StandardReelBank::new);
    }

    /**
     * Creates a mapper for standard reel item names.
     *
     * <pre>
     * "A"   -> StandardReelItem.A
     * "WLD" -> StandardReelItem.WLD
     * "SCT" -> StandardReelItem.SCT
     * </pre>
     *
     * @return mapper using reel item names
     */
    public static StandardReelBankMapper<StandardReelItem> forNames() {
        return forItemMapper(new ReelItemNameMapper());
    }

    /**
     * Creates a mapper for standard reel item characters.
     *
     * <pre>
     * "A" -> StandardReelItem.A
     * "?" -> StandardReelItem.WLD
     * "@" -> StandardReelItem.SCT
     * </pre>
     *
     * @return mapper using reel item characters
     */
    public static StandardReelBankMapper<StandardReelItem> forCharacters() {
        return forItemMapper(new ReelItemCharacterMapper());
    }

    /**
     * Creates a complete standard mapping chain using the supplied item mapper.
     *
     * @param itemMapper mapper used to convert string identifiers to reel items
     * @param <I>        the type of items contained in the reels
     * @return mapper using the supplied item mapper
     * @throws NullPointerException if {@code itemMapper} is {@code null}
     */
    public static <I extends ReelItem> StandardReelBankMapper<I> forItemMapper(
            @NonNull ReelItemMapper<I> itemMapper) {

        ReelItemListMapper<I> itemListMapper = new DefaultReelItemListMapper<>(itemMapper);
        ReelMapper<I> reelMapper = new DefaultReelMapper<>(itemListMapper, StandardReel::new);

        return forReelMapper(reelMapper);
    }

    /**
     * Creates a mapper that places reels produced by the supplied mapper into
     * a standard reel bank.
     *
     * @param reelMapper mapper used to create individual reels
     * @param <I>        the type of items contained in the reels
     * @return mapper using the supplied reel mapper
     * @throws NullPointerException if {@code reelMapper} is {@code null}
     */
    public static <I extends ReelItem> StandardReelBankMapper<I> forReelMapper(
            @NonNull ReelMapper<I> reelMapper) {

        return new StandardReelBankMapper<>(reelMapper);
    }

    /**
     * Maps reel descriptions to a standard reel bank.
     *
     * @param descriptions reel descriptions in left-to-right order
     * @return standard reel bank containing the mapped reels
     * @throws NullPointerException if {@code descriptions} is {@code null}
     * @throws RuntimeException     if a reel description cannot be mapped or the
     *                              reel bank cannot be created
     */
    @Override
    public ReelBank<I> map(@NonNull List<ReelDescription> descriptions) {
        return reelBankMapper.map(descriptions);
    }
}
