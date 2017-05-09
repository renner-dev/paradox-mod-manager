package dev.renner.ui;

import dev.renner.backend.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.controlsfx.control.table.TableFilter;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by renne on 09.05.2017.
 */
public class ModEditorController implements Initializable {

    @FXML
    TableView<LocalisationHelper> localisationTableView;
    ObservableList<LocalisationHelper> data = FXCollections.observableArrayList();

    private Mod mod;
    private Localisation localisation;

    public ModEditorController(Mod selectedItem) {
        this.mod = selectedItem;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.localisationTableView.getColumns().clear();
        TableFilter filter = new TableFilter(this.localisationTableView);
        filter.executeFilter();

        System.out.println(mod.path.getAbsolutePath());
        localisation = Localisation.parseFolder(new File(mod.path.getAbsolutePath() + "/localisation/"));

        {
            TableColumn<LocalisationHelper, String> column = new TableColumn("Key");
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().key));
            this.localisationTableView.getColumns().add(column);
        }

        for (Language language : Language.values()) {
            TableColumn<LocalisationHelper, String> column = new TableColumn(language.getUiStr());
            column.setCellValueFactory(param ->
            {
                String value = "";
                for (LocalText localText : param.getValue().values) {
                    if (localText.language.equals(language)) {
                        return new SimpleStringProperty(localText.value);
                    }
                }

                return new SimpleStringProperty(value);
            });
            this.localisationTableView.getColumns().add(column);
        }

        this.localisationTableView.setItems(this.data);
        for (Map.Entry<String, List<LocalText>> entry : localisation.localisatioMap.entrySet()) {
            LocalisationHelper localisationHelper = new LocalisationHelper();
            localisationHelper.key = entry.getKey();
            localisationHelper.values = entry.getValue();

            this.data.add(localisationHelper);
        }

    }
}
