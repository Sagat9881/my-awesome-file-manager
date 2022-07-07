package ru.apzakharov;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PanelController implements Initializable {

    @FXML
    private TableView<FileInfo> filesTable;

    @FXML
    private ComboBox<String> disksBox;
    @FXML
    private TextField pathField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initializeFileTable();
        initializeComboBox();

        updateList(Paths.get("C:\\Users\\Павел\\Desktop\\DIY\\my-awesome-file-manager\\my-awesome-file-manager\\src\\main\\resources\\A"));

    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            updateList(upperPath);
        }
    }

    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        updateList(Paths.get(element.getSelectionModel().getSelectedItem()));
    }

    public void updateList(Path path) {
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            filesTable.getItems().clear();
            filesTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            filesTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось обновить список файлов :(\n Причина: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    public String getSelectedFileName() {
        String result = null;
        if (filesTable.isFocused()) {
            result = filesTable.getSelectionModel().getSelectedItem().getFileName();
        }

        return result;
    }

    public String getCurrentPath(){
        return pathField.getText();
    }

    private void initializeComboBox() {
        disksBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            disksBox.getItems().add(p.toString());
        }
        disksBox.getSelectionModel().select(0);
    }

    private void initializeFileTable() {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>("Тип");
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getType()));
        fileTypeColumn.setPrefWidth(24D);

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Имя");
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
        fileNameColumn.setPrefWidth(240D);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setPrefWidth(120);
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
        });

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата изменения");
        fileDateColumn.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().getLastModified().format(dtf));
        });
        fileDateColumn.setPrefWidth(120);

        filesTable.getSortOrder().add(fileTypeColumn);
        filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);

        filesTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedDirectory = filesTable.getSelectionModel().getSelectedItem().getFileName();
                Path path = Paths.get(pathField.getText()).resolve(selectedDirectory);
                if (Files.isDirectory(path)) {
                    updateList(path);
                }
            }
        });
    }


}
