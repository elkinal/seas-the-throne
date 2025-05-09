package edu.cornell.jade.seasthethrone.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import edu.cornell.jade.seasthethrone.GDXRoot;
import edu.cornell.gdiac.backend.GDXApp;
import edu.cornell.gdiac.backend.GDXAppSettings;

import java.awt.*;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
  public static void main(String[] args) {
    if (StartupHelper.startNewJvmIfRequired())
      return; // This handles macOS support and helps on Windows.
    createApplication();
  }

  private static GDXApp createApplication() {
    return new GDXApp(new GDXRoot(), getDefaultConfiguration());
  }

  private static GDXAppSettings getDefaultConfiguration() {
    GDXAppSettings configuration = new GDXAppSettings();
    configuration.vSyncEnabled = true;
    configuration.foregroundFPS = 60;
    configuration.title = "seasthethrone";
    //// Limits FPS to the refresh rate of the currently active monitor.
    // configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
    //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can
    // be
    //// useful for testing performance, but can also be very stressful to some hardware.
    //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen
    // tearing.
    configuration.height = 1080;
    configuration.width = 1920;
    configuration.fullscreen = false;
    return configuration;
  }
}
