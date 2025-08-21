package world.landfall.deepspace.render;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import foundry.veil.api.event.VeilRenderLevelStageEvent;

import world.landfall.deepspace.Deepspace;


public class SpaceSkyRenderer {
    private static final ResourceLocation SPACE_SKY_SHADER = Veil.veilPath("space_sky");

    public static void render(PoseStack poseStack, MultiBufferSource source, float partialTicks) {
        ShaderProgram shader = VeilRenderSystem.setShader(SPACE_SKY_SHADER);
        if (shader == null) return;

    }
}
