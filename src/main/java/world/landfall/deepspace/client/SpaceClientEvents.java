package world.landfall.deepspace.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.ModKeyMappings;
import world.landfall.deepspace.dimension.SpaceDimensionEffects;
import world.landfall.deepspace.network.JetpackPacket;

@EventBusSubscriber(modid = Deepspace.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class SpaceClientEvents {
    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(Deepspace.MODID, "space"), new SpaceDimensionEffects());
    }
    @EventBusSubscriber(modid = Deepspace.MODID, value = Dist.CLIENT)
    public static class Tick {
        @SubscribeEvent
        public static void onClientTick(PlayerTickEvent.Pre event) {
            while (ModKeyMappings.ROCKET_FORWARD.get().consumeClick()) {
                PacketDistributor.sendToServer(
                        new JetpackPacket.RocketForward()
                );
            }
            while (ModKeyMappings.BEGIN_FLYING.get().consumeClick()) {
                PacketDistributor.sendToServer(
                        new JetpackPacket.BeginFlying()
                );
            }
        }
    }
} 