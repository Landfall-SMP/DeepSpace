package world.landfall.deepspace.blockentity;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.types.Type;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.lighting.LightEventListener;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.ModAttatchments;
import world.landfall.deepspace.ModBlocks;
import world.landfall.deepspace.block.OxygenatorBlock;
import world.landfall.deepspace.render.shapes.Sphere;

import java.util.Set;

public class OxygenatorBlockEntity extends BlockEntity {
    public static final BlockEntityType<OxygenatorBlockEntity> TYPE = BlockEntityType.Builder.of(
            OxygenatorBlockEntity::new,
            ModBlocks.OXYGENATOR_BLOCK.get()
    ).build(null);
    public OxygenatorBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }
    public static void tick(Level level, BlockPos pos, BlockState state, OxygenatorBlockEntity blockEntity) {

        int radius = state.getValue(OxygenatorBlock.RADIUS);
        var corner1 = pos.offset(radius, radius, radius);
        var corner2 = pos.offset(-radius, -radius, -radius);
        var ticks = level.getFluidTicks().count();
        if (ticks % 40 != 0)
            return;
        level.getNearbyPlayers(TargetingConditions.DEFAULT, null, AABB.of(BoundingBox.fromCorners(
                corner1,
                corner2
        ))).forEach((player) -> {
            if (OxygenatorBlock.canOxygenate(state) && player.position().distanceTo(pos.getCenter()) < radius) {
                player.setData(ModAttatchments.LAST_OXYGENATED, 0f);
//                var z = new Zombie(level);
//                z.teleportTo(pos.getX(), pos.getY(), pos.getZ());
//                var finder = new PathFinder(new FlyNodeEvaluator(), 6);
//                var path = finder.findPath(
//                        new PathNavigationRegion(level, player.blockPosition(), player.blockPosition()),
//                        z,
//                        Set.of(pos),
//                        5,
//                        1,
//                        1
//                );
//                if (path != null && path.canReach())
//                    player.setData(ModAttatchments.LAST_OXYGENATED, 0f);

            }
        });

    }
    public static class Renderer implements BlockEntityRenderer<OxygenatorBlockEntity> {

        public static final ResourceLocation BUBBLE_SHADER_LOC = Deepspace.path("bubble");
        public static final RenderStateShard.ShaderStateShard BUBBLE_SHADER_SHARD = new RenderStateShard.ShaderStateShard(() -> {
            ShaderProgram shader = VeilRenderSystem.setShader(BUBBLE_SHADER_LOC);
            return VeilRenderBridge.toShaderInstance(shader);
        });

        public Renderer(BlockEntityRendererProvider.Context ctx) {

        }
        private static RenderType type() {
//            return RenderType.SOLID;
            var renderType = RenderType.CompositeState.builder()
                    .setShaderState(BUBBLE_SHADER_SHARD)
                    .setCullState(RenderStateShard.CullStateShard.NO_CULL)
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setLayeringState(RenderStateShard.LayeringStateShard.VIEW_OFFSET_Z_LAYERING)
                    .setWriteMaskState(RenderStateShard.WriteMaskStateShard.COLOR_WRITE)
                    .createCompositeState(true);
            return RenderType.create(
                    "bubble",
                    DefaultVertexFormat.BLOCK,
                    VertexFormat.Mode.TRIANGLES,
                    786432, true, false,
                    renderType
            );
        }
        @Override
        public void render(OxygenatorBlockEntity oxygenatorBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
            var state = oxygenatorBlockEntity.getBlockState();
            var mesh = new Sphere(state.getValue(OxygenatorBlock.RADIUS), 32, 16);
            var cam = Minecraft.getInstance().gameRenderer.getMainCamera();
            var type = type();
            var buf = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
            poseStack.pushPose();
            mesh.render(poseStack, buf, oxygenatorBlockEntity.worldPosition.getCenter().toVector3f().sub(cam.getPosition().toVector3f()), new Quaternionf());
            type.draw(buf.buildOrThrow());
            poseStack.popPose();

        }
    }
}
