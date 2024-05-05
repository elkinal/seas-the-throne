package edu.cornell.jade.seasthethrone.gamemodel.boss;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.jade.seasthethrone.util.Direction;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class SwordfishModel extends BossModel {

  /** {@link SwordfishModel} constructor using Builder */
  public SwordfishModel(BossModel.Builder builder) {
    super(builder);
  }

  @Override
  public void progressFrame() {
    int frame = getFrameNumber();
    if (isDead()) {
      filmStrip = deathAnimation;
    } else if (isHit()) {
      switch (direction()){
        case UP:
          filmStrip = hitUpAnimation;
          break;
        case DOWN:
          filmStrip = hitDownAnimation;
          break;
        case LEFT:
          filmStrip = hitLeftAnimation;
          break;
        case RIGHT:
          filmStrip = hitRightAnimation;
          break;
      }
    } else {
      if (isAttack()) {
        //TODO: change to attack animations when we have them
        switch (direction()){
          case UP:
            filmStrip = moveUpAnimation;
            break;
          case DOWN:
            filmStrip = moveDownAnimation;
            break;
          case LEFT:
            filmStrip = moveLeftAnimation;
            break;
          case RIGHT:
            filmStrip = moveRightAnimation;
            break;
        }
      }
      else {
        switch (direction()){
          case UP:
            filmStrip = moveUpAnimation;
            break;
          case DOWN:
            filmStrip = moveDownAnimation;
            break;
          case LEFT:
            filmStrip = moveLeftAnimation;
            break;
          case RIGHT:
            filmStrip = moveRightAnimation;
            break;
        }
      }
    }
    filmStrip.setFrame(frame);

    if (isDead()) {
      if (frameCounter % frameDelay == 0 && getFrameNumber() < getFramesInAnimation() - 1) {
        setFrameNumber(getFrameNumber() + 1);
        deathCount -= 1;
      } else {
        setFrameNumber(getFrameNumber());
        deathCount -= 1;
      }
    } else if (isHit()) {
      if (frameCounter % frameDelay == 0 && getFrameNumber() < getFramesInAnimation() - 1) {
        setFrameNumber(getFrameNumber() + 1);
        hitCount -= 1;
      } else {
        setFrameNumber(getFrameNumber());
        hitCount -= 1;
      }
      if (hitCount == 0) {
        setFrameNumber(0);
      }
    } else {
      if (frameCounter % frameDelay == 0) {
        setFrameNumber((getFrameNumber() + 1) % getFramesInAnimation());
        if (isAttack()) {
          attackCount -= 1;
        }
      }
    }
    frameCounter += 1;
  }
  public Direction direction() {
    float vx = this.getVX();
    float vy = this.getVY();
    Direction faceDirection;

    if (Math.abs(vx) > Math.abs(vy)) {
      if (vx > 0) faceDirection = Direction.RIGHT;
      else faceDirection = Direction.LEFT;
    } else{
      if (vy > 0) faceDirection = Direction.UP;
      else faceDirection = Direction.DOWN;
    }
    return faceDirection;
  }
  //TODO: change this to the Swordfish Hitbox
  @Override
  void setHitbox() {
    hitbox = new float[]{
        -2,     1.2f,
        -0.6f,  2.5f,
        1,      2.5f,
        2.4f,   1.2f,
        2.4f,   -2.5f,
        -2,     -2.5f
    };
  }
}
