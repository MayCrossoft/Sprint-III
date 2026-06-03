package ru.samsung.gamestudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.samsung.gamestudio.*;
import ru.samsung.gamestudio.components.*;
import ru.samsung.gamestudio.managers.ContactManager;
import ru.samsung.gamestudio.managers.MemoryManager;
import ru.samsung.gamestudio.objects.*;

import java.util.ArrayList;
import java.util.Iterator;

public class GameScreen extends ScreenAdapter {

    MyGdxGame    myGdxGame;
    GameSession  gameSession;
    ShipObject   shipObject;

    ArrayList<TrashObject>     trashArray   = new ArrayList<>();
    ArrayList<BulletObject>    bulletArray  = new ArrayList<>();
    ArrayList<PowerupObject>   powerupArray = new ArrayList<>();

    // Анимации
    ArrayList<ExplosionParticle> explosions  = new ArrayList<>();
    ArrayList<FlyUpText>         flyUpTexts  = new ArrayList<>();

    ContactManager contactManager;

    // ── PLAY state UI ────────────────────────────────────────────────────────
    MovingBackgroundView backgroundView;
    ImageView            topBlackoutView;
    LiveView             liveView;
    TextView             scoreTextView;
    ButtonView           pauseButton;
    PowerupIndicatorView powerupIndicator;
    SpeedTierView        speedTierView;

    // ── PAUSED state UI ──────────────────────────────────────────────────────
    ImageView  fullBlackoutView;
    TextView   pauseTextView;
    ButtonView homeButton;
    ButtonView continueButton;

    // ── ENDED state UI ───────────────────────────────────────────────────────
    TextView        recordsTextView;
    RecordsListView recordsListView;
    ButtonView      homeButton2;

    // ── Transitions ──────────────────────────────────────────────────────────
    ScreenFade   screenFade;
    private Runnable pendingAction;

    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame   = myGdxGame;
        gameSession      = new GameSession();
        contactManager   = new ContactManager(myGdxGame.world);

        shipObject = new ShipObject(
                GameSettings.SCREEN_WIDTH / 2, 150,
                GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
                GameResources.SHIP_IMG_PATH, myGdxGame.world);

        // ── PLAY UI ──
        backgroundView   = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        topBlackoutView  = new ImageView(0, 1180, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView         = new LiveView(305, 1215);
        scoreTextView    = new TextView(myGdxGame.commonWhiteFont, 50, 1215);
        pauseButton      = new ButtonView(605, 1200, 46, 54, GameResources.PAUSE_IMG_PATH);
        powerupIndicator = new PowerupIndicatorView(myGdxGame.commonWhiteFont);
        speedTierView    = new SpeedTierView(myGdxGame.largeWhiteFont);

        // ── PAUSED UI ──
        fullBlackoutView = new ImageView(0, 0, GameResources.BLACKOUT_FULL_IMG_PATH);
        pauseTextView    = new TextView(myGdxGame.largeWhiteFont, 282, 842, "Pause");
        homeButton       = new ButtonView(138, 695, 200, 70, myGdxGame.commonBlackFont,
                                          GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        continueButton   = new ButtonView(393, 695, 200, 70, myGdxGame.commonBlackFont,
                                          GameResources.BUTTON_SHORT_BG_IMG_PATH, "Continue");

        // ── ENDED UI ──
        recordsListView  = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        recordsTextView  = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        homeButton2      = new ButtonView(280, 365, 160, 70, myGdxGame.commonBlackFont,
                                          GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");

        screenFade = new ScreenFade(GameSettings.SCREEN_FADE_DURATION);
    }

    @Override
    public void show() {
        restartGame();
        screenFade.startFadeIn();
    }

    // ── RENDER ────────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {

        // Применяем отложенные коллизии (баг-фикс ContactManager)
        contactManager.processQueue();

        handleInput();

        if (gameSession.state == GameState.PLAYING) {
            updatePlaying(delta);
        }

        screenFade.update(delta);
        if (pendingAction != null && screenFade.isFadeOutDone()) {
            pendingAction.run();
            pendingAction = null;
        }

        draw();
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    private void updatePlaying(float delta) {

        // Спавн трэша
        if (gameSession.shouldSpawnTrash()) {
            TrashType type = TrashType.random();
            trashArray.add(new TrashObject(type, GameResources.TRASH_IMG_PATH, myGdxGame.world));
        }

        // Спавн пауэрапа
        if (gameSession.shouldSpawnPowerup()) {
            PowerupType pt = PowerupType.random();
            powerupArray.add(new PowerupObject(pt, myGdxGame.world));
        }

        // Стрельба
        if (shipObject.needToShoot()) {
            bulletArray.add(new BulletObject(
                    shipObject.getX(), shipObject.getY() + shipObject.height / 2,
                    GameSettings.BULLET_WIDTH, GameSettings.BULLET_HEIGHT,
                    GameResources.BULLET_IMG_PATH, myGdxGame.world));
            if (myGdxGame.audioManager.isSoundOn)
                myGdxGame.audioManager.shootSound.play();
        }

        // Конец игры
        if (!shipObject.isAlive()) {
            gameSession.endGame();
            recordsListView.setRecords(MemoryManager.loadRecordsTable());
            screenFade.startFadeIn(); // fade при появлении экрана рекордов
        }

        updateTrash();
        updateBullets();
        updatePowerups();
        updateAnimations(delta);

        backgroundView.move();
        gameSession.updateScore();
        scoreTextView.setText("Score: " + gameSession.getScore());
        liveView.setLeftLives(shipObject.getLiveLeft());

        shipObject.updatePowerup();
        powerupIndicator.update(shipObject.getActivePowerup(), shipObject.getPowerupProgress());

        // Скорость трэша при slow-mo
        float trashSpeed = shipObject.isSlowMoActive()
                ? GameSettings.TRASH_VELOCITY * 0.4f
                : GameSettings.TRASH_VELOCITY;
        for (TrashObject t : trashArray) {
            com.badlogic.gdx.math.Vector2 vel = t.body.getLinearVelocity();
            t.body.setLinearVelocity(vel.x, -trashSpeed);
        }

        speedTierView.setTier(gameSession.getSpeedTier());
        speedTierView.update(delta);

        myGdxGame.stepWorld();
    }

    private void updateTrash() {
        for (int i = 0; i < trashArray.size(); i++) {
            TrashObject trash = trashArray.get(i);
            boolean destroy   = !trash.isAlive() || !trash.isInFrame();

            if (!trash.isAlive()) {
                int pts = trash.getScoreValue();
                gameSession.destructionRegistration(pts);
                // Взрыв
                explosions.add(new ExplosionParticle(
                        trash.getX(), trash.getY(), trash.width, trash.texture));
                // Fly-up текст
                flyUpTexts.add(new FlyUpText(
                        myGdxGame.commonWhiteFont,
                        "+" + pts,
                        trash.getX() - 20, trash.getY() + 20));
                if (myGdxGame.audioManager.isSoundOn)
                    myGdxGame.audioManager.explosionSound.play(0.2f);
            }

            if (destroy) {
                myGdxGame.world.destroyBody(trash.body);
                trashArray.remove(i--);
            }
        }
    }

    private void updateBullets() {
        for (int i = 0; i < bulletArray.size(); i++) {
            if (bulletArray.get(i).hasToBeDestroyed()) {
                myGdxGame.world.destroyBody(bulletArray.get(i).body);
                bulletArray.remove(i--);
            }
        }
    }

    private void updatePowerups() {
        for (int i = 0; i < powerupArray.size(); i++) {
            PowerupObject pu = powerupArray.get(i);
            if (pu.hasToBeDestroyed()) {
                if (pu.wasCollected) {
                    shipObject.activatePowerup(pu.powerupType);
                    if (myGdxGame.audioManager.isSoundOn)
                        myGdxGame.audioManager.explosionSound.play(0.5f);
                }
                myGdxGame.world.destroyBody(pu.body);
                powerupArray.remove(i--);
            }
        }
    }

    private void updateAnimations(float delta) {
        Iterator<ExplosionParticle> eit = explosions.iterator();
        while (eit.hasNext()) {
            ExplosionParticle ep = eit.next();
            ep.update();
            if (ep.isFinished()) eit.remove();
        }
        Iterator<FlyUpText> fit = flyUpTexts.iterator();
        while (fit.hasNext()) {
            FlyUpText ft = fit.next();
            ft.update();
            if (ft.isFinished()) fit.remove();
        }
    }

    // ── INPUT ─────────────────────────────────────────────────────────────────

    private void handleInput() {
        if (!screenFade.isIdle()) return;

        if (Gdx.input.isTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(
                    new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            switch (gameSession.state) {
                case PLAYING:
                    if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        gameSession.pauseGame();
                    }
                    shipObject.move(myGdxGame.touch);
                    break;

                case PAUSED:
                    if (Gdx.input.justTouched()) {
                        if (continueButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            gameSession.resumeGame();
                        }
                        if (homeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            pendingAction = () -> myGdxGame.setScreen(myGdxGame.menuScreen);
                            screenFade.startFadeOut();
                        }
                    }
                    break;

                case ENDED:
                    if (Gdx.input.justTouched()) {
                        if (homeButton2.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                            pendingAction = () -> myGdxGame.setScreen(myGdxGame.menuScreen);
                            screenFade.startFadeOut();
                        }
                    }
                    break;
            }
        }
    }

    // ── DRAW ──────────────────────────────────────────────────────────────────

    private void draw() {
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();

        backgroundView.draw(myGdxGame.batch);

        for (TrashObject trash : trashArray)     trash.draw(myGdxGame.batch);
        for (PowerupObject pu : powerupArray)    pu.draw(myGdxGame.batch);
        shipObject.draw(myGdxGame.batch);
        for (BulletObject bullet : bulletArray)  bullet.draw(myGdxGame.batch);

        // Анимации
        for (ExplosionParticle ep : explosions)  ep.draw(myGdxGame.batch);
        for (FlyUpText ft : flyUpTexts)          ft.draw(myGdxGame.batch);

        // HUD
        topBlackoutView.draw(myGdxGame.batch);
        scoreTextView.draw(myGdxGame.batch);
        liveView.draw(myGdxGame.batch);
        pauseButton.draw(myGdxGame.batch);
        powerupIndicator.draw(myGdxGame.batch);
        speedTierView.draw(myGdxGame.batch);

        // Оверлеи
        if (gameSession.state == GameState.PAUSED) {
            fullBlackoutView.draw(myGdxGame.batch);
            pauseTextView.draw(myGdxGame.batch);
            homeButton.draw(myGdxGame.batch);
            continueButton.draw(myGdxGame.batch);
        } else if (gameSession.state == GameState.ENDED) {
            fullBlackoutView.draw(myGdxGame.batch);
            recordsTextView.draw(myGdxGame.batch);
            recordsListView.draw(myGdxGame.batch);
            homeButton2.draw(myGdxGame.batch);
        }

        myGdxGame.batch.end();

        screenFade.render(); // поверх всего
    }

    // ── RESTART ───────────────────────────────────────────────────────────────

    private void restartGame() {
        // Баг-фикс: уничтожаем тела пуль (раньше не делалось)
        for (BulletObject b : bulletArray) myGdxGame.world.destroyBody(b.body);
        bulletArray.clear();

        for (TrashObject t : trashArray)   myGdxGame.world.destroyBody(t.body);
        trashArray.clear();

        for (PowerupObject p : powerupArray) myGdxGame.world.destroyBody(p.body);
        powerupArray.clear();

        explosions.clear();
        flyUpTexts.clear();

        if (shipObject != null) myGdxGame.world.destroyBody(shipObject.body);
        shipObject = new ShipObject(
                GameSettings.SCREEN_WIDTH / 2, 150,
                GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
                GameResources.SHIP_IMG_PATH, myGdxGame.world);

        gameSession.startGame();
    }

    @Override
    public void dispose() {
        screenFade.dispose();
        powerupIndicator.dispose();
    }
}
