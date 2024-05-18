package edu.cornell.jade.seasthethrone.gamemodel.boss;

import edu.cornell.jade.seasthethrone.util.Direction;
import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class SharkBossModel extends BossModel{
  private FilmStrip idleUpAnimation;
  private FilmStrip getHitUpAnimation;
  private Direction faceDirection;
  /**
   * {@link BossModel} constructor using an x and y coordinate.
   *
   * @param builder builder for BossModel
   */
  public SharkBossModel(Builder builder) {
    super(builder);
    getHitUpAnimation = builder.getHitUpAnimation;
    idleUpAnimation = builder.idleUpAnimation;
  }
  public void getDirection (){
    if (getVY()>0)
      faceDirection = Direction.UP;
    else
      faceDirection = Direction.DOWN;
  }
  @Override
  public void progressFrame() {
    getDirection();
    int frame = getFrameNumber();
    if (isDead()) {
      filmStrip = falloverAnimation;
    } else if (isHit()) {
      if (faceDirection == Direction.DOWN)
        filmStrip = getHitAnimation;
      else
        filmStrip = getHitUpAnimation;
    } else {
      if (isAttack()) filmStrip = attackAnimation;
      else if (isIdle()) {
        if (faceDirection ==Direction.DOWN)
          filmStrip = idleAnimation;
        else
          filmStrip = idleUpAnimation;
      }
      else filmStrip = shootAnimation;
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

}
