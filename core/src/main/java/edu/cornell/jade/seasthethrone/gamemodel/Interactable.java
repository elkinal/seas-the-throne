package edu.cornell.jade.seasthethrone.gamemodel;

import com.badlogic.gdx.math.Vector2;

public interface Interactable {

  boolean isPlayerInRange(Vector2 playerPos);

  void setPlayerInRange(boolean inRange);

  boolean getPlayerInRange();

}
