package world.landfall.deepspace;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.DimensionSpecialEffectsManager;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import world.landfall.deepspace.dimension.SpaceDimensionEffects;
import world.landfall.deepspace.dimension.SpaceDimensionType;

@EventBusSubscriber(modid = Deepspace.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModDimensionEffects {
    public static DimensionSpecialEffects DEEPSPACE_EFFECTS = new SpaceDimensionEffects();
    @SubscribeEvent
    public static void onRegisterDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(Deepspace.MODID, "space"), DEEPSPACE_EFFECTS);
    }
}