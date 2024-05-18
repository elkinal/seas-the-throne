package edu.cornell.jade.seasthethrone;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.jade.seasthethrone.input.InputController;
import edu.cornell.jade.seasthethrone.render.GameCanvas;
import edu.cornell.jade.seasthethrone.util.ScreenListener;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;

public class GDXRoot extends Game implements ScreenListener {
  private static final int MIN_LOAD_TIME = 100;
  public static final int EXIT_GAME = 1;
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
  AssetDirectory directory;

  @Override
  public void create() {
    canvas = new GameCanvas();

    loading = new LoadScreen("assets.json", canvas, MIN_LOAD_TIME, 1);
    loading.setScreenListener(this);

    controller = new GameplayController();
    controller.setScreenListener(this);

    menus = new MenuController(canvas);
    menus.setScreenListener(this);

    options = new OptionScreen("loading.json", canvas);
    options.setViewport(new FitViewport(canvas.getWidth(), canvas.getHeight()));
    options.setScreenListener(this);

    setScreen(menus);
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
    // ------- title screen exits
    // title to loading (to game)
    if (screen instanceof TitleScreen && exitCode == EXIT_GAME) {
      loading = new LoadScreen("assets.json", canvas, MIN_LOAD_TIME, EXIT_GAME);
      loading.setScreenListener(this);
      setScreen(loading);
    }

    // title to options
    if (screen instanceof TitleScreen && exitCode == EXIT_OPTIONS) {
      System.out.println("title to options");
      options.setExit(EXIT_TITLE);
      setScreen(options);
      //      menus.leftScreen(true);
    }

    // anything to loading (to title)
    if (screen == loading && exitCode == EXIT_TITLE) {
      setScreen(menus);
      loading.dispose();
      loading = null;
    }

    // from loading to game
    if (screen == loading && exitCode == EXIT_GAME) {
      setScreen(controller);
      controller.setAssets(loading.getAssets());
      if (shouldLoadSave()) {
        controller.loadState();
        loadSave = false;
      }
      loading.dispose();
      loading = null;
    }

    // ----- options screen exits
    // options to title
    if (screen == options && exitCode == EXIT_TITLE) {
      System.out.println("options to title");
      controller.getPrefs();
      setScreen(menus);
      //      menus.leftScreen(false);
    }

    // options to pause menu (in game)
    if (screen == options && exitCode == EXIT_PAUSE) {
      controller.getPrefs();
      setScreen(controller);
    }

    // ---- game screen exits
    // game to hub world
    if (screen == controller && exitCode == EXIT_GAME) {
      loading = new LoadScreen("assets.json", canvas, MIN_LOAD_TIME, EXIT_GAME);
      loading.setScreenListener(this);
      setScreen(loading);
    }

    // game to title
    //    if (screen == controller && exitCode == EXIT_TITLE) {
    //      loading = new LoadScreen("loading.json", canvas, MIN_LOAD_TIME, EXIT_TITLE);
    //      loading.setScreenListener(this);
    //      setScreen(loading);
    //    }

    // game to options
    if (screen == controller && exitCode == EXIT_OPTIONS) {
      options.setExit(EXIT_PAUSE);
      setScreen(options);
    }
  }

  public void setLoadSave(boolean loadSave) {
    this.loadSave = loadSave;
  }

  public boolean shouldLoadSave() {return loadSave;}



}
