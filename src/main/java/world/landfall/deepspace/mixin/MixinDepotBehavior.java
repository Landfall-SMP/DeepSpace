package world.landfall.deepspace.mixin;

import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.DepotItemHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import world.landfall.deepspace.integration.CreateIntegration;
import world.landfall.deepspace.item.JetHelmetItem;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mixin(value = DepotBehaviour.class, remap = false)
public abstract class MixinDepotBehavior extends BlockEntityBehaviour {

    @Shadow TransportedItemStack heldItem;
    @Shadow List<TransportedItemStack> incoming;
    @Shadow ItemStackHandler processingOutputBuffer;
    @Shadow public DepotItemHandler itemHandler;
    @Shadow TransportedItemStackHandlerBehaviour transportedHandler;
    @Shadow Supplier<Integer> maxStackSize;
    @Shadow Supplier<Boolean> canAcceptItems;
    @Shadow Predicate<Direction> canFunnelsPullFrom;
    @Shadow Consumer<ItemStack> onHeldInserted;
    @Shadow Predicate<ItemStack> acceptedItems;
    @Shadow boolean allowMerge;

    public MixinDepotBehavior(SmartBlockEntity be) {
        super(be);
    }

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void tick(CallbackInfo ci) {
        if (heldItem != null && heldItem.processedBy instanceof CreateIntegration.AerateType) {
            Level world = blockEntity.getLevel();
            if (world.getBlockTicks().count() % 8 == 0) {
                var component = heldItem.stack.getComponents().get(JetHelmetItem.JetHelmetComponent.SUPPLIER.get());
                if (component != null && component.maxOxygen() >= 0 && component.currentOxygen() < component.maxOxygen()) {
                    heldItem.stack.set(JetHelmetItem.JetHelmetComponent.SUPPLIER, new JetHelmetItem.JetHelmetComponent(component.currentOxygen()+1, component.maxOxygen()));
                }
            }
        }
    }
    @Shadow protected abstract boolean handleBeltFunnelOutput();
    @Shadow protected abstract boolean tick(TransportedItemStack heldItem);
}
