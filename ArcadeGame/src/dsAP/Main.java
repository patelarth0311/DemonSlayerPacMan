package ds;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.application.Platform;

public class Main extends Application  {

    @Override
    public void start(Stage stage) throws Exception {

        BackgroundFill background_fill = new BackgroundFill(Color.web("#000000"),
                CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(background_fill);



                Game gl = new GameLaunch(945, 945);
                VBox root = new VBox(gl);

                root.setBackground(background);
                Scene scene = new Scene(root);
                stage.setResizable(false);
               // setup stage
                stage.setTitle("Demon Slayer Pac-Man");
                stage.setScene(scene);

                stage.setOnCloseRequest(event -> {

                                Platform.exit();

                        });
                 stage.sizeToScene();
                 stage.show();
                 gl.play();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
