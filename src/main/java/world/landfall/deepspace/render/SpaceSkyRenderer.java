package world.landfall.deepspace.render;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.Veil;
import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.StonecutterBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import foundry.veil.api.event.VeilRenderLevelStageEvent;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import world.landfall.deepspace.Deepspace;

import world.landfall.deepspace.render.shapes.Sphere;


public class SpaceSkyRenderer {
    private static final ResourceLocation SKY_TEXTURE = ResourceLocation.fromNamespaceAndPath(Deepspace.MODID, "textures/sky/stars.png");
    private static final Sphere skySphere = new Sphere(100, 20, 40);

    private static final RenderType SPACE_SKY_RENDER_TYPE = VeilRenderType.solid();


    public static void init() {
        VeilEventPlatform.INSTANCE.onVeilRegisterFixedBuffers(registry -> {
            registry.registerFixedBuffer(VeilRenderLevelStageEvent.Stage.AFTER_LEVEL, SPACE_SKY_RENDER_TYPE);
        });
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, matrixStack, frustrumMatrix,projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
            //VertexConsumer builder = bufferSource.getBuffer(SPACE_SKY_RENDER_TYPE);
            BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);

            switch (stage) {

                case AFTER_SOLID_BLOCKS -> {

                    var poseStack = matrixStack.toPoseStack();
                    poseStack.pushPose();
                    skySphere.render(poseStack, builder, new Vector3f(0, 0, 0), camera.rotation().invert(new Quaternionf()));
                    SPACE_SKY_RENDER_TYPE.draw(builder.buildOrThrow());
                    bufferSource.endBatch(SPACE_SKY_RENDER_TYPE);
                    poseStack.popPose();
                }


            }
        });
    }
}