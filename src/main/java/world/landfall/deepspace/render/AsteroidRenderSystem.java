package world.landfall.deepspace.render;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.quasar.particle.ParticleEmitter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.planet.Planet;
import world.landfall.deepspace.planet.PlanetRegistry;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT)
public class AsteroidRenderSystem {

    public static final ResourceLocation ASTEROID_EMITTER_ID = Deepspace.path("asteroid");
    public static List<ParticleEmitter> ASTEROID_EMITTERS = new ArrayList<>();


    @SubscribeEvent
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ClientLevel level)) return;

        resetParticles(player, level);
    }
    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ClientLevel level)) return;

        resetParticles(player, level);
    }

    public static void resetParticles(Player player, ClientLevel level) {
        if (!ASTEROID_EMITTERS.isEmpty()) {
            ASTEROID_EMITTERS.forEach(
                    ParticleEmitter::remove
            );
            ASTEROID_EMITTERS.clear();
        }
        var dim = level.dimension().location();
        if (!dim.equals(Deepspace.path("space")))
            return;
        var particleSystem = VeilRenderSystem.renderer().getParticleManager();
        for (var planet : PlanetRegistry.getAllPlanets()) {
            var decorations = planet.getDecorations();
            if (decorations.isEmpty())
                return;
            decorations.stream().filter(d -> d.type().equals(Planet.PlanetDecoration.ASTEROIDS)).findFirst().ifPresent(decoration -> {
                var emitter = particleSystem.createEmitter(ASTEROID_EMITTER_ID);
                particleSystem.addParticleSystem(
                        emitter
                );
                ASTEROID_EMITTERS.add(emitter);
            });
        }
    }
}
