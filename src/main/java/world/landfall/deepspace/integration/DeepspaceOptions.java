package world.landfall.deepspace.integration;

import net.caffeinemc.mods.sodium.client.gui.options.TextProvider;
import net.minecraft.network.chat.Component;

public class DeepspaceOptions {

    public enum Detail implements TextProvider {
        NONE("options.deepspace.decorationDetail.none"), BASIC("options.deepspace.decorationDetail.basic"), EXPENSIVE("options.deepspace.decorationDetail.expensive");
        final String value;
        Detail(String _value) {
            value = _value;
        }
        @Override
        public String toString() {
            return value;
        }

        @Override
        public Component getLocalizedName() {
            return Component.translatable(value);
        }
    }
    public Detail atmosphereDetail;
    public Detail shadingDetail;

    public DeepspaceOptions(Detail atmosphereDetail, Detail shadingDetail) {

        this.atmosphereDetail = atmosphereDetail;
        this.shadingDetail = shadingDetail;
    }
    public void load(Detail atmosphereDetail, Detail shadingDetail) {
        this.atmosphereDetail = atmosphereDetail;
        this.shadingDetail = shadingDetail;
    }
    public static DeepspaceOptions defaults() {
        return new DeepspaceOptions(Detail.BASIC, Detail.BASIC);
    }

}
