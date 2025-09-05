package world.landfall.deepspace;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import world.landfall.deepspace.item.AngelBlockItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Deepspace.MODID);
    public static final DeferredItem<Item> ANGEL_BLOCK_ITEM = ITEMS.register("angel_block", () -> new AngelBlockItem(new Item.Properties()));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
