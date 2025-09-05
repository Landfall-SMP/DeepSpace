package world.landfall.deepspace.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.planet.Planet;
import world.landfall.deepspace.planet.PlanetRegistry;
import world.landfall.deepspace.planet.PlanetUtils;

import java.util.Collection;

/**
 * Command for interacting with the planet registry system.
 */
@EventBusSubscriber(modid = Deepspace.MODID)
public class PlanetCommand {
    
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        dispatcher.register(Commands.literal("planets")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("list")
                .executes(PlanetCommand::listPlanets))
            .then(Commands.literal("reload")
                .executes(PlanetCommand::reloadPlanets))
            .then(Commands.literal("sync")
                .executes(PlanetCommand::syncPlanets))
            .then(Commands.literal("info")
                .executes(PlanetCommand::showPlayerPlanetInfo))
        );
    }
    
    /**
     * Lists all registered planets.
     */
    private static int listPlanets(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Collection<Planet> planets = PlanetRegistry.getAllPlanets();
        
        if (planets.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No planets are currently registered."), false);
            return 0;
        }
        
        source.sendSuccess(() -> Component.literal("Registered planets (" + planets.size() + "):"), false);
        
        for (Planet planet : planets) {
            Vec3 min = planet.getBoundingBoxMin();
            Vec3 max = planet.getBoundingBoxMax();
            
            Component planetInfo = Component.literal(String.format(
                "- %s (%s): %s [%.1f,%.1f,%.1f] to [%.1f,%.1f,%.1f]",
                planet.getName(),
                planet.getId(),
                planet.getDimension().location(),
                min.x, min.y, min.z,
                max.x, max.y, max.z
            ));
            
            source.sendSuccess(() -> planetInfo, false);
        }
        
        return planets.size();
    }
    
    /**
     * Reloads planets from the configuration file.
     */
    private static int reloadPlanets(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        try {
            int oldCount = PlanetRegistry.getPlanetCount();
            PlanetRegistry.loadPlanets();
            int newCount = PlanetRegistry.getPlanetCount();
            
            // Sync to all players after reload
            PlanetRegistry.syncToAllPlayers();
            
            source.sendSuccess(() -> Component.literal(
                String.format("Reloaded planets: %d -> %d planets", oldCount, newCount)
            ), true);
            
            return newCount;
            
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to reload planets: " + e.getMessage()));
            return 0;
        }
    }
    
    /**
     * Manually syncs planets to all players.
     */
    private static int syncPlanets(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        try {
            PlanetRegistry.syncToAllPlayers();
            int planetCount = PlanetRegistry.getPlanetCount();
            
            source.sendSuccess(() -> Component.literal(
                String.format("Synchronized %d planets to all players", planetCount)
            ), true);
            
            return planetCount;
            
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to sync planets: " + e.getMessage()));
            return 0;
        }
    }
    
    /**
     * Shows information about the planet the player is currently in.
     */
    private static int showPlayerPlanetInfo(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        if (source.getEntity() == null) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }
        
        if (!(source.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }
        
        Planet currentPlanet = PlanetUtils.getPlayerPlanet(player);
        Vec3 playerPos = player.position();
        
        if (currentPlanet != null) {
            Vec3 min = currentPlanet.getBoundingBoxMin();
            Vec3 max = currentPlanet.getBoundingBoxMax();
            Vec3 center = currentPlanet.getCenter();
            double distanceToCenter = playerPos.distanceTo(center);
            
            source.sendSuccess(() -> Component.literal(String.format(
                "Current Planet: %s (%s)", currentPlanet.getName(), currentPlanet.getId()
            )), false);
            
            source.sendSuccess(() -> Component.literal(String.format(
                "Dimension: %s", currentPlanet.getDimension().location()
            )), false);
            
            source.sendSuccess(() -> Component.literal(String.format(
                "Bounding Box: [%.1f,%.1f,%.1f] to [%.1f,%.1f,%.1f]",
                min.x, min.y, min.z, max.x, max.y, max.z
            )), false);
            
            source.sendSuccess(() -> Component.literal(String.format(
                "Center: [%.1f,%.1f,%.1f] (%.1f blocks away)",
                center.x, center.y, center.z, distanceToCenter
            )), false);
            
            if (!currentPlanet.getDescription().isEmpty()) {
                source.sendSuccess(() -> Component.literal(
                    "Description: " + currentPlanet.getDescription()
                ), false);
            }
            
        } else {
            source.sendSuccess(() -> Component.literal(
                "You are not currently in a registered planet dimension"
            ), false);
            
            Planet nearestPlanet = PlanetUtils.getNearestPlanet(playerPos);
            if (nearestPlanet != null) {
                double distance = PlanetUtils.getDistanceToNearestPlanet(playerPos);
                source.sendSuccess(() -> Component.literal(String.format(
                    "Nearest planet: %s (%.1f blocks away)",
                    nearestPlanet.getName(), distance
                )), false);
            }
        }
        
        return 1;
    }
} 