package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import edu.cornell.jade.seasthethrone.util.ScreenListener;

public class GDXRoot extends Game implements ScreenListener {
  private SpriteBatch batch;
  private Texture image;
  private GameplayController controller;
  private LoadScreen loading;

  @Override
  public void create() {
    batch = new SpriteBatch();
    image = new Texture("libgdx.png");

    controller = new GameplayController();
    controller.setScreenListener(this);
    loading = new LoadScreen(controller.renderEngine.getGameCanvas(), 200);
    loading.setScreenListener(this);
    setScreen(loading);
  }

  @Override
  public void dispose() {
    batch.dispose();
    image.dispose();
  }

  @Override
  public void exitScreen(Screen screen, int exitCode) {
    if (screen == loading && exitCode == 0) {
      setScreen(controller);

      loading.dispose();
      loading = null;
    }

    if (screen == controller && exitCode == 1) {
      loading = new LoadScreen(controller.renderEngine.getGameCanvas(), 200);
      loading.setScreenListener(this);
      setScreen(loading);
    }
  }
}
