package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.FitViewport;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.util.ScreenListener;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;
import edu.cornell.jade.seasthethrone.audio.SoundPlayer;

public class GDXRoot extends Game implements ScreenListener {
  private static final int MIN_LOAD_TIME = 1;
  public static final int EXIT_SWAP = 1;

  /** Exit to go to pause screen */
  public static final int EXIT_PAUSE = 2;

  /** Exit to go to options */
  public static final int EXIT_OPTIONS = 3;

  /** Exit to go to title screen */
  public static final int EXIT_TITLE = 4;

  /** The game screen */
  private GameplayController controller;

  /** The loading screen */
  private LoadScreen loading;

  /** The menu screen */
  private MenuController menus;

  /** The options screen */
  private OptionScreen options;

  private GameCanvas canvas;

  /** If the game should load from save or start a fresh save */
  private boolean loadSave;

  /** AssetManager to load game assets (textures, sounds, etc.) */
  private AssetDirectory directory;

  /** Plays sounds */
  private SoundPlayer soundPlayer;

  @Override
  public void create() {
    soundPlayer = new SoundPlayer();

    canvas = new GameCanvas();

    loading = new LoadScreen("assets.json", canvas, MIN_LOAD_TIME, 1, soundPlayer);
    loading.resetWithExitCode(EXIT_TITLE);
    loading.setScreenListener(this);

    menus = new MenuController(canvas, soundPlayer);
    menus.setScreenListener(this);

    options = new OptionScreen("loading.json", canvas, soundPlayer);
    options.setViewport(new FitViewport(canvas.getWidth(), canvas.getHeight()));
    options.setScreenListener(this);

    setScreen(loading);
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
    options.dispose();
    super.dispose();
  }

  /**
   * The given screen has made a request to exit.
   *
   * <p>The value exitCode can be used to implement menu options.
   *
   * @param screen The screen requesting to exit
   * @param exitCode The state of the screen upon exit
   */
  @Override
  public void exitScreen(Screen screen, int exitCode) {
    soundPlayer.stopMusic();

    // ------- title screen exits
    // title to loading (to game)
    if (screen instanceof TitleScreen && exitCode == EXIT_SWAP) {
      loading.resetWithExitCode(EXIT_SWAP);
      loading.setScreenListener(this);
      setScreen(loading);
    }

    // title to options
    if (screen instanceof TitleScreen && exitCode == EXIT_OPTIONS) {
      options.setExit(EXIT_TITLE);
      setScreen(options);
    }

    // ------- loading screen exits
    // to loading (to title)
    if (screen == loading && exitCode == EXIT_TITLE) {
      // this means this is the first load, i.e. we just started the game
      if (!soundPlayer.populated()) {
        soundPlayer.populate(loading.getAssets());
        controller = new GameplayController(soundPlayer, loading.getAssets());
        controller.setupGameplay();
        controller.setScreenListener(this);
      }

      setScreen(menus);
    }

    // from loading to game (from start screen)
    if (screen == loading && exitCode == EXIT_SWAP) {
      soundPlayer.replaceCurrentMusic("music");
      setScreen(controller);
      if (shouldLoadSave()) {
        controller.loadState();
        loadSave = false;
      }
    }

    // ----- options screen exits
    // options to title
    if (screen == options && exitCode == EXIT_TITLE) {
      controller.getPrefs();
      setScreen(menus);
    }

    // options to pause menu (in game)
    if (screen == options && exitCode == EXIT_PAUSE) {
      controller.getPrefs();
      setScreen(controller);
      soundPlayer.replaceCurrentMusic("music");
    }

    // ---- game screen exits
    // game to hub world
    if (screen == controller && exitCode == EXIT_SWAP) {
      loading.resetWithExitCode(EXIT_SWAP);
      loading.setScreenListener(this);
      setScreen(loading);
    }
    // game to options
    if (screen == controller && exitCode == EXIT_OPTIONS) {
      options.setExit(EXIT_PAUSE);
      setScreen(options);
    }
  }

  public void setLoadSave(boolean loadSave) {
    this.loadSave = loadSave;
  }

  public boolean shouldLoadSave() {
    return loadSave;
  }
}
