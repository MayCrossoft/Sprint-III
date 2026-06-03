package ru.samsung.gamestudio;

public class GameSettings {

    // Device settings
    public static final int SCREEN_WIDTH  = 720;
    public static final int SCREEN_HEIGHT = 1280;

    // Physics settings
    public static final float STEP_TIME           = 1f / 60f;
    public static final int   VELOCITY_ITERATIONS = 6;
    public static final int   POSITION_ITERATIONS = 6;
    public static final float SCALE               = 0.05f;

    public static float SHIP_FORCE_RATIO = 10;
    public static float TRASH_VELOCITY   = 20;
    public static long  STARTING_TRASH_APPEARANCE_COOL_DOWN = 2000; // ms
    public static int   BULLET_VELOCITY  = 200; // m/s
    public static int   SHOOTING_COOL_DOWN = 1000; // ms

    // Collision bits
    public static final short TRASH_BIT   = 2;
    public static final short SHIP_BIT    = 4;
    public static final short BULLET_BIT  = 8;
    public static final short POWERUP_BIT = 16;

    // Object sizes
    public static final int SHIP_WIDTH   = 150;
    public static final int SHIP_HEIGHT  = 150;
    public static final int TRASH_WIDTH  = 140;
    public static final int TRASH_HEIGHT = 100;
    public static final int BULLET_WIDTH  = 15;
    public static final int BULLET_HEIGHT = 45;

    // Trash types — multipliers relative to base size
    // SMALL: 0.7x size, 1 HP, 150 pts
    // NORMAL: 1.0x, 1 HP, 100 pts
    // LARGE: 1.4x, 2 HP,  60 pts
    // ARMORED: 1.2x, 3 HP, 200 pts
    public static final int TRASH_HP_SMALL   = 1;
    public static final int TRASH_HP_NORMAL  = 1;
    public static final int TRASH_HP_LARGE   = 2;
    public static final int TRASH_HP_ARMORED = 3;

    public static final int TRASH_SCORE_SMALL   = 150;
    public static final int TRASH_SCORE_NORMAL  = 100;
    public static final int TRASH_SCORE_LARGE   =  60;
    public static final int TRASH_SCORE_ARMORED = 200;

    // Powerup
    public static final int   POWERUP_WIDTH    = 60;
    public static final int   POWERUP_HEIGHT   = 60;
    public static final float POWERUP_VELOCITY = 15f;
    public static final long  POWERUP_DURATION = 5000; // ms

    // Explosion animation
    public static final int   EXPLOSION_FRAMES   = 8;   // кол-во кадров спрайтшита (можно 1 — просто fade)
    public static final float EXPLOSION_DURATION = 0.4f; // секунды

    // Screen transition
    public static final float SCREEN_FADE_DURATION = 0.35f; // секунды

    // Speed progression visual thresholds (время в секундах)
    public static final float SPEED_TIER1 = 15f;
    public static final float SPEED_TIER2 = 30f;
    public static final float SPEED_TIER3 = 60f;
}
