package com.github.hotchemi;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.Nullable;

/**
 * @author Shintaro Katafuchi
 */
@State(
    name = "TwitterSettings",
    storages = {
        @Storage(
            file = StoragePathMacros.APP_CONFIG + "/tweet_code_settings.xml"
        )
    }
)
public class TwitterSettings implements PersistentStateComponent<TwitterSettings> {

  public String token;

  public String secret;

  @Nullable
  @Override
  public TwitterSettings getState() {
    return this;
  }

  @Override
  public void loadState(TwitterSettings twitterSettings) {
    token = twitterSettings.token;
    secret =twitterSettings.secret;
  }

  public static TwitterSettings getInstance() {
    return ServiceManager.getService(TwitterSettings.class);
  }
}
