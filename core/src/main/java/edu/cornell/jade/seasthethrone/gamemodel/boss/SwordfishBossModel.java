package edu.cornell.jade.seasthethrone.gamemodel.boss;

import edu.cornell.jade.seasthethrone.util.Direction;

public class SwordfishBossModel extends BossModel{
  Direction faceDirection = Direction.LEFT;
  public SwordfishBossModel (Builder builder){super(builder);}
  @Override
  public void progressFrame() {
    getDirection();
    int frame = getFrameNumber();
    if (isDead()) {
      if (isExecute) filmStrip = deathAnimation;
      else filmStrip = falloverAnimation;
    } else if (isHit()) {
      switch (faceDirection) {
        case UP -> filmStrip = getHitUpAnimation;
        case DOWN -> filmStrip = getHitDownAnimation;
        case LEFT -> filmStrip = getHitAnimation;
        case RIGHT -> filmStrip = getHitRightAnimation;
      }
    } else {
      if (isAttack()) {
        switch (faceDirection) {
          case UP -> filmStrip = attackUpAnimation;
          case DOWN -> filmStrip = attackDownAnimation;
          case LEFT -> filmStrip = attackAnimation;
          case RIGHT -> filmStrip = attackRightAnimation;
        }
      }
      else {
        switch (faceDirection) {
          case UP -> filmStrip = shootUpAnimation;
          case DOWN -> filmStrip = shootDownAnimation;
          case LEFT -> filmStrip = shootAnimation;
          case RIGHT -> filmStrip = shootRightAnimation;
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

  public void getDirection() {
    float vx = getVX();
    float vy = getVY();
    if (Math.abs(vx) > Math.abs(vy)) {
      if (vx > 0) {
        faceDirection = Direction.RIGHT;
      }
      else {
        faceDirection = Direction.LEFT;
      }
    } else if (Math.abs(vx) < Math.abs(vy)) {
      if (vy > 0) {
        faceDirection = Direction.UP;
      }
      else {
        faceDirection = Direction.DOWN;
      }
    }
  }
}
