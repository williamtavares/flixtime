package flixtime;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.image4j.codec.ico.ICODecoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Controller {

    @FXML
    public VBox homeSection;

    @FXML
    public VBox labelContainer;

    private TrayIcon trayIcon;
    private boolean firstTime;

    public void createButtons() {
            List<HBox> hboxList = new ArrayList<>();

            int index = 1;
            for(Screen screen  : Screen.getScreens()) { //iterate over every row returned
                HBox hbox = new HBox();
                ToggleButton button = new ToggleButton();
                button.setText("Darken Monitor "+index);
                button.getStyleClass().add("darkButton");
                button.setOnAction(this::handleButtonAction);
                button.setId(String.valueOf(index));
                button.setFocusTraversable(false);
                hbox.getChildren().clear(); //remove all Buttons that are currently in the container
                hbox.getChildren().addAll(button); //then add all your Buttons that you just created
                hbox.getStyleClass().add("buttonContainer");
                hbox.setAlignment(Pos.CENTER);
                hboxList.add(hbox);
                index++;
            }

            homeSection.getChildren().addAll(hboxList);
            homeSection.setSpacing(10);
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        ToggleButton button = (ToggleButton) event.getSource();
        if (button.isSelected()) {
            try {
                Parent part = FXMLLoader.load(getClass().getResource("darkOverlay.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(part);
                stage.initStyle(StageStyle.TRANSPARENT);
                scene.setFill(Color.TRANSPARENT);

                final Node source = (Node) event.getSource();
                String id = source.getId();
                Screen selectedScreen = Screen.getScreens().get(Integer.parseInt(id)-1);
                Rectangle2D bounds = selectedScreen.getVisualBounds();
                stage.setX(bounds.getMinX() + 100);
                stage.setY(bounds.getMinY() + 100);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
                stage.setOnHidden(e -> button.setSelected(false));
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        if(!button.isSelected()) {
                            stage.close();
                        } else {
                            stage.show();
                        }
                    }
                });
            } catch (IOException e) {
                System.out.println(e);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void createTrayIcon(final Stage stage, Parent root) {
        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            SystemTray tray = SystemTray.getSystemTray();
            // load an image
            java.awt.Image image = null;
            try {
                image = ImageIO.read(this.getClass().getResource("/resources/images/icon.png"));
            } catch (IOException ex) {
                System.out.println(ex);
            }

            //Minimize the application to tray when "ESC" is pressed while in the app
            root.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    //If escape key is pressed, close the window
                    if(event.getCode().getCode() == 27) {
                        hide(stage);
                    }
                }
            });

//            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//                @Override
//                public void handle(WindowEvent t) {
//                    hide(stage);
//                }
//            });
            // create a action listener to listen for default action executed on the tray icon
            final ActionListener closeListener = new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.exit(0);
                }
            };

            ActionListener showListener = new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.show();
                        }
                    });
                }
            };
            // create a popup menu
            PopupMenu popup = new PopupMenu();

//            MenuItem showItem = new MenuItem("Show");
//            showItem.addActionListener(showListener);
//            popup.add(showItem);

            MenuItem closeItem = new MenuItem("Close");
            closeItem.addActionListener(closeListener);
            popup.add(closeItem);
            /// ... add other items
            // construct a TrayIcon
            trayIcon = new TrayIcon(image, "FlixTime", popup);
            // set the TrayIcon properties
            trayIcon.addActionListener(showListener);
            // ...
            // add the tray image
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
            }
            // ...
        }
    }

    public void showProgramIsMinimizedMsg() {
        if (firstTime) {
            trayIcon.displayMessage("FlixTime",
                    "Running in the background",
                    TrayIcon.MessageType.INFO);
            firstTime = false;
        }
    }

    private void hide(final Stage stage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (SystemTray.isSupported()) {
                    stage.hide();
                    showProgramIsMinimizedMsg();
                } else {
                    System.exit(0);
                }
            }
        });
    }

    public void closeWindow(InputEvent event) {
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}

