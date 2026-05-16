package com.graphics.flappy;

/**
 * Entidad del pajaro (fase 1: version simple de un solo rectangulo).
 */
public class Bird {
    private final float x;
    private final float width;
    private final float height;
    private final float gravity;
    private final float jumpImpulse;
    private final float maxFallSpeed;

    private float y;
    private float velY;

    public Bird(float x, float y, float width, float height, float gravity, float jumpImpulse, float maxFallSpeed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gravity = gravity;
        this.jumpImpulse = jumpImpulse;
        this.maxFallSpeed = maxFallSpeed;
    }

    public void reset(float y) {
        this.y = y;
        this.velY = 0.0f;
    }

    public void jump() {
        velY = jumpImpulse;
    }

    // integracion de euler (fisica de caida)
    public void update(float dt) {
        velY += gravity * dt;
        if (velY < maxFallSpeed) {
            velY = maxFallSpeed;
        }
        y += velY * dt;
    }

    // hitbox

    public float top() {
        return y + height * 0.5f;
    }

    public float bottom() {
        return y - height * 0.5f;
    }

    public float left() {
        return x - width * 0.5f;
    }

    public float right() {
        return x + width * 0.5f;
    }

    // getters para el dibujado

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float velY() {
        return velY;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }
}
