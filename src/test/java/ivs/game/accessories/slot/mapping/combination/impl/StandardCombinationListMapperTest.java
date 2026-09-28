package ivs.game.accessories.slot.mapping.combination.impl;

import ivs.game.accessories.slot.mapping.combination.CombinationDescription;
import ivs.game.accessories.slot.mapping.combination.CombinationMapper;
import ivs.game.accessories.slot.mapping.reel.ReelItemMapper;
import ivs.game.accessories.slot.matcher.Combination;
import ivs.game.accessories.slot.reel.impl.StandardReelItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests assembly of standard combination mapping components.
 */
class StandardCombinationListMapperTest {

    /**
     * Verifies that names of regular and special reel items are mapped.
     */
    @Test
    void mapsReelItemNames() {
        Map<Integer, Combination<StandardReelItem>> result =
                StandardCombinationListMapper.forNames().map(List.of(
                        new CombinationDescription(2, "regular", List.of("A", "K", "A")),
                        new CombinationDescription(5, "special", List.of("WLD", "SCT", "BON", "JPT", "MUL"))
                ));

        assertEquals(List.of(2, 5), List.copyOf(result.keySet()));
        assertEquals(
                List.of(StandardReelItem.A, StandardReelItem.K, StandardReelItem.A),
                result.get(2).getItems()
        );
        assertEquals(
                List.of(
                        StandardReelItem.WLD,
                        StandardReelItem.SCT,
                        StandardReelItem.BON,
                        StandardReelItem.JPT,
                        StandardReelItem.MUL
                ),
                result.get(5).getItems()
        );
    }

    /**
     * Verifies that characters of regular and special reel items are mapped.
     */
    @Test
    void mapsReelItemCharacters() {
        Map<Integer, Combination<StandardReelItem>> result =
                StandardCombinationListMapper.forCharacters().map(List.of(
                        new CombinationDescription(3, "regular", List.of("A", "K", "A")),
                        new CombinationDescription(7, "special", List.of("?", "@", "&", "!", "*"))
                ));

        assertEquals(List.of(3, 7), List.copyOf(result.keySet()));
        assertEquals(
                List.of(StandardReelItem.A, StandardReelItem.K, StandardReelItem.A),
                result.get(3).getItems()
        );
        assertEquals(
                List.of(
                        StandardReelItem.WLD,
                        StandardReelItem.SCT,
                        StandardReelItem.BON,
                        StandardReelItem.JPT,
                        StandardReelItem.MUL
                ),
                result.get(7).getItems()
        );
    }

    /**
     * Verifies that the supplied combination mapper creates list entries.
     */
    @Test
    void usesSuppliedCombinationMapper() {
        CombinationDescription description =
                new CombinationDescription(9, "custom", List.of("item"));
        Combination<StandardReelItem> combination =
                new Combination<>(9, "custom", StandardReelItem.A);

        CombinationMapper<StandardReelItem> combinationMapper = source -> {
            assertSame(description, source);
            return combination;
        };

        StandardCombinationListMapper<StandardReelItem> mapper =
                StandardCombinationListMapper.forCombinationMapper(combinationMapper);

        Map<Integer, Combination<StandardReelItem>> result = mapper.map(List.of(description));

        assertSame(combination, result.get(9));
    }

    /**
     * Verifies that a supplied item mapper is used to convert combination values.
     */
    @Test
    void usesSuppliedItemMapper() {
        ReelItemMapper<StandardReelItem> itemMapper = value -> switch (value) {
            case "first" -> StandardReelItem.A;
            case "second" -> StandardReelItem.K;
            default -> throw new IllegalArgumentException("Unknown item: " + value);
        };

        StandardCombinationListMapper<StandardReelItem> mapper =
                StandardCombinationListMapper.forItemMapper(itemMapper);

        Map<Integer, Combination<StandardReelItem>> result = mapper.map(List.of(
                new CombinationDescription(4, "custom", List.of("first", "second", "first"))
        ));

        assertEquals(
                List.of(StandardReelItem.A, StandardReelItem.K, StandardReelItem.A),
                result.get(4).getItems()
        );
    }

    /**
     * Verifies that the factory rejects a null item mapper argument.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullItemMapperArgument() {
        assertThrows(
                NullPointerException.class,
                () -> StandardCombinationListMapper.forItemMapper(null)
        );
    }

    /**
     * Verifies that the factory rejects a null combination mapper argument.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullCombinationMapperArgument() {
        assertThrows(
                NullPointerException.class,
                () -> StandardCombinationListMapper.forCombinationMapper(null)
        );
    }

    /**
     * Verifies that mapping rejects a null description list.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void rejectsNullDescriptionList() {
        StandardCombinationListMapper<StandardReelItem> mapper =
                StandardCombinationListMapper.forNames();

        assertThrows(NullPointerException.class, () -> mapper.map(null));
    }
}
