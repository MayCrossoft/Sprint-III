package ru.samsung.gamestudio.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import ru.samsung.gamestudio.GameSettings;

/**
 * Простой fade-in / fade-out через ShapeRenderer (чёрный прямоугольник поверх всего).
 *
 * Использование:
 *   fade.startFadeIn();   // экран появляется из черноты
 *   fade.startFadeOut();  // экран уходит в черноту
 *   fade.update(delta);
 *   // рисовать поверх всего:
 *   fade.render();
 *   if (fade.isFadeOutDone()) { ... переключить экран }
 */
public class ScreenFade {

    public enum State { IDLE, FADE_IN, FADE_OUT }

    private State state = State.IDLE;
    private float alpha = 0f;
    private final float duration;
    private final ShapeRenderer shapeRenderer;

    private boolean fadeOutDone = false;

    public ScreenFade(float duration) {
        this.duration      = duration;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void startFadeIn() {
        state       = State.FADE_IN;
        alpha       = 1f;
        fadeOutDone = false;
    }

    public void startFadeOut() {
        state       = State.FADE_OUT;
        alpha       = 0f;
        fadeOutDone = false;
    }

    public void update(float delta) {
        float step = delta / duration;
        switch (state) {
            case FADE_IN:
                alpha -= step;
                if (alpha <= 0f) { alpha = 0f; state = State.IDLE; }
                break;
            case FADE_OUT:
                alpha += step;
                if (alpha >= 1f) { alpha = 1f; state = State.IDLE; fadeOutDone = true; }
                break;
            default:
                break;
        }
    }

    /** Рисовать последним, поверх всего batch.end() */
    public void render() {
        if (alpha <= 0f) return;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, alpha));
        shapeRenderer.rect(0, 0, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public boolean isFadeOutDone()  { return fadeOutDone; }
    public boolean isIdle()         { return state == State.IDLE; }
    public State   getState()       { return state; }

    public void dispose() { shapeRenderer.dispose(); }
}
