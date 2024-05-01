package edu.cornell.jade.seasthethrone.gamemodel.gate;

import com.badlogic.gdx.graphics.Texture;
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
    this.WORLD_SCALE = worldScale;
    if (getWidth() > getHeight()) {
      this.texture = new TextureRegion(new Texture("levels/horizontal_gate.png"));
      texture.setRegion(0, 0, (int) (obs.width / WORLD_SCALE), (int) texture.getRegionHeight());
    } else {
      this.texture = new TextureRegion(new Texture("levels/vertical_gate.png"));
      texture.setRegion(0, 0, (int) texture.getRegionWidth(), (int) (obs.height / WORLD_SCALE));
    }
  }

  public void draw(RenderingEngine renderer) {
    if (isActive()){
      if (getWidth() > getHeight()) {
        drawHorizontal(renderer);
      } else {
        drawVertical(renderer);
      }
    }
  }

  @Override
  public void progressFrame() {}

  @Override
  public void alwaysUpdate() {}

  @Override
  public void neverUpdate() {}

  @Override
  public void setAlwaysAnimate(boolean animate) {}

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
    float y_offset = WORLD_SCALE * texture.getRegionHeight() / 2f;
    renderer.draw(texture, pos.x, pos.y);
  }
}
