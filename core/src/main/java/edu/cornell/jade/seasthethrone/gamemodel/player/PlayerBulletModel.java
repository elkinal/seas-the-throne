package edu.cornell.jade.seasthethrone.gamemodel.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.gamemodel.BulletModel;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class PlayerBulletModel extends BulletModel {

    /** Amount of damage the player bullet inflicts (on bosses) */
    private int damage;

    /** Create a player bullet */
    public PlayerBulletModel(Builder builder) {
        super(builder);
        damage = 5;
    }
    @Override
    public void draw(RenderingEngine renderer) {
        int frame = getFrameNumber();
        FilmStrip filmStrip = getFilmStrip();
        filmStrip.setFrame(frame);

        Vector2 pos = getPosition();

        renderer.draw(filmStrip, pos.x, pos.y, Color.PINK, angle());
    }

    /** Returns amount of damage to inflict (on bosses) */
    public int getDamage() {
        return damage;
    }
}
