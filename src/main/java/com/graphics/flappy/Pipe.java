package com.graphics.flappy;

/**
 * Tuberia con hueco central (gap) para Flappy Bird.
 */
public class Pipe {
    private float x;
    private final float gapCenterY;
    private boolean scoredByPlayer1;
    private boolean scoredByPlayer2;

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

    public boolean scoredByPlayer1() {
        return scoredByPlayer1;
    }

    public boolean scoredByPlayer2() {
        return scoredByPlayer2;
    }

    public void markScoredByPlayer1() {
        scoredByPlayer1 = true;
    }

    public void markScoredByPlayer2() {
        scoredByPlayer2 = true;
    }
}
