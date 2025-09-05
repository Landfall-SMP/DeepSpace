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
import org.slf4j.Logger;
import world.landfall.deepspace.Deepspace;
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
    public static void refreshMeshes() {
        var sun = PlanetRegistry.getSun();
        MESH = new Cube(sun.getBoundingBoxMin().toVector3f(), sun.getBoundingBoxMax().toVector3f());
    }
    public static void init() {
        refreshMeshes();
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage,
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
            if (!stage.equals(VeilRenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)) return;

            RenderType planetRenderType = planetRenderType();
            var poseStack = matrixStack.toPoseStack();
            BufferBuilder sunBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
            MESH.render(poseStack, sunBuilder, camera.getPosition().toVector3f().mul(-1), new Quaternionf());
            RenderSystem.setShaderTexture(0, TEXTURE);
            planetRenderType.draw(sunBuilder.buildOrThrow());

        });
    }

}
