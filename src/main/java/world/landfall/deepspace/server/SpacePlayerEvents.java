package world.landfall.deepspace.server;

import foundry.veil.api.quasar.particle.ParticleSystemManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.ModAttatchments;
import world.landfall.deepspace.ModItems;
import world.landfall.deepspace.item.JetpackItem;

public class SpacePlayerEvents {
    @EventBusSubscriber(modid = Deepspace.MODID)
    public static class Tick {
        @SubscribeEvent
        public static void playerTick(PlayerTickEvent.Post event) {

            Player player = event.getEntity();
            var dimension = player.level().dimension().location();
            var noGravity = dimension.equals(ResourceLocation.parse("deepspace:space"));
            player.setNoGravity(noGravity);
            //player.setIgnoreFallDamageFromCurrentImpulse(noGravity);
            if (noGravity) {
                player.setDeltaMovement(player.getDeltaMovement().add(new Vec3(0, -.01f, 0)));

            }
            var jetpackSlot = player.getItemBySlot(EquipmentSlot.CHEST);
            var hasJetpack = jetpackSlot.is(ModItems.JETPACK_ITEM.get());
            if (!player.hasData(ModAttatchments.IS_FLYING_JETPACK)) return;
            var isFlying = player.getData(ModAttatchments.IS_FLYING_JETPACK.get());

//        var isEquipped = stack.getEquipmentSlot() == EquipmentSlot.CHEST;
//        if (!isEquipped) {
//            return;
//        }
            //player.setSwimming(isFlying);

            if (!hasJetpack) {
                player.setData(ModAttatchments.IS_FLYING_JETPACK, false);
                player.setData(ModAttatchments.IS_ROCKETING_FORWARD, false);
                player.setData(ModAttatchments.JETPACK_VELOCITY, new Vector3f());
                return;
            }
            var jetpackComponent = jetpackSlot.getComponents().get(JetpackItem.JetpackComponent.SUPPLIER.get());
            if (isFlying) {
                player.setPose(Pose.FALL_FLYING);
                if (player.isShiftKeyDown() || player.onGround() || (jetpackComponent != null && !jetpackComponent.canFly())) {
                    player.setData(ModAttatchments.IS_FLYING_JETPACK, false);
                    player.setData(ModAttatchments.IS_ROCKETING_FORWARD, false);
                    player.setData(ModAttatchments.JETPACK_VELOCITY, new Vector3f());
                    return;
                }
                var lookAngle = player.getLookAngle();
                var deltas = player.getDeltaMovement();
                Vector3f storedVelocity = player.getData(ModAttatchments.JETPACK_VELOCITY);

                var keyPressed = player.getData(ModAttatchments.IS_ROCKETING_FORWARD);
                var rocketVelocity = lookAngle.toVector3f().mul(.04f);
                if (!noGravity)
                    rocketVelocity.add(new Vector3f(0f, .04f, 0f)).mul(.1f, 2f, .1f);
                Vector3f newVelocity = new Vector3f(storedVelocity);
                if (keyPressed) {
                    newVelocity.add(rocketVelocity);
                    var random = player.level().getRandom();
                    for (int i = 0; i < 4; i++) {
                        var offset = new Vector3f(random.nextFloat() * 2 - 1,random.nextFloat() * 2 - 1,random.nextFloat() * 2 - 1).mul(.4f);
                        var oppositeForce = new Vector3f(newVelocity).normalize();
                        offset.sub(oppositeForce.mul(2));
                        player.level().addParticle(ParticleTypes.FLAME,
                                player.getX() + offset.x, player.getY() + offset.y, player.getZ() + offset.z,
                                oppositeForce.x * -.1, oppositeForce.y * -.1, oppositeForce.z * -.1
                        );

                    }
                }
                newVelocity.mul(.99f);
                if (newVelocity.length() > 2) newVelocity.mul(.9f);
                if (!keyPressed)
                    newVelocity.add(new Vector3f(0, -.01f, 0));
                //player.setPos(player.getPosition(0).add(new Vec3(newVelocity.x, newVelocity.y, newVelocity.z)));
                player.setData(ModAttatchments.JETPACK_VELOCITY, newVelocity);
                player.setDeltaMovement(new Vec3(newVelocity.x, newVelocity.y, newVelocity.z));
                //player.gameEvent(GameEvent.ELYTRA_GLIDE);
                var rot = angle(newVelocity.x, newVelocity.z);
                rot = Float.isNaN(rot) ? 0 : rot;
                player.setYBodyRot(rot);
                var headRot = player.getYHeadRot() - rot;
                var headAngle = (Math.abs(headRot) *
                        (headRot < 0 ? -1 : 1) +
                        (headRot < 0 ? 360+180 : 180))
                        % 360 - 180;
//                if (Math.abs(headAngle) + rot > 180)
//                    player.setYHeadRot((headAngle < 0 ? -180 : 180)-rot);
                if (Float.isNaN(headAngle))
                    headAngle = -rot;
                var maxHeadTurn = 70;
                player.setYHeadRot(Math.clamp(headAngle, -maxHeadTurn, maxHeadTurn) * (1 - Math.abs((float)player.getLookAngle().y)) + rot);
            } else {
                var deltas = player.getDeltaMovement();
                player.setData(ModAttatchments.JETPACK_VELOCITY, deltas.toVector3f());
            }
        }
        private static float angle(float x, float y) {
            var rot = (float)Math.atan(y/x) / ((float)Math.PI*2) * 360;
            if (x < 0)
                rot += 180;
            else if (y < 0)
                rot += 360;
            return rot - 90;
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
