package world.landfall.deepspace.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.landfall.deepspace.Deepspace;

import java.awt.*;
import java.util.function.Supplier;

public class JetHelmetItem extends ArmorItem implements IClientItemExtensions {
    public JetHelmetItem() {
        super(ArmorMaterials.IRON, Type.HELMET,new Properties()
                .durability(-1)
                .component(JetHelmetComponent.SUPPLIER, new JetHelmetComponent(100, 100))
                .component(DataComponents.RARITY, Rarity.EPIC)
        );

    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        var equipped = slotId == 39;
        if (equipped && entity instanceof Player player && !player.isCreative()) {
            var tick = player.tickCount;
            var component = stack.getComponents().get(JetHelmetComponent.SUPPLIER.get());
            if (component == null) return;
            if (tick % 40 == 0 && component.maxOxygen >= 0) {
                stack.set(JetHelmetComponent.SUPPLIER, new JetHelmetComponent(component.currentOxygen - 1, component.maxOxygen));
            }
        }

    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        var component = stack.getComponents().get(JetHelmetComponent.SUPPLIER.get());
        return component != null && component.maxOxygen >= 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var component = stack.getComponents().get(JetHelmetComponent.SUPPLIER.get());
        return (component != null) ? (int)((float)component.playerOxygen()/Player.TOTAL_AIR_SUPPLY*12f) : 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        var component = stack.getComponents().get(JetHelmetComponent.SUPPLIER.get());
        return (component != null && component.playerOxygen() > 3) ? Color.WHITE.getRGB() : Color.RED.getRGB();
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return ResourceLocation.parse("minecraft:block/glass");
    }

//    @Override
//    public @NotNull Model getGenericArmorModel(@NotNull LivingEntity livingEntity, @NotNull ItemStack itemStack, @NotNull EquipmentSlot equipmentSlot, @NotNull HumanoidModel<?> original) {
//        return new JetSuitArmorModel<Player>(original.head);
//    }

    public record JetHelmetComponent(int currentOxygen, int maxOxygen) {

        public static final Codec<JetHelmetComponent> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("current_oxygen").forGetter(JetHelmetComponent::currentOxygen),
                        Codec.INT.fieldOf("max_oxygen").forGetter(JetHelmetComponent::maxOxygen)
                ).apply(instance, JetHelmetComponent::new)
        );
        public static final StreamCodec<ByteBuf, JetHelmetComponent> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, JetHelmetComponent::currentOxygen,
                ByteBufCodecs.INT, JetHelmetComponent::maxOxygen,
                JetHelmetComponent::new
        );
        public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Deepspace.MODID);
        public static final Supplier<DataComponentType<JetHelmetComponent>> SUPPLIER = REGISTRAR.registerComponentType(
                "jet_helmet",
                builder -> builder
                        .persistent(CODEC)
                        .networkSynchronized(STREAM_CODEC)
        );

        public static void register(IEventBus eventBus) {
            REGISTRAR.register(eventBus);
        }
        public int playerOxygen() {
            return maxOxygen < 0 ? Player.TOTAL_AIR_SUPPLY : (int)Math.clamp((float)currentOxygen / maxOxygen * Player.TOTAL_AIR_SUPPLY, 0, Player.TOTAL_AIR_SUPPLY);
        }
    }
}
