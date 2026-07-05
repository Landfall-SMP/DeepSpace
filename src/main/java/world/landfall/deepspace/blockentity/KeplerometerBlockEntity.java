package world.landfall.deepspace.blockentity;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.ModBlocks;
import world.landfall.deepspace.Util;
import world.landfall.deepspace.network.KeplerometerSublevelDataPacket;
import world.landfall.deepspace.planet.PlanetRegistry;

import java.util.List;

public class KeplerometerBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation {
    public static final BlockEntityType<KeplerometerBlockEntity> TYPE = BlockEntityType.Builder.of(
            KeplerometerBlockEntity::new,
            ModBlocks.KEPLEROMETER_BLOCK.get()
    ).build(null);

    private float lastPerigee = 0f;
    private float lastApogee = 0f;

    public KeplerometerBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
    }

    public void setLastPerigee(float lastPerigee) {
        this.lastPerigee = lastPerigee;
    }
    public void setLastApogee(float lastApogee) {
        this.lastApogee = lastApogee;
    }


    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        var sublevelAccess = SableCompanion.INSTANCE.getContaining(this);


        if (lastPerigee == 0f || sublevelAccess == null) {
            tooltip.add(Component.literal("Not in orbit!").withStyle(ChatFormatting.RED));
            return true;
        }

        tooltip.add(
                Component.literal("Minimum: ").append(
                        Component.literal(""+lastPerigee).withStyle(ChatFormatting.AQUA)
                )
        );tooltip.add(
                Component.literal("Maximum: ").append(
                        Component.literal(""+lastApogee).withStyle(ChatFormatting.AQUA)
                )
        );

        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.dimension().location().equals(Deepspace.path("space")))
            return;
        var sublevelAccess = SableCompanion.INSTANCE.getContaining(this);
        if (sublevelAccess == null) return;
        if (level.getServer() == null) return;
        if (level.getServer().getTickCount() % 20 == 0) {
            PacketDistributor.sendToServer(new KeplerometerSublevelDataPacket.Serverbound(worldPosition, sublevelAccess.getUniqueId()));
        }
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (t instanceof KeplerometerBlockEntity blockEntity) blockEntity.tick();
    }
}
