package world.landfall.deepspace;

import com.ibm.icu.impl.StaticUnicodeSets;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyMappingLookup;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Deepspace.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeyMappings {
    public static final Lazy<KeyMapping> BEGIN_FLYING = Lazy.of(() -> new KeyMapping(
            "key.deepspace.begin_flying",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            "key.categories.movement"
    ));
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(BEGIN_FLYING.get());

    }
}
