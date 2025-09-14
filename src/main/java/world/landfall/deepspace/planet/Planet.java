package world.landfall.deepspace.planet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a planet in the Deep Space dimension with its associated dimension and bounding box.
 */
public class Planet {
    public record PlanetDecoration(Type type, float scale, int color) {
        public enum Type {
            ATMOSPHERE("atmosphere"),
            RINGS("rings"),
            ASTEROIDS("asteroids");
            public final String type;
            private Type(String type) {
                this.type = type;
            }
        }
        public static final Codec<PlanetDecoration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("type").forGetter(decoration -> decoration.type.type),
                Codec.FLOAT.fieldOf("scale").forGetter(PlanetDecoration::scale),
                Codec.INT.fieldOf("color").forGetter(PlanetDecoration::color)
        ).apply(instance, (type, scale, color) -> new PlanetDecoration(Type.valueOf(type), scale, color)));
        public static void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull PlanetDecoration decoration) {
            buffer.writeInt(decoration.type.ordinal());
            buffer.writeFloat(decoration.scale);
            buffer.writeInt(decoration.color);
        }
        public static PlanetDecoration fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return new PlanetDecoration(Type.values()[buffer.readInt()], buffer.readFloat(), buffer.readInt());
        }

    }
    public static final Codec<Planet> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("id").forGetter(Planet::getId),
            Codec.STRING.fieldOf("name").forGetter(Planet::getName),
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(planet -> planet.getDimension().location()),
            Vec3.CODEC.fieldOf("boundingBoxMin").forGetter(Planet::getBoundingBoxMin),
            Vec3.CODEC.fieldOf("boundingBoxMax").forGetter(Planet::getBoundingBoxMax),
            Codec.list(PlanetDecoration.CODEC).optionalFieldOf("decorations").forGetter(Planet::getDecorations),
            Codec.STRING.optionalFieldOf("description", "").forGetter(Planet::getDescription)
        ).apply(instance, (id, name, dimensionLocation, min, max, decorations, description) ->
            new Planet(id, name, ResourceKey.create(Registries.DIMENSION, dimensionLocation), min, max, decorations.get(), description)
        )
    );


    private final String id;
    private final String name;
    private final ResourceKey<Level> dimension;
    private final Vec3 boundingBoxMin;
    private final Vec3 boundingBoxMax;
    private final String description;
    private final Collection<PlanetDecoration> decorations;

    /**
     * Creates a new Planet instance.
     *
     * @param id The unique identifier for this planet
     * @param name The display name of the planet
     * @param dimension The dimension this planet represents
     * @param boundingBoxMin The minimum coordinates of the planet's bounding box in Deep Space
     * @param boundingBoxMax The maximum coordinates of the planet's bounding box in Deep Space
     * @param description Optional description of the planet
     */
    public Planet(@NotNull String id, @NotNull String name, @NotNull ResourceKey<Level> dimension,
                  @NotNull Vec3 boundingBoxMin, @NotNull Vec3 boundingBoxMax, @Nullable Collection<PlanetDecoration> decorations, @Nullable String description) {
        this.id = Objects.requireNonNull(id, "Planet ID cannot be null");
        this.name = Objects.requireNonNull(name, "Planet name cannot be null");
        this.dimension = Objects.requireNonNull(dimension, "Planet dimension cannot be null");
        this.boundingBoxMin = Objects.requireNonNull(boundingBoxMin, "Bounding box minimum cannot be null");
        this.boundingBoxMax = Objects.requireNonNull(boundingBoxMax, "Bounding box maximum cannot be null");
        this.description = description != null ? description : "";
        this.decorations = decorations != null ? decorations : List.of();

        // Validate bounding box
        if (boundingBoxMin.x > boundingBoxMax.x || boundingBoxMin.y > boundingBoxMax.y || boundingBoxMin.z > boundingBoxMax.z) {
            throw new IllegalArgumentException("Invalid bounding box: minimum coordinates must be less than maximum coordinates");
        }
    }

    /**
     * Creates a new Planet instance without description.
     */
    public Planet(@NotNull String id, @NotNull String name, @NotNull ResourceKey<Level> dimension,
                  @NotNull Vec3 boundingBoxMin, @NotNull Vec3 boundingBoxMax, @NotNull Collection<PlanetDecoration> decorations) {
        this(id, name, dimension, boundingBoxMin, boundingBoxMax, decorations, "");
    }

    /**
     * @return The unique identifier for this planet
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * @return The display name of the planet
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * @return The dimension this planet represents
     */
    @NotNull
    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    /**
     * @return The minimum coordinates of the planet's bounding box in Deep Space
     */
    @NotNull
    public Vec3 getBoundingBoxMin() {
        return boundingBoxMin;
    }

    /**
     * @return The maximum coordinates of the planet's bounding box in Deep Space
     */
    @NotNull
    public Vec3 getBoundingBoxMax() {
        return boundingBoxMax;
    }

    @NotNull
    public Optional<List<PlanetDecoration>> getDecorations() {
        return Optional.of(decorations.stream().toList());
    }

    /**
     * @return The description of the planet
     */
    @NotNull
    public String getDescription() {
        return description;
    }

    /**
     * Checks if a given position is within this planet's bounding box.
     *
     * @param position The position to check
     * @return true if the position is within the bounding box, false otherwise
     */
    public boolean isWithinBounds(@NotNull Vec3 position) {
        Objects.requireNonNull(position, "Position cannot be null");
        return position.x >= boundingBoxMin.x && position.x <= boundingBoxMax.x &&
               position.y >= boundingBoxMin.y && position.y <= boundingBoxMax.y &&
               position.z >= boundingBoxMin.z && position.z <= boundingBoxMax.z;
    }

    /**
     * Checks if a player is touching the planet
     *
     * @param player The player to check
     * @return true if the player is colliding with this planet, false otherwise
     */
    public boolean isPlayerTouching(@NotNull Player player) {
        //return isWithinBounds(player.position()) || isWithinBounds(player.position().add(0, 2, 0));
        Objects.requireNonNull(player);
        var position = player.position();
        return position.x >= boundingBoxMin.x - .5 && position.x <= boundingBoxMax.x + .5 &&
               position.y >= boundingBoxMin.y - 2 && position.y <= boundingBoxMax.y &&
               position.z >= boundingBoxMin.z - .5 && position.z <= boundingBoxMax.z + .5;
    }

    /**
     * Gets the center point of the planet's bounding box.
     *
     * @return The center coordinates
     */
    @NotNull
    public Vec3 getCenter() {
        return new Vec3(
            (boundingBoxMin.x + boundingBoxMax.x) / 2.0,
            (boundingBoxMin.y + boundingBoxMax.y) / 2.0,
            (boundingBoxMin.z + boundingBoxMax.z) / 2.0
        );
    }

    /**
     * Writes this planet to a network buffer for client synchronization.
     *
     * @param buffer The buffer to write to
     */
    public void toNetwork(@NotNull FriendlyByteBuf buffer) {
        Objects.requireNonNull(buffer, "Buffer cannot be null");
        buffer.writeUtf(id);
        buffer.writeUtf(name);
        buffer.writeResourceLocation(dimension.location());
        buffer.writeDouble(boundingBoxMin.x);
        buffer.writeDouble(boundingBoxMin.y);
        buffer.writeDouble(boundingBoxMin.z);
        buffer.writeDouble(boundingBoxMax.x);
        buffer.writeDouble(boundingBoxMax.y);
        buffer.writeDouble(boundingBoxMax.z);
        buffer.writeCollection(decorations, PlanetDecoration::toNetwork);
        buffer.writeUtf(description);
    }

    /**
     * Reads a planet from a network buffer.
     *
     * @param buffer The buffer to read from
     * @return The planet instance
     */
    @NotNull
    public static Planet fromNetwork(@NotNull FriendlyByteBuf buffer) {
        Objects.requireNonNull(buffer, "Buffer cannot be null");
        String id = buffer.readUtf();
        String name = buffer.readUtf();
        ResourceLocation dimensionLocation = buffer.readResourceLocation();
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, dimensionLocation);
        Vec3 boundingBoxMin = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        Vec3 boundingBoxMax = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        Collection<PlanetDecoration> decorations = buffer.readList(PlanetDecoration::fromNetwork);
        String description = buffer.readUtf();
        return new Planet(id, name, dimension, boundingBoxMin, boundingBoxMax, decorations, description);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Planet planet = (Planet) obj;
        return Objects.equals(id, planet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Planet{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", dimension=" + dimension +
               ", boundingBoxMin=" + boundingBoxMin +
               ", boundingBoxMax=" + boundingBoxMax +
               ", description='" + description + '\'' +
               '}';
    }
} 