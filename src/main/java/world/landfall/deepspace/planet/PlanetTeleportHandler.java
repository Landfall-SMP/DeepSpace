package world.landfall.deepspace.planet;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.joml.Vector3f;
import org.slf4j.Logger;
import world.landfall.deepspace.Deepspace;

import java.util.Set;

@EventBusSubscriber(modid = Deepspace.MODID)
public class PlanetTeleportHandler {
    private static final Vec3[] directions = new Vec3[] {
            new Vec3(1, 0, 0),
            new Vec3(-1, 0, 0),
            new Vec3(0, 1, 0),
            new Vec3(0, -1, 0),
            new Vec3(1, 0, 1),
            new Vec3(1, 0, -1),
            new Vec3(1, 1, 1),
            new Vec3(-1, 1, 1),
            new Vec3(1, -1, 1),
            new Vec3(-1, -1, 1),
            new Vec3(1, 1, -1),
            new Vec3(-1, 1, -1),
            new Vec3(1, -1, -1),
            new Vec3(-1, -1, -1),
    };
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final RandomSource random = RandomSource.create();
    private static final float DISTANCE_FROM_PLANET_TO_TELEPORT_FROM = 3;
    // How high above the height limit do you need to go to teleport to deep space
    private static final int SPACE_DISTANCE_FROM_CEILING = 10;
    @SubscribeEvent
    public static void serverPlayerTick(PlayerTickEvent.Post event) {

        var player = event.getEntity();
        var level = player.level();
        if (level.getServer()==null) return;
        var planet = PlanetUtils.getPlayerPlanet(player);
        var closestPlanet = PlanetUtils.getNearestPlanet(player.position());
        var dimension = level.dimension().location();
        var height = level.getHeight();
        if (player.position().y > height + SPACE_DISTANCE_FROM_CEILING && planet != null) {
            LOGGER.info("Teleporting");
            var pos = getSafePlanetExitLocation(planet);
            player.teleportTo(
                    player.getServer().getLevel(
                            ResourceKey.create(Registries.DIMENSION,ResourceLocation.parse("deepspace:space"))
                    ),
                    pos.x,
                    pos.y,
                    pos.z,
                    Set.of(),
                    0,
                    0
            );
        } else if (dimension.equals(ResourceLocation.parse("deepspace:space")) && (closestPlanet.isPlayerTouching(player))) {
            player.teleportTo(
                    player.getServer().getLevel(
                            closestPlanet.getDimension()
                    ),
                    0, 300, 0,
                    Set.of(),
                    0, 0
            );
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING));
        }
    }

    private static Vec3 getSafePlanetExitLocation(Planet planet) {
        var center = planet.getCenter();
        var size = planet.getBoundingBoxMax().subtract(planet.getBoundingBoxMin());

        var maxSize = Math.max(
                Math.max(
                        size.x,size.y
                ),size.z
        ) / 2;

        var pos = directions[random.nextIntBetweenInclusive(0, directions.length-1)];
        return center.add(pos.scale(maxSize).scale(DISTANCE_FROM_PLANET_TO_TELEPORT_FROM));
        //return center;
    }
}
