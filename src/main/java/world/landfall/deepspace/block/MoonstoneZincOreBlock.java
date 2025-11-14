package world.landfall.deepspace.block;

import com.simibubi.create.AllItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MoonstoneZincOreBlock extends Block {
    public MoonstoneZincOreBlock() {
        super(Properties.of()
                .strength(4, 3)
                .sound(SoundType.STONE));
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.Builder params) {
        return params.getParameter(LootContextParams.TOOL).isCorrectToolForDrops(state) ?
                List.of(AllItems.RAW_ZINC.asStack()) :
                List.of();
    }
}
