package world.landfall.deepspace;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.function.Consumer;

@GameTestHolder(Deepspace.MODID)
@EventBusSubscriber(modid = Deepspace.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModGameTests {
    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        event.register(ModGameTests.class);
    }
    private static final Logger LOGGER = LogUtils.getLogger();
    @GameTest(
            setupTicks = 20L,
            templateNamespace = Deepspace.MODID
    )
    public static void run(GameTestHelper helper) {
        LOGGER.info("Gametest ran! {}", helper.testInfo);
        helper.succeed();
    }

}
