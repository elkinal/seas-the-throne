package edu.cornell.jade.seasthethrone.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.badlogic.gdx.graphics.g3d.particles.ParticleChannels.Color;

public class Credits implements Renderable {
  /** Texture for the credits */
  private TextureRegion texture;

  private Viewport viewport;

  /** If its time to run the credits */
  private boolean run;

  private int scroll;

  public Credits(Viewport viewport) {
    texture = new TextureRegion(new Texture("credits.png"));
    this.viewport = viewport;
    run = false;
    scroll = 0;
  }

  @Override
  public void draw(RenderingEngine renderer) {
    if (!run) return;

    System.out.println("drawing");
    GameCanvas canvas = renderer.getGameCanvas();

    float scale = 0.5f * canvas.getWidth() / texture.getRegionWidth();
    float width = scale * texture.getRegionWidth();
    float height = scale * texture.getRegionHeight();
    float ox = (renderer.getGameCanvas().getWidth() - width)/ 2f;
    float oy = -height + 3*scroll;
    scroll ++;

    if (oy > 3 * height) {
      run = false;
      scroll = 0;
    }

    canvas.beginUI();
    canvas.getUiBatch().setProjectionMatrix(viewport.getCamera().combined);
    canvas.drawUI(
        texture, ox, oy, width, height);
    canvas.endUI();
  }

  public void setRun(boolean run) {
    this.run = run;
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
}
