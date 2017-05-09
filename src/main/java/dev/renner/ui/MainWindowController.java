package dev.renner.ui;

import dev.renner.backend.util.Constants;
import dev.renner.backend.Game;
import dev.renner.backend.Mod;
import dev.renner.backend.util.ParadoxHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

        final ContextMenu randomListContextMenu = new ContextMenu();
        MenuItem replaceCardMenuItem = new MenuItem("Merge mods into one");
        replaceCardMenuItem.setOnAction(event -> {
            this.gamesListView.getSelectionModel().getSelectedItem().mergeMods();
        });
        randomListContextMenu.getItems().add(replaceCardMenuItem);

        this.games = FXCollections.observableArrayList();
        this.mods = FXCollections.observableArrayList();

        this.gamesListView.setItems(this.games);
        this.gamesListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                randomListContextMenu.show(gamesListView, event.getScreenX(), event.getScreenY());
            }
        });


        File paradoxFolder = new File(ParadoxHelper.getParadoxDirectory());
        String[] folders = paradoxFolder.list((a, b) -> {
            File dir = new File(a.getAbsolutePath() + "/" + b);
            return dir.isDirectory() && !dir.getName().trim().toLowerCase().equals(Constants.MOD_MANAGER_SETTINGS_FOLDER_NAME.toLowerCase().trim()) && !dir.getName().trim().toLowerCase().equals("pdxexporter");
        });

        for (String s : folders) {
            Game game = new Game(s, new File(paradoxFolder.getAbsolutePath() + "/" + s));
            this.games.add(game);
        }

        for (Game game : games) {
            System.out.println("Adding mods for game: " + game.getName());
            game.mods.addAll(Mod.getAllMods(game.gameFolder.getAbsolutePath() + "/mod/"));
            for (Mod mod : game.mods) {
                System.out.println(mod.path);
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

        this.modsListView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && !this.modsListView.getSelectionModel().getSelectedItem().isWorkshopMod ) {
                try{
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("ModEditor.fxml"));
                    ModEditorController modEditorController = new ModEditorController(this.modsListView.getSelectionModel().getSelectedItem());
                    fxmlLoader.setController(modEditorController);
                    Parent root = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.DECORATED);
                    stage.setTitle("Mod Editor");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        this.launchGameButton.setOnAction(event -> {
            URI uri = null;
            try {
                String url = "steam://run/" + this.gamesListView.getSelectionModel().getSelectedItem().paradoxGame.getSteamID();
                System.out.println("Staring game: " + url);
                uri = new URI(url);

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

}
