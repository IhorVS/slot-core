/**
 * Provides mappings from external string values to reel items, reels, and reel
 * banks.
 *
 * <p>Reel mapping components are assembled from the smallest mapping operation
 * to the complete reel bank mapper:</p>
 *
 * <pre>
 * ReelItemMapper
 *        |
 *        v
 * ReelItemListMapper
 *        |
 *        v
 * ReelMapper
 *        |
 *        v
 * ReelBankMapper
 * </pre>
 *
 * <h2>Creating a standard reel bank mapper</h2>
 *
 * <p>{@code StandardReelBankMapper} assembles the standard reel mapping
 * components internally. The application only needs to provide a mapper for
 * its reel item identifiers.</p>
 *
 * <p>The following example creates a mapper for standard reel items represented
 * by their enum constant names:</p>
 *
 * <pre>{@code
 * ReelBankMapper<StandardReelItem> reelBankMapper =
 *         new StandardReelBankMapper<>(
 *                 new ReelItemNameMapper()
 *         );
 * }</pre>
 *
 * <p>The resulting mapper creates {@code StandardReel} instances and places
 * them into a {@code StandardReelBank}.</p>
 *
 * <h2>Mapping reels with individual visible sizes</h2>
 *
 * <p>The standard mapper can create reels with different item sequences and
 * visible sizes:</p>
 *
 * <pre>{@code
 * ReelBank<StandardReelItem> reelBank = reelBankMapper.map(
 *         List.of(
 *                 new ReelDescription(List.of("A", "K", "WLD", "SCT"), 3),
 *                 new ReelDescription(List.of("K", "A", "MUL"), 2)
 *         )
 * );
 * }</pre>
 *
 * <h2>Mapping reels with a common visible size</h2>
 *
 * <p>For a rectangular field, the same visible size can be assigned to every
 * reel without creating individual {@link ReelDescription} objects:</p>
 *
 * <pre>{@code
 * ReelBank<StandardReelItem> reelBank = reelBankMapper.map(
 *         List.of(
 *                 List.of("A", "K", "WLD", "SCT"),
 *                 List.of("K", "A", "MUL", "WLD"),
 *                 List.of("Q", "SCT", "A", "K")
 *         ),
 *         3
 * );
 * }</pre>
 *
 * <p>The overload creates one {@link ReelDescription} for every item list and
 * assigns the supplied visible size to all reels.</p>
 *
 * <h2>Mapping character identifiers</h2>
 *
 * <p>To map single-character identifiers, create the standard mapper with a
 * {@code ReelItemCharacterMapper}:</p>
 *
 * <pre>{@code
 * ReelBankMapper<StandardReelItem> reelBankMapper =
 *         new StandardReelBankMapper<>(
 *                 new ReelItemCharacterMapper()
 *         );
 *
 * ReelBank<StandardReelItem> reelBank = reelBankMapper.map(
 *         List.of(
 *                 List.of("A", "K", "?", "@"),
 *                 List.of("K", "A", "@", "?")
 *         ),
 *         3
 * );
 * }</pre>
 *
 * <h2>Custom component assembly</h2>
 *
 * <p>Applications that need to replace an individual mapping component can
 * assemble the mapping chain explicitly:</p>
 *
 * <pre>{@code
 * ReelItemMapper<StandardReelItem> itemMapper =
 *         new ReelItemNameMapper();
 *
 * ReelItemListMapper<StandardReelItem> itemListMapper =
 *         new DefaultReelItemListMapper<>(itemMapper);
 *
 * ReelMapper<StandardReelItem> reelMapper =
 *         new DefaultReelMapper<>(
 *                 itemListMapper,
 *                 StandardReel::new
 *         );
 *
 * ReelBankMapper<StandardReelItem> reelBankMapper =
 *         new DefaultReelBankMapper<>(
 *                 reelMapper,
 *                 StandardReelBank::new
 *         );
 * }</pre>
 *
 * <p>A custom reel mapper can also be combined with the standard reel bank
 * implementation:</p>
 *
 * <pre>{@code
 * ReelBankMapper<GameReelItem> reelBankMapper =
 *         new StandardReelBankMapper<>(customReelMapper);
 * }</pre>
 *
 * <p>Mapper implementations do not prescribe a particular lifecycle. A calling
 * application may create mapper instances locally, store them in fields, or
 * reuse them as application-level constants.</p>
 */
package ivs.game.accessories.slot.mapping.reel;
