package ivs.game.accessories.slot.prize;

import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.scatter.ScatterCombinationMatch;
import ivs.game.accessories.slot.reel.ReelItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class StandardPrizeResolverTest {
    private static final ReelItem A = mock();
    private static final ReelItem B = mock();
    private static final ReelItem C = mock();

    private static final Combination<ReelItem> A_COMBINATION =
            new Combination<>(0, "A", A);
    private static final Combination<ReelItem> B_COMBINATION =
            new Combination<>(1, "B", B);
    private static final Combination<ReelItem> AAA_COMBINATION =
            new Combination<>(2, "A", A, A, A);

    private static final Map<Combination<ReelItem>, Set<String>> PRIZES_BY_COMBINATION = Map.of(
            A_COMBINATION, Set.of("PAY_1"),
            B_COMBINATION, Set.of("PAY_B"),
            AAA_COMBINATION, Set.of("PAY_20", "BONUS_1")
    );

    private static final StandardPrizeResolver<ReelItem, String> RESOLVER =
            new StandardPrizeResolver<>(PRIZES_BY_COMBINATION);

    @Test
    void resolvesConfiguredPrizesByCombination() {
        var result = RESOLVER.resolve(List.of(match(AAA_COMBINATION)));

        assertEquals(
                Map.of(AAA_COMBINATION, Set.of("PAY_20", "BONUS_1")),
                result
        );
    }

    @Test
    void omitsMatchesWithoutConfiguredPrizes() {
        var result = RESOLVER.resolve(List.of(
                match(A_COMBINATION),
                match(new Combination<>(3, "C", C))
        ));

        assertEquals(Map.of(A_COMBINATION, Set.of("PAY_1")), result);
    }

    @Test
    void repeatedMatchesShareOnePrizeEntry() {
        var result = RESOLVER.resolve(List.of(
                match(A_COMBINATION),
                match(A_COMBINATION)
        ));

        assertEquals(Map.of(A_COMBINATION, Set.of("PAY_1")), result);
    }

    @Test
    void resolverCanBeReusedForDifferentMatchLists() {
        assertEquals(
                Map.of(A_COMBINATION, Set.of("PAY_1")),
                RESOLVER.resolve(List.of(match(A_COMBINATION)))
        );
        assertEquals(
                Map.of(B_COMBINATION, Set.of("PAY_B")),
                RESOLVER.resolve(List.of(match(B_COMBINATION)))
        );
    }

    @Test
    void constructorRejectsNullConfiguration() {
        //noinspection DataFlowIssue
        assertThrows(
                NullPointerException.class,
                () -> new StandardPrizeResolver<
                        ReelItem,
                        String
                        >(null)
        );
    }

    @Test
    void resolveRejectsNullMatches() {
        //noinspection DataFlowIssue
        assertThrows(
                NullPointerException.class,
                () -> RESOLVER.resolve(null)
        );
    }

    private static ScatterCombinationMatch<ReelItem> match(Combination<ReelItem> combination) {
        return new ScatterCombinationMatch<>(
                combination,
                List.of(new FieldPosition(0, 0))
        );
    }
}
