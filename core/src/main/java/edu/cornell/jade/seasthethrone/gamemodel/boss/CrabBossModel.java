package edu.cornell.jade.seasthethrone.gamemodel.boss;

import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class CrabBossModel extends BossModel{
  private FilmStrip terminatedAnimation;
  private boolean isTerminated;
  public CrabBossModel(Builder builder){
    super(builder);
    terminatedAnimation = builder.terminatedAnimation;
    isTerminated = false;
  }
  public void executeBoss() {
    setFrameNumber(0);
    executeCount = frameDelay * deathAnimation.getSize();
    isExecute = true;
  }
  @Override
  public void progressFrame() {
    int frame = getFrameNumber();
    if (isDead()) {
      if (isExecute) {
        filmStrip = deathAnimation;
      }
      else if (finishExecute){
        filmStrip = terminatedAnimation;
      }
      else {
        filmStrip = falloverAnimation;
      }
    } else if (isHit()) {
      filmStrip = getHitAnimation;
    } else {
      if (isAttack()) filmStrip = attackAnimation;
      else if (isIdle()) filmStrip = idleAnimation;
      else filmStrip = shootAnimation;
    }
    filmStrip.setFrame(frame);

    if (isDead()) {
      if (isExecute){
        if (frameCounter % frameDelay == 0 && getFrameNumber() < getFramesInAnimation() - 1) {
          setFrameNumber(getFrameNumber() + 1);
          executeCount -= 1;
        } else {
          setFrameNumber(getFrameNumber());
          executeCount -= 1;
        }
        if (executeCount <=0){
          finishExecute = true;
          isExecute = false;
          isTerminated = true;
          setFrameNumber(0);
        }
      }
      else if (isTerminated){
        setFrameNumber(0);
      }
      else {
        if (frameCounter % frameDelay == 0 && getFrameNumber() < getFramesInAnimation() - 1) {
          setFrameNumber(getFrameNumber() + 1);
          deathCount -= 1;
        } else {
          setFrameNumber(getFrameNumber());
          deathCount -= 1;
        }
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
  @Override
  public void update(float delta) {
    if (finishExecute) setActive(false);
  }
}
