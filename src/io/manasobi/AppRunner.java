package io.manasobi;

import io.manasobi.kafka.ProducerFactory;
import io.manasobi.utils.FileUtils;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import kafka.javaapi.producer.Producer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;

@SpringBootApplication
public class AppRunner extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Anypoint Log Connector");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(15);


        Text scenetitle = new Text("Kafka Producer");
        //scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        scenetitle.setId("welcome-text");

        grid.add(scenetitle, 0, 0, 2, 1);
        grid.setMargin(scenetitle, new Insets(30, 0, 10, 30));

        Label userName = null;
        userName = new Label("페이지");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("SIZE");
        grid.add(pw, 0, 2);

        ChoiceBox cb = new ChoiceBox(FXCollections.observableArrayList(
                "10,000", "100,000", "300,000", "500,000", "1,000,000")
        );

        cb.getSelectionModel().select(0);

        grid.add(cb, 1, 2);


        Button btn = new Button("execute");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 5);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                //actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setId("actiontarget");
                actiontarget.setText("Sign in button pressed");
            }
        });

        TextArea ta = TextAreaBuilder.create().prefWidth(320).prefRowCount(3).wrapText(true).build();
        ta.setEditable(false);

        grid.add(ta, 0, 5, 2, 1);

        Scene scene = new Scene(grid, 400, 620);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(AppRunner.class.getResource("Login.css").toExternalForm());
        primaryStage.show();
    }


    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(AppRunner.class);

        app.addListeners(getConfigFileApplicationListener(args));

        ConfigurableApplicationContext context = app.run(args);

        Producer producer = ProducerFactory.getInstance();

        JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");

        try {
            System.out.println(jdbcTemplate.getDataSource().getConnection().getSchema());
        } catch (SQLException e) {
            e.printStackTrace();
        }


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
