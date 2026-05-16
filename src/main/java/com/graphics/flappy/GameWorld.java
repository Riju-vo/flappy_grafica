package com.graphics.flappy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Logica central del juego (2 jugadores + dificultad progresiva).
 */
public class GameWorld {
    private final Bird bird1;
    private final Bird bird2;
    private final List<Pipe> pipes;
    private final Random random;

    private final float pipeWidth;
    private final float gapHeight;
    private final float basePipeSpeed;
    private final float maxPipeSpeed;
    private final float baseSpawnInterval;
    private final float minSpawnInterval;
    private final float gapMinCenter;
    private final float gapMaxCenter;
    private final float spawnX;
    private final float despawnX;

    private GameState state;
    private float spawnTimer;
    private int score1;
    private int score2;
    private boolean player1Alive;
    private boolean player2Alive;

    public GameWorld(
            Bird bird1,
            Bird bird2,
            float pipeWidth,
            float gapHeight,
            float basePipeSpeed,
            float maxPipeSpeed,
            float baseSpawnInterval,
            float minSpawnInterval,
            float gapMinCenter,
            float gapMaxCenter,
            float spawnX,
            float despawnX
    ) {
        this.bird1 = bird1;
        this.bird2 = bird2;
        this.pipeWidth = pipeWidth;
        this.gapHeight = gapHeight;
        this.basePipeSpeed = basePipeSpeed;
        this.maxPipeSpeed = maxPipeSpeed;
        this.baseSpawnInterval = baseSpawnInterval;
        this.minSpawnInterval = minSpawnInterval;
        this.gapMinCenter = gapMinCenter;
        this.gapMaxCenter = gapMaxCenter;
        this.spawnX = spawnX;
        this.despawnX = despawnX;
        this.pipes = new ArrayList<>();
        this.random = new Random();
        reset();
    }

    public void reset() {
        bird1.reset(0.15f);
        bird2.reset(-0.15f);
        pipes.clear();
        spawnTimer = 0.0f;
        score1 = 0;
        score2 = 0;
        player1Alive = true;
        player2Alive = true;
        state = GameState.START;
    }

    public void jumpPlayer1() {
        if (state == GameState.GAME_OVER) {
            reset();
        }
        if (!player1Alive) {
            return;
        }
        state = GameState.PLAYING;
        bird1.jump();
    }

    public void jumpPlayer2() {
        if (state == GameState.GAME_OVER) {
            reset();
        }
        if (!player2Alive) {
            return;
        }
        state = GameState.PLAYING;
        bird2.jump();
    }

    public void update(float dt) {
        if (state != GameState.PLAYING) {
            return;
        }

        float currentPipeSpeed = currentPipeSpeed();
        float currentSpawnInterval = currentSpawnInterval();

        updateBirds(dt);
        updateGameOverState();
        if (state == GameState.GAME_OVER) {
            return;
        }

        spawnTimer += dt;
        if (spawnTimer >= currentSpawnInterval) {
            spawnTimer = 0.0f;
            spawnPipe();
        }

        Iterator<Pipe> it = pipes.iterator();
        while (it.hasNext()) {
            Pipe pipe = it.next();
            pipe.moveLeft(currentPipeSpeed, dt);

            updateScore(pipe);
            updateCollisions(pipe);
            updateGameOverState();
            if (state == GameState.GAME_OVER) {
                return;
            }

            if (pipe.x() + (pipeWidth * 0.5f) < despawnX) {
                it.remove();
            }
        }
    }

    private void updateBirds(float dt) {
        if (player1Alive) {
            bird1.update(dt);
            if (bird1.top() >= 1.0f || bird1.bottom() <= -1.0f) {
                player1Alive = false;
            }
        }

        if (player2Alive) {
            bird2.update(dt);
            if (bird2.top() >= 1.0f || bird2.bottom() <= -1.0f) {
                player2Alive = false;
            }
        }
    }

    private void updateScore(Pipe pipe) {
        if (player1Alive && pipe.x() + (pipeWidth * 0.5f) < bird1.x() && !pipe.scoredByPlayer1()) {
            pipe.markScoredByPlayer1();
            score1++;
        }

        if (player2Alive && pipe.x() + (pipeWidth * 0.5f) < bird2.x() && !pipe.scoredByPlayer2()) {
            pipe.markScoredByPlayer2();
            score2++;
        }
    }

    private void updateCollisions(Pipe pipe) {
        if (player1Alive && collides(bird1, pipe)) {
            player1Alive = false;
        }
        if (player2Alive && collides(bird2, pipe)) {
            player2Alive = false;
        }
    }

    private void updateGameOverState() {
        if (!player1Alive && !player2Alive) {
            state = GameState.GAME_OVER;
        }
    }

    private void spawnPipe() {
        float gapCenterY = gapMinCenter + random.nextFloat() * (gapMaxCenter - gapMinCenter);
        pipes.add(new Pipe(spawnX, gapCenterY));
    }

    private boolean collides(Bird bird, Pipe pipe) {
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

    public int currentLevel() {
        return 1 + (maxScore() / 5);
    }

    public float currentPipeSpeed() {
        float speed = basePipeSpeed + (currentLevel() - 1) * 0.09f;
        return Math.min(speed, maxPipeSpeed);
    }

    public float currentSpawnInterval() {
        float interval = baseSpawnInterval - (currentLevel() - 1) * 0.07f;
        return Math.max(interval, minSpawnInterval);
    }

    public Bird bird1() {
        return bird1;
    }

    public Bird bird2() {
        return bird2;
    }

    public boolean player1Alive() {
        return player1Alive;
    }

    public boolean player2Alive() {
        return player2Alive;
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

    public int score1() {
        return score1;
    }

    public int score2() {
        return score2;
    }

    public int maxScore() {
        return Math.max(score1, score2);
    }

    public GameState state() {
        return state;
    }
}

