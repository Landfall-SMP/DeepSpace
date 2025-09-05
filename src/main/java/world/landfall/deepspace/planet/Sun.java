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

import java.util.Objects;

public class Sun {
    public static final Codec<Sun> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Vec3.CODEC.fieldOf("boundingBoxMin").forGetter(Sun::getBoundingBoxMin),
            Vec3.CODEC.fieldOf("boundingBoxMax").forGetter(Sun::getBoundingBoxMax),
            Codec.DOUBLE.fieldOf("hurtRadius").forGetter(Sun::getHurtRadius)
        ).apply(instance, Sun::new)
    );
    private final Vec3 boundingBoxMin;
    private final Vec3 boundingBoxMax;
    private final double hurtRadius;

    public Sun(@NotNull Vec3 _boundingBoxMin, @NotNull Vec3 _boundingBoxMax, double _hurtRadius) {
        boundingBoxMin = Objects.requireNonNull(_boundingBoxMin);
        boundingBoxMax = Objects.requireNonNull(_boundingBoxMax);
        hurtRadius = _hurtRadius;
        if (boundingBoxMin.x > boundingBoxMax.x || boundingBoxMin.y > boundingBoxMax.y || boundingBoxMin.z > boundingBoxMax.z) {
            throw new IllegalArgumentException("Invalid bounding box: minimum coordinates must be less than maximum coordinates");
        }
    }

    public Vec3 getBoundingBoxMin() {
        return boundingBoxMin;
    }

    public Vec3 getBoundingBoxMax() {
        return boundingBoxMax;
    }

    public double getHurtRadius() {
        return hurtRadius;
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
        buffer.writeDouble(boundingBoxMin.x);
        buffer.writeDouble(boundingBoxMin.y);
        buffer.writeDouble(boundingBoxMin.z);
        buffer.writeDouble(boundingBoxMax.x);
        buffer.writeDouble(boundingBoxMax.y);
        buffer.writeDouble(boundingBoxMax.z);
        buffer.writeDouble(hurtRadius);
    }
    /**
     * Reads a planet from a network buffer.
     *
     * @param buffer The buffer to read from
     * @return The planet instance
     */
    @NotNull
    public static Sun fromNetwork(@NotNull FriendlyByteBuf buffer) {
        Objects.requireNonNull(buffer, "Buffer cannot be null");
        Vec3 boundingBoxMin = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        Vec3 boundingBoxMax = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        double hurtRadius = buffer.readDouble();
        return new Sun(boundingBoxMin, boundingBoxMax, hurtRadius);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Sun sun = (Sun) obj;
        return Objects.equals(sun.boundingBoxMin, boundingBoxMin)
                && Objects.equals(sun.boundingBoxMax, boundingBoxMax)
                && sun.hurtRadius == hurtRadius;
    }
    @Override
    public int hashCode() {
        return Objects.hash(boundingBoxMin) ^ Objects.hash(boundingBoxMax) ^ Objects.hash(hurtRadius);
    }
    @Override
    public String toString() {
        return "Sun{" +
                "boundingBoxMin=" + boundingBoxMin +
                ", boundingBoxMax=" + boundingBoxMax +
                ", hurtRadius=" + hurtRadius +
                '}';
    }


}
