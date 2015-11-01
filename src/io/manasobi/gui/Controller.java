package io.manasobi.gui;

import io.manasobi.kafka.KafkaMessageProducer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class Controller {

    @FXML
    private TextField page;

    @FXML
    private ChoiceBox size;

    @FXML
    private CheckBox isTruncateTable;

    @FXML
    private TextArea console;

    private static KafkaMessageProducer producer;

    @FXML
    protected void handleExecuteButtonAction(ActionEvent e) {

        int pageParam = Integer.valueOf(page.getText());

        int sizeParam = Integer.valueOf(size.getSelectionModel().getSelectedItem().toString().replaceAll(",", ""));

        boolean enableTruncateTableJob = isTruncateTable.isSelected();

        console.setText("");

        if (producer == null) {
            producer = new KafkaMessageProducer();
        }

        producer.process(console, pageParam, sizeParam, enableTruncateTableJob);
    }
}
