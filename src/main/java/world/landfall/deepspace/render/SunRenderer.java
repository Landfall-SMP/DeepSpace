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
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.integration.IrisIntegration;
import world.landfall.deepspace.planet.PlanetRegistry;
import world.landfall.deepspace.render.shapes.Cube;

public class SunRenderer {
    private static final Logger logger = LogUtils.getLogger();
    private static Cube MESH;
    private static final ResourceLocation TEXTURE = Deepspace.path("textures/sun.png");
    private static final ResourceLocation PLANET_SHADER = Veil.veilPath("planet");
    private static final RenderStateShard.ShaderStateShard PLANET_RENDER_TYPE = new RenderStateShard.ShaderStateShard(() -> {
        ShaderProgram shader = VeilRenderSystem.setShader(PLANET_SHADER);
        return VeilRenderBridge.toShaderInstance(shader);
    });
    private static RenderType planetRenderType() {
        return PlanetRenderer.planetRenderType();
    }
    public static void refreshMeshes() {
        var sun = PlanetRegistry.getSun();
        MESH = new Cube(sun.getBoundingBoxMin().toVector3f(), sun.getBoundingBoxMax().toVector3f(), 1f);
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

        RenderType planetRenderType = planetRenderType();
        var poseStack = matrixStack.toPoseStack();
        BufferBuilder sunBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
        MESH.render(poseStack, sunBuilder, camera.getPosition().toVector3f().mul(-1), new Quaternionf());
        RenderSystem.setShaderTexture(0, TEXTURE);
        var overloadedColor = 70f;
        RenderSystem.setShaderColor(overloadedColor, overloadedColor, overloadedColor, 3f);
        IrisIntegration.bindPipeline();
        planetRenderType.draw(sunBuilder.buildOrThrow());
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.restoreProjectionMatrix();
    }
    public static void init() {
        refreshMeshes();
        SpaceRenderSystem.registerRenderer(SunRenderer::render, VeilRenderLevelStageEvent.Stage.AFTER_PARTICLES);
    }
    private static Matrix4f projectionMatrix(double fov, GameRenderer gameRenderer) {
        Matrix4f mat = new Matrix4f();
        return mat.perspective(
                (float)(fov * (float)(Math.PI / 180.0)),
                (float)gameRenderer.getMinecraft().getWindow().getWidth() / (float)gameRenderer.getMinecraft().getWindow().getHeight(),
                0.05f,
                gameRenderer.getDepthFar() * 4f
        );
    }


}
