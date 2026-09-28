package ivs.game.accessories.slot.mapping.line.impl;

import ivs.game.accessories.slot.mapping.line.FieldLineMapper;
import ivs.game.accessories.slot.matcher.FieldPosition;
import ivs.game.accessories.slot.matcher.linear.FieldLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests mapping ordered row descriptions to field lines.
 */
@ExtendWith(MockitoExtension.class)
class DefaultFieldLineListMapperTest {

    @Mock
    private FieldLineMapper lineMapper;

    @InjectMocks
    private DefaultFieldLineListMapper mapper;

    /**
     * Verifies that line descriptions are mapped in their original order and
     * their indices are used as line identifiers.
     */
    @Test
    void mapsLineDescriptions() {
        List<Integer> firstDescription = List.of(0, 0, 0);
        List<Integer> secondDescription = List.of(0, 1, 0);
        FieldLine firstLine = new FieldLine(
                0,
                List.of(
                        new FieldPosition(0, 0),
                        new FieldPosition(1, 0),
                        new FieldPosition(2, 0)
                )
        );
        FieldLine secondLine = new FieldLine(
                1,
                List.of(
                        new FieldPosition(0, 0),
                        new FieldPosition(1, 1),
                        new FieldPosition(2, 0)
                )
        );

        when(lineMapper.map(0, firstDescription)).thenReturn(firstLine);
        when(lineMapper.map(1, secondDescription)).thenReturn(secondLine);

        List<FieldLine> result = mapper.map(List.of(firstDescription, secondDescription));

        assertEquals(List.of(firstLine, secondLine), result);

        InOrder inOrder = inOrder(lineMapper);
        inOrder.verify(lineMapper).map(0, firstDescription);
        inOrder.verify(lineMapper).map(1, secondDescription);
    }

    /**
     * Verifies that an empty description list produces an empty result without
     * invoking the field line mapper.
     */
    @Test
    void mapsEmptyLineDescriptionList() {
        List<FieldLine> result = mapper.map(List.of());

        assertEquals(List.of(), result);
        verifyNoInteractions(lineMapper);
    }

    /**
     * Verifies that a {@code null} description list is rejected before mapping.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullLineDescriptionList() {
        assertThrows(NullPointerException.class, () -> mapper.map(null));

        verifyNoInteractions(lineMapper);
    }

    /**
     * Verifies that a list containing a {@code null} line description is rejected
     * before any description is mapped.
     */
    @Test
    void rejectsNullLineDescription() {
        List<List<Integer>> lines = Arrays.asList(List.of(0, 0, 0), null);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> mapper.map(lines));

        assertEquals("List must not contain null line descriptions", e.getMessage());
        verifyNoInteractions(lineMapper);
    }

    /**
     * Verifies that a mapping failure is propagated without attempting to map
     * subsequent line descriptions.
     */
    @Test
    void propagatesLineMappingFailure() {
        List<Integer> firstDescription = List.of(0, 0, 0);
        List<Integer> secondDescription = List.of(0, -1, 0);
        IllegalArgumentException expectedException = new IllegalArgumentException("Invalid row index");

        when(lineMapper.map(0, firstDescription)).thenReturn(
                new FieldLine(
                        0,
                        List.of(
                                new FieldPosition(0, 0),
                                new FieldPosition(1, 0),
                                new FieldPosition(2, 0)
                        )
                )
        );
        when(lineMapper.map(1, secondDescription)).thenThrow(expectedException);

        IllegalArgumentException actualException = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map(List.of(firstDescription, secondDescription))
        );

        assertSame(expectedException, actualException);

        InOrder inOrder = inOrder(lineMapper);
        inOrder.verify(lineMapper).map(0, firstDescription);
        inOrder.verify(lineMapper).map(1, secondDescription);
    }

    /**
     * Verifies that a {@code null} field line mapper is rejected during construction.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullFieldLineMapper() {
        assertThrows(NullPointerException.class, () -> new DefaultFieldLineListMapper(null));
    }
}
