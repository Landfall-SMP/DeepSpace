package world.landfall.deepspace;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import world.landfall.deepspace.block.AngelBlock;
import world.landfall.deepspace.block.OxygenatorBlock;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Deepspace.MODID);
    public static final DeferredBlock<Block> ANGEL_BLOCK = BLOCKS.register("angel_block", () -> new AngelBlock(BlockBehaviour.Properties.of()));
    public static final DeferredBlock<Block> OXYGENATOR_BLOCK = BLOCKS.register("oxygenator", OxygenatorBlock::new);
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
