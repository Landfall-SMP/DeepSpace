package world.landfall.deepspace.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.landfall.deepspace.ModBlocks;
import world.landfall.deepspace.blockentity.OxygenatorBlockEntity;

public class OxygenatorBlock extends Block implements EntityBlock {

    public static final BooleanProperty ACTIVE = BlockStateProperties.ENABLED;
    public static final IntegerProperty RADIUS = IntegerProperty.create("radius", 5, 30);
    public OxygenatorBlock() {
        super(Properties.of());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new OxygenatorBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE, RADIUS);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return stateDefinition.any().setValue(ACTIVE, true).setValue(RADIUS, 5);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {

        return (level1, blockPos, blockState, ent) -> {
            if (ent instanceof OxygenatorBlockEntity ox_ent)
                OxygenatorBlockEntity.tick(level, blockPos, blockState, ox_ent);
        };
    }

    public static boolean canOxygenate(BlockState state) {
        if (!state.is(ModBlocks.OXYGENATOR_BLOCK) || !state.hasBlockEntity()) return false;

        return state.getValue(ACTIVE);
    }
}
