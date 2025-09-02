package world.landfall.deepspace.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.planet.Planet;
import world.landfall.deepspace.planet.PlanetRegistry;
import world.landfall.deepspace.render.shapes.Cube;
import world.landfall.deepspace.render.shapes.Sphere;

import java.util.Collection;
import java.util.HashMap;
import org.slf4j.Logger;

public class PlanetRenderer {

    private static final Logger logger = LogUtils.getLogger();

    private static final HashMap<String, Cube> MESHES = new HashMap<>();
    private static final HashMap<String, ResourceLocation> TEXTURES = new HashMap<>();
    private static final ResourceLocation PLANET_SHADER = Veil.veilPath("planet");
    private static final RenderStateShard.ShaderStateShard PLANET_RENDER_TYPE = new RenderStateShard.ShaderStateShard(() -> {
        ShaderProgram shader = VeilRenderSystem.setShader(PLANET_SHADER);
        return VeilRenderBridge.toShaderInstance(shader);
    });

    private static RenderType planetRenderType() {
        var renderType = RenderType.CompositeState.builder()
                .setShaderState(PLANET_RENDER_TYPE)
                .createCompositeState(true);
        return RenderType.create(
                "planet",
                DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.TRIANGLES,
                256, true, false,
                renderType
        );
    }

    public static void init() {
        for (var x : PlanetRegistry.getAllPlanets()) {
            MESHES.put(x.getId(),new Cube(x.getBoundingBoxMin().toVector3f(), x.getBoundingBoxMax().toVector3f()));
            logger.info("Made mesh for planet {}",x.getName());
        }
        logger.info("init");
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage(
                (stage,
                 levelRenderer,
                 bufferSource,
                 matrixStack,
                 frustumMatrix,
                 projectionMatrix,
                 renderTick,
                 partialTicks,
                 camera,
                 frustum
        ) -> {
            BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
            var instance = Minecraft.getInstance();
            if (!instance.level.dimension().location().equals(ResourceLocation.fromNamespaceAndPath(Deepspace.MODID,"space")))
                return;
            if (stage.equals(VeilRenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)) {
                RenderType renderType = planetRenderType();
                var texture = ResourceLocation.parse("deepspace:textures/sarrion.png");
                RenderSystem.setShaderTexture(0, texture);
                var poseStack = matrixStack.toPoseStack();
                for (var x : MESHES.entrySet())
                    x.getValue().render(poseStack, builder, camera.getPosition().toVector3f().mul(-1), new Quaternionf());
                renderType.draw(builder.buildOrThrow());
            }
        });
    }
}
