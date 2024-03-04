package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GDXRoot extends Game {
  private SpriteBatch batch;
  private Texture image;

  private GameplayController controller;

  @Override
  public void create() {
    batch = new SpriteBatch();
    image = new Texture("libgdx.png");

    controller = new GameplayController();
    setScreen(controller);
  }

  @Override
  public void dispose() {
    batch.dispose();
    image.dispose();
  }
}
