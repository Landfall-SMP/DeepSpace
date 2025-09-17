package world.landfall.deepspace.render.shapes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import foundry.veil.api.client.color.Color;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.slf4j.Logger;

import java.util.LinkedList;

public class Plane implements DeepSpaceRenderable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private LinkedList<Triangle> TRIANGLES = new LinkedList<>();
    private final Vector3f center;
    private final float scale;
    public Plane(Vector3f center, float scale, Quaternionf rotation) {
        this.center = center;
        this.scale = scale;
        var vertexes = new Vector3f[4];
        for (int i = 0; i < 4; i++)
            vertexes[i] = new Vector3f(center).add(
                    new Vector3f((i == 0 || i == 2) ? scale : -scale, (i == 2 || i == 3) ? scale : -scale, 0)
                            .rotate(rotation)
            );
        TRIANGLES.add(new Triangle(
                new Vector3f[] {vertexes[0], vertexes[1], vertexes[2]},
                new Vector2f[] {new Vector2f(0, 0), new Vector2f(0, 1), new Vector2f(1, 0)},
                new Vector3f[] {new Vector3f(0, 1, 0).rotate(rotation),new Vector3f(0, 1, 0).rotate(rotation),new Vector3f(0, 1, 0).rotate(rotation)}
        ));
        TRIANGLES.add(new Triangle(
                new Vector3f[] {vertexes[1], vertexes[2], vertexes[3]},
                new Vector2f[] {new Vector2f(0, 1), new Vector2f(1, 0), new Vector2f(1, 1)},
                new Vector3f[] {new Vector3f(0, 1, 0).rotate(rotation),new Vector3f(0, 1, 0).rotate(rotation),new Vector3f(0, 1, 0).rotate(rotation)}
        ));

    }
    @Override
    public void render(PoseStack stack, VertexConsumer consumer, Vector3fc dimensions, Quaternionf rotation) {

        for (var triangle : TRIANGLES) {

            for (int i = 0; i < 3; i++) {
                var oldVertex = triangle.vertexes[i];
                var vertex = new Vector3f(oldVertex.x, oldVertex.y, oldVertex.z);
                vertex.rotate(rotation);

                var UV = triangle.UV[i];
                var normal = new Vector3f(vertex).sub(center).normalize();
                consumer.addVertex(vertex.x+dimensions.x(), vertex.y+dimensions.y(), vertex.z+dimensions.z(),
                        Color.WHITE.argb(),
                        UV.x, UV.y,
                        0, 255, normal.x, normal.y, normal.z
                );
            }
        }
    }
}
