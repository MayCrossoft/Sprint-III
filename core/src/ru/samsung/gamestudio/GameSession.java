package ru.samsung.gamestudio;

import com.badlogic.gdx.utils.TimeUtils;
import ru.samsung.gamestudio.managers.MemoryManager;

import java.util.ArrayList;

public class GameSession {

    public GameState state;
    long nextTrashSpawnTime;
    long nextPowerupSpawnTime;
    long sessionStartTime;
    long pauseStartTime;
    private int score;
    int destructedTrashNumber;
    private int bonusScore; // очки от типов трэша

    public GameSession() {}

    public void startGame() {
        state                 = GameState.PLAYING;
        score                 = 0;
        bonusScore            = 0;
        destructedTrashNumber = 0;
        sessionStartTime      = TimeUtils.millis();
        nextTrashSpawnTime    = sessionStartTime
                + (long)(GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN * getTrashPeriodCoolDown());
        nextPowerupSpawnTime  = sessionStartTime + 8000L; // первый пауэрап через 8 сек
    }

    public void pauseGame() {
        state          = GameState.PAUSED;
        pauseStartTime = TimeUtils.millis();
    }

    public void resumeGame() {
        long pauseDuration    = TimeUtils.millis() - pauseStartTime;
        state                 = GameState.PLAYING;
        // Баг-фикс: сдвигаем все временные метки на длительность паузы
        sessionStartTime     += pauseDuration;
        nextTrashSpawnTime   += pauseDuration;
        nextPowerupSpawnTime += pauseDuration;
    }

    public void endGame() {
        updateScore();
        state = GameState.ENDED;
        ArrayList<Integer> recordsTable = MemoryManager.loadRecordsTable();
        if (recordsTable == null) recordsTable = new ArrayList<>();
        int foundIdx = 0;
        for (; foundIdx < recordsTable.size(); foundIdx++) {
            if (recordsTable.get(foundIdx) < getScore()) break;
        }
        recordsTable.add(foundIdx, getScore());
        MemoryManager.saveTableOfRecords(recordsTable);
    }

    /** Регистрирует уничтожение мусора с очками по типу */
    public void destructionRegistration(int scoreValue) {
        destructedTrashNumber += 1;
        bonusScore            += scoreValue;
    }

    public void updateScore() {
        score = (int)(TimeUtils.millis() - sessionStartTime) / 100 + bonusScore;
    }

    public int getScore() { return score; }

    // ── Trash spawn ───────────────────────────────────────────────────────────

    public boolean shouldSpawnTrash() {
        if (nextTrashSpawnTime <= TimeUtils.millis()) {
            nextTrashSpawnTime = TimeUtils.millis()
                    + (long)(GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN * getTrashPeriodCoolDown());
            return true;
        }
        return false;
    }

    // ── Powerup spawn ─────────────────────────────────────────────────────────

    public boolean shouldSpawnPowerup() {
        if (nextPowerupSpawnTime <= TimeUtils.millis()) {
            // Следующий пауэрап через 12-20 секунд
            nextPowerupSpawnTime = TimeUtils.millis() + 12000L + (long)(Math.random() * 8000L);
            return true;
        }
        return false;
    }

    // ── Speed tier (визуальная обратная связь) ────────────────────────────────

    /** 0=нормально, 1=быстро, 2=опасно, 3=макс */
    public int getSpeedTier() {
        float elapsed = (TimeUtils.millis() - sessionStartTime) / 1000f;
        if (elapsed >= GameSettings.SPEED_TIER3) return 3;
        if (elapsed >= GameSettings.SPEED_TIER2) return 2;
        if (elapsed >= GameSettings.SPEED_TIER1) return 1;
        return 0;
    }

    private float getTrashPeriodCoolDown() {
        return (float)Math.exp(-0.001 * (TimeUtils.millis() - sessionStartTime + 1) / 1000);
    }
}
