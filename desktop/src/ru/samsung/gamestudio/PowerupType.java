package ru.samsung.gamestudio;

public enum PowerupType {
    SHIELD(GameResources.POWERUP_SHIELD_IMG_PATH),
    RAPID_FIRE(GameResources.POWERUP_RAPID_IMG_PATH),
    SLOW_MO(GameResources.POWERUP_SLOW_IMG_PATH);

    public final String texturePath;

    PowerupType(String texturePath) {
        this.texturePath = texturePath;
    }

    public static PowerupType random() {
        PowerupType[] vals = values();
        return vals[(int)(Math.random() * vals.length)];
    }
}
