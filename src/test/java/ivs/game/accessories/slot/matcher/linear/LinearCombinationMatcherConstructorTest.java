package ivs.game.accessories.slot.matcher.linear;

import ivs.game.accessories.slot.field.SlotField;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.policy.AllCombinationMatchPolicy;
import ivs.game.accessories.slot.reel.ReelItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@SuppressWarnings("DataFlowIssue")
@ExtendWith(MockitoExtension.class)
class LinearCombinationMatcherConstructorTest {
    private static final AllCombinationMatchPolicy<
            LinearCombinationMatch<ReelItem>
            > ALL_POLICY = new AllCombinationMatchPolicy<>();

    @SuppressWarnings("unchecked")
    private final SlotField<ReelItem> field = mock(SlotField.class);
    private final ReelItem A = mock(ReelItem.class);
    private final ReelItem wildSubstitute = mock(ReelItem.class);

    private final FieldLine line = new FieldLine(0, List.of(new FieldPosition(0, 0)));

    private final Combination<ReelItem> combination = new Combination<>(0, "test", A, A, A);

    @Test
    void constructorRejectsNullField() {
        assertThrows(
                NullPointerException.class,
                () -> new LinearCombinationMatcher<>(
                        null,
                        List.of(line),
                        List.of(combination),
                        Set.of(wildSubstitute),
                        ALL_POLICY
                )
        );
    }

    @Test
    void constructorRejectsNullLines() {
        assertThrows(
                NullPointerException.class,
                () -> new LinearCombinationMatcher<>(
                        field,
                        null,
                        List.of(combination),
                        Set.of(wildSubstitute),
                        ALL_POLICY
                )
        );
    }

    @Test
    void constructorRejectsEmptyLines() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LinearCombinationMatcher<>(
                        field,
                        List.of(),
                        List.of(combination),
                        Set.of(wildSubstitute),
                        ALL_POLICY
                )
        );
    }

    @Test
    void constructorRejectsNullLineElements() {
        assertThrows(
                NullPointerException.class,
                () -> new LinearCombinationMatcher<>(
                        field,
                        List.of((FieldLine) null),
                        List.of(combination),
                        Set.of(wildSubstitute),
                        ALL_POLICY
                )
        );
    }

    @Test
    void constructorRejectsNullCombinations() {
        assertThrows(
                NullPointerException.class,
                () -> new LinearCombinationMatcher<>(
                        field,
                        List.of(line),
                        null,
                        Set.of(wildSubstitute),
                        ALL_POLICY
                )
        );
    }

    @Test
    void constructorRejectsEmptyCombinations() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LinearCombinationMatcher<>(
                        field,
                        List.of(line),
                        List.of(),
                        Set.of(wildSubstitute),
                        ALL_POLICY
                )
        );
    }

    @Test
    void constructorRejectsNullCombinationElements() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LinearCombinationMatcher<>(
                        field,
                        List.of(line),
                        Collections.singletonList(null),
                        Set.of(wildSubstitute),
                        ALL_POLICY
                )
        );
    }

    @Test
    void constructorRejectsNullWildSubstitutes() {
        assertThrows(
                NullPointerException.class,
                () -> new LinearCombinationMatcher<>(
                        field,
                        List.of(line),
                        List.of(combination),
                        null,
                        ALL_POLICY
                )
        );
    }

    @Test
    void constructorRejectsNullWildSubstituteElements() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LinearCombinationMatcher<>(
                        field,
                        List.of(line),
                        List.of(combination),
                        Collections.singleton(null),
                        ALL_POLICY
                )
        );
    }
}
