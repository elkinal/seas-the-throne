package edu.cornell.jade.seasthethrone.gamemodel.gate;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class GateWallModel extends BoxModel implements Renderable {
    private final float WORLD_SCALE;

    private TextureRegion texture;

    public GateWallModel(LevelObject obs, float worldScale) {
        super(obs.x, obs.y, obs.width, obs.height);
        this.texture = obs.texture;
        this.WORLD_SCALE = worldScale;
        texture.setRegion(0, 0, (int) (obs.width / WORLD_SCALE), (int) texture.getRegionHeight());

    }

    public void draw(RenderingEngine renderer) {
        if (getWidth() > getHeight()) {
            drawHorizontal(renderer);
        } else {
            drawVertical(renderer);
        }

    }

    @Override
    public FilmStrip progressFrame() {
        return null;
    }

    @Override
    public void alwaysUpdate() {

    }

    @Override
    public void neverUpdate() {

    }

    @Override
    public void setAlwaysAnimate(boolean animate) {

    }

    @Override
    public boolean alwaysAnimate() {
        return false;
    }

    private void drawHorizontal(RenderingEngine renderer) {
        Vector2 pos = getPosition();
        float y_offset = WORLD_SCALE * texture.getRegionHeight() / 2f;
        renderer.draw(texture, pos.x, pos.y + y_offset);
    }

    private void drawVertical(RenderingEngine renderer) {
        Vector2 pos = getPosition();

        renderer.draw(texture, pos.x, pos.y);
    }
}
