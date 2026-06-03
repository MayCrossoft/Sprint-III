package ru.samsung.gamestudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.samsung.gamestudio.GameResources;
import ru.samsung.gamestudio.GameSettings;
import ru.samsung.gamestudio.MyGdxGame;
import ru.samsung.gamestudio.components.*;

public class MenuScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;

    MovingBackgroundView backgroundView;
    TextView             titleView;
    ButtonView           startButtonView;
    ButtonView           settingsButtonView;
    ButtonView           exitButtonView;

    ScreenFade screenFade;
    private Runnable pendingAction;

    public MenuScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView     = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        titleView          = new TextView(myGdxGame.largeWhiteFont, 180, 960, "Space Cleaner");
        startButtonView    = new ButtonView(140, 646, 440, 70, myGdxGame.commonBlackFont,
                                            GameResources.BUTTON_LONG_BG_IMG_PATH, "start");
        settingsButtonView = new ButtonView(140, 551, 440, 70, myGdxGame.commonBlackFont,
                                            GameResources.BUTTON_LONG_BG_IMG_PATH, "settings");
        exitButtonView     = new ButtonView(140, 456, 440, 70, myGdxGame.commonBlackFont,
                                            GameResources.BUTTON_LONG_BG_IMG_PATH, "exit");

        screenFade = new ScreenFade(GameSettings.SCREEN_FADE_DURATION);
    }

    @Override
    public void show() {
        screenFade.startFadeIn();
    }

    @Override
    public void render(float delta) {
        handleInput();

        backgroundView.move();
        screenFade.update(delta);

        if (pendingAction != null && screenFade.isFadeOutDone()) {
            pendingAction.run();
            pendingAction = null;
        }

        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();
        backgroundView.draw(myGdxGame.batch);
        titleView.draw(myGdxGame.batch);
        exitButtonView.draw(myGdxGame.batch);
        settingsButtonView.draw(myGdxGame.batch);
        startButtonView.draw(myGdxGame.batch);
        myGdxGame.batch.end();

        screenFade.render();
    }

    private void handleInput() {
        if (!screenFade.isIdle()) return;

        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(
                    new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (startButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                pendingAction = () -> myGdxGame.setScreen(myGdxGame.gameScreen);
                screenFade.startFadeOut();
            }
            if (exitButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                pendingAction = () -> Gdx.app.exit();
                screenFade.startFadeOut();
            }
            if (settingsButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                pendingAction = () -> myGdxGame.setScreen(myGdxGame.settingsScreen);
                screenFade.startFadeOut();
            }
        }
    }

    @Override
    public void dispose() {
        screenFade.dispose();
    }
}
