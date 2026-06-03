package ru.samsung.gamestudio.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import ru.samsung.gamestudio.GameResources;
import ru.samsung.gamestudio.PowerupType;
import ru.samsung.gamestudio.GameSettings;

/**
 * HUD-элемент: иконка + полоска таймера активного пауэрапа.
 * Рисуется в правом нижнем углу экрана.
 */
public class PowerupIndicatorView extends View {

    private static final int ICON_SIZE  = 48;
    private static final int BAR_WIDTH  = 80;
    private static final int BAR_HEIGHT = 10;
    private static final int PADDING    = 8;

    private final ShapeRenderer shapeRenderer;
    private final BitmapFont    font;

    private PowerupType currentType;
    private float       progress; // 0..1
    private Texture     iconTexture;

    public PowerupIndicatorView(BitmapFont font) {
        super(GameSettings.SCREEN_WIDTH - ICON_SIZE - BAR_WIDTH - PADDING * 3,
              PADDING,
              ICON_SIZE + BAR_WIDTH + PADDING * 3,
              ICON_SIZE);
        this.font          = font;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void update(PowerupType type, float progress) {
        if (this.currentType != type) {
            this.currentType = type;
            if (iconTexture != null) iconTexture.dispose();
            iconTexture = (type != null) ? new Texture(type.texturePath) : null;
        }
        this.progress = progress;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (currentType == null || progress <= 0f) return;

        float iconX = x;
        float iconY = PADDING;

        // Иконка
        batch.setColor(1f, 1f, 0.6f, 1f);
        batch.draw(iconTexture, iconX, iconY, ICON_SIZE, ICON_SIZE);
        batch.setColor(1f, 1f, 1f, 1f);

        // Название
        font.setColor(1f, 1f, 0.3f, 1f);
        font.draw(batch, typeName(), iconX + ICON_SIZE + PADDING, iconY + ICON_SIZE - 4);
        font.setColor(1f, 1f, 1f, 1f);

        // Полоска — рисуем через ShapeRenderer (нужен flush batch)
        batch.end();
        drawBar(iconX + ICON_SIZE + PADDING, iconY + 4);
        batch.begin();
    }

    private void drawBar(float bx, float by) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Фон полоски
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.7f);
        shapeRenderer.rect(bx, by, BAR_WIDTH, BAR_HEIGHT);
        // Заполнение
        shapeRenderer.setColor(0.3f, 0.9f, 0.4f, 0.9f);
        shapeRenderer.rect(bx, by, BAR_WIDTH * progress, BAR_HEIGHT);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private String typeName() {
        if (currentType == null) return "";
        switch (currentType) {
            case SHIELD:     return "SHIELD";
            case RAPID_FIRE: return "RAPID";
            case SLOW_MO:    return "SLOW";
            default:         return "";
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        if (iconTexture != null) iconTexture.dispose();
    }
}
