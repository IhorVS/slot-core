package ivs.game.accessories.slot.matcher.orchestration;

import ivs.game.accessories.slot.matcher.CombinationMatch;
import ivs.game.accessories.slot.matcher.CombinationMatcher;
import ivs.game.accessories.slot.reel.ReelItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParallelCombinationMatcherOrchestratorTest {

    private final Executor executor = Runnable::run;

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
        var orchestrator = new ParallelCombinationMatcherOrchestrator<>(
                executor
        );

        assertThrows(NullPointerException.class, () -> orchestrator.match(null));
    }

    @Test
    void matchRejectsNullMatcherElement() {
        var orchestrator = new ParallelCombinationMatcherOrchestrator<>(
                executor
        );

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
    void matchSubmitsAllMatchersBeforeCollectingResultsAndPreservesOrder() throws Exception {
        when(firstMatcher.match()).thenReturn(List.of(firstMatch));
        when(secondMatcher.match()).thenReturn(List.of(secondMatch));
        when(thirdMatcher.match()).thenReturn(List.of(thirdMatch));

        BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
        var orchestrator = new ParallelCombinationMatcherOrchestrator<>(tasks::add);

        var result = CompletableFuture.supplyAsync(() -> orchestrator.match(
                List.of(firstMatcher, secondMatcher, thirdMatcher)
        ));

        Runnable firstTask = tasks.poll(2, TimeUnit.SECONDS);
        Runnable secondTask = tasks.poll(2, TimeUnit.SECONDS);
        Runnable thirdTask = tasks.poll(2, TimeUnit.SECONDS);

        assertNotNull(firstTask);
        assertNotNull(secondTask);
        assertNotNull(thirdTask);

        thirdTask.run();
        secondTask.run();
        firstTask.run();

        assertEquals(
                List.of(firstMatch, secondMatch, thirdMatch),
                result.get(2, TimeUnit.SECONDS)
        );
    }

    @Test
    void matchPropagatesMatcherException() {
        RuntimeException failure = new IllegalStateException("Matcher failed");
        when(firstMatcher.match()).thenThrow(failure);

        var orchestrator = new ParallelCombinationMatcherOrchestrator<>(executor);

        RuntimeException actual = assertThrows(
                RuntimeException.class,
                () -> orchestrator.match(List.of(firstMatcher))
        );

        assertSame(failure, actual);
    }
}
