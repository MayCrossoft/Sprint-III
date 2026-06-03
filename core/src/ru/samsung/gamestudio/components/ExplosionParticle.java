package ru.samsung.gamestudio.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Лёгкая анимация взрыва: несколько частиц разлетаются и угасают.
 * Не требует спрайтшита — использует текстуру трэша с тинтом.
 */
public class ExplosionParticle {

    private static final int PARTICLE_COUNT = 6;

    private final float[] px, py, vx, vy;
    private float alpha;
    private float size;
    private final Texture texture;

    private boolean finished = false;

    public ExplosionParticle(float cx, float cy, float size, Texture texture) {
        this.size    = size * 0.4f;
        this.alpha   = 1f;
        this.texture = texture;

        px = new float[PARTICLE_COUNT];
        py = new float[PARTICLE_COUNT];
        vx = new float[PARTICLE_COUNT];
        vy = new float[PARTICLE_COUNT];

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = i * (2 * Math.PI / PARTICLE_COUNT) + Math.random() * 0.5;
            float speed  = 3f + (float)Math.random() * 3f;
            px[i] = cx;
            py[i] = cy;
            vx[i] = (float)Math.cos(angle) * speed;
            vy[i] = (float)Math.sin(angle) * speed;
        }
    }

    public void update() {
        if (finished) return;
        alpha -= 0.045f;
        size  *= 0.96f;
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            px[i] += vx[i];
            py[i] += vy[i];
            vy[i] -= 0.15f; // гравитация
        }
        if (alpha <= 0f) finished = true;
    }

    public void draw(SpriteBatch batch) {
        if (finished) return;
        batch.setColor(1f, 0.6f, 0.1f, Math.max(0f, alpha));
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            batch.draw(texture, px[i] - size / 2f, py[i] - size / 2f, size, size);
        }
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public boolean isFinished() { return finished; }
}
