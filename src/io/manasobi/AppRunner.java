package io.manasobi;

import io.manasobi.utils.FileUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;

@SpringBootApplication
public class AppRunner extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("gui/application.fxml"));

        Scene scene = new Scene(root, 600, 450);
        stage.setScene(scene);
        stage.setTitle("Anypoint Log Connector ver-1.0.0");
        stage.show();

    }

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(AppRunner.class);

        app.addListeners(getConfigFileApplicationListener(args));
        app.run(args);

        launch(args);
    }

    private static ConfigFileApplicationListener getConfigFileApplicationListener(String[] args) {

        String configFilePath;

        String userDir = System.getProperty("user.dir");

        String configFile = "/build/classes/application.yml";

        if (FileUtils.existsFile(userDir + configFile)) {
            configFilePath = userDir + configFile;
        } else {
            configFilePath = userDir + "/anypoint-kafka-producer.conf";
        }

        ConfigFileApplicationListener listener = new ConfigFileApplicationListener();

        listener.setSearchLocations(configFilePath);

        return listener;
    }
}
