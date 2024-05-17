package edu.cornell.jade.seasthethrone.ai.clam;

import edu.cornell.jade.seasthethrone.ai.BossController;
import edu.cornell.jade.seasthethrone.ai.CrabBossController;
import edu.cornell.jade.seasthethrone.bpedit.AttackPattern;
import edu.cornell.jade.seasthethrone.gamemodel.boss.BossModel;
import edu.cornell.jade.seasthethrone.gamemodel.player.PlayerModel;

abstract class ClamController implements BossController {
  /** Enumeration of AI states. */
  private static enum State {
    /** The enemy is stationary */
    IDLE,
    /** The enemy is attacking */
    ATTACK,
    DEAD
  }
  /*
   * -----------------------------------
   * CONSTANTS
   * -----------------------------------
   */
  /** The distance the player must be from the boss before it begins attacking. */
  private static float AGRO_DISTANCE = 50f;

  /*
   * -------------------------------
   * STATE
   * -------------------------------
   */
  /** The boss's current state */
  private State state;

  /** Attack pattern of clam */
  private final AttackPattern attack;

  /** Model shooting the attack */
  private final BossModel boss;

  /** Player to be shot at */
  private final PlayerModel player;

  /**
   * Constructs a crab controller
   *
   * @param boss boss model shooting
   * @param attack attack pattern being shot
   * @param player player being shot at
   */
  public ClamController(BossModel boss, AttackPattern attack, PlayerModel player) {
    this.attack = attack;
    this.boss = boss;
    this.player = player;
    this.state = State.IDLE;
  }

  @Override
  public void update(float delta) {
    if (boss.isDead()) {
      dispose();
      state = State.DEAD;
    }

    switch (state) {
      case IDLE:
        if (boss.getPosition().dst(player.getPosition()) < AGRO_DISTANCE && boss.isInRoom()) {
          state = State.ATTACK;
        }
        break;
      case ATTACK:
        attack.update(player.getX(), player.getY());
        break;
      case DEAD:
        break;
    }
  }

  @Override
  public boolean isDead() {
    return boss.isDead();
  }

  @Override
  public boolean isBoss() {
    return false;
  }

  @Override
  public void dispose() {
    attack.cleanup();
  }

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
