package world.landfall.deepspace.item;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import world.landfall.deepspace.ModAttatchments;

public class RocketBoosterItem extends Item {
    public RocketBoosterItem() {
        super(
                new Properties()
                        .rarity(Rarity.UNCOMMON)
        );

    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.setData(ModAttatchments.IS_FLYING_JETPACK, true);
        var velocity = player.getData(ModAttatchments.JETPACK_VELOCITY);
        var item = player.getItemInHand(usedHand);
        player.setData(ModAttatchments.JETPACK_VELOCITY, new Vector3f(velocity).add(player.getLookAngle().toVector3f().mul(2)));
        player.getCooldowns().addCooldown(item.getItem(), 40);
        item.consume(1, player);
        return InteractionResultHolder.consume(item);
    }
}
