package ivs.game.accessories.slot.mapping.reel.impl;

import ivs.game.accessories.slot.mapping.reel.ReelBankMapper;
import ivs.game.accessories.slot.mapping.reel.ReelDescription;
import ivs.game.accessories.slot.mapping.reel.ReelMapper;
import ivs.game.accessories.slot.reel.Reel;
import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.function.Function;

/**
 * Maps reel descriptions to a reel bank using configured reel and reel bank
 * mapping components.
 *
 * <p>Each description is delegated to the configured {@link ReelMapper}. The
 * resulting reels are then passed to the reel bank creator in their original
 * order.</p>
 *
 * @param <I> the type of items contained in the reels
 */
@RequiredArgsConstructor
public final class DefaultReelBankMapper<I extends ReelItem> implements ReelBankMapper<I> {

    @NonNull
    private final ReelMapper<I> reelMapper;

    @NonNull
    private final Function<List<Reel<I>>, ? extends ReelBank<I>> reelBankCreator;

    /**
     * Maps reel descriptions and creates a reel bank.
     *
     * @param descriptions reel descriptions in left-to-right order
     * @return mapped reel bank
     * @throws NullPointerException     if {@code descriptions} is {@code null}, or
     *                                  the reel bank creator returns {@code null}
     * @throws IllegalArgumentException if the list contains a {@code null} description
     * @throws RuntimeException         if a reel cannot be mapped or the reel bank cannot be created
     */
    @Override
    public ReelBank<I> map(@NonNull List<ReelDescription> descriptions) {
        Validate.noNullElements(descriptions, "List must not contain null reel descriptions");

        List<Reel<I>> reels = descriptions.stream()
                .map(description -> reelMapper.map(description.values(), description.visibleSize()))
                .toList();

        ReelBank<I> reelBank = reelBankCreator.apply(reels);
        return Validate.notNull(reelBank, "Reel bank creator must not return null");
    }
}
