package ivs.game.accessories.slot.matcher.policy;

import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.scatter.ScatterCombinationMatch;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class AllCombinationMatchPolicyTest {

    @Test
    void retainsEveryMatchInInputOrderAndReturnsInputList() {
        // Input: two matches in a defined order.
        // Expected: every match is retained in that order and the original
        // input list is returned without copying.
        var first = match(1);
        var second = match(3);
        var matches = List.of(first, second);
        var policy = new AllCombinationMatchPolicy<ScatterCombinationMatch<StandardReelItem>>();

        assertEquals(matches, policy.apply(matches));
        assertSame(matches, policy.apply(matches));
    }

    private static ScatterCombinationMatch<StandardReelItem> match(int length) {
        return new ScatterCombinationMatch<>(
                new Combination<>(0, "test", StandardReelItem.A),
                positions(length)
        );
    }

    private static List<FieldPosition> positions(int length) {
        return IntStream.range(0, length)
                .mapToObj(row -> new FieldPosition(0, row))
                .toList();
    }
}
