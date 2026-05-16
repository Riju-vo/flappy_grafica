package com.graphics.flappy;

/**
 * Dibuja un pajaro compuesto por varias figuras geometricas.
 */
public class BirdRenderer {

    public void draw(
            Bird bird,
            ShapeRenderer renderer,
            float timeSeconds,
            float bodyR,
            float bodyG,
            float bodyB,
            float wingR,
            float wingG,
            float wingB
    ) {
        float tilt = clamp(bird.velY() * 0.7f, -0.55f, 0.45f);
        float flap = (float) Math.sin(timeSeconds * 14.0f);
        float jumpBoost = clamp(bird.velY() * 0.9f, -0.20f, 0.35f);
        float wingAngle = -0.20f + flap * 0.35f + jumpBoost;

        float bx = bird.x();
        float by = bird.y();
        float w = bird.width();
        float h = bird.height();

        // Cuerpo principal.
        renderer.drawRect(bx, by, w * 1.00f, h * 0.82f, tilt, bodyR, bodyG, bodyB);

        // Cola.
        renderer.drawTriangle(
                localX(bx, -w * 0.62f, 0.0f, tilt),
                localY(by, -w * 0.62f, 0.0f, tilt),
                w * 0.38f,
                h * 0.38f,
                tilt + 3.14159f,
                0.94f, 0.70f, 0.15f);

        // Ala (animada).
        renderer.drawRect(
                localX(bx, -w * 0.08f, h * 0.04f, tilt),
                localY(by, -w * 0.08f, h * 0.04f, tilt),
                w * 0.58f,
                h * 0.34f,
                tilt + wingAngle,
                wingR, wingG, wingB);

        // Pico (triangulo distinguible).
        renderer.drawTriangle(
                localX(bx, w * 0.60f, 0.0f, tilt),
                localY(by, w * 0.60f, 0.0f, tilt),
                w * 0.35f,
                h * 0.30f,
                tilt,
                0.98f, 0.52f, 0.12f);

        // Ojo.
        renderer.drawRect(
                localX(bx, w * 0.24f, h * 0.15f, tilt),
                localY(by, w * 0.24f, h * 0.15f, tilt),
                w * 0.18f,
                h * 0.18f,
                tilt,
                1.0f, 1.0f, 1.0f);
        // Pupila.
        renderer.drawRect(
                localX(bx, w * 0.26f, h * 0.13f, tilt),
                localY(by, w * 0.26f, h * 0.13f, tilt),
                w * 0.07f,
                h * 0.07f,
                tilt,
                0.08f, 0.08f, 0.08f);
    }

    private float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    private float localX(float cx, float localX, float localY, float rotation) {
        return cx + (float) (Math.cos(rotation) * localX - Math.sin(rotation) * localY);
    }

    private float localY(float cy, float localX, float localY, float rotation) {
        return cy + (float) (Math.sin(rotation) * localX + Math.cos(rotation) * localY);
    }
}
