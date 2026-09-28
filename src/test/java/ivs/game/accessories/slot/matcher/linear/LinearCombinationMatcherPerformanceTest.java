package ivs.game.accessories.slot.matcher.linear;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.field.impl.StandardSlotField;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.policy.AllCombinationMatchPolicy;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Measures linear matching on a five-reel, three-row field with 40 lines
 * and 78 combinations: three lengths for each symbol from A to Z.
 *
 * <p>This is a diagnostic timing test, not a stable performance threshold.
 * It is disabled during ordinary test runs.</p>
 *
 * <p>Run separately with:
 * {@code mvn test -Dtest=LinearCombinationMatcherPerformanceTest -Dslot.perf=true}</p>
 */
@EnabledIfSystemProperty(named = "slot.perf", matches = "true")
class LinearCombinationMatcherPerformanceTest {

    private static final int WARMUP_RUNS = 500;
    private static final int MEASURED_RUNS = 2_000;

    @Test
    void measureMatchingTime() {
        List<FieldLine> lines = createLines();
        List<Combination<StandardReelItem>> combinations = createCombinations();

        // Each reel shows A, B, C from top to bottom.
        SlotField<StandardReelItem> regularField = new StandardSlotField<>(
                Collections.nCopies(5, List.of(
                        StandardReelItem.A,
                        StandardReelItem.B,
                        StandardReelItem.C
                ))
        );

        // Every line contains AAAAA and matches AAA, AAAA, and AAAAA.
        SlotField<StandardReelItem> allAField = new StandardSlotField<>(
                Collections.nCopies(5, Collections.nCopies(3, StandardReelItem.A))
        );

        var regularMatcher = new LinearCombinationMatcher<>(
                regularField,
                lines,
                combinations,
                Set.of(StandardReelItem.WLD),
                new AllCombinationMatchPolicy<>()
        );
        var allAMatcher = new LinearCombinationMatcher<>(
                allAField,
                lines,
                combinations,
                Set.of(StandardReelItem.WLD),
                new AllCombinationMatchPolicy<>()
        );

        // 40 lines multiplied by three matching A combinations.
        assertEquals(120, allAMatcher.match().size());

        measure("Regular field", regularMatcher);
        measure("All A", allAMatcher);
    }

    private static void measure(
            String name,
            LinearCombinationMatcher<StandardReelItem> matcher
    ) {
        // Run the matcher before measuring so JVM startup has less influence.
        for (int run = 0; run < WARMUP_RUNS; run++) {
            matcher.match();
        }

        long totalMatches = 0;
        long started = System.nanoTime();

        for (int run = 0; run < MEASURED_RUNS; run++) {
            totalMatches += matcher.match().size();
        }

        long elapsed = System.nanoTime() - started;
        double microsecondsPerRun = elapsed / 1_000.0 / MEASURED_RUNS;

        System.out.printf(
                "%s: %.2f us/match(), %d matches/run%n",
                name,
                microsecondsPerRun,
                totalMatches / MEASURED_RUNS
        );
    }

    private static List<FieldLine> createLines() {
        List<FieldLine> lines = new ArrayList<>();

        for (int id = 0; id < 40; id++) {
            int pattern = id;
            List<FieldPosition> positions = new ArrayList<>();

            // A base-three digit selects one of three rows on each reel.
            // The first 40 patterns give us 40 distinct five-position lines.
            for (int column = 0; column < 5; column++) {
                positions.add(new FieldPosition(column, pattern % 3));
                pattern /= 3;
            }

            lines.add(new FieldLine(id, positions));
        }

        return List.copyOf(lines);
    }

    private static List<Combination<StandardReelItem>> createCombinations() {
        List<Combination<StandardReelItem>> combinations = new ArrayList<>();
        int id = 0;

        for (char letter = 'A'; letter <= 'Z'; letter++) {
            StandardReelItem item = StandardReelItem.valueOf(String.valueOf(letter));

            // For example, A produces AAA, AAAA, and AAAAA.
            for (int length = 3; length <= 5; length++) {
                combinations.add(new Combination<>(
                        id++,
                        String.valueOf(letter),
                        Collections.nCopies(length, item)
                ));
            }
        }

        return List.copyOf(combinations);
    }
}
