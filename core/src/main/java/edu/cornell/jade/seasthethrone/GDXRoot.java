package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import edu.cornell.jade.seasthethrone.render.*;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class GDXRoot extends ApplicationAdapter {
  private SpriteBatch batch;
  private Texture image;
  private RenderingEngine renderingEngine;
  private GameCanvas canvas;

  @Override
  public void create() {
    batch = new SpriteBatch();
    image = new Texture("libgdx.png");
    renderingEngine = new RenderingEngine();
    canvas = new GameCanvas();
    // renderingEngine.addRenderable(new FishTest());
    renderingEngine.addRenderable(new PlayerTest());
  }

  @Override
  public void render() {
    Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    batch.begin();
    batch.draw(image, 140, 210);
    batch.end();
    canvas.begin();
    renderingEngine.drawRenderables(canvas);
    canvas.end();
  }

  @Override
  public void dispose() {
    batch.dispose();
    image.dispose();
  }
}
