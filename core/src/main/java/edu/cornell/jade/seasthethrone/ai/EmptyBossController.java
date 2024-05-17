package edu.cornell.jade.seasthethrone.ai;

import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;

public class EmptyBossController implements BossController {

  private BossModel boss;

  /**
   * Constructs a empty controller
   *
   * @param boss boss model shooting
   */
  public EmptyBossController(BossModel boss) {
    this.boss = boss;
  }

  @Override
  public void update(float delta) {}

  @Override
  public boolean isDead() {
    return boss.isDead();
  }

  @Override
  public boolean isBoss() {
    return false;
  }

  @Override
  public void dispose() {}

  @Override
  public void remove() {
    boss.markRemoved(true);
  }

  @Override
  public int getHealth() {
    return boss.getHealth();
  }

  @Override
  public int getMaxHealth() { return boss.getFullHealth(); }

  @Override
  public void transferState(int storedHp) {
    boss.setHealth(storedHp);
  }

  @Override
  public BossModel getBoss() {
    return boss;
  }
}
