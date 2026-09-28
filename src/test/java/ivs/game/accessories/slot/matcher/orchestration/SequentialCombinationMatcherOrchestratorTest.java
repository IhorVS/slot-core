package ivs.game.accessories.slot.matcher.orchestration;

import ivs.game.accessories.slot.matcher.CombinationMatch;
import ivs.game.accessories.slot.matcher.CombinationMatcher;
import ivs.game.accessories.slot.reel.ReelItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SequentialCombinationMatcherOrchestratorTest {

    @Mock
    private CombinationMatcher<
            ReelItem, CombinationMatch<ReelItem>> firstMatcher;
    @Mock
    private CombinationMatcher<
            ReelItem, CombinationMatch<ReelItem>> secondMatcher;
    @Mock
    private CombinationMatcher<
            ReelItem, CombinationMatch<ReelItem>> thirdMatcher;

    @Mock
    private CombinationMatch<ReelItem> firstMatch;
    @Mock
    private CombinationMatch<ReelItem> secondMatch;
    @Mock
    private CombinationMatch<ReelItem> thirdMatch;

    @SuppressWarnings("DataFlowIssue")
    @Test
    void matchRejectsNullMatchersList() {
        var orchestrator = new SequentialCombinationMatcherOrchestrator<>();

        assertThrows(NullPointerException.class, () -> orchestrator.match(null));
    }

    @Test
    void matchRejectsNullMatcherElement() {
        var orchestrator = new SequentialCombinationMatcherOrchestrator<>();

        assertThrows(
                IllegalArgumentException.class,
                () -> orchestrator.match(Arrays.asList(
                        firstMatcher,
                        null,
                        thirdMatcher
                ))
        );
    }

    @Test
    void matchRunsMatchersInSuppliedOrderAndReturnsTheirResults() {
        when(firstMatcher.match()).thenReturn(List.of(firstMatch));
        when(secondMatcher.match()).thenReturn(List.of(secondMatch));
        when(thirdMatcher.match()).thenReturn(List.of(thirdMatch));

        List<CombinationMatcher<
                ReelItem, ? extends CombinationMatch<ReelItem>>> matchers =
                List.of(firstMatcher, secondMatcher, thirdMatcher);

        var matches = new SequentialCombinationMatcherOrchestrator<>().match(matchers);

        assertEquals(List.of(firstMatch, secondMatch, thirdMatch), matches);

        InOrder inOrder = inOrder(firstMatcher, secondMatcher, thirdMatcher);
        inOrder.verify(firstMatcher).match();
        inOrder.verify(secondMatcher).match();
        inOrder.verify(thirdMatcher).match();
    }
}
