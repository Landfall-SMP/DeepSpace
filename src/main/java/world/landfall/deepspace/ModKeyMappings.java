package world.landfall.deepspace;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Deepspace.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeyMappings {
    public static final Lazy<KeyMapping> ROCKET_FORWARD = Lazy.of(() -> new KeyMapping(
            "key.deepspace.rocket_forward",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_SPACE,
            "key.categories.movement"
    ));
    public static final Lazy<KeyMapping> BEGIN_FLYING = Lazy.of(() -> new KeyMapping(
            "key.deepspace.begin_flying",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_SPACE,
            "key.categories.movement"
    ));
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        ROCKET_FORWARD.get().setKeyConflictContext(new IKeyConflictContext() {
            @Override
            public boolean isActive() {
                return true;
            }

            @Override
            public boolean conflicts(@NotNull IKeyConflictContext iKeyConflictContext) {
                return false;
            }
        });
        event.register(ROCKET_FORWARD.get());
        event.register(BEGIN_FLYING.get());
    }
}
