package world.landfall.deepspace.server;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.ModAttatchments;

public class SpaceServerEvents {
    @EventBusSubscriber(modid = Deepspace.MODID)
    public static class Tick {
        @SubscribeEvent
        public static void playerTick(PlayerTickEvent.Pre event) {

            var player = event.getEntity();
            var dimension = player.level().dimension().location();
            var noGravity = dimension.equals(ResourceLocation.parse("deepspace:space"));
            player.setNoGravity(noGravity);
            //player.setIgnoreFallDamageFromCurrentImpulse(noGravity);
            if (noGravity) {
                player.setDeltaMovement(player.getDeltaMovement().add(new Vec3(0, -.01f, 0)));

            };
        }
        @SubscribeEvent
        public static void fallEvent(LivingFallEvent event) {
            if (event.getEntity() instanceof Player player)
                event.setDistance(player.level().dimension().location().equals(ResourceLocation.parse("deepspace:space")) ? 0f : event.getDistance());
        }
        @SubscribeEvent
        public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            var player = event.getEntity();
            player.setData(ModAttatchments.IS_FLYING_JETPACK, false);
            player.setData(ModAttatchments.IS_ROCKETING_FORWARD, false);
        }
    }
}
