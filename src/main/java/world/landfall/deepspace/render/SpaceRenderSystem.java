package world.landfall.deepspace.render;

import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

public class SpaceRenderSystem {

    private static final Collection<Renderer> renderers = new LinkedList<>();
    public static void init() {

        SpaceSkyRenderer.init();
        PlanetRenderer.init();
        SunRenderer.init();
        PlanetDecorationsRenderer.init();

        VeilEventPlatform.INSTANCE.preVeilPostProcessing((location, pipeline, ctx) -> {
            pipeline.getOrCreateUniform("Time").setFloat(0.0f);
            pipeline.getOrCreateUniform("SunLocation").setVector(new float[] {0, 0, 0});
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
                        if (stage.equals(x.stage) || x.stage == null)
                            x.e.onRenderLevelStage(stage, levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, renderTick, partialTicks, camera, frustum);
                    }
                });
    }
    public static void registerRenderer(@NotNull VeilRenderLevelStageEvent e, @Nullable VeilRenderLevelStageEvent.Stage stage) { // null stage if the renderer wants access to all of them
        renderers.add(new Renderer(Objects.requireNonNull(e), stage));
    }

    private record Renderer(VeilRenderLevelStageEvent e, VeilRenderLevelStageEvent.Stage stage) {}
}
