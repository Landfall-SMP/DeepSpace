package world.landfall.deepspace;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerFlyableFallEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import world.landfall.deepspace.item.AngelBlockItem;
import world.landfall.deepspace.item.JetHelmetItem;
import world.landfall.deepspace.item.JetpackItem;
import world.landfall.deepspace.item.RocketBoosterItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Deepspace.MODID);
    public static final DeferredItem<Item> ANGEL_BLOCK_ITEM = ITEMS.register("angel_block", () -> new AngelBlockItem(new Item.Properties()));
    public static final DeferredItem<JetpackItem> JETPACK_ITEM = ITEMS.register("jetpack", JetpackItem::new);
    public static final DeferredItem<JetHelmetItem> JET_HELMET_ITEM = ITEMS.register("jet_helmet", JetHelmetItem::new);
    public static final DeferredItem<RocketBoosterItem> ROCKET_BOOSTER_ITEM = ITEMS.register("rocket_booster", RocketBoosterItem::new);
    public static void register(IEventBus eventBus) {
        JetpackItem.JetpackComponent.register(eventBus);
        JetHelmetItem.JetHelmetComponent.register(eventBus);
        ITEMS.register(eventBus);
    }
}
