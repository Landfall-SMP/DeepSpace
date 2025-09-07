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
import foundry.veil.api.client.render.shader.uniform.ShaderUniform;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.integration.IrisIntegration;
import world.landfall.deepspace.planet.PlanetRegistry;
import world.landfall.deepspace.render.shapes.Cube;

import java.util.HashMap;
import org.slf4j.Logger;

public class PlanetRenderer {

    private static final Logger logger = LogUtils.getLogger();

    private static final HashMap<String, Cube> MESHES = new HashMap<>();
    private static final HashMap<String, ResourceLocation> TEXTURES = new HashMap<>();
    private static final ResourceLocation PLANET_SHADER = Veil.veilPath("planet");
    private static final ResourceLocation ATMOSPHERE_SHADER = Veil.veilPath("atmosphere");
    private static final RenderStateShard.ShaderStateShard PLANET_RENDER_TYPE = new RenderStateShard.ShaderStateShard(() -> {
        ShaderProgram shader = VeilRenderSystem.setShader(PLANET_SHADER);
        return VeilRenderBridge.toShaderInstance(shader);
    });
    private static final RenderStateShard.ShaderStateShard ATMOSPHERE_RENDER_TYPE = new RenderStateShard.ShaderStateShard(() -> {
        ShaderProgram shader = VeilRenderSystem.setShader(ATMOSPHERE_SHADER);
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
    private static RenderType atmosphereRenderType() {
        var renderType = RenderType.CompositeState.builder()
                .setShaderState(ATMOSPHERE_RENDER_TYPE)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .createCompositeState(true);
        return RenderType.create(
                "atmosphere",
                DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL,
                VertexFormat.Mode.TRIANGLES,
                786432, true, false,
                renderType
        );
    }
    public static void refreshMeshes() {
        MESHES.clear();
        TEXTURES.clear();
        for (var x : PlanetRegistry.getAllPlanets()) {
            MESHES.put(x.getId(),new Cube(x.getBoundingBoxMin().toVector3f(), x.getBoundingBoxMax().toVector3f()));
            TEXTURES.put(x.getId(), Deepspace.path("textures/"+x.getId()+".png"));
            logger.info("Made mesh for planet {}",x.getName());
        }

    }
    public static void init() {
        refreshMeshes();
        VeilEventPlatform.INSTANCE.preVeilPostProcessing((location, pipeline, ctx) -> {
            pipeline.getOrCreateUniform("Time").setFloat(0.0f);
        });
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
            var instance = Minecraft.getInstance();
            if (!instance.level.dimension().location().equals(ResourceLocation.fromNamespaceAndPath(Deepspace.MODID,"space")))
                return;
            if (stage.equals(VeilRenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)) {
                RenderType planetRenderType = IrisIntegration.isShaderPackEnabled() ? RenderType.solid() :  planetRenderType();
                RenderType atmosphereRenderType = atmosphereRenderType();
                VeilRenderSystem.setShader(Veil.veilPath("atmosphere"));
                var TIME_UNIFORM = VeilRenderSystem.getShader().getOrCreateUniform("Time");
                TIME_UNIFORM.setFloat(camera.getPartialTickTime() + renderTick);
                var poseStack = matrixStack.toPoseStack();
                for (var x : MESHES.entrySet()) {
                    // Planet surface
                    BufferBuilder planetBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
                    var texture = TEXTURES.get(x.getKey());

                    RenderSystem.setShaderTexture(0, texture);

                    x.getValue().render(poseStack, planetBuilder, camera.getPosition().toVector3f().mul(-1), new Quaternionf());

                    planetRenderType.draw(planetBuilder.buildOrThrow());

                    // Planet Atmosphere
                    BufferBuilder atmosphereBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
                    x.getValue().render(poseStack, atmosphereBuilder, camera.getPosition().toVector3f().mul(-1), new Quaternionf());
                    RenderSystem.setShaderTexture(0, Deepspace.path("textures/atmosphere.png"));
                    atmosphereRenderType.draw(atmosphereBuilder.buildOrThrow());
                }
            }
        });
    }
}
