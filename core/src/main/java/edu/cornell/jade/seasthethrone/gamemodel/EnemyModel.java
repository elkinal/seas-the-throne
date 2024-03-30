package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import edu.cornell.jade.seasthethrone.util.JsonHandler;

import java.awt.desktop.SystemEventListener;
import java.util.HashMap;

/**
 * Superclass for all enemy models that assigns filmstrips to all universal animations for each
 * enemy, such as idle animations, death animations, etc.
 */
public class EnemyModel {
  // all enemy textures
  /** Boss texture when idle */
  protected Texture bossIdleTexture;

  /** Boss texture when shooting bullets */
  protected Texture bossShootTexture;

  /** Boss texture when hit by the player */
  protected Texture bossHurtTexture;

  /** Boss texture when is defeated */
  protected Texture bossDeathTexture;

  // textures that may be necessary but currently are not:
  // the concern is that minibosses only have one movement filmstrip but bosses have 4
  //    /** Boss texture when facing left */
  //    protected Texture bossLeftTexture;
  //
  //    /** Boss texture when facing right */
  //    protected Texture bossRightTexture;
  //
  //    /** Boss texture when facing up */
  //    protected Texture bossUpTexture;
  //
  //    /** Boss texture when facing down */
  //    protected Texture bossDownTexture;

  /**
   * Constructor for EnemyModel: the method used to assign all textures.
   *
   * @param fileName this should always be "assets.json"
   * @param enemyName this should be the enemy name in the json file (eg. crab)
   */
  public EnemyModel(String fileName, String enemyName) {
    HashMap<String, Object> bossTextures =
        (HashMap<String, Object>) JsonHandler.jsonToMap(fileName).get("textures");
    HashMap<String, Object> specificBossTextures =
        (HashMap<String, Object>) bossTextures.get(enemyName);

    bossIdleTexture =
        new Texture(
            (String) ((HashMap<String, Object>) specificBossTextures.get("idle")).get("file"));
    bossShootTexture =
        new Texture(
            (String) ((HashMap<String, Object>) specificBossTextures.get("shoot")).get("file"));
    //    bossHurtTexture =
    //        new Texture(
    //            (String) ((HashMap<String, Object>)
    // specificBossTextures.get("hurt")).get("file"));
    //    bossDeathTexture =
    //        new Texture(
    //            (String) ((HashMap<String, Object>)
    // specificBossTextures.get("death")).get("file"));
  }
}
