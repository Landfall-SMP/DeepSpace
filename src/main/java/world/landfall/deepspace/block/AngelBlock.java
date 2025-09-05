package world.landfall.deepspace.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import world.landfall.deepspace.ModBlocks;
import world.landfall.deepspace.ModItems;

import java.util.List;

public class AngelBlock extends Block {
    public AngelBlock(Properties properties) {
        super(properties
                .destroyTime(2f)
                .strength(3.5f)
        );
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        //return super.getDrops(state, params);
        return List.of(new ItemStack(ModItems.ANGEL_BLOCK_ITEM.asItem()));

    }

}
