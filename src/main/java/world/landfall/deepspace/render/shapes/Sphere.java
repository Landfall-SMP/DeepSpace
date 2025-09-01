package world.landfall.deepspace.render.shapes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import foundry.veil.api.client.color.Color;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.LinkedList;

public class Sphere implements DeepSpaceRenderable {
    private final LinkedList<Triangle> triangles;
    public Sphere(double radius, int width, int height) {
        triangles = new LinkedList<Triangle>();
        // Thanks to "bobobobo" on StackExchange for the algorithm
        for (int h = 0; h < height*2; h++) {
            double phi1 = ((double)(h) / height)*Math.PI - Math.PI/2;
            double phi2 = ((double)(h+1) / height)*Math.PI - Math.PI/2;
            for (int w = 0; w < width*2; w++) {
                double theta1 = ((double)(w) / width)*2*Math.PI - Math.PI;
                double theta2 = ((double)(w+1) / width)*2*Math.PI - Math.PI;
                double shiftAmount = (h == 0 || h == height-1 || h == height || h == height*2 - 1) ? .5 : 0;
                //double shiftAmount = .5;
                Vector3f vertex1 = vertexAtAngles(theta1, phi1, radius);
                Vector2f uv1 = new Vector2f((float)(w+shiftAmount - width/2.)/width*1f, (float)(h+0-height/2.)/(height+0)*1);

                Vector3f vertex2 = vertexAtAngles(theta1, phi2, radius);
                Vector2f uv2 = new Vector2f((float)(w+shiftAmount - width/2.)/width*1f, (float)(h+1-height/2.)/(height+0)*1);

                Vector3f vertex3 = vertexAtAngles(theta2, phi2, radius);
                Vector2f uv3 = new Vector2f((float)(w+1+shiftAmount - width/2.)/(width)*1f, (float)(h+1-height/2.)/(height+0)*1);

                Vector3f vertex4 = vertexAtAngles(theta2, phi1, radius);
                Vector2f uv4 = new Vector2f((float)(w+1+shiftAmount - width/2.)/(width)*1f, (float)(h+0-height/2.)/(height+0)*1);


                if (h == height / 2) {
                    addTriangle(vertex1, vertex3, vertex4, uv1, uv3, uv4);
                }
                else if (h + 1 == height / 2 & false) {
                    addTriangle(vertex3, vertex1, vertex2, uv3, uv1, uv2);

                }
                else {
                    addTriangle(vertex1, vertex2, vertex4, uv1, uv2, uv4);

                    addTriangle(vertex2, vertex3, vertex4, uv2, uv3, uv4);

                }
                System.out.println(w + " out of " + width + " width");
                System.out.println(h + " out of " + width + " height");
            }
        }
    }
    private void addTriangle(Vector3f x, Vector3f y, Vector3f z, Vector2f xUV, Vector2f yUV, Vector2f zUV) {

        triangles.add(new Triangle(
                new Vector3f[] {x, y, z},
                new Vector2f[] {xUV, yUV, zUV}
        ));
    }

    private Vector3f vertexAtAngles(double theta, double phi, double radius) {
        double x = radius * Math.sin(phi) * Math.cos(theta);
        double y = radius * Math.sin(phi) * Math.sin(theta);
        double z = radius * Math.cos(phi);
        return new Vector3f((float)x, (float)y, (float)z);
    }
    @Override
    public void render(PoseStack stack, VertexConsumer consumer, Vector3fc dimensions, Quaternionf rotation) {
        for (var triangle : triangles) {
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
