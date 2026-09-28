package ivs.game.accessories.slot.matcher.orchestration;

import ivs.game.accessories.slot.matcher.CombinationMatch;
import ivs.game.accessories.slot.matcher.CombinationMatcher;
import ivs.game.accessories.slot.matcher.CombinationMatcherOrchestrator;
import ivs.game.accessories.slot.reel.ReelItem;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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

    public ParallelCombinationMatcherOrchestrator(@NonNull Executor executor) {
        this.executor = executor;
    }

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
                .<CombinationMatch<I>>flatMap(future -> getMatches(future).stream())
                .toList();
    }

    private List<? extends CombinationMatch<I>> getMatches(
            CompletableFuture<List<? extends CombinationMatch<I>>> future
    ) {
        try {
            return future.join();
        } catch (CompletionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw exception;
        }
    }
}
