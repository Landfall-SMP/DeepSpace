package world.landfall.deepspace;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.neoforged.bus.api.IEventBus;

public class ModDamageTypes {

    public static final ResourceKey<DamageType> NO_AIR_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Deepspace.path("no_air"));
    public static DamageSource noAirDamage(Entity receiver) {
        return new DamageSource(
                receiver.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(NO_AIR_DAMAGE),
                (Entity)null
        );
    }
    public static void register(IEventBus eventBus) {

        eventBus.register(NO_AIR_DAMAGE);
    }
}
