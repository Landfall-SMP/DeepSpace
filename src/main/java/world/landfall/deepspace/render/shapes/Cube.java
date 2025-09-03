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

public class Cube implements DeepSpaceRenderable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private LinkedList<Triangle> TRIANGLES = new LinkedList<>();
    public Cube(Vector3f _corner1, Vector3f _corner2) {
        var corner1 = new Vector3f(
                _corner1.x,
                _corner1.y,
                _corner1.z
        );
        var corner2 = new Vector3f(
                _corner2.x,
                _corner2.y,
                _corner2.z
        );
        //corner1.mul(2);
        //corner2.mul(2);
        Vector3f center = new Vector3f(corner1).add(corner2).div(2);

        Vector3f diff = new Vector3f(corner1).sub(corner2).div(2);
        Quaternionf[] rotations = new Quaternionf[] {
                new Quaternionf(),
                new Quaternionf().rotateLocalX((float)Math.PI/2),
                new Quaternionf().rotateLocalX(-(float)Math.PI/2),
                new Quaternionf().rotateLocalX((float)Math.PI),
                new Quaternionf().rotateLocalY((float)Math.PI/2),
                new Quaternionf().rotateLocalY(-(float)Math.PI/2)
        };
        for (var x : rotations) {
            Vector3f[] vertexes = new Vector3f[] {
                    new Vector3f(diff.x, diff.y, diff.z),
                    new Vector3f(diff.x, -diff.y, diff.z),
                    new Vector3f(-diff.x, diff.y, diff.z),
                    new Vector3f(diff.x, -diff.y, diff.z),
                    new Vector3f(-diff.x, diff.y, diff.z),
                    new Vector3f(-diff.x, -diff.y, diff.z)
            };
            var triangle1 = new Triangle(
                    new Vector3f[] {
                            vertexes[0].rotate(x).add(center),
                            vertexes[1].rotate(x).add(center),
                            vertexes[2].rotate(x).add(center)
                    },
                    new Vector2f[] {
                            new Vector2f(0, 0),
                            new Vector2f(0, 1),
                            new Vector2f(1, 0)
                    }
            );
            var triangle2 = new Triangle(
                    new Vector3f[] {
                            vertexes[4].rotate(x).add(center),
                            vertexes[3].rotate(x).add(center),
                            vertexes[5].rotate(x).add(center)
                    },
                    new Vector2f[] {
                            new Vector2f(1, 0),
                            new Vector2f(0, 1),
                            new Vector2f(1, 1)
                    }
            );
            TRIANGLES.add(triangle1);
            TRIANGLES.add(triangle2);

        }
        LOGGER.info("Made {} triangles",TRIANGLES.size());
    }
    @Override
    public void render(PoseStack stack, VertexConsumer consumer, Vector3fc dimensions, Quaternionf rotation) {
        for (var triangle : TRIANGLES) {
            for (int i = 0; i < 3; i++) {
                var oldVertex = triangle.vertexes[i];
                var vertex = new Vector3f(oldVertex.x, oldVertex.y, oldVertex.z);
                vertex.rotate(rotation);
                var UV = triangle.UV[i];
                var normal = new Vector3f(vertex).mul(1);
                consumer.addVertex(vertex.x+dimensions.x(), vertex.y+dimensions.y(), vertex.z+dimensions.z(),
                        Color.WHITE.argb(),
                        UV.x, UV.y,
                        0, 255, normal.x, normal.y, normal.z
                );
            }
        }
    }
    private static class Triangle {
        public final Vector3f[] vertexes;
        public final Vector2f[] UV;
        public Triangle(Vector3f[] _vertexes, Vector2f[] _UV) {
            vertexes = _vertexes;
            UV = _UV;
        }
    }
}
