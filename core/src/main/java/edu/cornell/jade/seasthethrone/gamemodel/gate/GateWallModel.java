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

  private TextureRegion texture_up;

  private TextureRegion texture_down;

  public GateWallModel(LevelObject obs, float worldScale) {
    super(obs.x, obs.y, obs.width, obs.height);
    this.WORLD_SCALE = worldScale;
    if (getWidth() > getHeight()) {
      this.texture_up = new TextureRegion(new Texture("levels/gate_horizontal_up.png"));
      texture_up.setRegion(0, 0, (int) (obs.width / WORLD_SCALE), (int) texture_up.getRegionHeight());

      this.texture_down = new TextureRegion(new Texture("levels/gate_horizontal_down.png"));
      texture_down.setRegion(0, 0, (int) (obs.width / WORLD_SCALE), (int) texture_down.getRegionHeight());
    } else {
      this.texture_up = new TextureRegion(new Texture("levels/gate_vertical_up.png"));
      texture_up.setRegion(0, 0, (int) texture_up.getRegionWidth(), (int) (obs.height / WORLD_SCALE));

      this.texture_down = new TextureRegion(new Texture("levels/gate_vertical_down.png"));
      texture_down.setRegion(0, 0, (int) texture_down.getRegionWidth(), (int) (obs.height / WORLD_SCALE));
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
    float y_offset = WORLD_SCALE * texture_up.getRegionHeight() / 2f;
    renderer.draw(texture_up, pos.x, pos.y + y_offset);
  }

  private void drawVertical(RenderingEngine renderer) {
    Vector2 pos = getPosition();
    float y_offset = WORLD_SCALE * texture_up.getRegionHeight() / 2f;
    renderer.draw(texture_up, pos.x, pos.y);
  }
}
