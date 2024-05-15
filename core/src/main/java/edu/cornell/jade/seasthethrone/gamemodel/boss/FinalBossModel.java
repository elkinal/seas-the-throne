package edu.cornell.jade.seasthethrone.gamemodel.boss;

import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class FinalBossModel extends BossModel{
  private boolean isSpawning = false;
  private int spawnTimer;
  private FilmStrip spawnAnimation;

  public FinalBossModel (Builder builder) {
    super(builder);
    spawnAnimation = builder.spawnAnimation;
  }
  public void setSpawned(){
    setFrameNumber(0);
    isSpawning = true;
    spawnTimer = spawnAnimation.getSize() * frameDelay;
  }
  @Override
  public void progressFrame() {
    int frame = getFrameNumber();
    if (isSpawning)
      filmStrip = spawnAnimation;
    else if (isDead()) {
      if (isExecute) filmStrip = deathAnimation;
      else filmStrip = falloverAnimation;
    } else if (isHit()) {
      filmStrip = getHitAnimation;
    } else {
      if (isAttack()) filmStrip = attackAnimation;
      else filmStrip = shootAnimation;
    }
    filmStrip.setFrame(frame);
    if (isSpawning){
      if (frameCounter % frameDelay == 0 && getFrameNumber() < getFramesInAnimation() - 1) {
        setFrameNumber(getFrameNumber() + 1);
        spawnTimer -= 1;
      } else {
        setFrameNumber(getFrameNumber());
        spawnTimer -= 1;
      }
      if (spawnTimer <= 0){
        isSpawning = false;
      }
    }
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
