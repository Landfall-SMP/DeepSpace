package world.landfall.deepspace;

import world.landfall.deepspace.integration.DeepspaceOptions;

public class ModOptions {
    private static DeepspaceOptions OPTIONS;

    public static void init() {
        OPTIONS = DeepspaceOptions.defaults();
    }
    public static DeepspaceOptions options() {
        if (OPTIONS == null) throw new IllegalStateException("womp womp");
        return OPTIONS;
    }
}
