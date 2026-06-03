package ru.samsung.gamestudio.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.samsung.gamestudio.GameSettings;

/**
 * Отображает текущий «уровень опасности» (тир скорости) в виде надписи.
 * Показывается 2 секунды при смене тира, затем угасает.
 */
public class SpeedTierView extends View {

    private final BitmapFont font;
    private String tierLabel = "";
    private float  showTimer = 0f;  // сколько осталось показывать
    private int    lastTier  = -1;

    private static final float SHOW_DURATION = 2.5f;

    public SpeedTierView(BitmapFont font) {
        super(GameSettings.SCREEN_WIDTH / 2f - 120, GameSettings.SCREEN_HEIGHT / 2f - 40);
        this.font = font;
    }

    /**
     * Передавать каждый кадр текущий тир (0,1,2,3).
     * При смене тира автоматически показывается уведомление.
     */
    public void setTier(int tier) {
        if (tier != lastTier) {
            lastTier   = tier;
            tierLabel  = getTierLabel(tier);
            showTimer  = SHOW_DURATION;
        }
    }

    public void update(float delta) {
        if (showTimer > 0f) showTimer -= delta;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (showTimer <= 0f || tierLabel.isEmpty()) return;
        float alpha = Math.min(1f, showTimer / 0.5f); // fade out последние 0.5s
        font.setColor(1f, 0.4f + 0.6f * alpha, 0.1f, alpha);
        font.draw(batch, tierLabel, x, y);
        font.setColor(1f, 1f, 1f, 1f);
    }

    private String getTierLabel(int tier) {
        switch (tier) {
            case 1: return "⚡ Speed UP!";
            case 2: return "🔥 Danger!";
            case 3: return "💀 MAX SPEED!";
            default: return "";
        }
    }
}
