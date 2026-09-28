package ivs.game.accessories.slot.matcher.policy;

import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.scatter.ScatterCombinationMatch;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShortestCombinationMatchPolicyTest {

    @Test
    void retainsShortestMatchFromEachGroupInInputOrder() {
        // Input: group A contains lengths 1 and 3; group B contains length 2.
        // Expected: the shortest match from each group is retained.
        var shortestA = match("A", 1);
        var longestA = match("A", 3);
        var onlyB = match("B", 2);

        assertEquals(
                List.of(shortestA, onlyB),
                new ShortestCombinationMatchPolicy<ScatterCombinationMatch<StandardReelItem>>()
                        .apply(List.of(longestA, shortestA, onlyB))
        );
    }

    @Test
    void returnsEmptyForEmptyInput() {
        assertEquals(
                List.of(),
                new ShortestCombinationMatchPolicy<ScatterCombinationMatch<StandardReelItem>>()
                        .apply(List.of())
        );
    }

    private static ScatterCombinationMatch<StandardReelItem> match(
            String groupId,
            int length
    ) {
        return new ScatterCombinationMatch<>(
                new Combination<>(0, groupId, StandardReelItem.A),
                IntStream.range(0, length)
                        .mapToObj(row -> new FieldPosition(0, row))
                        .toList()
        );
    }
}
