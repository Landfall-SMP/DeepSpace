package world.landfall.deepspace.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import world.landfall.deepspace.Deepspace;

import java.util.Objects;

/**
 * This class is responsible for the dimension's registry entries.
 */
public final class SpaceDimensionType {
    private SpaceDimensionType() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * unique identifier for the dimension in the game's registries.
     */
    public static final ResourceLocation SPACE_LOCATION = Objects.requireNonNull(
        ResourceLocation.tryParse(Deepspace.MODID + ":space"),
        "Failed to create ResourceLocation for space dimension"
    );

    /**
     * Used to reference the dimension in teleport commands etc
     */
    public static final ResourceKey<Level> SPACE_DIMENSION = ResourceKey.create(Registries.DIMENSION, SPACE_LOCATION);
    public static final ResourceKey<DimensionType> SPACE_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, SPACE_LOCATION);

    /**
     * Handles the registration of the dimension type
     */
    private static final DeferredRegister<DimensionType> DIMENSION_TYPES = DeferredRegister.create(Registries.DIMENSION_TYPE, Deepspace.MODID);

    /**
     * Registers the dimension type with the mod event bus.
     * This must be called during mod initialization.
     *
     * @param modEventBus The mod's event bus to register the dimension type on
     * @throws NullPointerException if modEventBus is null
     */
    public static void register(@NotNull IEventBus modEventBus) {
        Objects.requireNonNull(modEventBus, "modEventBus cannot be null");
        DIMENSION_TYPES.register(modEventBus);
    }
} 