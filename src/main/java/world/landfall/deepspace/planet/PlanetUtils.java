package world.landfall.deepspace.planet;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Utility class providing helper methods for planet operations and queries.
 */
public final class PlanetUtils {
    
    private PlanetUtils() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Gets the planet that a player is currently in based on their dimension.
     *
     * @param player The player to check
     * @return The planet the player is in, or null if not found
     */
    @Nullable
    public static Planet getPlayerPlanet(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        return PlanetRegistry.getPlanetByDimension(player.level().dimension());
    }
    
    /**
     * Gets the planet that contains the given position in Deep Space.
     *
     * @param position The position to check
     * @return The first planet containing the position, or null if none found
     */
    @Nullable
    public static Planet getPlanetAtPosition(@NotNull Vec3 position) {
        Objects.requireNonNull(position, "Position cannot be null");
        List<Planet> planets = PlanetRegistry.getPlanetsAtPosition(position);
        return planets.isEmpty() ? null : planets.get(0);
    }
    
    /**
     * Gets all planets that contain the given position in Deep Space.
     *
     * @param position The position to check
     * @return A list of planets containing the position
     */
    @NotNull
    public static List<Planet> getAllPlanetsAtPosition(@NotNull Vec3 position) {
        Objects.requireNonNull(position, "Position cannot be null");
        return PlanetRegistry.getPlanetsAtPosition(position);
    }
    
    /**
     * Checks if a position is within any planet's bounding box.
     *
     * @param position The position to check
     * @return true if the position is within any planet's bounds, false otherwise
     */
    public static boolean isPositionInAnyPlanet(@NotNull Vec3 position) {
        Objects.requireNonNull(position, "Position cannot be null");
        return !PlanetRegistry.getPlanetsAtPosition(position).isEmpty();
    }
    
    /**
     * Gets the distance from a position to the center of the nearest planet.
     *
     * @param position The position to check from
     * @return The distance to the nearest planet center, or Double.MAX_VALUE if no planets exist
     */
    public static double getDistanceToNearestPlanet(@NotNull Vec3 position) {
        Objects.requireNonNull(position, "Position cannot be null");
        
        return PlanetRegistry.getAllPlanets().stream()
            .mapToDouble(planet -> position.distanceTo(planet.getCenter()))
            .min()
            .orElse(Double.MAX_VALUE);
    }
    
    /**
     * Gets the nearest planet to a given position.
     *
     * @param position The position to check from
     * @return The nearest planet, or null if no planets exist
     */
    @Nullable
    public static Planet getNearestPlanet(@NotNull Vec3 position) {
        Objects.requireNonNull(position, "Position cannot be null");
        
        return PlanetRegistry.getAllPlanets().stream()
            .min((p1, p2) -> Double.compare(
                position.distanceTo(p1.getCenter()),
                position.distanceTo(p2.getCenter())
            ))
            .orElse(null);
    }
    
    /**
     * Gets the planet associated with a specific dimension.
     *
     * @param dimension The dimension to look up
     * @return An Optional containing the planet if found, empty otherwise
     */
    @NotNull
    public static Optional<Planet> getPlanetForDimension(@NotNull ResourceKey<Level> dimension) {
        Objects.requireNonNull(dimension, "Dimension cannot be null");
        return Optional.ofNullable(PlanetRegistry.getPlanetByDimension(dimension));
    }
    
    /**
     * Checks if a dimension has an associated planet.
     *
     * @param dimension The dimension to check
     * @return true if the dimension has an associated planet, false otherwise
     */
    public static boolean hasPlanetForDimension(@NotNull ResourceKey<Level> dimension) {
        Objects.requireNonNull(dimension, "Dimension cannot be null");
        return PlanetRegistry.getPlanetByDimension(dimension) != null;
    }
    
    /**
     * Gets the bounding box volume of a planet.
     *
     * @param planet The planet to calculate volume for
     * @return The volume of the planet's bounding box
     */
    public static double getPlanetVolume(@NotNull Planet planet) {
        Objects.requireNonNull(planet, "Planet cannot be null");
        
        Vec3 min = planet.getBoundingBoxMin();
        Vec3 max = planet.getBoundingBoxMax();
        
        double width = max.x - min.x;
        double height = max.y - min.y;
        double depth = max.z - min.z;
        
        return width * height * depth;
    }
    
    /**
     * Checks if two planets have overlapping bounding boxes.
     *
     * @param planet1 The first planet
     * @param planet2 The second planet
     * @return true if the planets' bounding boxes overlap, false otherwise
     */
    public static boolean doPlanetsOverlap(@NotNull Planet planet1, @NotNull Planet planet2) {
        Objects.requireNonNull(planet1, "Planet1 cannot be null");
        Objects.requireNonNull(planet2, "Planet2 cannot be null");
        
        Vec3 min1 = planet1.getBoundingBoxMin();
        Vec3 max1 = planet1.getBoundingBoxMax();
        Vec3 min2 = planet2.getBoundingBoxMin();
        Vec3 max2 = planet2.getBoundingBoxMax();
        
        return !(max1.x < min2.x || max2.x < min1.x ||
                 max1.y < min2.y || max2.y < min1.y ||
                 max1.z < min2.z || max2.z < min1.z);
    }
    
    /**
     * Validates that a planet's configuration is valid.
     *
     * @param planet The planet to validate
     * @return true if the planet is valid, false otherwise
     */
    public static boolean isValidPlanet(@NotNull Planet planet) {
        Objects.requireNonNull(planet, "Planet cannot be null");
        
        try {
            // Check that ID and name are not empty
            if (planet.getId().trim().isEmpty() || planet.getName().trim().isEmpty()) {
                return false;
            }
            
            // Check that bounding box is valid (constructor already validates this, but double-check)
            Vec3 min = planet.getBoundingBoxMin();
            Vec3 max = planet.getBoundingBoxMax();
            
            return min.x < max.x && min.y < max.y && min.z < max.z;
            
        } catch (Exception e) {
            return false;
        }
    }
} 