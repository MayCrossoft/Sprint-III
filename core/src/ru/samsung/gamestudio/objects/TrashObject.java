package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import ru.samsung.gamestudio.GameSettings;
import ru.samsung.gamestudio.TrashType;

import java.util.Random;

public class TrashObject extends GameObject {

    private static final int paddingHorizontal = 30;

    private int livesLeft;
    public final TrashType trashType;

    public TrashObject(TrashType type, String texturePath, World world) {
        super(
            texturePath,
            type.width / 2 + paddingHorizontal
                + (new Random()).nextInt(Math.max(1,
                    GameSettings.SCREEN_WIDTH - 2 * paddingHorizontal - type.width)),
            GameSettings.SCREEN_HEIGHT + type.height / 2,
            type.width, type.height,
            GameSettings.TRASH_BIT,
            world
        );
        this.trashType = type;
        body.setLinearVelocity(new Vector2(0, -GameSettings.TRASH_VELOCITY));
        livesLeft = type.maxHp;
    }

    public boolean isAlive()   { return livesLeft > 0; }
    public boolean isInFrame() { return getY() + height / 2 > 0; }

    /** Очки, которые даёт этот мусор при уничтожении */
    public int getScoreValue() { return trashType.scoreValue; }

    @Override
    public void draw(SpriteBatch batch) {
        // Тинт по типу, + мигание при ударе пропорционально потере HP
        float hpRatio = (float) livesLeft / trashType.maxHp;
        // При получении урона слегка белеем (flash) — это делается в hit() через флаг
        batch.setColor(trashType.tintR * hpRatio + (1 - hpRatio) * 1f,
                       trashType.tintG * hpRatio + (1 - hpRatio) * 0.3f,
                       trashType.tintB * hpRatio + (1 - hpRatio) * 0.3f,
                       1f);
        super.draw(batch);
        batch.setColor(1f, 1f, 1f, 1f); // сбрасываем тинт
    }

    @Override
    public void hit() {
        livesLeft -= 1;
    }
}
