package edu.cornell.jade.seasthethrone.gamemodel.gate;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.level.LevelObject;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.render.Renderable;
import edu.cornell.jade.seasthethrone.render.RenderingEngine;

public class GateWallModel extends BoxModel implements Renderable {
    private final float WORLD_SCALE;

    private TextureRegion texture;

    public GateWallModel(LevelObject obs, float worldScale) {
        super(obs.x, obs.y, obs.width, obs.height);
        this.texture = obs.texture;
        this.WORLD_SCALE = worldScale;
    }

    public void draw(RenderingEngine renderer) {
        Vector2 pos = getPosition();
        float y_offset = WORLD_SCALE*texture.getRegionHeight()/2f - getHeight()/2f;
        renderer.draw(texture, pos.x, pos.y + y_offset);
    }
}
