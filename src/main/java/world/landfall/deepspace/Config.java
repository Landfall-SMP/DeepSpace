package world.landfall.deepspace;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import world.landfall.deepspace.integration.DeepspaceOptions;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = Deepspace.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.EnumValue<DeepspaceOptions.Detail> PLANET_DECORATION_DETAIL = BUILDER.defineEnum("decorationDetail", DeepspaceOptions.Detail.BASIC);
    public static final ModConfigSpec.EnumValue<DeepspaceOptions.Detail> PLANET_SHADING_DETAIL = BUILDER.defineEnum("shadingDetail", DeepspaceOptions.Detail.BASIC);
    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (!SPEC.isEmpty())
            ModOptions.init(PLANET_DECORATION_DETAIL.get(), PLANET_SHADING_DETAIL.get());
        else
            ModOptions.init();

    }
}
