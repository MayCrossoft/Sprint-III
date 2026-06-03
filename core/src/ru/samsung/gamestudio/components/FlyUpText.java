package ru.samsung.gamestudio.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Текст, который появляется в точке (x,y), поднимается вверх и угасает.
 */
public class FlyUpText {

    private final BitmapFont font;
    private final String     text;
    private float x, y;
    private float alpha = 1f;
    private boolean finished = false;

    public FlyUpText(BitmapFont font, String text, float x, float y) {
        this.font = font;
        this.text = text;
        this.x    = x;
        this.y    = y;
    }

    public void update() {
        if (finished) return;
        y     += 2.5f;
        alpha -= 0.025f;
        if (alpha <= 0f) finished = true;
    }

    public void draw(SpriteBatch batch) {
        if (finished) return;
        font.setColor(1f, 1f, 0.3f, Math.max(0f, alpha));
        font.draw(batch, text, x, y);
        font.setColor(1f, 1f, 1f, 1f);
    }

    public boolean isFinished() { return finished; }
}
