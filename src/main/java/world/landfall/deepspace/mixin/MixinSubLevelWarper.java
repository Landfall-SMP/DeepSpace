package world.landfall.deepspace.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import dev.egg.DimensionalSable;
import dev.egg.SubLevelTemplate;
import dev.egg.SubLevelWarper;
import dev.egg.registries.BlockEntityRegistry;
import dev.ryanhcode.sable.api.sublevel.KinematicContraption;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import world.landfall.deepspace.Util;

import java.util.*;

@Mixin(value = SubLevelWarper.class, remap = false)
public abstract class MixinSubLevelWarper {

    @Inject(at = @At("TAIL"), method = "WarpSubLevels", cancellable = true)
    private static void WarpSubLevels(Collection<SubLevel> compoundSubLevel, ServerSubLevelContainer sourceContainer, ServerSubLevelContainer destinationContainer, Vector3d center, Vector3d position, CallbackInfo ci) {
        ci.cancel();
        HashMap<UUID, DimensionalSable.Pair<UUID, Vec3i>> oldToNew = new HashMap();
        HashMap<UUID, CompoundTag> subLevelTags = new HashMap();
        HashMap<UUID, ServerLevelPlot> subLevelPlots = new HashMap();
        HashMap<UUID, Set<Entity>> visitedEntities = new HashMap();

        for(SubLevel subLevel : compoundSubLevel) {
            ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
            double boxX = subLevel.boundingBox().width();
            double boxY = subLevel.boundingBox().height();
            double boxZ = subLevel.boundingBox().length();
            AABB box = new AABB(-boxX / (double)2.0F + center.x, -boxY / (double)2.0F + center.y, -boxZ / (double)2.0F + center.z, boxX / (double)2.0F + center.x, boxY / (double)2.0F + center.y, boxZ / (double)2.0F + center.z);
            box.inflate((double)1.0F);
            List<Entity> candidates = sourceContainer.getLevel().getEntities((Entity)null, box);
            visitedEntities.put(subLevel.getUniqueId(), new HashSet(candidates));

            for(KinematicContraption contraption : serverSubLevel.getPlot().getContraptions()) {
                ((AbstractContraptionEntity)contraption).disassemble();
            }

            CompoundTag tag = SubLevelTemplate.save(serverSubLevel.getPlot());
            Pose3d pose = new Pose3d();
            pose.position().set((new Vector3d(subLevel.logicalPose().position())).sub(new Vector3d(center)).add(position));
            pose.orientation().set(subLevel.logicalPose().orientation());
            ServerSubLevel copy = (ServerSubLevel)destinationContainer.allocateNewSubLevel(pose);
            subLevelTags.put(subLevel.getUniqueId(), tag);
            Vec3i start = serverSubLevel.getPlot().getCenterBlock().offset(0, sourceContainer.getLevel().dimensionType().minY(), 0);
            Vec3i end = copy.getPlot().getCenterBlock().offset(0, destinationContainer.getLevel().dimensionType().minY(), 0);
            Vec3i offset = end.subtract(start);
            oldToNew.put(subLevel.getUniqueId(), DimensionalSable.Pair.of(copy.getUniqueId(), offset));
            subLevelPlots.put(subLevel.getUniqueId(), copy.getPlot());
        }

        Set<UUID> visited = new HashSet();
        SubLevelPhysicsSystem physics = SubLevelPhysicsSystem.get(destinationContainer.getLevel());

        for(SubLevel subLevel : compoundSubLevel) {
            ServerLevelPlot plot = (ServerLevelPlot)subLevelPlots.get(subLevel.getUniqueId());
            ServerSubLevel copy = plot.getSubLevel();
            Pose3d pose = new Pose3d(copy.logicalPose());
            SubLevelTemplate.load(plot, (CompoundTag)subLevelTags.get(subLevel.getUniqueId()), new BlockEntityRegistry.MoveInfo(oldToNew, (new Vector3d(position)).sub(center), sourceContainer.getLevel(), destinationContainer.getLevel()));
            physics.getPipeline().teleport(copy, pose.position(), pose.orientation());
            if (subLevel.getName() != null) {
                copy.setName(subLevel.getName());
            }

            for(Entity entity : visitedEntities.get(subLevel.getUniqueId())) {
                TeleportEntity(entity, sourceContainer, destinationContainer, center, position, subLevel, oldToNew, visited);
            }
        }

        for(SubLevel subLevel : compoundSubLevel) {
            if (sourceContainer.getSubLevel(subLevel.getUniqueId()) != null)
                sourceContainer.removeSubLevel(subLevel, SubLevelRemovalReason.REMOVED);

        }
        Util.OLD_TO_NEW.putAll(oldToNew);

    }
    @Shadow
    private static void TeleportEntity(Entity entity, ServerSubLevelContainer sourceContainer, ServerSubLevelContainer destinationContainer, Vector3d center, Vector3d position, SubLevel subLevel, HashMap<UUID, DimensionalSable.Pair<UUID, Vec3i>> oldToNew, Set<UUID> visited) {}

}
