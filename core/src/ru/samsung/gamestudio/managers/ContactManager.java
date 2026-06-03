package ru.samsung.gamestudio.managers;

import com.badlogic.gdx.physics.box2d.*;
import ru.samsung.gamestudio.GameSettings;
import ru.samsung.gamestudio.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Баг-фикс: hit() больше не вызывается прямо внутри beginContact (во время Box2D step).
 * Контакты накапливаются в очереди и применяются после step через processQueue().
 */
public class ContactManager {

    private final List<GameObject[]> contactQueue = new ArrayList<>();

    public ContactManager(World world) {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixA = contact.getFixtureA();
                Fixture fixB = contact.getFixtureB();

                int cA = fixA.getFilterData().categoryBits;
                int cB = fixB.getFilterData().categoryBits;

                boolean trashBullet = (cA == GameSettings.TRASH_BIT && cB == GameSettings.BULLET_BIT)
                                   || (cB == GameSettings.TRASH_BIT && cA == GameSettings.BULLET_BIT);
                boolean trashShip   = (cA == GameSettings.TRASH_BIT && cB == GameSettings.SHIP_BIT)
                                   || (cB == GameSettings.TRASH_BIT && cA == GameSettings.SHIP_BIT);
                boolean powerupShip = (cA == GameSettings.POWERUP_BIT && cB == GameSettings.SHIP_BIT)
                                   || (cB == GameSettings.POWERUP_BIT && cA == GameSettings.SHIP_BIT);

                if (trashBullet || trashShip || powerupShip) {
                    contactQueue.add(new GameObject[]{
                        (GameObject) fixA.getUserData(),
                        (GameObject) fixB.getUserData()
                    });
                }
            }
            @Override public void endContact(Contact c) {}
            @Override public void preSolve(Contact c, Manifold m) {}
            @Override public void postSolve(Contact c, ContactImpulse i) {}
        });
    }

    /** Вызывать каждый кадр ПОСЛЕ world.step() */
    public void processQueue() {
        for (GameObject[] pair : contactQueue) {
            pair[0].hit();
            pair[1].hit();
        }
        contactQueue.clear();
    }
}
