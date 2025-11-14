package world.landfall.deepspace;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import world.landfall.deepspace.block.AngelBlock;
import world.landfall.deepspace.block.LunarSoilBlock;
import world.landfall.deepspace.block.MoonstoneZincOreBlock;
import world.landfall.deepspace.block.OxygenatorBlock;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Deepspace.MODID);
    public static final DeferredBlock<Block> ANGEL_BLOCK = BLOCKS.register("angel_block", () -> new AngelBlock(BlockBehaviour.Properties.of()));
    public static final DeferredBlock<Block> OXYGENATOR_BLOCK = BLOCKS.register("oxygenator", OxygenatorBlock::new);
    public static final DeferredBlock<Block> MOONSTONE_ZINC_ORE_BLOCK = BLOCKS.register("moonstone_zinc_ore_block", MoonstoneZincOreBlock::new);
    public static final DeferredBlock<Block> LUNAR_SOIL = BLOCKS.register("lunar_soil", () -> new LunarSoilBlock(BlockBehaviour.Properties.of()
            .strength(1, 1)
            .sound(SoundType.SAND)
            .mapColor(MapColor.COLOR_LIGHT_GRAY)
            .speedFactor(.7f)
    ));
    public static final DeferredBlock<Block> LUNAR_COBBLE = BLOCKS.register("lunar_cobble", () -> new Block(BlockBehaviour.Properties.of()
            .strength(4, 3)
            .sound(SoundType.STONE)
            .mapColor(MapColor.COLOR_GRAY)
    ));
    public static final DeferredBlock<Block> MOON_STONE = BLOCKS.register("moonstone", () -> new Block(BlockBehaviour.Properties.of()
            .strength(4, 3)
            .sound(SoundType.STONE)
            .mapColor(MapColor.COLOR_GRAY)
    ));
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
