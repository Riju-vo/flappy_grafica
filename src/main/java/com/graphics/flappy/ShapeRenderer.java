package com.graphics.flappy;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * Renderer 2D minimo con un quad base y uniforms de offset/escala/color.
 */
public class ShapeRenderer {
    private int program;
    private int quadVao;
    private int quadVbo;
    private int triVao;
    private int triVbo;
    private int uOffsetLocation;
    private int uScaleLocation;
    private int uRotationLocation;
    private int uColorLocation;

    public void init() {
        createShaders();
        createQuadBase();
        createTriangleBase();
    }

    public void beginFrame(float r, float g, float b) {
        GL11.glClearColor(r, g, b, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL20.glUseProgram(program);
    }

    public void drawRect(float x, float y, float width, float height, float r, float g, float b) {
        drawRect(x, y, width, height, 0.0f, r, g, b);
    }

    public void drawRect(float x, float y, float width, float height, float rotation, float r, float g, float b) {
        GL30.glBindVertexArray(quadVao);
        GL20.glUniform2f(uOffsetLocation, x, y);
        GL20.glUniform2f(uScaleLocation, width, height);
        GL20.glUniform1f(uRotationLocation, rotation);
        GL20.glUniform3f(uColorLocation, r, g, b);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
    }

    public void drawTriangle(float x, float y, float width, float height, float rotation, float r, float g, float b) {
        GL30.glBindVertexArray(triVao);
        GL20.glUniform2f(uOffsetLocation, x, y);
        GL20.glUniform2f(uScaleLocation, width, height);
        GL20.glUniform1f(uRotationLocation, rotation);
        GL20.glUniform3f(uColorLocation, r, g, b);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
    }

    public void cleanup() {
        GL30.glDeleteVertexArrays(quadVao);
        GL15.glDeleteBuffers(quadVbo);
        GL30.glDeleteVertexArrays(triVao);
        GL15.glDeleteBuffers(triVbo);
        GL20.glDeleteProgram(program);
    }

    private void createShaders() {
        String vertexSrc = """
                #version 330 core
                layout (location = 0) in vec3 aPos;
                uniform vec2 uOffset;
                uniform vec2 uScale;
                uniform float uRotation;
                void main() {
                    float c = cos(uRotation);
                    float s = sin(uRotation);
                    mat2 rot = mat2(c, s, -s, c);
                    vec2 finalPos = rot * (aPos.xy * uScale) + uOffset;
                    gl_Position = vec4(finalPos, aPos.z, 1.0);
                }
                """;

        String fragmentSrc = """
                #version 330 core
                uniform vec3 uColor;
                out vec4 fragColor;
                void main() {
                    fragColor = vec4(uColor, 1.0);
                }
                """;

        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShader, vertexSrc);
        GL20.glCompileShader(vertexShader);
        ensureShaderCompiled(vertexShader, "Vertex");

        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShader, fragmentSrc);
        GL20.glCompileShader(fragmentShader);
        ensureShaderCompiled(fragmentShader, "Fragment");

        program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vertexShader);
        GL20.glAttachShader(program, fragmentShader);
        GL20.glLinkProgram(program);
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Error al enlazar programa: " + GL20.glGetProgramInfoLog(program));
        }

        uOffsetLocation = GL20.glGetUniformLocation(program, "uOffset");
        uScaleLocation = GL20.glGetUniformLocation(program, "uScale");
        uRotationLocation = GL20.glGetUniformLocation(program, "uRotation");
        uColorLocation = GL20.glGetUniformLocation(program, "uColor");
        if (uOffsetLocation == -1 || uScaleLocation == -1 || uRotationLocation == -1 || uColorLocation == -1) {
            throw new RuntimeException("No se pudieron obtener uniforms del shader");
        }

        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
    }

    private void createQuadBase() {
        float[] vertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, 0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f
        };

        quadVao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(quadVao);

        quadVbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, quadVbo);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    private void createTriangleBase() {
        float[] vertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, 0.0f, 0.0f,
                -0.5f, 0.5f, 0.0f
        };

        triVao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(triVao);

        triVbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, triVbo);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    private void ensureShaderCompiled(int shader, String type) {
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException(type + " shader: " + GL20.glGetShaderInfoLog(shader));
        }
    }
}
