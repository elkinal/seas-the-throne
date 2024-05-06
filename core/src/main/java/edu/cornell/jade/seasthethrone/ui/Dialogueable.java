package edu.cornell.jade.seasthethrone.ui;

public interface Dialogueable {

  /** Shows the dialogue box */
  public default void show() {
  }

  /** Hides the dialogue box */
  public default void hide() {
  }

  /** Sets the text of the dialogue box */
  public default void setTexts(String... texts) {
  }
}
