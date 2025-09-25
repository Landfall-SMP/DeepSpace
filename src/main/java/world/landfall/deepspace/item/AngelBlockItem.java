package world.landfall.deepspace.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import world.landfall.deepspace.ModBlocks;
import world.landfall.deepspace.ModItems;

public class AngelBlockItem extends BlockItem {
    public AngelBlockItem(Properties properties) {
        super(ModBlocks.ANGEL_BLOCK.get(),
                properties
                        .component(DataComponents.RARITY, Rarity.RARE)
        );
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        return super.canPlace(context, state);
        //return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var result = super.use(level, player, usedHand);
        if (result.getResult() == InteractionResult.PASS) {
            var direction = player.getLookAngle();
            var reach = player.blockInteractionRange();
            var normal = direction.normalize();
            var pos = new Vec3(normal.x, normal.y, normal.z).scale(reach).add(player.position());
            var newResult = place(new BlockPlaceContext(player, usedHand, player.getUseItem(), new BlockHitResult(pos, Direction.getNearest(normal.x, normal.y, normal.z), roundToBlockPos(pos.add(new Vec3(-.5, 1, -.5))),true)));
            if (!player.isCreative() && (newResult.equals(InteractionResult.SUCCESS) || newResult.equals(InteractionResult.CONSUME)))
                player.getItemInHand(usedHand).shrink(1);
            if (newResult.equals(InteractionResult.SUCCESS))
                return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }

        return result;
    }
    private static BlockPos roundToBlockPos(Vec3 input) {
        return new BlockPos(
                (int)Math.round(input.x),
                (int)Math.round(input.y),
                (int)Math.round(input.z)
        );
    }
}