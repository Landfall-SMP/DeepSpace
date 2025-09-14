package world.landfall.deepspace.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import foundry.veil.Veil;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.slf4j.Logger;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.planet.Planet;
import world.landfall.deepspace.planet.PlanetRegistry;
import world.landfall.deepspace.render.shapes.Cube;

import java.awt.*;
import java.util.HashMap;

public class PlanetDecorationsRenderer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final HashMap<String, Atmosphere> ATMOSPHERE_MESHES = new HashMap<>();
    private static final ResourceLocation ATMOSPHERE_SHADER = Veil.veilPath("atmosphere");
    private static final RenderStateShard.ShaderStateShard ATMOSPHERE_RENDER_TYPE = new RenderStateShard.ShaderStateShard(() -> {
        ShaderProgram shader = VeilRenderSystem.setShader(ATMOSPHERE_SHADER);
        return VeilRenderBridge.toShaderInstance(shader);
    });
    public static void refreshMeshes() {
        ATMOSPHERE_MESHES.clear();
        for (var x : PlanetRegistry.getAllPlanets()) {
            var decorations = x.getDecorations();
            if (decorations.isEmpty()) return;
            for (var decoration : decorations.get()) {
                if (decoration.type() == Planet.PlanetDecoration.Type.ATMOSPHERE)
                    ATMOSPHERE_MESHES.put(x.getId(), new Atmosphere(
                            new Cube(x.getBoundingBoxMin().toVector3f(), x.getBoundingBoxMax().toVector3f(), decoration.scale()),
                            decoration.scale(),
                            decoration.color()
                    ));
            }
        }
    }
    public static void init() {
        refreshMeshes();
        SpaceRenderSystem.registerRenderer(PlanetDecorationsRenderer::render, VeilRenderLevelStageEvent.Stage.AFTER_ENTITIES);
    }
    private static RenderType atmosphereRenderType() {
        var renderType = RenderType.CompositeState.builder()
                .setShaderState(ATMOSPHERE_RENDER_TYPE)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.CullStateShard.NO_CULL)
                .createCompositeState(true);
        return RenderType.create(
                "atmosphere",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.TRIANGLES,
                786432, true, false,
                renderType
        );
    }
    public static void render(
            VeilRenderLevelStageEvent.Stage stage,
            LevelRenderer levelRenderer,
            MultiBufferSource.BufferSource bufferSource,
            MatrixStack matrixStack,
            Matrix4fc frustumMatrix,
            Matrix4fc projectionMatrix,
            int renderTick,
            DeltaTracker partialTicks,
            Camera camera,
            Frustum frustum
    ) {
        var instance = Minecraft.getInstance();
        if (!instance.level.dimension().location().equals(ResourceLocation.fromNamespaceAndPath(Deepspace.MODID,"space")))
            return;
        var atmosphereRenderType = atmosphereRenderType();
        var poseStack = matrixStack.toPoseStack();

        for (var x : ATMOSPHERE_MESHES.entrySet()) {

            // Planet Atmosphere
            BufferBuilder atmosphereBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
            x.getValue().cube.render(poseStack, atmosphereBuilder, camera.getPosition().toVector3f().mul(-1), new Quaternionf());
            var color = new Color(x.getValue().color);
            RenderSystem.setShaderColor(color.getRed()/256f, color.getGreen()/256f, color.getBlue()/256f, 1f);
            RenderSystem.setShaderTexture(0, Deepspace.path("textures/atmosphere.png"));
            atmosphereRenderType.draw(atmosphereBuilder.buildOrThrow());
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }

    }
    private record Atmosphere(Cube cube, float scale, int color) {}
}
