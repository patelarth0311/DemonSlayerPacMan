package ds;

import javafx.application.Platform;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;


public class GameLaunch extends Game {

    private HBox hbox;
    private static VBox vb = new VBox();
    private Hyperlink start;
    private Hyperlink question;
    private Hyperlink about;
    private Random rng;
    static Label score;
    static Label level;
    static BorderPane root;
    public static Background background;
    public static Font font;
    private Font headerFont;



    String fontString = "../demonslayergifs/Blood-Crow-Condensed/";




    {
        try {
            headerFont = Font.loadFont(new FileInputStream((fontString + "bloodcrowsc.ttf")), 60);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }




    {
        try {
            font = Font.loadFont(new FileInputStream((fontString+ "bloodcrowsc.ttf")), 80);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static Font fontSmall;

    {
        try {
            fontSmall = Font.loadFont(new FileInputStream((fontString + "bloodcrowc.ttf")), 30);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static void runNow(Runnable target) {
        Thread thread = new Thread(target);
        thread.setDaemon(true);
        thread.start();
    }



    GameLaunch(int width, int height){
        super(width,height,60);


    }


    // Sets up the loading page
    public void loading() {

        Text text = new Text("Demon Slayer Pac-Man");
        text.setFont(font);
        text.setFill(Color.web("#c4044f"));





        final StringProperty[] percentage = {new SimpleStringProperty(String.format("%d%%", 0))};
        Label loading = new Label();
        loading.setFont(font);
        loading.setTextFill(Color.web("#ffffff"));






        vb.getChildren().addAll(text,chars(),loading);

        vb.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setMargin(vb.getChildren().get(0),new Insets(150,0,350,35));
        VBox.setMargin(vb.getChildren().get(2),new Insets(0,20,5,50));


        Task<Void> task = new Task<>() {

            @Override
            protected Void call() throws Exception {
                int steps = 100;
                for (int i = 0; i < steps; i++) {
                    Thread.sleep(30);
                    percentage[0] = new SimpleStringProperty(String.format("%d%%", i));
                    Platform.runLater(() -> loading.textProperty().bind(percentage[0]));
                }
                try {
                    font = Font.loadFont(new FileInputStream((fontString + "bloodcrowc.ttf")), 60);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String buttonStyle = "-fx-accent: #ffffff;" +
                        "-fx-underline: false;";
                start = new Hyperlink("Start");
                start.setFont(font);
                start.setStyle(buttonStyle);
                question = new Hyperlink("Help");
                question.setFont(font);
                question.setStyle(buttonStyle);
                about = new Hyperlink("About");
                about.setFont(font);
                about.setStyle(buttonStyle);

                return null;
            }


        };
        task.setOnFailed(failed -> failed.getSource().getException().printStackTrace());


        task.setOnSucceeded(suc -> {
            BackgroundFill background_fill = new BackgroundFill(Color.web("#000000"),
                    CornerRadii.EMPTY, Insets.EMPTY);
            Background background = new Background(background_fill);
            Stage stage = new Stage();
            Platform.runLater(() -> {
               vb.getChildren().remove(2);
               HBox hb = new HBox(200);
               hb.setAlignment(Pos.BOTTOM_CENTER);
               hb.getChildren().addAll(about,start,question);
                vb.getChildren().add(hb);

                //About
                about.setOnAction(abt -> {
                    VBox vb2 = new VBox(60);
                    vb2.setBackground(background);
                    vb2.setAlignment(Pos.CENTER);
                    Text welcome = new Text("Welcome");
                    Text info = new Text("I created this simple arcade game of Pac-Man, but with some personal touches.\n" +
                            "As I do watch Demon Slayer, I decided to implement aspects of the anime when I was creating this game.\n" +
                            "Rather than playing as PacMan, one plays as the ghost. As implied by the title, the objective is not to get slayed " +
                            "by either one of the four characters from the show. Enjoy playing.\n"+
                            "- Arth Patel");

                    vb2.getChildren().addAll(welcome,info);
                    welcome.setFont(headerFont);
                    welcome.setFill(Color.web("#45b790"));
                    vb2.setPrefWidth(550);
                    stage.setResizable(false);
                    info.setFont(fontSmall);
                    info.setFill(Color.web("#ffffff"));

                    info.setWrappingWidth(590);
                    Scene scene = new Scene(vb2, 700,700);
                    stage.setScene(scene);

                    stage.show();



                });

                //Help
                question.setOnAction(quest-> {

                    VBox vb2 = new VBox(60);
                    vb2.setBackground(background);
                    vb2.setAlignment(Pos.CENTER);
                    Text game_play = new Text("Game Play");
                    Text info = new Text("To start the game, click on \"Start\".\n\n" +
                            "Objectives\n"+
                            "- Collect all the dots to proceed to the next level.\n"+
                            "- Avoid contact with a demon slayer.\n" +
                            "- Collect the demon eyes to gain immunity for 8 seconds.\n\n" +
                            "Controls\n" +
                            "Up-Arrow: Move up\n"+
                            "Down-Arrow: Move down\n"+ "Right-Arrow: Move right\n"+
                            "Left-Arrow: Move left\n" +
                            "Q: Quit current game\n");

                    vb2.getChildren().addAll(game_play,info);
                    game_play.setFont(  headerFont);
                    game_play.setFill(Color.web("#f0a048"));
                    vb2.setPrefWidth(550);
                    stage.setResizable(false);
                    info.setFont(fontSmall);
                    info.setFill(Color.web("#ffffff"));
                    info.setWrappingWidth(590);
                    Scene scene = new Scene(vb2, 800,800);
                    stage.setScene(scene);
                    stage.show();

                });

                // Start game
                start.setOnAction(launch-> {
                    stop();
                    vb.getChildren().clear();
                    DemoGame game = new DemoGame(945, 945);
                    scoreLvlMenu();
                    vb.getChildren().addAll(root,game);

                    game.play();

                });
            });
        });


        runNow(() -> {
            new Thread(task).start();
        });

    }


    // init characters for the welcome page
    public HBox chars() {
        hbox = new HBox(8);

        Image ghost = new Image(new File("../demonslayergifs/ghost2.gif").toURI().toString());
        Image tanjiro = new Image(new File("../demonslayergifs/tenor.gif").toURI().toString());
        Image nezz = new Image(new File("../demonslayergifs/nezz.gif").toURI().toString());
        Image zen = new Image(new File("../demonslayergifs/zen.gif").toURI().toString());
        Image boar = new Image(new File("../demonslayergifs/boar.gif").toURI().toString());

        Image[] imgArr = new Image[] {ghost, tanjiro, nezz, zen, boar};

        for (Image image : imgArr) {
            hbox.getChildren().addAll(new ImageView(image));
        }
        for (Node child : hbox.getChildren()) {
            HBox.setMargin(child, new Insets(0,0,36,0));

        }
        HBox.setMargin(hbox.getChildren().get(0), new Insets(30,40,20,20));
        hbox.setAlignment(Pos.CENTER);

        return hbox;
}


    @Override
    protected void init() {
        loading();
        getChildren().addAll(vb);
    }

    @Override
    protected void update() {
    }


    // Creates the top menu that displays the score and level.
    public void scoreLvlMenu() {
        Font font = null;
        try {

            font = Font.loadFont(new FileInputStream(new File("../demonslayergifs/Blood-Crow-Condensed/bloodcrowc.ttf")), 40);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        score = new Label(String.format("Score: %d", DemoGame.scoreLvl()));

        score.setFont(font);

        level = new Label(String.format("Level: %d", DemoGame.level()));

        level.setFont(font);


        level.setTextFill(Color.web("#0085b0"));
        score.setTextFill(Color.web("#0085b0"));

        BackgroundFill background_fill = new BackgroundFill(Color.web("#000000"),
                CornerRadii.EMPTY, Insets.EMPTY);
        background = new Background(background_fill);
        HBox menu = new HBox();
        menu.setPrefHeight(90);
        menu.setAlignment(Pos.CENTER);
       Region spacer = new Region();
        spacer.getStyleClass().add("menu-bar");

        HBox.setHgrow(spacer, Priority.SOMETIMES);
        root = new BorderPane();
        spacer.setBackground(background);
        menu.getChildren().addAll(score, spacer,level);
        HBox.setMargin(menu.getChildren().get(0), new Insets(0,0,0,40));
        HBox.setMargin(menu.getChildren().get(2), new Insets(0,40,0,0));
        root.setTop(menu);

    }




}
