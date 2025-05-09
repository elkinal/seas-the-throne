package edu.cornell.jade.seasthethrone.gamemodel.boss;

import edu.cornell.jade.seasthethrone.util.FilmStrip;

public class FinalBossModel extends BossModel{
  private boolean isHard;
  private int transformTimer;
  private FilmStrip transformAnimation;
  private FilmStrip finalAttackAnimation;
  private FilmStrip finalShootAnimation;
  private FilmStrip finalGetHitAnimation;
  private FilmStrip terminatedAnimation;
  private FilmStrip catchBreathAnimation;
  /** Whether the boss is terminated */
  private boolean isTerminated;
  /**Whether the boss is catching breath */
  private boolean catchBreath;

  public FinalBossModel (Builder builder) {
    super(builder);
    transformAnimation = builder.transformAnimation;
    finalAttackAnimation = builder.finalAttackAnimation;
    finalShootAnimation = builder.finalShootAnimation;
    finalGetHitAnimation = builder.finalGetHitAnimation;
    terminatedAnimation = builder.terminatedAnimation;
    catchBreathAnimation = builder.catchBreathAnimation;
    isTerminated = false;
    catchBreath = false;
  }

  /**
   * Sets the animation to the spawn animation and change all relevant assets to the "final" asset version.
   * Also changes the boss's hitbox
   */
  public void launchPhaseTwo(){
    isHard = true;
    transformTimer = transformAnimation.getSize() * frameDelay;
    initShapes(hitbox2());
    initBounds();
    createFixtures();
  }
  @Override
  public void executeBoss() {
    setFrameNumber(0);
    executeCount = frameDelay * deathAnimation.getSize();
    isExecute = true;
  }
  @Override
  public void progressFrame() {
    int frame = getFrameNumber();
    if (transformTimer>0)
      filmStrip = transformAnimation;
    else if (isDead()) {
      if (isExecute) filmStrip = deathAnimation;
      else if (catchBreath) filmStrip = catchBreathAnimation;
      else if (finishExecute) filmStrip = terminatedAnimation;
      else filmStrip = falloverAnimation;
    } else if (isHit()) {
      if (isHard)
        filmStrip = finalGetHitAnimation;
      else
        filmStrip = getHitAnimation;
    } else {
      if (isAttack()) {
        if (isHard)
          filmStrip = finalAttackAnimation;
        else if (isIdle())
          filmStrip = idleAnimation;
        else
          filmStrip = attackAnimation;
      }
      else {
        if (isHard)
          filmStrip = finalShootAnimation;
        else
          filmStrip = shootAnimation;
      }
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
          setFrameNumber(0);
        }
      }
      else if (catchBreath){
        if (frameCounter % (frameDelay*4) == 0) {
          if (getFrameNumber() < getFramesInAnimation() - 1)
            setFrameNumber(getFrameNumber() + 1);
          else
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
        if (deathCount<= 0){
          catchBreath = true;
          setFrameNumber(0);
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
    } else if (transformTimer > 0){
      if (frameCounter % frameDelay == 0 && getFrameNumber() < getFramesInAnimation() - 1) {
        setFrameNumber(getFrameNumber() + 1);
        transformTimer -= 1;
      } else {
        setFrameNumber(getFrameNumber());
        transformTimer -= 1;
      }
      if (transformTimer == 0) {
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

  private static float[] hitbox2(){
    return new float[]{
            -1.6f, 1.2f,
            -4.5f, 4,
            -3, 8,
            -1, 5,
            0.5f, 7.7f,
            3.3f, 6,
            1.2f, 1,
            1.2f, -6.3f,
            -1.6f, -6.3f
    };
  }
}
