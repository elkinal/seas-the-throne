package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.jade.seasthethrone.model.BoxModel;
import edu.cornell.jade.seasthethrone.physics.CollisionMask;

/**
 * Model for the player spear. When the spear is extended, it will have an active hitbox that will
 * allow the spear to pierce through enemies.
 */
public class PlayerSpearModel extends BoxModel {
  /** Width of spear */
  private static float SPEAR_WIDTH = 0.5f;

  /** Length of spear */
  private static float SPEAR_LENGTH = 3.2f;

  /** Offset length of the spear center from the body center (when extended) */
  private static float SPEAR_OFFSET = 2.5f;

  /** Maximum number of fish that can be speared */
  private static int MAX_SPEAR_CAPACITY = 5;

  /** If the spear is extended (during dash) */
  private boolean spearExtended;

  /** Number of fish currently speared */
  private int numSpeared;

  /**
   * The size is expressed in physics units NOT pixels.
   *
   * @param x      Initial x position of the spear center
   * @param y      Initial y position of the spear center
   * @param width  spear width in physics units
   * @param height spear width in physics units
   */
  public PlayerSpearModel(float x, float y, float width, float height) {
    super(x, y, width, height);
    spearExtended = false;
  }

  /**
   * Create new player body at position (x,y)
   */
  public PlayerSpearModel(float x, float y) {
    this(x, y, SPEAR_WIDTH, SPEAR_LENGTH);
  }

  /** Check if spear is extended */
  public boolean isSpearExtended() {
    return spearExtended;
  }

  /** Extend or retract spear, activating or deactivating
   * the spear hitbox
   *
   * @param value If the spear should be extended
   */
   public void setSpear(boolean value){
    setActive(value);
    spearExtended = value;
  }

  /**
   * Update the spear mode
   *
   * @param bodyPosition  Vector representing the position of the player's body
   * @param dashDirection Vector representing the direction of the dash
   * */
  public void updateSpear(Vector2 bodyPosition, Vector2 dashDirection) {
    setPosition(bodyPosition.add(dashDirection.nor().scl(SPEAR_OFFSET)));
    setAngle(dashDirection.angleRad() + (float)Math.PI/2);
  }

  public int getNumSpeared() {
    return numSpeared;
  }

  /**
   * If possible, increment spear counter and return true.
   * Otherwise, return false.
   * */
  public boolean incrementSpear(){
    if(numSpeared < MAX_SPEAR_CAPACITY){
      numSpeared += 1;
      return true;
    } return false;
  }

  /**
   * Decrement spear counter.
   * @pre spear counter is above 0
   */
  public void decrementSpear(){
    numSpeared -= 1;
  }

//  /**
//   * Return if the player is able to spear
//   * @pre spearExtended is false
//   * */
//  public boolean canSpear(){
//    return numSpeared == 0;
//  }

  /** Overriding the current activatePhysics method to start the body off as inactive */
  @Override
  public boolean activatePhysics(World world) {
    // Make a body, if possible
    bodyinfo.active = false;
    body = world.createBody(bodyinfo);
    body.setUserData(this);

    // Only initialize if a body was created.
    if (body != null) {
      createFixtures();
      return true;
    }

    bodyinfo.active = false;
    return false;
  }
}
