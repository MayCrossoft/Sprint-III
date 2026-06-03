package ru.samsung.gamestudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.samsung.gamestudio.GameResources;
import ru.samsung.gamestudio.GameSettings;
import ru.samsung.gamestudio.managers.MemoryManager;
import ru.samsung.gamestudio.MyGdxGame;
import ru.samsung.gamestudio.components.*;

import java.util.ArrayList;

public class SettingsScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;

    MovingBackgroundView backgroundView;
    TextView             titleTextView;
    ImageView            blackoutImageView;
    ButtonView           returnButton;
    TextView             musicSettingView;
    TextView             soundSettingView;
    TextView             clearSettingView;

    ScreenFade screenFade;
    private Runnable pendingAction; // действие после fade-out

    public SettingsScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView    = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        titleTextView     = new TextView(myGdxGame.largeWhiteFont, 256, 956, "Settings");
        blackoutImageView = new ImageView(85, 365, GameResources.BLACKOUT_MIDDLE_IMG_PATH);
        clearSettingView  = new TextView(myGdxGame.commonWhiteFont, 173, 599, "clear records");

        musicSettingView = new TextView(
                myGdxGame.commonWhiteFont, 173, 717,
                "music: " + translateStateToText(MemoryManager.loadIsMusicOn()));

        soundSettingView = new TextView(
                myGdxGame.commonWhiteFont, 173, 658,
                "sound: " + translateStateToText(MemoryManager.loadIsSoundOn()));

        returnButton = new ButtonView(
                280, 447, 160, 70,
                myGdxGame.commonBlackFont,
                GameResources.BUTTON_SHORT_BG_IMG_PATH, "return");

        screenFade = new ScreenFade(GameSettings.SCREEN_FADE_DURATION);
    }

    @Override
    public void show() {
        // Баг-фикс: сбрасываем текст при каждом входе на экран
        clearSettingView.setText("clear records");
        screenFade.startFadeIn();
    }

    @Override
    public void render(float delta) {
        handleInput();

        backgroundView.move();
        screenFade.update(delta);

        // Если fade-out завершён — переключаем экран
        if (pendingAction != null && screenFade.isFadeOutDone()) {
            pendingAction.run();
            pendingAction = null;
        }

        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();
        backgroundView.draw(myGdxGame.batch);
        titleTextView.draw(myGdxGame.batch);
        blackoutImageView.draw(myGdxGame.batch);
        returnButton.draw(myGdxGame.batch);
        musicSettingView.draw(myGdxGame.batch);
        soundSettingView.draw(myGdxGame.batch);
        clearSettingView.draw(myGdxGame.batch);
        myGdxGame.batch.end();

        screenFade.render();
    }

    void handleInput() {
        if (!screenFade.isIdle()) return; // блокируем ввод во время перехода

        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(
                    new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (returnButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                pendingAction = () -> myGdxGame.setScreen(myGdxGame.menuScreen);
                screenFade.startFadeOut();
            }
            if (clearSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.saveTableOfRecords(new ArrayList<>());
                clearSettingView.setText("clear records (cleared)");
            }
            if (musicSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.saveMusicSettings(!MemoryManager.loadIsMusicOn());
                musicSettingView.setText("music: " + translateStateToText(MemoryManager.loadIsMusicOn()));
                myGdxGame.audioManager.updateMusicFlag();
            }
            if (soundSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                MemoryManager.saveSoundSettings(!MemoryManager.loadIsSoundOn());
                soundSettingView.setText("sound: " + translateStateToText(MemoryManager.loadIsSoundOn()));
                myGdxGame.audioManager.updateSoundFlag();
            }
        }
    }

    private String translateStateToText(boolean state) {
        return state ? "ON" : "OFF";
    }

    @Override
    public void dispose() {
        screenFade.dispose();
    }
}
