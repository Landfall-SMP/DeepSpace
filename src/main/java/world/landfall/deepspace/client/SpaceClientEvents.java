package world.landfall.deepspace.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.dimension.SpaceDimensionEffects;

@EventBusSubscriber(modid = Deepspace.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class SpaceClientEvents {
    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(Deepspace.MODID, "space"), new SpaceDimensionEffects());
    }
} 