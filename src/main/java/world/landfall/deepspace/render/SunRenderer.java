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
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.slf4j.Logger;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.integration.IrisIntegration;
import world.landfall.deepspace.planet.PlanetRegistry;
import world.landfall.deepspace.render.shapes.Cube;

public class SunRenderer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static Cube MESH;
    private static final ResourceLocation TEXTURE = Deepspace.path("textures/sun.png");
    private static final ResourceLocation SUN_SHADER = Veil.veilPath("sun");
    private static final RenderStateShard.ShaderStateShard SUN_RENDER_TYPE = new RenderStateShard.ShaderStateShard(() -> {
        ShaderProgram shader = VeilRenderSystem.setShader(SUN_SHADER);
        return VeilRenderBridge.toShaderInstance(shader);
    });
    private static RenderType sunRenderType() {
        var renderType = RenderType.CompositeState.builder()
                .setShaderState(SUN_RENDER_TYPE)
                .createCompositeState(true);
        return RenderType.create(
                "sun",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.TRIANGLES,
                786432, true, false,
                renderType
        );
    }
    public static void refreshMeshes() {
        var sun = PlanetRegistry.getSun();
        MESH = new Cube(sun.getBoundingBoxMin().toVector3f(), sun.getBoundingBoxMax().toVector3f(), 1f, false);
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

        RenderType sunRenderType = sunRenderType();
        var poseStack = matrixStack.toPoseStack();
        BufferBuilder sunBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
        MESH.render(poseStack, sunBuilder, camera.getPosition().toVector3f().mul(-1), new Quaternionf());
        RenderSystem.setShaderTexture(0, TEXTURE);
        var overloadedColor = 2f;
        if (IrisIntegration.isShaderPackEnabled())
            RenderSystem.setShaderColor(overloadedColor, overloadedColor, overloadedColor, 3f);
        var scale = 1f;
        if (IrisIntegration.isShaderPackEnabled())
            scale = 1.5f;
        VeilRenderSystem.setShader(Veil.veilPath("sun")).getOrCreateUniform("scale").setFloat(scale);
        IrisIntegration.bindPipeline();
        sunRenderType.draw(sunBuilder.buildOrThrow());
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }
    public static void init() {
        refreshMeshes();
        SpaceRenderSystem.registerRenderer(SunRenderer::render, VeilRenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS);
    }


}
