package ivs.game.accessories.slot.matcher;

import ivs.game.accessories.slot.reel.ReelItem;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;
import java.util.List;

/**
 * Describes the symbols that form a combination.
 *
 * <p>The meaning of item order is defined by the matcher. A linear matcher
 * uses the order, while a scatter matcher may ignore it. The class can be
 * extended when a specialized matcher needs additional combination data.</p>
 *
 * <p>Subclasses that add equality-significant fields must implement
 * {@code equals} and {@code hashCode} with Lombok's
 * {@code @EqualsAndHashCode(callSuper = true)} or provide an equivalent
 * implementation that includes the superclass state.</p>
 *
 * @param <I> the type of reel items in the combination
 */
@EqualsAndHashCode
@ToString
@Getter
public class Combination<I extends ReelItem> {  // TODO may be interface need
    private final int id;
    private final String groupId;
    private final List<I> items;

    /**
     * Creates a combination in the specified group from a list of symbols.
     *
     * @param id      the identifier of the combination
     * @param groupId the identifier of the combination group
     * @param items   the symbols in the combination
     */
    public Combination(int id, String groupId, @NonNull List<I> items) {
        this.id = id;

        this.groupId = Validate.notBlank(groupId, "Group ID must not be blank");
        Validate.isTrue(!items.isEmpty(), "Combination must contain at least one item");
        Validate.noNullElements(items, "Items must not contain null elements");

        this.items = List.copyOf(items);
    }

    /**
     * Creates a combination in the specified group from symbols.
     *
     * @param id      the identifier of the combination
     * @param groupId the identifier of the combination group
     * @param items   the symbols in the combination
     */
    @SafeVarargs
    public Combination(int id, String groupId, I... items) {
        this(id, groupId, Arrays.asList(items));
    }

    /**
     * Returns the identifier of the combination group.
     *
     * @return the combination group identifier
     */
    public final String getGroupId() {
        return groupId;
    }

    /**
     * Returns the symbols that form the combination.
     *
     * @return an unmodifiable list of symbols
     */
    public final List<I> getItems() {
        return items;
    }

    /**
     * Returns the number of symbols in the combination.
     *
     * @return the combination size
     */
    public final int size() {
        return items.size();
    }
}
