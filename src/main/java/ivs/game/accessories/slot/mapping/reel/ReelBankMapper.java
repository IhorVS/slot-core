package ivs.game.accessories.slot.mapping.reel;

import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.List;

/**
 * Maps external reel descriptions to a {@link ReelBank}.
 *
 * <p>Each description is mapped independently, allowing reels to have different
 * item sequences and visible sizes. The order of descriptions determines the
 * order of reels in the resulting bank.</p>
 *
 * <p>A rectangular reel bank can be mapped from reel item lists and one common
 * visible size.</p>
 *
 * <pre>
 * ReelDescription 0 -> Reel 0
 * ReelDescription 1 -> Reel 1
 * ReelDescription 2 -> Reel 2
 *                           |
 *                           v
 *                       ReelBank
 * </pre>
 *
 * <p>The particular reel and reel bank implementations are selected by the
 * mapper implementation.</p>
 *
 * @param <I> the type of items contained in the reels
 */
public interface ReelBankMapper<I extends ReelItem> {

    /**
     * Maps reel descriptions to a reel bank.
     *
     * @param descriptions reel descriptions in left-to-right order
     * @return mapped reel bank
     * @throws NullPointerException if {@code descriptions} is {@code null}
     * @throws RuntimeException     if a reel description cannot be mapped or the
     *                              reel bank cannot be created
     */
    ReelBank<I> map(List<ReelDescription> descriptions);

    /**
     * Maps a rectangular reel configuration to a reel bank.
     *
     * <p>The same visible size is assigned to every reel. Reel item lists retain
     * their original left-to-right order.</p>
     *
     * @param reels       external reel item identifiers for every reel
     * @param visibleSize the number of items visible on every reel
     * @return mapped reel bank
     * @throws NullPointerException     if {@code reels} is {@code null}
     * @throws IllegalArgumentException if {@code reels} contains a {@code null} element
     * @throws RuntimeException         if a reel cannot be mapped or the reel bank cannot be created
     */
    default ReelBank<I> map(@NonNull List<List<String>> reels, int visibleSize) {
        Validate.noNullElements(reels, "List must not contain null reels");

        List<ReelDescription> descriptions = reels.stream()
                .map(values -> new ReelDescription(values, visibleSize))
                .toList();

        return map(descriptions);
    }
}
