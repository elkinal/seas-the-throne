package edu.cornell.jade.seasthethrone.gamemodel.player;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class PlayerBulletModel extends BulletModel {

    /** Amount of damage the player bullet inflicts (on bosses) */
    private int damage;

    /** Create a player bullet */
    public PlayerBulletModel(Builder builder) {
        super(builder);
        damage = 5;
    }

    /** Returns amount of damage to inflict (on bosses) */
    public int getDamage() {
        return damage;
    }
}
