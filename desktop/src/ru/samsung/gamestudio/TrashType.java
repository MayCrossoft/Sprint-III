package ru.samsung.gamestudio;

import ru.samsung.gamestudio.GameSettings;

/**
 * Описывает тип мусора: размер, HP, очки и цветовой тинт для отличия на экране.
 * Тинт применяется через SpriteBatch.setColor() перед отрисовкой.
 */
public enum TrashType {

    SMALL(
        (int)(GameSettings.TRASH_WIDTH  * 0.7f),
        (int)(GameSettings.TRASH_HEIGHT * 0.7f),
        GameSettings.TRASH_HP_SMALL,
        GameSettings.TRASH_SCORE_SMALL,
        0.85f, 0.85f, 1.0f   // синеватый
    ),
    NORMAL(
        GameSettings.TRASH_WIDTH,
        GameSettings.TRASH_HEIGHT,
        GameSettings.TRASH_HP_NORMAL,
        GameSettings.TRASH_SCORE_NORMAL,
        1f, 1f, 1f            // белый (без тинта)
    ),
    LARGE(
        (int)(GameSettings.TRASH_WIDTH  * 1.4f),
        (int)(GameSettings.TRASH_HEIGHT * 1.4f),
        GameSettings.TRASH_HP_LARGE,
        GameSettings.TRASH_SCORE_LARGE,
        1.0f, 0.7f, 0.4f      // оранжеватый
    ),
    ARMORED(
        (int)(GameSettings.TRASH_WIDTH  * 1.2f),
        (int)(GameSettings.TRASH_HEIGHT * 1.2f),
        GameSettings.TRASH_HP_ARMORED,
        GameSettings.TRASH_SCORE_ARMORED,
        0.6f, 0.6f, 0.6f      // серый
    );

    public final int width;
    public final int height;
    public final int maxHp;
    public final int scoreValue;
    public final float tintR, tintG, tintB;

    TrashType(int width, int height, int maxHp, int scoreValue,
              float tintR, float tintG, float tintB) {
        this.width      = width;
        this.height     = height;
        this.maxHp      = maxHp;
        this.scoreValue = scoreValue;
        this.tintR      = tintR;
        this.tintG      = tintG;
        this.tintB      = tintB;
    }

    /** Случайный тип с весами: SMALL 30%, NORMAL 45%, LARGE 15%, ARMORED 10% */
    public static TrashType random() {
        float r = (float)Math.random();
        if (r < 0.30f) return SMALL;
        if (r < 0.75f) return NORMAL;
        if (r < 0.90f) return LARGE;
        return ARMORED;
    }
}
