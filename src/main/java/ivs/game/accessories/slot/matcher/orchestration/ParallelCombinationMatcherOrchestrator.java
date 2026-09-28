package ivs.game.accessories.slot.matcher.orchestration;

import ivs.game.accessories.slot.matcher.CombinationMatch;
import ivs.game.accessories.slot.matcher.CombinationMatcher;
import ivs.game.accessories.slot.matcher.CombinationMatcherOrchestrator;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Runs combination matchers in parallel using the supplied executor.
 *
 * <p>All matcher tasks are submitted before their results are collected.
 * Results are returned in the order of the supplied matchers, regardless of
 * the order in which the tasks complete.</p>
 *
 * <p>Supplied matchers must be safe for concurrent execution. In particular,
 * they must not modify shared mutable state while matching.</p>
 *
 * @param <I> the type of reel items on the field
 */
public final class ParallelCombinationMatcherOrchestrator<
        I extends ReelItem
        > implements CombinationMatcherOrchestrator<I> {
    private final Executor executor;

    /**
     * Creates an orchestrator using the supplied executor.
     *
     * @param executor the executor used to run matchers
     * @throws NullPointerException if {@code executor} is null
     */
    public ParallelCombinationMatcherOrchestrator(@NonNull Executor executor) {
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Each matcher is submitted to the configured executor. The returned
     * list is unmodifiable.</p>
     */
    @Override
    public List<CombinationMatch<I>> match(
            @NonNull List<? extends CombinationMatcher<
                    I, ? extends CombinationMatch<I>>> matchers
    ) {
        Validate.noNullElements(matchers, "Matchers must not contain null elements");

        List<CompletableFuture<
                List<? extends CombinationMatch<I>>>> futures = matchers.stream()
                .map(matcher -> CompletableFuture
                        .<List<? extends CombinationMatch<I>>>supplyAsync(
                                matcher::match,
                                executor
                        ))
                .toList();

        return futures.stream()
                .<CombinationMatch<I>>flatMap(future -> future.join().stream())
                .toList();
    }
}
