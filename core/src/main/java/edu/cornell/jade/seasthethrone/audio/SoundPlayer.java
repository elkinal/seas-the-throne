package edu.cornell.jade.seasthethrone.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

import edu.cornell.gdiac.audio.AudioEngine;
import edu.cornell.gdiac.audio.AudioSource;
import edu.cornell.gdiac.audio.MusicQueue;
import edu.cornell.gdiac.audio.SoundEffect;
import edu.cornell.jade.seasthethrone.assets.AssetDirectory;

/**
 * Simplifies playing sounds and music.
 *
 * Provides methods to play sound effects and music so we don't have to think.
 */
public class SoundPlayer {
  private AudioEngine audioEngine;

  /** Map from sound effect name to sound effect */
  private ObjectMap<String, SoundEffect> soundEffects;

  /** Map from song name to sound effect */
  private ObjectMap<String, AudioSource> music;

  /** Music queue for actually playing stuff */
  private MusicQueue musicQueue;

  /** If the player has been populated */
  private boolean populated;

  /**
   * Constructs a SoundPlayer
   *
   * @param dir directory containing loaded json
   */
  public SoundPlayer() {
    audioEngine = (AudioEngine) Gdx.audio;
    soundEffects = new ObjectMap<>();
    music = new ObjectMap<>();

    musicQueue = audioEngine.newMusicBuffer(false, 44100);
    musicQueue.setLooping(true);
    musicQueue.setVolume(0.5f);

    populated = false;
  }

  /**
   * Populates the sound player with sounds from the given directory.
   *
   * @param dir directory containing sounds
   */
  public void populate(AssetDirectory dir) {
    soundEffects.put("dash", dir.getEntry("dash", SoundEffect.class));
    soundEffects.put("door-close", dir.getEntry("door-close", SoundEffect.class));
    soundEffects.put("enemy-hit", dir.getEntry("enemy-hit", SoundEffect.class));
    soundEffects.put("get-ammo", dir.getEntry("get-ammo", SoundEffect.class));
    soundEffects.put("hit-enemy-with-spear", dir.getEntry("hit-enemy-with-spear", SoundEffect.class));
    soundEffects.put("interact", dir.getEntry("interact", SoundEffect.class));
    soundEffects.put("shoot-bullet", dir.getEntry("shoot-bullet", SoundEffect.class));
    soundEffects.put("player-hit", dir.getEntry("player-hit", SoundEffect.class));
    soundEffects.put("menu-change", dir.getEntry("menu-change", SoundEffect.class));
    soundEffects.put("menu-select", dir.getEntry("menu-select", SoundEffect.class));
    soundEffects.put("cant-interact", dir.getEntry("cant-interact", SoundEffect.class));

    music.put("music", dir.getEntry("music", AudioSource.class));

    populated = true;
  }

  /**
   * Returns if this object was ever populated, meaning poplate was called.
   *
   * @return if this object was ever popluated
   */
  public boolean populated() {
    return populated;
  }

  /**
   * Plays a sound effect.
   *
   * @param name name of the sound effect
   * @return id of the sound if sucessful, else -1
   */
  public long playSoundEffect(String name) {
    return soundEffects.get(name).play();
  }

  /**
   * Plays music, looping, and replacing currently playing music
   *
   * @param name name of the song
   */
  public void replaceCurrentMusic(String name) {
    musicQueue.clearSources();
    musicQueue.addSource(music.get(name));
    musicQueue.play();
  }

  /**
   * Pause currently playing music
   */
  public void pause() {
    musicQueue.pause();
  }

  /**
   * Stop currently playing music
   */
  public void stopMusic() {
    musicQueue.clearSources();
  }

}
