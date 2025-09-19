package world.landfall.deepspace.mixin;

import com.google.common.collect.ImmutableList;
import net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpl;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatter;
import net.caffeinemc.mods.sodium.client.gui.options.control.CyclingControl;
import net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl;
import net.caffeinemc.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;
import net.caffeinemc.mods.sodium.client.gui.options.storage.OptionStorage;
import net.caffeinemc.mods.sodium.client.gui.options.storage.SodiumOptionsStorage;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import world.landfall.deepspace.ModOptions;
import world.landfall.deepspace.integration.DeepspaceOptions;
import world.landfall.deepspace.integration.DeepspaceOptionsStorage;
import world.landfall.deepspace.integration.SodiumIntegration;

import java.util.List;

@Mixin(SodiumOptionsGUI.class)
public class MixinSodiumOptionsGUI extends Screen {
    @Shadow(remap = false)
    @Final
    private List<OptionPage> pages;

    @Unique OptionPage deepSpace$spaceOptionsPage;
    @Unique private static final MinecraftOptionsStorage deepSpace$vanillaOpts = new MinecraftOptionsStorage();
    @Unique private static final DeepspaceOptionsStorage deepSpace$deepspaceOpts = new DeepspaceOptionsStorage();

    protected MixinSodiumOptionsGUI(Component title) {
        super(title);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void iris$onInit(Screen prevScreen, CallbackInfo ci) {
        deepSpace$spaceOptionsPage = new OptionPage(Component.translatable("options.deepspace.spaceOptions"), ImmutableList.of(
                OptionGroup.createBuilder().add(
                        OptionImpl.createBuilder(DeepspaceOptions.Detail.class, deepSpace$deepspaceOpts)
                                .setName(Component.translatable("options.deepspace.decorationDetail"))
                                .setTooltip(Component.translatable(String.format("options.deepspace.decorationDetail.tooltip")))
                                .setControl(option -> {
                                    DeepspaceOptions.Detail[] allowedValues = DeepspaceOptions.Detail.values();
                                    return new CyclingControl<>(option, DeepspaceOptions.Detail.class, allowedValues);
                                })
                                .setBinding((DeepspaceOptions options, DeepspaceOptions.Detail value) -> options.atmosphereDetail = DeepspaceOptions.Detail.values()[value.ordinal()], options -> options.atmosphereDetail)
                                .build()
                ).build()
        ));
        System.out.println("verified!");
        pages.add(deepSpace$spaceOptionsPage);
    }
}
