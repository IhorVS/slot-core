package ivs.game.accessories.slot.mapping.reel.impl;

import ivs.game.accessories.slot.mapping.reel.ReelDescription;
import ivs.game.accessories.slot.mapping.reel.ReelMapper;
import ivs.game.accessories.slot.reel.Reel;
import ivs.game.accessories.slot.reel.ReelBank;
import ivs.game.accessories.slot.reel.ReelItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests mapping reel descriptions to a reel bank.
 */
@ExtendWith(MockitoExtension.class)
class DefaultReelBankMapperTest {

    @Mock
    private ReelMapper<ReelItem> reelMapper;

    @Mock
    private Function<List<Reel<ReelItem>>, ReelBank<ReelItem>> reelBankCreator;

    @Mock
    private Reel<ReelItem> firstReel;

    @Mock
    private Reel<ReelItem> secondReel;

    @Mock
    private ReelBank<ReelItem> reelBank;

    @InjectMocks
    private DefaultReelBankMapper<ReelItem> mapper;

    /**
     * Verifies that every description is mapped in its original order and that
     * the resulting reels are passed to the reel bank creator.
     */
    @Test
    void mapsDescriptionsAndCreatesReelBank() {
        ReelDescription firstDescription = new ReelDescription(List.of("A", "B"), 3);
        ReelDescription secondDescription = new ReelDescription(List.of("WLD", "SCT"), 2);
        List<ReelDescription> descriptions = List.of(firstDescription, secondDescription);
        List<Reel<ReelItem>> reels = List.of(firstReel, secondReel);

        when(reelMapper.map(firstDescription.values(), firstDescription.visibleSize())).thenReturn(firstReel);
        when(reelMapper.map(secondDescription.values(), secondDescription.visibleSize())).thenReturn(secondReel);
        when(reelBankCreator.apply(reels)).thenReturn(reelBank);

        ReelBank<ReelItem> result = mapper.map(descriptions);

        assertSame(reelBank, result);

        InOrder inOrder = inOrder(reelMapper, reelBankCreator);
        inOrder.verify(reelMapper).map(firstDescription.values(), firstDescription.visibleSize());
        inOrder.verify(reelMapper).map(secondDescription.values(), secondDescription.visibleSize());
        inOrder.verify(reelBankCreator).apply(reels);
    }

    /**
     * Verifies that an empty description list is passed to the reel bank creator
     * without invoking the reel mapper.
     */
    @Test
    void mapsEmptyDescriptionList() {
        List<Reel<ReelItem>> reels = List.of();

        when(reelBankCreator.apply(reels)).thenReturn(reelBank);

        ReelBank<ReelItem> result = mapper.map(List.of());

        assertSame(reelBank, result);
        verifyNoInteractions(reelMapper);
    }

    /**
     * Verifies that a {@code null} description list is rejected before mapping.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullDescriptionList() {
        assertThrows(NullPointerException.class, () -> mapper.map(null));

        verifyNoInteractions(reelMapper, reelBankCreator);
    }

    /**
     * Verifies that a description list containing a {@code null} element is
     * rejected before mapping.
     */
    @Test
    void rejectsNullDescription() {
        ReelDescription description = new ReelDescription(List.of("A", "B"), 3);
        List<ReelDescription> descriptions = Arrays.asList(description, null);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> mapper.map(descriptions));

        assertEquals("List must not contain null reel descriptions", e.getMessage());
        verifyNoInteractions(reelMapper, reelBankCreator);
    }

    /**
     * Verifies that an exception produced while mapping a reel is propagated and
     * the reel bank creator is not invoked.
     */
    @Test
    void propagatesReelMappingFailure() {
        ReelDescription description = new ReelDescription(List.of("A", "UNKNOWN"), 3);
        IllegalArgumentException expectedException = new IllegalArgumentException("Unknown reel item");

        when(reelMapper.map(description.values(), description.visibleSize())).thenThrow(expectedException);

        IllegalArgumentException actualException = assertThrows(
                IllegalArgumentException.class, () -> mapper.map(List.of(description)));

        assertSame(expectedException, actualException);
        verifyNoInteractions(reelBankCreator);
    }

    /**
     * Verifies that an exception produced by the reel bank creator is propagated.
     */
    @Test
    void propagatesReelBankCreationFailure() {
        ReelDescription description = new ReelDescription(List.of("A", "B"), 3);
        List<Reel<ReelItem>> reels = List.of(firstReel);
        IllegalArgumentException expectedException =
                new IllegalArgumentException("Reel bank must contain at least one reel");

        when(reelMapper.map(description.values(), description.visibleSize())).thenReturn(firstReel);
        when(reelBankCreator.apply(reels)).thenThrow(expectedException);

        IllegalArgumentException actualException = assertThrows(
                IllegalArgumentException.class, () -> mapper.map(List.of(description)));

        assertSame(expectedException, actualException);
    }

    /**
     * Verifies that a {@code null} value returned by the reel bank creator is rejected.
     */
    @Test
    void rejectsNullReelBank() {
        ReelDescription description = new ReelDescription(List.of("A", "B"), 3);
        List<Reel<ReelItem>> reels = List.of(firstReel);

        when(reelMapper.map(description.values(), description.visibleSize())).thenReturn(firstReel);
        when(reelBankCreator.apply(reels)).thenReturn(null);

        NullPointerException e = assertThrows(NullPointerException.class, () -> mapper.map(List.of(description)));

        assertEquals("Reel bank creator must not return null", e.getMessage());
    }

    /**
     * Verifies that a {@code null} reel mapper is rejected during construction.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullReelMapper() {
        assertThrows(NullPointerException.class, () -> new DefaultReelBankMapper<>(null, reelBankCreator));
    }

    /**
     * Verifies that a {@code null} reel bank creator is rejected during construction.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullReelBankCreator() {
        assertThrows(NullPointerException.class, () -> new DefaultReelBankMapper<>(reelMapper, null));
    }

    /**
     * Verifies that every reel item list is mapped with the common visible size
     * and that the resulting reels are passed to the reel bank creator.
     */
    @Test
    void mapsRectangularReelsAndCreatesReelBank() {
        List<String> firstValues = List.of("A", "B");
        List<String> secondValues = List.of("WLD", "SCT");
        List<List<String>> reelsConfig = List.of(firstValues, secondValues);
        List<Reel<ReelItem>> reels = List.of(firstReel, secondReel);

        when(reelMapper.map(firstValues, 3)).thenReturn(firstReel);
        when(reelMapper.map(secondValues, 3)).thenReturn(secondReel);
        when(reelBankCreator.apply(reels)).thenReturn(reelBank);

        ReelBank<ReelItem> result = mapper.map(reelsConfig, 3);

        assertSame(reelBank, result);

        InOrder inOrder = inOrder(reelMapper, reelBankCreator);
        inOrder.verify(reelMapper).map(firstValues, 3);
        inOrder.verify(reelMapper).map(secondValues, 3);
        inOrder.verify(reelBankCreator).apply(reels);
    }

    /**
     * Verifies that a {@code null} rectangular reel configuration is rejected
     * before mapping.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullRectangularReelConfiguration() {
        assertThrows(NullPointerException.class, () -> mapper.map(null, 3));

        verifyNoInteractions(reelMapper, reelBankCreator);
    }

    /**
     * Verifies that a rectangular reel configuration containing a {@code null}
     * reel item list is rejected before mapping.
     */
    @Test
    void rejectsNullReelInRectangularConfiguration() {
        List<List<String>> reelsConfig = Arrays.asList(
                List.of("A", "B"),
                null
        );

        IllegalArgumentException e =
                assertThrows(IllegalArgumentException.class, () -> mapper.map(reelsConfig, 3));

        assertEquals("List must not contain null reels", e.getMessage());
        verifyNoInteractions(reelMapper, reelBankCreator);
    }
}
