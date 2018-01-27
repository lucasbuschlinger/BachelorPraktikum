package de.opendiabetes.vault.plugin.importer.googlefitcrawler.javaFX.views;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.helper.Constants;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.models.ResolvedLocations;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConflictedLocations extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    private ObservableList<String> ads;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Conflicted Locations");
        initRootLayout();
        primaryStage.show();
    }


    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/ConflictView.fxml"));
            AnchorPane rootLayout = (AnchorPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String output = gson.toJson(ResolvedLocations.getInstance());
        File file = new File(System.getProperty("user.home") + Constants.RESOLVED_LOCATION_PATH);

        try {
            FileWriter writer = new FileWriter(file, false);
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
