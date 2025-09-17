package world.landfall.deepspace.render.shapes;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Triangle {
    public final Vector3f[] vertexes;
    public final Vector2f[] UV;
    public final Vector3f[] normals;
    public Triangle(Vector3f[] _vertexes, Vector2f[] _UV, Vector3f[] _normals) {
        vertexes = _vertexes;
        UV = _UV;
        normals = _normals;
    }
}