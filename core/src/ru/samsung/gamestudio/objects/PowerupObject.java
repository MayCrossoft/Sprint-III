package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import ru.samsung.gamestudio.GameSettings;
import ru.samsung.gamestudio.PowerupType;

import java.util.Random;

public class PowerupObject extends GameObject {

    private static final int paddingHorizontal = 40;

    public final PowerupType powerupType;
    public boolean wasCollected;

    // Пульсирующая анимация
    private float pulseTimer = 0f;

    public PowerupObject(PowerupType type, World world) {
        super(
            type.texturePath,
            paddingHorizontal + (new Random()).nextInt(
                Math.max(1, GameSettings.SCREEN_WIDTH - 2 * paddingHorizontal - GameSettings.POWERUP_WIDTH)),
            GameSettings.SCREEN_HEIGHT + GameSettings.POWERUP_HEIGHT / 2,
            GameSettings.POWERUP_WIDTH,
            GameSettings.POWERUP_HEIGHT,
            GameSettings.POWERUP_BIT,
            world
        );
        this.powerupType  = type;
        this.wasCollected = false;
        body.setLinearVelocity(new Vector2(0, -GameSettings.POWERUP_VELOCITY));
    }

    @Override
    public void draw(SpriteBatch batch) {
        pulseTimer += 0.05f;
        float scale = 1f + 0.08f * (float)Math.sin(pulseTimer);
        int drawW = (int)(width  * scale);
        int drawH = (int)(height * scale);
        int offsetX = (drawW - width)  / 2;
        int offsetY = (drawH - height) / 2;

        batch.setColor(1f, 1f, 0.6f, 0.9f); // золотистый
        batch.draw(
            getTexture(),
            getX() - drawW / 2f,
            getY() - drawH / 2f,
            drawW, drawH
        );
        batch.setColor(1f, 1f, 1f, 1f);
    }

    /** Открываем текстуру для кастомной отрисовки */
    public com.badlogic.gdx.graphics.Texture getTexture() {
        return texture;
    }

    public boolean hasToBeDestroyed() {
        return wasCollected || (getY() + height / 2 < 0);
    }

    @Override
    public void hit() {
        wasCollected = true;
    }
}
