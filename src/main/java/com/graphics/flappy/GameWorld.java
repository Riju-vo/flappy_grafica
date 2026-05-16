package com.graphics.flappy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Logica central del juego (sin OpenGL).
 */
public class GameWorld {
    private final Bird bird;
    private final List<Pipe> pipes;
    private final Random random;

    private final float pipeWidth;
    private final float gapHeight;
    private final float pipeSpeed;
    private final float spawnInterval;
    private final float gapMinCenter;
    private final float gapMaxCenter;
    private final float spawnX;
    private final float despawnX;

    private GameState state;
    private float spawnTimer;
    private int score;

    public GameWorld(
            Bird bird,
            float pipeWidth,
            float gapHeight,
            float pipeSpeed,
            float spawnInterval,
            float gapMinCenter,
            float gapMaxCenter,
            float spawnX,
            float despawnX
    ) {
        this.bird = bird;
        this.pipeWidth = pipeWidth;
        this.gapHeight = gapHeight;
        this.pipeSpeed = pipeSpeed;
        this.spawnInterval = spawnInterval;
        this.gapMinCenter = gapMinCenter;
        this.gapMaxCenter = gapMaxCenter;
        this.spawnX = spawnX;
        this.despawnX = despawnX;
        this.pipes = new ArrayList<>();
        this.random = new Random();
        reset();
    }

    public void reset() {
        bird.reset(0.0f);
        pipes.clear();
        spawnTimer = 0.0f;
        score = 0;
        state = GameState.START;
    }

    public void startAndJump() {
        if (state == GameState.GAME_OVER) {
            reset();
        }
        state = GameState.PLAYING;
        bird.jump();
    }

    public void update(float dt) {
        if (state != GameState.PLAYING) {
            return;
        }

        bird.update(dt);
        if (bird.top() >= 1.0f || bird.bottom() <= -1.0f) {
            state = GameState.GAME_OVER;
            return;
        }

        spawnTimer += dt;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0.0f;
            spawnPipe();
        }

        Iterator<Pipe> it = pipes.iterator();
        while (it.hasNext()) {
            Pipe pipe = it.next();
            pipe.moveLeft(pipeSpeed, dt);

            if (pipe.x() + (pipeWidth * 0.5f) < bird.x() && !pipe.scored()) {
                pipe.markScored();
                score++;
            }

            if (collides(pipe)) {
                state = GameState.GAME_OVER;
                return;
            }

            if (pipe.x() + (pipeWidth * 0.5f) < despawnX) {
                it.remove();
            }
        }
    }

    private void spawnPipe() {
        float gapCenterY = gapMinCenter + random.nextFloat() * (gapMaxCenter - gapMinCenter);
        pipes.add(new Pipe(spawnX, gapCenterY));
    }

    private boolean collides(Pipe pipe) {
        float pipeLeft = pipe.x() - (pipeWidth * 0.5f);
        float pipeRight = pipe.x() + (pipeWidth * 0.5f);
        boolean overlapX = bird.right() > pipeLeft && bird.left() < pipeRight;
        if (!overlapX) {
            return false;
        }

        float gapTop = pipe.gapCenterY() + (gapHeight * 0.5f);
        float gapBottom = pipe.gapCenterY() - (gapHeight * 0.5f);
        return bird.top() > gapTop || bird.bottom() < gapBottom;
    }

    public Bird bird() {
        return bird;
    }

    public List<Pipe> pipes() {
        return pipes;
    }

    public float pipeWidth() {
        return pipeWidth;
    }

    public float gapHeight() {
        return gapHeight;
    }

    public int score() {
        return score;
    }

    public GameState state() {
        return state;
    }
}

