package world.landfall.deepspace;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerFlyableFallEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import world.landfall.deepspace.item.AngelBlockItem;
import world.landfall.deepspace.item.JetHelmetItem;
import world.landfall.deepspace.item.JetpackItem;
import world.landfall.deepspace.item.RocketBoosterItem;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Deepspace.MODID);
    public static final DeferredItem<Item> ANGEL_BLOCK_ITEM = ITEMS.register("angel_block", () -> new AngelBlockItem(new Item.Properties()));
    public static final DeferredItem<JetpackItem> JETPACK_ITEM = ITEMS.register("jetpack", JetpackItem::new);
    public static final Supplier<ItemStack> CREATIVE_JETPACK_ITEM = () -> {
        var item = JETPACK_ITEM.toStack();
        item.set(JetpackItem.JetpackComponent.SUPPLIER, new JetpackItem.JetpackComponent(100, -1));
        return item;
    };
    public static final DeferredItem<JetHelmetItem> JET_HELMET_ITEM = ITEMS.register("jet_helmet", JetHelmetItem::new);
    public static final Supplier<ItemStack> CREATIVE_JET_HELMET_ITEM = () -> {
        var item = JET_HELMET_ITEM.toStack();
        item.set(JetHelmetItem.JetHelmetComponent.SUPPLIER, new JetHelmetItem.JetHelmetComponent(100, -1));
        return item;
    };
    public static final DeferredItem<RocketBoosterItem> ROCKET_BOOSTER_ITEM = ITEMS.register("rocket_booster", RocketBoosterItem::new);
    public static void register(IEventBus eventBus) {
        JetpackItem.JetpackComponent.register(eventBus);
        JetHelmetItem.JetHelmetComponent.register(eventBus);

        ITEMS.register(eventBus);
    }
}
