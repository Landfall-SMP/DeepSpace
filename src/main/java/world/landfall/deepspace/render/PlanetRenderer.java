package world.landfall.deepspace.render;

import net.minecraft.resources.ResourceLocation;
import world.landfall.deepspace.planet.Planet;
import world.landfall.deepspace.planet.PlanetRegistry;
import world.landfall.deepspace.render.shapes.Sphere;

import java.util.Collection;
import java.util.HashMap;

public class PlanetRenderer {
    private static HashMap<Integer, Sphere> meshes;
    private static Collection<Planet> planets = PlanetRegistry.getAllPlanets();
    private static HashMap<Integer, ResourceLocation> textures;


}
