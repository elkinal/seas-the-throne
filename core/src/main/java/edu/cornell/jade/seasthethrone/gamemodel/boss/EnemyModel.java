package edu.cornell.jade.seasthethrone.gamemodel.boss;

import com.badlogic.gdx.graphics.Texture;
import edu.cornell.jade.seasthethrone.model.PolygonModel;
import edu.cornell.jade.seasthethrone.util.FilmStrip;
import edu.cornell.jade.seasthethrone.util.JsonHandler;

import java.util.HashMap;

/**
 * Superclass for all enemy models that assigns filmstrips to all universal animations for each
 * enemy, such as idle animations, death animations, etc. It also contains all universal physics
 * elements necessary using ComplexModel.
 */
public class EnemyModel extends PolygonModel {
  // all universal enemy textures
  /** Boss texture when idle */
  protected Texture idleTexture;

  /** Boss texture when shooting bullets */
  protected Texture shootTexture;

  /** Boss texture when hit by the player */
  protected Texture hurtTexture;

  /** Boss texture when is defeated */
  protected Texture deathTexture;

  // all universal enemy filmstrips
  /** Filmstrip for idle animation */
  protected FilmStrip idleAnimation;

  /** Filmstrip for shoot animation */
  protected FilmStrip shootAnimation;

  /** Filmstrip for hurt animation */
  protected FilmStrip hurtAnimation;

  /** Filmstrip for death animation */
  protected FilmStrip deathAnimation;

  /**
   * Constructor for EnemyModel. Assigns all textures.
   *
   * @param points coordinates of the hitbox
   * @param x x coordinate of the enemy model
   * @param y y coordinate of the enemy model
   * @param enemyName Enemy name in the json file (eg. crab)
   * @param frameSize number of frames in all animations 
   */
  public EnemyModel(float[] points, float x, float y, String enemyName, int frameSize) {
    super(points, x, y);
    // FIXME: another stupid hack to make multiple named jellies work.
    // This code is terrible and I hate it with a burning passion

    String[] splitName = enemyName.replaceAll("[^a-zA-Z_]", "").split("_");
    // Assuming that names are going to be of the format "_..._(boss)"
    enemyName = splitName[splitName.length-1];

    HashMap<String, Object> bossTextures =
        (HashMap<String, Object>) JsonHandler.jsonToMap("assets.json").get("textures");
    HashMap<String, Object> specificBossTextures =
        (HashMap<String, Object>) bossTextures.get(enemyName);

    idleTexture =
        new Texture(
            (String) ((HashMap<String, Object>) specificBossTextures.get("idle")).get("file"));
    shootTexture =
        new Texture(
            (String) ((HashMap<String, Object>) specificBossTextures.get("shoot")).get("file"));
    hurtTexture =
        new Texture(
            (String) ((HashMap<String, Object>) specificBossTextures.get("hurt")).get("file"));
    deathTexture =
        new Texture(
            (String) ((HashMap<String, Object>) specificBossTextures.get("death")).get("file"));

    // make filmstrips
    setFilmstrips(frameSize);
  }

  /**
   * Instantiates all filmstrips with their respective textures.
   *
   * @param frameSize Number of frames in animation
   */
  private void setFilmstrips(int frameSize) {
    idleAnimation = makeFilmStrip(idleTexture, frameSize);
    shootAnimation = makeFilmStrip(shootTexture, frameSize);
    hurtAnimation = makeFilmStrip(hurtTexture, frameSize);
    deathAnimation = makeFilmStrip(deathTexture, frameSize);
  }

  /**
   * Helper method for 'setFilmstrips', makes the filmstrip.
   *
   * @param t Texture used in the filmstrip
   * @param frameSize Number of frames in animation
   * @return the Filmstrip to be assigned
   */
  private FilmStrip makeFilmStrip(Texture t, int frameSize) {
    int width = t.getWidth();
    return new FilmStrip(t, 1, width / frameSize);
  }
}
