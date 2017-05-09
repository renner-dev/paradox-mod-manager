package dev.renner.ui;

import dev.renner.backend.util.Constants;
import dev.renner.backend.Game;
import dev.renner.backend.Mod;
import dev.renner.backend.util.ParadoxHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by renne on 08.05.2017.
 */
public class MainWindowController implements Initializable {

    @FXML
    ListView<Game> gamesListView;
    ObservableList<Game> games;

    @FXML
    ListView<Mod> modsListView;
    ObservableList<Mod> mods;

    @FXML
    TextField filterTextField;

    @FXML
    Button launchGameButton;

    @FXML
    Button saveModpackButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.games = FXCollections.observableArrayList();
        this.mods = FXCollections.observableArrayList();

        this.gamesListView.setItems(this.games);


        this.setupModPacks();

        File paradoxFolder = new File(ParadoxHelper.getParadoxDirectory());
        String[] folders = paradoxFolder.list((a, b) -> {
            File dir = new File(a.getAbsolutePath() + "/" + b);
            return dir.isDirectory() && !dir.getName().trim().toLowerCase().equals(Constants.MOD_MANAGER_SETTINGS_FOLDER_NAME.toLowerCase().trim());
        });

        for (String s : folders) {
            Game game = new Game(new File(paradoxFolder.getAbsolutePath() + "/" + s));
            game.name = s;
            this.games.add(game);
        }

        for (Game game : games) {
            System.out.println("Adding mods for game: " + game.name);
            game.mods.addAll(Mod.getAllMods(game.gameFolder.getAbsolutePath() + "/mod/"));
            for (Mod mod : game.mods) {
                mod.active.addListener((observable, oldValue, newValue) ->
                {
                  //this.gamesListView.getSelectionModel().getSelectedItem().saveSettings();
                });
            }
            game.parse();
            System.out.println(game.mods);
        }

        this.gamesListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    this.mods.clear();
                    this.mods.addAll(((Game) newValue).mods);
                });

        FilteredList<Mod> filteredData = new FilteredList<>(this.mods, s -> true);
        this.filterTextField.textProperty().addListener(obs -> {
            String filter = this.filterTextField.getText();
            if (filter == null || filter.length() == 0) {
                filteredData.setPredicate(s -> true);
            } else {
                filteredData.setPredicate(s -> s.name.contains(filter));
            }
        });

        this.modsListView.setItems(filteredData);
        this.modsListView.setCellFactory(CheckBoxListCell.forListView(item -> item.active));
        this.modsListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    //this.gamesListView.getSelectionModel().getSelectedItem().saveSettings();
                });

        this.launchGameButton.setOnAction(event -> {
            URI uri = null;
            try {
                uri = new URI("steam://run/" + this.gamesListView.getSelectionModel().getSelectedItem().paradoxGame.getSteamID());

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(uri);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.gamesListView.getSelectionModel().selectFirst();

        this.saveModpackButton.setOnAction(event -> this.gamesListView.getSelectionModel().getSelectedItem().saveSettings());

    }

    private void setupModPacks() {

        File modpackDirectory = new File(ParadoxHelper.getParadoxDirectory() + "/modmanager");
        if (!modpackDirectory.exists()) {
            modpackDirectory.mkdir();
        }


    }

}
