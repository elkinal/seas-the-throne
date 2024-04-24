package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.util.ScreenListener;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;

public class GDXRoot extends Game implements ScreenListener {
  private static final int MIN_LOAD_TIME = 50;
  public static final int EXIT_SWAP = 1;
  public static final int EXIT_TO_GAME = 2;

  public static final int EXIT_QUIT = 0;
  private GameplayController controller;
  private LoadScreen loading;
  private GameCanvas canvas;

  private TitleScreen title;

  /** AssetManager to load game assets (textures, sounds, etc.) */
  AssetDirectory directory;

  @Override
  public void create() {
    canvas = new GameCanvas();

    controller = new GameplayController();
    controller.setScreenListener(this);
    title = new TitleScreen("loading.json", canvas);
    title.setScreenListener(this);
    loading = new LoadScreen("assets.json", canvas, MIN_LOAD_TIME, 1);
    loading.setScreenListener(this);

    setScreen(title);
  }

  @Override
  public void dispose() {
    setScreen(null);
    controller.dispose();
    canvas.dispose();
    canvas = null;
    if (directory != null) {
      directory.unloadAssets();
      directory.dispose();
      directory = null;
    }
    super.dispose();

  }

  /**
   * The given screen has made a request to exit.
   *
   * The value exitCode can be used to implement menu options.
   *
   * @param screen   The screen requesting to exit
   * @param exitCode The state of the screen upon exit
   */
  @Override
  public void exitScreen(Screen screen, int exitCode) {
    if (screen == title && exitCode == EXIT_SWAP) {
      loading = new LoadScreen("assets.json", canvas, MIN_LOAD_TIME, 1);
      loading.setScreenListener(this);
      setScreen(loading);
    }

    if (screen == loading && exitCode == EXIT_SWAP) {
      setScreen(controller);

      loading.dispose();
      loading = null;
    }

    if (screen == controller && exitCode == EXIT_SWAP) {
      loading = new LoadScreen("assets.json", canvas, MIN_LOAD_TIME, 1);
      loading.setScreenListener(this);
      setScreen(loading);
    }
  }
}
