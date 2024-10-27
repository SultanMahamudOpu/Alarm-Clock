package org.example.alarmclock;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlarmClockApp extends Application {

    private Label timeLabel;
    private Label dateLabel;
    private TextField alarmHourField, alarmMinuteField;
    private ComboBox<String> amPmComboBox;
    private Label stopwatchLabel;
    private Timeline clockTimeline, alarmTimeline, stopwatchTimeline;
    private int stopwatchSeconds = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Main layout with TabPane for Clock, Alarm, and Stopwatch
        TabPane tabPane = new TabPane();

        // Clock Tab
        Tab clockTab = new Tab("Clock");
        clockTab.setContent(createClockTab());
        clockTab.setClosable(false);

        // Alarm Tab
        Tab alarmTab = new Tab("Alarm");
        alarmTab.setContent(createAlarmTab());
        alarmTab.setClosable(false);

        // Stopwatch Tab
        Tab stopwatchTab = new Tab("Stopwatch");
        stopwatchTab.setContent(createStopwatchTab());
        stopwatchTab.setClosable(false);

        // Add tabs to TabPane
        tabPane.getTabs().addAll(clockTab, alarmTab, stopwatchTab);

        // Scene Setup
        Scene scene = new Scene(tabPane, 450, 350);
        primaryStage.setTitle("Alarm Clock");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start the clock timeline
        startClock();
    }

    // Create Clock Tab content with modern enhancements
    private VBox createClockTab() {
        VBox clockLayout = new VBox(20);
        clockLayout.setAlignment(Pos.CENTER);
        clockLayout.setStyle("-fx-background-color: linear-gradient(#4facfe, #00f2fe); -fx-padding: 30px;");

        // Apply Drop Shadow
        DropShadow shadow = new DropShadow(10, Color.BLACK);

        // Current Time and Date Display
        timeLabel = new Label();
        dateLabel = new Label();
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");
        dateLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        timeLabel.setEffect(shadow);
        dateLabel.setEffect(shadow);

        updateDateTime();
        clockLayout.getChildren().addAll(timeLabel, dateLabel);

        return clockLayout;
    }

    // Create Alarm Tab content with modern enhancements
    private VBox createAlarmTab() {
        VBox alarmLayout = new VBox(20);
        alarmLayout.setAlignment(Pos.CENTER);
        alarmLayout.setStyle("-fx-background-color: linear-gradient(#2c3e50, #34495e); -fx-padding: 30px;");

        // Alarm Setup Section
        Label alarmLabel = new Label("Set Alarm (HH:MM AM/PM):");
        alarmLabel.setStyle("-fx-text-fill: white;");

        HBox alarmSetup = new HBox(10);
        alarmSetup.setAlignment(Pos.CENTER);
        alarmHourField = createStyledTextField("HH");
        alarmMinuteField = createStyledTextField("MM");

        amPmComboBox = new ComboBox<>();
        amPmComboBox.getItems().addAll("AM", "PM");
        amPmComboBox.setValue("AM");

        Button setAlarmButton = createStyledButton("Set Alarm");
        setAlarmButton.setOnAction(e -> setAlarm());

        alarmSetup.getChildren().addAll(alarmHourField, alarmMinuteField, amPmComboBox, setAlarmButton);
        alarmLayout.getChildren().addAll(alarmLabel, alarmSetup);

        return alarmLayout;
    }

    // Create Stopwatch Tab content with modern enhancements
    private VBox createStopwatchTab() {
        VBox stopwatchLayout = new VBox(20);
        stopwatchLayout.setAlignment(Pos.CENTER);
        stopwatchLayout.setStyle("-fx-background-color: linear-gradient(#1abc9c, #16a085); -fx-padding: 30px;");

        // Stopwatch Display
        stopwatchLabel = new Label("00:00:00");
        stopwatchLabel.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: bold;");
        stopwatchLabel.setEffect(new DropShadow(10, Color.BLACK));

        // Stopwatch Buttons
        HBox stopwatchButtons = new HBox(15);
        stopwatchButtons.setAlignment(Pos.CENTER);

        Button startStopwatchButton = createStyledButton("Start");
        Button stopStopwatchButton = createStyledButton("Stop");
        Button resetStopwatchButton = createStyledButton("Reset");

        startStopwatchButton.setOnAction(e -> startStopwatch());
        stopStopwatchButton.setOnAction(e -> stopStopwatch());
        resetStopwatchButton.setOnAction(e -> resetStopwatch());

        stopwatchButtons.getChildren().addAll(startStopwatchButton, stopStopwatchButton, resetStopwatchButton);
        stopwatchLayout.getChildren().addAll(stopwatchLabel, stopwatchButtons);

        return stopwatchLayout;
    }

    // Method to update the time and date labels
    private void updateDateTime() {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy");
        LocalDateTime now = LocalDateTime.now();
        timeLabel.setText(now.format(timeFormat));
        dateLabel.setText(now.format(dateFormat));
    }

    // Start clock timeline
    private void startClock() {
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateDateTime()));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    // Alarm setup
    private void setAlarm() {
        try {
            int alarmHour = Integer.parseInt(alarmHourField.getText());
            int alarmMinute = Integer.parseInt(alarmMinuteField.getText());
            String amPm = amPmComboBox.getValue();
            String alarmTime = String.format("%02d:%02d %s", alarmHour, alarmMinute, amPm);

            alarmTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> checkAlarm(alarmTime)));
            alarmTimeline.setCycleCount(Timeline.INDEFINITE);
            alarmTimeline.play();
        } catch (NumberFormatException ex) {
            showAlert("Invalid Time", "Please enter a valid time.");
        }
    }

    // Check Alarm Time
    private void checkAlarm(String alarmTime) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        LocalDateTime now = LocalDateTime.now();
        String currentTime = now.format(timeFormat);

        if (currentTime.equals(alarmTime)) {
            alarmTimeline.stop();
            showPopup("Alarm", "Time's up!");
        }
    }

    // Stopwatch methods
    private void startStopwatch() {
        stopwatchTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateStopwatch()));
        stopwatchTimeline.setCycleCount(Timeline.INDEFINITE);
        stopwatchTimeline.play();
    }

    private void stopStopwatch() {
        if (stopwatchTimeline != null) {
            stopwatchTimeline.stop();
        }
    }

    private void resetStopwatch() {
        stopwatchSeconds = 0;
        stopwatchLabel.setText("00:00:00");
    }

    private void updateStopwatch() {
        stopwatchSeconds++;
        int hours = stopwatchSeconds / 3600;
        int minutes = (stopwatchSeconds % 3600) / 60;
        int seconds = stopwatchSeconds % 60;
        stopwatchLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    // Create a styled TextField with rounded corners and padding
    private TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle("-fx-background-radius: 15; -fx-padding: 10px; -fx-text-fill: #fff; -fx-background-color: #16a085;");
        textField.setMaxWidth(60);
        return textField;
    }

    // Create a styled Button with hover effect
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-radius: 15; -fx-padding: 10px; -fx-text-fill: white; -fx-background-color: #2ecc71;");

        // Hover and Click effects
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;"));
            button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;"));

        // Adding scale transition for click effect
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
        button.setOnMousePressed(e -> {
            scaleTransition.setToX(0.95);
            scaleTransition.setToY(0.95);
            scaleTransition.playFromStart();
        });
        button.setOnMouseReleased(e -> {
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.playFromStart();
        });

        return button;
    }

    // Show alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Show popup notification for alarm
    private void showPopup(String title, String message) {
        Popup popup = new Popup();
        VBox popupContent = new VBox();
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setStyle("-fx-background-color: #e74c3c; -fx-padding: 20; -fx-background-radius: 10;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        popupContent.getChildren().addAll(titleLabel, messageLabel);
        popup.getContent().add(popupContent);

        // Set popup position
        popup.setAutoHide(true);
        popup.setAnchorX(300);
        popup.setAnchorY(200);
        popup.show(timeLabel.getScene().getWindow());

        // Fade in effect
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), popupContent);
        scaleTransition.setFromX(0.5);
        scaleTransition.setFromY(0.5);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.play();
    }
}
