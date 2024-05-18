package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import edu.cornell.jade.seasthethrone.audio.SoundPlayer;
import edu.cornell.jade.seasthethrone.input.InputController;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.util.ScreenListener;

public class MenuController implements Screen {
  private InputController inputController;

  private ScreenViewport viewport;

  private TitleScreen titleScreen;

  private ScreenListener listener;

  private SoundPlayer soundPlayer;


  public MenuController(GameCanvas canvas, SoundPlayer soundPlayer) {
    this.soundPlayer = soundPlayer;
    viewport = new ScreenViewport();
    inputController = new InputController(viewport);
    titleScreen = new TitleScreen("loading.json", canvas, viewport, soundPlayer);

    inputController.add(titleScreen);

  }

  public void update() {
    inputController.update();
  }

  public void setScreenListener(ScreenListener listener) {
    this.listener = listener;
    titleScreen.setScreenListener(listener);
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float v) {
    update();
    titleScreen.render(v);
  }

  @Override
  public void resize(int i, int i1) {

  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void hide() {

  }

  @Override
  public void dispose() {

  }
}
