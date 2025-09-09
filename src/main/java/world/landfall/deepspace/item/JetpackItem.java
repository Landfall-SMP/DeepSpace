package world.landfall.deepspace.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerFlyableFallEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.slf4j.Logger;
import world.landfall.deepspace.Deepspace;
import world.landfall.deepspace.ModAttatchments;
import world.landfall.deepspace.ModKeyMappings;

import java.awt.*;
import java.util.function.Supplier;
@EventBusSubscriber(modid = Deepspace.MODID)
public class JetpackItem extends Item implements Equipable {
    private static Logger LOGGER = LogUtils.getLogger();
    public JetpackItem() {
        super(new Properties()
                .durability(-1)
                .component(JetpackComponent.SUPPLIER, new JetpackComponent(100, 100))
        );
    }

//
//    @Override
//    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
//        if (!entity.level().isClientSide) {
//            int nextFlightTick = flightTicks + 1;
//            if (nextFlightTick % 10 == 0) {
//                if (nextFlightTick % 20 == 0) {
//                    var component = stack.getComponents().get(JetpackComponent.SUPPLIER.get());
//                    if (component == null) return false;
//                    stack.applyComponents(DataComponentMap.builder().set(
//                            JetpackComponent.SUPPLIER,
//                            new JetpackComponent(component.currentFuel - 1, component.maxFuel)
//                    ).build());
//                }
//
//                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
//            }
//        }
//        return true;
//    }


    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        var inSpace = level.dimension().location().equals(ResourceLocation.parse("deepspace:space"));
        if (!(entity instanceof Player player)) return;
        if (!player.hasData(ModAttatchments.IS_FLYING_JETPACK)) return;
        var isFlying = player.getData(ModAttatchments.IS_FLYING_JETPACK.get());
//        var isEquipped = stack.getEquipmentSlot() == EquipmentSlot.CHEST;
//        if (!isEquipped) {
//            return;
//        }
        if (isFlying) {
            if (player.isShiftKeyDown() || player.onGround()) {
                player.setData(ModAttatchments.IS_FLYING_JETPACK, false);
                player.setData(ModAttatchments.IS_ROCKETING_FORWARD, false);
                player.setData(ModAttatchments.JETPACK_VELOCITY, new Vector3f());
                return;
            }
            var lookAngle = player.getLookAngle();
            var deltas = player.getDeltaMovement();
            Vector3f storedVelocity = player.getData(ModAttatchments.JETPACK_VELOCITY);

            var keyPressed = player.getData(ModAttatchments.IS_ROCKETING_FORWARD);
            Vector3f newVelocity = new Vector3f(storedVelocity).add(lookAngle.toVector3f().mul(keyPressed ? .2f : 0)).mul(.9f);
            if (newVelocity.length() > 2) newVelocity.mul(.9f);
            newVelocity.add(new Vector3f(0, -.01f, 0));
            //player.setPos(player.getPosition(0).add(new Vec3(newVelocity.x, newVelocity.y, newVelocity.z)));
            player.setData(ModAttatchments.JETPACK_VELOCITY, newVelocity);
            player.setDeltaMovement(new Vec3(newVelocity.x, newVelocity.y, newVelocity.z));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        var component = stack.getComponents().get(JetpackComponent.SUPPLIER.get());
        return component != null && component.maxFuel > 0;
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return Color.WHITE.getRGB();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var component = stack.getComponents().get(JetpackComponent.SUPPLIER.get());
        return component != null ? (int)((float)component.currentFuel/component.maxFuel*13f): 0;
    }

    public static boolean isFlyEnabled(ItemStack stack) {
        var component = stack.getComponents().get(JetpackComponent.SUPPLIER.get());
        if (component == null) {
            LOGGER.error("A jetpack was created without its component !");
            return false;
        }
        return component.currentFuel > 0 || component.maxFuel < 0;

    }

    public record JetpackComponent(int currentFuel, int maxFuel) {
        public static final Codec<JetpackComponent> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                    Codec.INT.fieldOf("current_fuel").forGetter(JetpackComponent::currentFuel),
                    Codec.INT.fieldOf("max_fuel").forGetter(JetpackComponent::maxFuel)
                ).apply(instance, JetpackComponent::new)
        );
        public static final StreamCodec<ByteBuf, JetpackComponent> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, JetpackComponent::currentFuel,
                ByteBufCodecs.INT, JetpackComponent::maxFuel,
                JetpackComponent::new
        );
        public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Deepspace.MODID);
        public static final Supplier<DataComponentType<JetpackComponent>> SUPPLIER = REGISTRAR.registerComponentType(
                "jetpack",
                builder -> builder
                        .persistent(CODEC)
                        .networkSynchronized(STREAM_CODEC)
        );
        public static void register(IEventBus eventBus) {
            REGISTRAR.register(eventBus);
        }

    }
    @SubscribeEvent
    public static void fallFlyEvent(PlayerFlyableFallEvent event) {

    }
}
