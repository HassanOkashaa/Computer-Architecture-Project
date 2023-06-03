package main;

import java.io.File;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class MainView extends Application {

	private Architecture architecture;
	private HandleScenes handlescenes;
	private Stage stage;
	
	public void start(Stage stage) throws Exception {

		GridPane root = new GridPane();
		architecture = new Architecture();
		handlescenes = new HandleScenes(root, 100.0, 50.0, this, architecture);
		Image icon = new Image("CAlogoNoBkg.png");
		this.stage = stage;
		this.stage.getIcons().add(icon);

		BackgroundImage backgroundimage = new BackgroundImage(new Image("CAphoto.png"), BackgroundRepeat.REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(100, 100, true, true, true, false));
		Background background = new Background(backgroundimage);
		root.setBackground(background);
		
		handlescenes.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.SPACE) {
					handlescenes.setRoot(handlescenes.home());
				}
			}
		});
		this.stage.setTitle("Double Big Harvard");
		this.stage.setScene(handlescenes);
		this.stage.setFullScreen(true);
		this.stage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}


}
