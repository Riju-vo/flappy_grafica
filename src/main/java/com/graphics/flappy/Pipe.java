package com.graphics.flappy;

/**
 * Tuberia con hueco central (gap) para Flappy Bird.
 */
public class Pipe {
    private float x;
    private final float gapCenterY;
    private boolean scored;

    public Pipe(float x, float gapCenterY) {
        this.x = x;
        this.gapCenterY = gapCenterY;
    }

    public void moveLeft(float speed, float dt) {
        x -= speed * dt;
    }

    public float x() {
        return x;
    }

    public float gapCenterY() {
        return gapCenterY;
    }

    public boolean scored() {
        return scored;
    }

    public void markScored() {
        scored = true;
    }
}

