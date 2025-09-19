package world.landfall.deepspace.integration;

import net.caffeinemc.mods.sodium.client.gui.options.storage.OptionStorage;
import world.landfall.deepspace.ModOptions;

public class DeepspaceOptionsStorage implements OptionStorage<DeepspaceOptions> {
    private final DeepspaceOptions options = ModOptions.options();
    @Override
    public DeepspaceOptions getData() {
        return options;
    }

    @Override
    public void save() {

    }
}
