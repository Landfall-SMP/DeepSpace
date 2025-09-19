package world.landfall.deepspace.integration;

import net.caffeinemc.mods.sodium.client.gui.options.storage.OptionStorage;
import world.landfall.deepspace.Config;
import world.landfall.deepspace.ModOptions;

public class DeepspaceOptionsStorage implements OptionStorage<DeepspaceOptions> {
    private final DeepspaceOptions options = ModOptions.options();
    @Override
    public DeepspaceOptions getData() {
        return options;
    }

    @Override
    public void save() {
        Config.PLANET_DECORATION_DETAIL.set(options.atmosphereDetail);
        Config.PLANET_SHADING_DETAIL.set(options.shadingDetail);
        Config.PLANET_SHADING_DETAIL.save();
        Config.PLANET_DECORATION_DETAIL.save();
    }
}
