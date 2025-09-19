package world.landfall.deepspace;

import world.landfall.deepspace.integration.DeepspaceOptions;

public class ModOptions {
    private static DeepspaceOptions OPTIONS;

    public static void init() {
        if (OPTIONS == null)
            OPTIONS = DeepspaceOptions.defaults();
    }
    public static void init(DeepspaceOptions.Detail decorationDetail, DeepspaceOptions.Detail shadingDetail) {
        OPTIONS = new DeepspaceOptions(decorationDetail, shadingDetail);
    }
    public static DeepspaceOptions options() {
        if (OPTIONS == null) throw new IllegalStateException("Options not configured !");
        return OPTIONS;
    }
}
