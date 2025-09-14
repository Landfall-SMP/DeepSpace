package world.landfall.deepspace.render;

import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

public class SpaceRenderSystem {
    private static final Collection<Renderer> renderers = new LinkedList<>();
    public static void init() {

        SpaceSkyRenderer.init();
        PlanetRenderer.init();
        SunRenderer.init();

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
                    for (Renderer x : renderers) {
                        if (stage.equals(x.stage))
                            x.e.onRenderLevelStage(stage, levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, renderTick, partialTicks, camera, frustum);
                    }
                });
    }
    public static void registerRenderer(@NotNull VeilRenderLevelStageEvent e, @NotNull VeilRenderLevelStageEvent.Stage stage) {
        renderers.add(new Renderer(Objects.requireNonNull(e), Objects.requireNonNull(stage)));
    }

    private record Renderer(VeilRenderLevelStageEvent e, VeilRenderLevelStageEvent.Stage stage) {}
}
