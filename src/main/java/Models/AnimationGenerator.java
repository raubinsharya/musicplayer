package Models;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationGenerator {
    public static void applyTranslateAnimationOn(Node node, int duration, double from, double to,EventHandler<ActionEvent> eventEventHandler) {
        TranslateTransition translateTransition
                = new TranslateTransition(Duration.millis(duration), node);
        translateTransition.setFromX(from);
        translateTransition.setToX(to);
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(true);
        translateTransition.play();
    }

    public static void applyFadeAnimationOn(Node node, int duration, double from, double to, EventHandler<ActionEvent> eventHandler) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), node);
        fadeTransition.setOnFinished(eventHandler);
        fadeTransition.setFromValue(from);
        fadeTransition.setToValue(to);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(1);
        fadeTransition.play();
    }
    public static void applyScaleAnimation(Node node,int duration,double from,double to,EventHandler<ActionEvent> eventEventHandler){
        ScaleTransition scaleTransition=new ScaleTransition(Duration.millis(duration),node);
        scaleTransition.setFromX(from);
        scaleTransition.setFromY(from);
        scaleTransition.setByX(to);
        scaleTransition.setByY(to);
        scaleTransition.setOnFinished(eventEventHandler);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(1);
        scaleTransition.play();
    }
}
