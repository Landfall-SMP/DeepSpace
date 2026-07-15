package world.landfall.deepspace.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import world.landfall.deepspace.ClientOxygenatorTracker;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.ModBlocks;
import world.landfall.deepspace.ModOptions;
import world.landfall.deepspace.integration.IrisIntegration;
import world.landfall.deepspace.planet.PlanetRegistry;
import world.landfall.deepspace.render.shapes.Cube;
import world.landfall.deepspace.render.shapes.Sphere;

public class OxygenatorRenderer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation BUBBLE_SHADER_LOC = Deepspace.path("bubble");
    private static final RenderStateShard.ShaderStateShard BUBBLE_SHADER_SHARD = new RenderStateShard.ShaderStateShard(() -> {
        ShaderProgram shader = VeilRenderSystem.setShader(BUBBLE_SHADER_LOC);
        return VeilRenderBridge.toShaderInstance(shader);
    });
    private static RenderType type(boolean shaderPack) {
//            return RenderType.SOLID;
        var renderType = RenderType.CompositeState.builder()
                .setShaderState(BUBBLE_SHADER_SHARD)
                .setCullState(RenderStateShard.CullStateShard.NO_CULL)
                .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                .setLayeringState(RenderStateShard.LayeringStateShard.VIEW_OFFSET_Z_LAYERING)
                .setWriteMaskState(RenderStateShard.WriteMaskStateShard.COLOR_WRITE)
                .createCompositeState(true);
        var renderTypeShaderPack = RenderType.CompositeState.builder()
                .setShaderState(BUBBLE_SHADER_SHARD)
                .setCullState(RenderStateShard.CullStateShard.NO_CULL)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setLayeringState(RenderStateShard.LayeringStateShard.VIEW_OFFSET_Z_LAYERING)
                .setWriteMaskState(RenderStateShard.WriteMaskStateShard.COLOR_WRITE)
                .createCompositeState(true);
        return RenderType.create(
                "bubble",
                DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.TRIANGLES,
                186432, true, false,
                shaderPack ? renderTypeShaderPack : renderType
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
        for (var oxygenatorBlockEntity : ClientOxygenatorTracker.loop()) {
            var state = oxygenatorBlockEntity.getBlockState();

            if (!state.is(ModBlocks.OXYGENATOR_BLOCK))
                return;

            var mesh = new Sphere(oxygenatorBlockEntity.getRadius(), 32, 32);
            var cam = Minecraft.getInstance().gameRenderer.getMainCamera();
            var type = type(IrisIntegration.isShaderPackEnabled());
            var poseStack = matrixStack.toPoseStack();
            var buf = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
            VeilRenderSystem.setShader(Deepspace.path("bubble"));
            var enabled = oxygenatorBlockEntity.isEnabled();
            var TIME_UNIFORM = VeilRenderSystem.getShader().getUniform("Time");

            assert Minecraft.getInstance().getCameraEntity() != null;
            TIME_UNIFORM.setFloat(Minecraft.getInstance().getCameraEntity().tickCount + cam.getPartialTickTime());
            if (!enabled)
                return;
            RenderSystem.setShaderTexture(0, Deepspace.path("textures/atmosphere.png"));
            poseStack.pushPose();
            SubLevelAccess levelAccess = SableCompanion.INSTANCE.getContaining(oxygenatorBlockEntity.getLevel(), oxygenatorBlockEntity.getBlockPos());
            if (levelAccess != null) {
                Pose3dc pose = levelAccess.logicalPose();
                mesh.render(poseStack, buf, pose.transformPosition(oxygenatorBlockEntity.getBlockPos().getCenter()).toVector3f().sub(cam.getPosition().toVector3f()), new Quaternionf());
            } else {
                mesh.render(poseStack, buf, oxygenatorBlockEntity.getBlockPos().getCenter().toVector3f().sub(cam.getPosition().toVector3f()), new Quaternionf());
            }
            type.draw(buf.buildOrThrow());
            poseStack.popPose();
        }
    }
    public static void init() {
        SpaceRenderSystem.registerRenderer(OxygenatorRenderer::render, VeilRenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS);
    }


}
