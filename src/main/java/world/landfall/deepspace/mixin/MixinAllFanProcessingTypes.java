package world.landfall.deepspace.mixin;

import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import world.landfall.deepspace.integration.CreateIntegration;

@Mixin(value = AllFanProcessingTypes.class, remap = false)
public abstract class MixinAllFanProcessingTypes {
    @Shadow @Final public static AllFanProcessingTypes.BlastingType BLASTING = register("blasting", new AllFanProcessingTypes.BlastingType());
    @Shadow @Final public static AllFanProcessingTypes.HauntingType HAUNTING = register("haunting", new AllFanProcessingTypes.HauntingType());
    @Shadow @Final public static AllFanProcessingTypes.SmokingType SMOKING = register("smoking", new AllFanProcessingTypes.SmokingType());
    @Shadow @Final public static AllFanProcessingTypes.SplashingType SPLASHING = register("splashing", new AllFanProcessingTypes.SplashingType());
    @Unique @Final
    private static CreateIntegration.AerateType deepSpace$AERATE = register("aerate", new CreateIntegration.AerateType());

    @Shadow
    private static <T extends FanProcessingType> T register(String name, T type) {
        return null;
    }

}
