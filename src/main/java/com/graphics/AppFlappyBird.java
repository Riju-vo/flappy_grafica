package com.graphics;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import com.graphics.flappy.Bird;
import com.graphics.flappy.BirdRenderer;
import com.graphics.flappy.GameState;
import com.graphics.flappy.GameWorld;
import com.graphics.flappy.Pipe;
import com.graphics.flappy.ShapeRenderer;

/**
 * App principal de Flappy Bird.
 * En fase 1 queda como orquestador: ventana, input, loop, render y titulo.
 */
public class AppFlappyBird {

    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 700;

    private static final float BIRD_X = -0.45f;
    private static final float BIRD_WIDTH = 0.10f;
    private static final float BIRD_HEIGHT = 0.10f;
    private static final float GRAVITY = -1.9f;
    private static final float JUMP_IMPULSE = 0.85f;
    private static final float MAX_FALL_SPEED = -1.8f;

    private static final float PIPE_WIDTH = 0.18f;
    private static final float GAP_HEIGHT = 0.48f;
    private static final float PIPE_SPEED = 0.62f;
    private static final float SPAWN_INTERVAL = 1.5f;
    private static final float GAP_MIN_CENTER = -0.45f;
    private static final float GAP_MAX_CENTER = 0.45f;
    private static final float PIPE_SPAWN_X = 1.2f;
    private static final float PIPE_DESPAWN_X = -1.3f;

    private long window;
    private ShapeRenderer renderer;
    private BirdRenderer birdRenderer;
    private GameWorld game;
    private float renderTime;

    private boolean prevSpace;
    private boolean prevR;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("No se pudo iniciar GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // crear ventana de manera oculta
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Flappy Bird OpenGL", 0, 0);
        if (window == 0) {
            throw new RuntimeException("No se pudo crear la ventana");
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1); // activar la sincronizacion vertical (1 refresco antes de intercambiar los
                                  // buffers)
        GLFW.glfwShowWindow(window); // mostrar la ventana creada antes
        GL.createCapabilities(); // Conecta las funciones de java con las nativas de Opengl en la grafica(usar
                                 // los GL11...)

        renderer = new ShapeRenderer();
        renderer.init();
        birdRenderer = new BirdRenderer();

        Bird bird = new Bird(BIRD_X, 0.0f, BIRD_WIDTH, BIRD_HEIGHT, GRAVITY, JUMP_IMPULSE, MAX_FALL_SPEED);
        game = new GameWorld(
                bird,
                PIPE_WIDTH,
                GAP_HEIGHT,
                PIPE_SPEED,
                SPAWN_INTERVAL,
                GAP_MIN_CENTER,
                GAP_MAX_CENTER,
                PIPE_SPAWN_X,
                PIPE_DESPAWN_X);
        updateTitle();
    }

    private void processInput() {
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }

        boolean spaceNow = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS;
        if (spaceNow && !prevSpace) {
            game.startAndJump();
            updateTitle();
        }
        prevSpace = spaceNow;

        boolean rNow = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS;
        if (rNow && !prevR && game.state() == GameState.GAME_OVER) {
            game.reset();
            updateTitle();
        }
        prevR = rNow;
    }

    private void update(float dt) {
        GameState prevState = game.state();
        int prevScore = game.score();
        game.update(dt);
        if (prevState != game.state() || prevScore != game.score()) {
            updateTitle();
        }
    }

    private void render(float dt) {
        renderTime += dt;
        renderer.beginFrame(0.52f, 0.80f, 0.92f);

        for (Pipe pipe : game.pipes()) {
            float gapTop = pipe.gapCenterY() + (game.gapHeight() * 0.5f);
            float gapBottom = pipe.gapCenterY() - (game.gapHeight() * 0.5f);

            float topHeight = 1.0f - gapTop;
            if (topHeight > 0.0f) {
                float topY = gapTop + topHeight * 0.5f;
                renderer.drawRect(pipe.x(), topY, game.pipeWidth(), topHeight, 0.18f, 0.70f, 0.25f);
            }

            float bottomHeight = gapBottom + 1.0f;
            if (bottomHeight > 0.0f) {
                float bottomY = -1.0f + bottomHeight * 0.5f;
                renderer.drawRect(pipe.x(), bottomY, game.pipeWidth(), bottomHeight, 0.18f, 0.70f, 0.25f);
            }
        }

        Bird bird = game.bird();
        birdRenderer.draw(bird, renderer, renderTime);

        if (game.state() == GameState.GAME_OVER) {
            renderer.drawRect(0.0f, 0.0f, 2.0f, 0.22f, 0.15f, 0.18f, 0.22f);
        }
    }

    private void updateTitle() {
        String base = "Flappy Bird OpenGL | Puntos: " + game.score();
        if (game.state() == GameState.START) {
            GLFW.glfwSetWindowTitle(window, base + " | SPACE para empezar");
            return;
        }
        if (game.state() == GameState.GAME_OVER) {
            GLFW.glfwSetWindowTitle(window, base + " | GAME OVER - SPACE o R para reiniciar");
            return;
        }
        GLFW.glfwSetWindowTitle(window, base);
    }

    private void loop() {
        float lastTime = (float) GLFW.glfwGetTime();
        while (!GLFW.glfwWindowShouldClose(window)) {
            float now = (float) GLFW.glfwGetTime();
            float dt = now - lastTime;
            lastTime = now;
            if (dt > 0.033f) {
                dt = 0.033f;
            }

            processInput();
            update(dt);
            render(dt);

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void cleanup() {
        renderer.cleanup();
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public static void main(String[] args) {
        new AppFlappyBird().run();
    }
}
