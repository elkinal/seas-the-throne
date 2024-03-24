package edu.cornell.jade.seasthethrone.gamemodel.boss;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class CrabBossModel extends BossModel{
    /** Number of frames in boss animation TODO: stop hardcoding animation */
    private static int FRAMES_IN_ANIMATION = 4;

    /** Max health points of crab boss */
    private static int CRAB_BOSS_HEALTH = 100;

    //TODO: stop hardcoding textures
    private Texture CRAB_SHOOTING = new Texture("bosses/crab/crab_shoot.png");

    /**
     * {@link CrabBossModel} constructor using an x and y coordinate.
     *
     * @param x The x-position for this boss in world coordinates
     * @param y The y-position for this boss in world coordinates
     */
    public CrabBossModel(float[] points, float x, float y) {
        super(points, x, y);

        this.filmStrip = new FilmStrip(CRAB_SHOOTING, 1, FRAMES_IN_ANIMATION);
        frameCounter = 1;
        frameDelay = 12;
        scale = 0.16f;

        health = CRAB_BOSS_HEALTH;
    }

    protected boolean createJoints(World world) {
        return true;
    }

    public int getFramesInAnimation() {
        return FRAMES_IN_ANIMATION;
    }
}
