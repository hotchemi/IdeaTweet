package com.github.hotchemi;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Shintaro Katafuchi
 */
public class IdeaTweetAction extends AnAction {

  public IdeaTweetAction() {
    super(null, null, IconLoader.findIcon("/twitter-bird-light-bgs.png"));
  }

  @Override
  public void actionPerformed(AnActionEvent event) {
    final Project project = event.getData(PlatformDataKeys.PROJECT);
    String tweet = Messages.showInputDialog(project, "What's happening?", "IdeaTweet", IconLoader.findIcon("/twitter.png"));
    if (tweet != null && tweet.length() == 0) {
      Messages.showErrorDialog(project, "You must enter at least one letter!", "Can't Tweet");
      return;
    } else if (tweet != null && tweet.length() > 140) {
      Messages.showErrorDialog(project, "Over 140 letter!", "Can't Tweet");
      return;
    }
    try {
      doTweet(project, tweet);
    } catch (Exception e) {
      e.printStackTrace();
      Messages.showErrorDialog(project, "Try again later.", "Can't Tweet");
    }
  }

  private void doTweet(Project project, String tweet) throws Exception {
    final Twitter twitter = authorize(project);
    if (twitter == null) {
      Messages.showErrorDialog(project, "Failed to authorize with twitter", "Can't Tweet");
    } else {
      twitter.updateStatus(tweet);
      Messages.showMessageDialog(project, "Tweet Success!!", "IdeaTweet", Messages.getInformationIcon());
    }
  }

  private Twitter authorize(Project project) throws TwitterException, IOException, URISyntaxException {
    final String consumerKey = "CONSUMER_KEY";
    final String consumerSecret = "CONSUMER_SECRET";
    final Twitter twitter = new TwitterFactory().getInstance();
    twitter.setOAuthConsumer(consumerKey, consumerSecret);
    final TwitterSettings settings = TwitterSettings.getInstance();
    final AccessToken accessToken;
    if (settings.token == null) {
      final RequestToken requestToken = twitter.getOAuthRequestToken();
      Desktop.getDesktop().browse(new URI(requestToken.getAuthorizationURL()));
      final String pin = Messages.showInputDialog(project, "Enter PIN", "Twitter Authorization", null);
      if (pin != null) {
        if (StringUtil.isEmpty(pin)) {
          accessToken = twitter.getOAuthAccessToken(requestToken);
        } else {
          accessToken = twitter.getOAuthAccessToken(requestToken, pin);
        }
        settings.token = accessToken.getToken();
        settings.secret = accessToken.getTokenSecret();
      } else {
        return null;
      }
    } else {
      accessToken = new AccessToken(settings.token, settings.secret);
    }
    twitter.setOAuthAccessToken(accessToken);
    return twitter;
  }

}
