package world.landfall.deepspace.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import world.landfall.deepspace.Deepspace;

@EventBusSubscriber(modid = Deepspace.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class SpaceClientEvents {
    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ResourceLocation.tryParse(Deepspace.MODID + ":space"), new DimensionSpecialEffects(
            Float.NaN, // cloudHeight - NaN means no clouds hopefully
            false,  // hasGround
            DimensionSpecialEffects.SkyType.NONE, // skyType
            false,  // forceBrightLightmap
            false   // constantAmbientLight
        ) {
            @Override
            public Vec3 getBrightnessDependentFogColor(Vec3 color, float brightness) {
                return color.multiply(0.15f, 0.15f, 0.15f); // Dark fog
            }

            @Override
            public boolean isFoggyAt(int x, int y) {
                return false;
            }
        });
    }
} 