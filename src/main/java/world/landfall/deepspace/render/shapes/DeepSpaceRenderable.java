package world.landfall.deepspace.render.shapes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Quaternionf;
import org.joml.Vector3fc;

public interface DeepSpaceRenderable {
    void render(PoseStack stack, VertexConsumer consumer, Vector3fc dimensions, Quaternionf rotation);
}
