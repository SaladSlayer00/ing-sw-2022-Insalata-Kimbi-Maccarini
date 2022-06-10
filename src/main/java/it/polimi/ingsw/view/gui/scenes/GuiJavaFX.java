package it.polimi.ingsw.view.gui.scenes;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.view.gui.Gui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.IOException;

public class GuiJavaFX extends Application {

    @Override
    public void start(Stage stage) {
        Gui view = new Gui();
        ClientController clientController = new ClientController(view);
        view.addObserver(clientController);

        // Load root layout from fxml file.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/menu_scene.fxml"));
        Parent rootLayout = null;
        try {
            rootLayout = loader.load();
        } catch (IOException exception) {
            Client.LOGGER.severe(exception.getMessage());
            System.exit(1);
        }
        MainSceneController controller = loader.getController();
        controller.addObserver(clientController);

        //shows the scene with the main layout
        Scene scene = new Scene(rootLayout);
        stage.setScene(scene);
        stage.setWidth(1280d);
        stage.setHeight(720d);
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setTitle("Eryantis Board Game");
        stage.show();
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }
}