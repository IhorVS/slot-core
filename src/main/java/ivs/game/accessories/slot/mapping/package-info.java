/**
 * Provides format-independent mappings from external values to slot-core
 * components.
 *
 * <p>An application may obtain source values from YAML, JSON, a database,
 * environment variables, or construct them programmatically before passing
 * them to a mapper.</p>
 *
 * <p>The common mapping flow is:</p>
 *
 * <pre>
 * external configuration
 *          |
 *          v
 * deserialized values
 *          |
 *          v
 * slot-core mapper
 *          |
 *          v
 * slot-core components
 * </pre>
 *
 * <p>Mapping APIs are grouped by the type of slot component they create:</p>
 *
 * <ul>
 *     <li>{@code mapping.reel} maps reel items, reels, and reel banks;</li>
 *     <li>{@code mapping.line} maps field lines;</li>
 *     <li>{@code mapping.combination} maps combinations.</li>
 * </ul>
 *
 * <p>Mappers do not read configuration resources and do not depend on YAML,
 * JSON, or any other serialization library. Reading files, extracting
 * properties, and validating format-specific structures remain the
 * responsibility of the calling application.</p>
 */
package ivs.game.accessories.slot.mapping;
