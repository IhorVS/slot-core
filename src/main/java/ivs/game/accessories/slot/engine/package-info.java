/**
 * Coordinates complete slot spins using configured components and a matcher factory.
 *
 * <p>{@link ivs.game.accessories.slot.engine.SlotEngineConfig} contains the reel bank,
 * field lines, wild substitutes, combinations, and string prize identifiers.
 * Reels may define different visible window sizes. Configuration components
 * and collections are retained by reference and must remain unchanged during use.</p>
 *
 * <p>{@link ivs.game.accessories.slot.engine.StandardSlotEngine} accepts one explicit
 * physical position per reel, builds the field, and supplies the configuration
 * and field to {@link ivs.game.accessories.slot.engine.CombinationMatcherFactory}.
 * The factory chooses matcher types, combination subsets, and match policies.
 * Matchers are executed sequentially in the order returned by the factory.</p>
 *
 * <p>The engine validates the configuration on construction, checks line rows
 * against each generated field, and validates the factory's matcher list before
 * execution. An empty matcher list is allowed. Random position generation
 * and application-specific interpretation of prize identifiers are external.</p>
 *
 * <p>{@link ivs.game.accessories.slot.engine.SpinResult} contains physical reel
 * positions, the generated field, detected matches, and resolved prize identifiers.
 * Results returned by the engine have unmodifiable match and prize collections,
 * including nested prize sets. Reel positions are copied on construction and access.
 * Repeated matches remain separate and share one prize entry per combination.</p>
 */
package ivs.game.accessories.slot.engine;
