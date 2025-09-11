package world.landfall.deepspace.integration;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.logging.LogUtils;
import foundry.veil.api.client.render.texture.SimpleArrayTexture;
import foundry.veil.api.client.render.vertex.VertexArrayBuilder;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.texture.GlTexture;
import net.irisshaders.iris.gl.texture.InternalTextureFormat;
import net.irisshaders.iris.gl.texture.TextureDefinition;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.pbr.format.TextureFormat;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;

public class IrisIntegration {
    private static Logger logger = LogUtils.getLogger();
    private static IrisApi INSTANCE = IrisApi.getInstance();

    public static boolean isShaderPackEnabled() {
        return INSTANCE.isShaderPackInUse();
    }
    public static void bindPipeline() {
        Iris.getPipelineManager().getPipeline().map(i -> {
            if (i instanceof IrisRenderingPipeline ip) return ip; else return null;
        }).ifPresent(IrisRenderingPipeline::bindDefault);
    }
}
