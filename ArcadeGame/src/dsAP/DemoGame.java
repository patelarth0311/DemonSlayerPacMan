package ds;
  import java.io.File;
  import java.io.FileNotFoundException;

  import java.util.Objects;
  import java.util.Scanner;

  import javafx.animation.KeyFrame;
  import javafx.animation.Timeline;
  import javafx.application.Platform;
  import javafx.beans.property.SimpleStringProperty;
  import javafx.beans.property.StringProperty;
  import javafx.geometry.*;
  import javafx.scene.image.Image;
  import javafx.scene.image.ImageView;
  import javafx.scene.input.KeyCode;

  import javafx.scene.layout.*;
  import javafx.scene.paint.Color;
  import javafx.scene.text.Text;
  import javafx.util.Duration;



         public class DemoGame extends Game {
    public enum CellV {
        EMPTY, SMALLDOT, EYES, WALL, PLAYER, SLAYER, DEADEND
    }

    public enum FireBallDirection {
        RIGHT, LEFT, DOWN, UP,
    }

    public enum slayerDirection {
        RIGHT, LEFT, DOWN, UP,
    }


    int level = 0;
    int slayerTurn = 0;


    private static int numScore;
    private static int lvlNum = 1;
    int smallDotCounter = 0;
    boolean checkContact = false;

    double ghostSpeed = 5;
    double slayerSpeed = 3;

    boolean sameCol = false;
    boolean sameRow = false;
    private ImageView player;
    boolean immune = false;

    Timeline tl = new Timeline();

    String path = "../levels/";
    File[] gridPlan = new File[]{new File(path + "grid.txt"),
            new File(path + "grid2.txt"), new File(path + "grid3.txt"),
            new File(path + "grid4.txt")};

    String slayerPath = "../demonslayergifs/";
    ImageView[] slayerPick = new ImageView[]{new ImageView(new Image(new File(slayerPath + "tenor.gif").toURI().toString())),
            new ImageView(new Image(new File(slayerPath + "nezz.gif").toURI().toString())),
            new ImageView(new Image(new File(slayerPath + "boar.gif").toURI().toString())),
            new ImageView(new Image(new File(slayerPath + "zen.gif").toURI().toString()))
    };

    ImageView fireBall = new ImageView(new Image(new File(slayerPath + "purplefire.gif").toURI().toString()));





    GridPane grid = new GridPane();



    public DemoGame(int width, int height) {
        super(width, height, 60);


    }


    @Override
    protected void init() {


        getChildren().addAll(grid(gridPlan[level]), player, slayerPick[slayerTurn]);

        player.setX(45 * charCol(ghostCol));
        player.setY(45 * charRow(ghostRow));

        slayerPick[slayerTurn].setX(45 * charCol(slayerCol));
        slayerPick[slayerTurn].setY(45 * charRow(slayerRow));
        fireball();


    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void update() {


        if (smallDotCounter == 0) {

            getChildren().clear();
            grid = new GridPane();
            if (level < gridPlan.length) {
                level++;
                slayerTurn++;
            } // First four rounds of the game will have unique maps and characters
            if (level == 4) {
                level = (int) (Math.random() * 3);
                slayerTurn = (int) (Math.random() * 3);
            } // After the fourth, slayers and map are chosen randomly

            rows = 1;

            stop();
            getChildren().clear();
            init();
            lvlNum++;
            StringProperty level = new SimpleStringProperty(String.format("Level: %d", level()));
            GameLaunch.level.textProperty().bind(level);
            play();
        }


        checkDots(player.getX(), player.getY());



        if (slayerPick[slayerTurn] == slayerPick[1]) { // for nezuko
            launchFireBall(player, slayerPick[1]);
            checkFireBallContact(player, fireBall);
        }
        if (slayerPick[slayerTurn] == slayerPick[3]) { // for zenitsu
           slayerSpeed = 5;
        } else {
            slayerSpeed = 3;
        }

        StringProperty score = new SimpleStringProperty(String.format("Score: %d", scoreLvl()));
        GameLaunch.score.textProperty().bind(score);

        checkContact(player, slayerPick[slayerTurn]);




        if (slayerPick[slayerTurn] == slayerPick[2]) { // for boar
            checkBoar(slayerPick[slayerTurn], player);
        }

        move(slayerPick[slayerTurn]);

        check(slayerPick[slayerTurn], player);

        isKeyPressed(KeyCode.LEFT, () -> {
            player.setScaleX(1);
            if (!checkWallLeftRight(player.getX(), player.getY(), KeyCode.LEFT, player)) {
                player.setX(player.getX() - ghostSpeed);
            }

        });


        isKeyPressed(KeyCode.RIGHT, () -> {
            player.setScaleX(-1);
            if (!checkWallLeftRight(player.getX(), player.getY(), KeyCode.RIGHT, player)) {
                player.setX(player.getX() + ghostSpeed);
            }

        });
        isKeyPressed(KeyCode.DOWN, () -> {
            if (!checkWallUpDown(player.getX(), player.getY(), KeyCode.DOWN)) {
                player.setY(player.getY() + ghostSpeed);
            }

        });
        isKeyPressed(KeyCode.UP, () -> {
            if (!checkWallUpDown(player.getX(), player.getY(), KeyCode.UP)) {
                player.setY(player.getY() - ghostSpeed);

            }
        });



        isKeyPressed(KeyCode.Q, this::quitLossWindow);


    } // update


    ImageView[][] ivArr;
    CellV[][] values;

    int rows = 1;
    int column = 0;
    int ghostRow = 0;
    int ghostCol = 0;
    int slayerRow = 0;
    int slayerCol = 0;

    public GridPane grid(File gridPlan) { // sets the grid and its graphics
        try {
            Scanner scan = new Scanner(gridPlan);
            if (scan.hasNextLine()) {
                column = scan.nextLine().split(" ").length;
            }

            while (scan.hasNextLine()) {
                rows++;
                scan.nextLine();

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        ivArr = new ImageView[rows][column]; // graphics stored here
        values = new CellV[rows][column]; // enum values stored here


        Scanner scannerNew = null;
        try {
            scannerNew = new Scanner(gridPlan);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int row1 = 0;

        while (scannerNew.hasNextLine()) {
            int col1 = 0;
            String line = scannerNew.nextLine();
            Scanner lineToRead = new Scanner(line);

            while (lineToRead.hasNext()) {
                String letter = lineToRead.next();

                switch (letter) {
                    case "W":
                        ivArr[row1][col1] = new ImageView(new Image(new File("../demonslayergifs/wall.png").toURI().toString()));
                        values[row1][col1] = CellV.WALL;

                        break;
                    case "S":
                        ivArr[row1][col1] = new ImageView(new Image(new File("../demonslayergifs/smalldot.png").toURI().toString()));
                        smallDotCounter++;
                        values[row1][col1] = CellV.SMALLDOT;

                        break;
                    case "B":
                        ivArr[row1][col1] = new ImageView(new Image(new File("../demonslayergifs/eyes.gif").toURI().toString()));
                        values[row1][col1] = CellV.EYES;
                        break;
                    case "K":
                        ivArr[row1][col1] = slayerPick[slayerTurn];
                        values[row1][col1] = CellV.SLAYER;
                        slayerPick[slayerTurn] = ivArr[row1][col1];
                        slayerCol = col1;
                        slayerRow = row1;
                        break;
                    case "P":
                        ghostRow = row1;
                        ghostCol = col1;

                        ivArr[row1][col1] = new ImageView(new Image(new File("../demonslayergifs/ghost2.gif").toURI().toString()));
                        player = ivArr[row1][col1];
                        values[row1][col1] = CellV.PLAYER;

                        break;
                    case "D":
                        ivArr[row1][col1] = new ImageView();
                        values[row1][col1] = CellV.DEADEND;
                        break;
                    case "E":
                        ivArr[row1][col1] = new ImageView();
                        values[row1][col1] = CellV.EMPTY;
                        break;
                }
                col1++;

            }
            row1++;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {

                ivArr[i][j].setFitHeight(45);
                ivArr[i][j].setFitWidth(45);
                ivArr[i][j].setPreserveRatio(true);
                grid.add(ivArr[i][j], j, i);

            }

        }
        grid.setGridLinesVisible(false);
        return grid;
    }

    public int charRow(int row) {

        return row;
    }

    public int charCol(int col) {

        return col;
    }

    // Checks if there is a wall to the left or right of the player when either the right or left key is pressed.
    public boolean checkWallLeftRight(double playerX, double playerY, KeyCode key, ImageView iv) {

        double playerRelativeLoc = 0;
        if (key.equals(KeyCode.LEFT)) {
            playerRelativeLoc = playerX - 45;
        }
        if (key.equals(KeyCode.RIGHT)) {
            playerRelativeLoc = playerX + 45;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {


                if (ivArr[i][j] != null) {

                    if (values[i][j] == CellV.WALL) {

                        double wallX = j * 45;
                        double wallY = i * 45;

                        if ((playerRelativeLoc == wallX) && (playerY == wallY || ((playerY > wallY - 45 && playerY < wallY + 45)))) {
                            return true;
                        }

                    } else if (values[i][j] == CellV.DEADEND) {
                        double deadX = j * 45;

                        if (playerX == deadX && ((key.equals(KeyCode.LEFT) && deadX == 0))) {
                            iv.setX(900);
                            return true;
                        }
                        if (playerX == deadX && (key.equals(KeyCode.RIGHT) && deadX == 900)) {
                            iv.setX(0);
                            return true;
                        }
                    }
                }


            }
        }
        return false;
    }


    // Checks if there is a wall above or below  the player when either the up or down key is pressed.
    public boolean checkWallUpDown(double playerX, double playerY, KeyCode key) {

        double playerRelativeLoc = 0;
        if (key.equals(KeyCode.UP)) {
            playerRelativeLoc = playerY - 45;
        }
        if (key.equals(KeyCode.DOWN)) {
            playerRelativeLoc = playerY + 45;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {


                if (ivArr[i][j] != null) {

                    if (values[i][j] == CellV.WALL) {

                        double wallX = j * 45;
                        double wallY = i * 45;

                        if ((playerRelativeLoc == wallY) && (playerX == wallX || ((playerX > wallX - 45 && playerX < wallX + 45)))) {
                            return true;
                        }

                    }
                }


            }
        }
        return false;
    }


    //Checks if the player has reached contact with a white doll or an eye.
    public boolean checkDots(double playerX, double playerY) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {

                CellV dotOrEyes = null;
                double x = -1;
                double y = -1;

                if (values[i][j] == CellV.SMALLDOT) {

                    dotOrEyes = CellV.SMALLDOT;
                    x = j * 45;
                    y = i * 45;
                }

                if (values[i][j] == CellV.EYES) {
                    dotOrEyes = CellV.EYES;
                    x = j * 45;
                    y = i * 45;
                }

                //(playerX > wallX -45 && playerX < wallX+45)
                if ((playerY == y || ((playerY > y - 25 && playerY < y + 25))) && ((playerX == x || ((playerX > x - 25 && playerX < x + 25))))) {

                       grid.getChildren().remove(ivArr[i][j]);

                    if (dotOrEyes == CellV.SMALLDOT) {
                        numScore = numScore + 5;
                        smallDotCounter--;
                    } else {
                        numScore = numScore + 10;
                        player.setImage(new Image(new File("../demonslayergifs/redGhost.gif").toURI().toString()));
                        immune = true;


                        tl.setCycleCount(1);
                        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(8), event -> {
                            Platform.runLater(() -> player.setImage(new Image(new File("../demonslayergifs/ghost2.gif").toURI().toString())));

                            immune = false;
                            tl.stop();
                        }
                        ));

                        tl.play();

                    }

                    ivArr[i][j] = new ImageView();
                    ivArr[i][j].setFitHeight(45);
                    ivArr[i][j].setFitWidth(45);
                    ivArr[i][j].setPreserveRatio(true);
                    grid.add(ivArr[i][j], j, i);
                    values[i][j] = CellV.EMPTY;
                }


            }
        }
        return false;
    }


    int otherIndex = 0;
    int upDownIndex = 0;


    double closestYWallBelowDiff;
    double closestXWallRightDiff;

    slayerDirection direction = null;




    public boolean move(ImageView iv) {


        if (  sameRow == true|| sameCol == true) {
                return true;
            }

        double[] charLeftRightNew = new double[]{iv.getX() - slayerSpeed, iv.getX() + slayerSpeed};
        double[] charLeftRightCheck = new double[]{iv.getX() - 45, iv.getX() + 45};
        double[] charUpDownCheck = new double[]{iv.getY() - 45, iv.getY() + 45};
        double[] charUpDownNew = new double[]{iv.getY() - slayerSpeed, iv.getY() + slayerSpeed};
        double closestYWallAboveDiff = iv.getY();
        double closestXWallLeftDiff = iv.getX();
        double closestYBlock;
        double closestXBlock;

        closestYWallBelowDiff = 810 - iv.getY();
        if (upDownIndex == 1) {
            closestYBlock = 810;
        } else {
            closestYBlock = 0;
        }

        closestXWallRightDiff = 900 - iv.getX();

        if (otherIndex == 1) {
            closestXBlock = 900;
        } else {
            closestXBlock = 0;
        }

        double[] closestYWallDiff = new double[]{closestYWallAboveDiff, closestYWallBelowDiff};
        double[] closestXWallDiff = new double[]{closestXWallLeftDiff, closestXWallRightDiff};

        double diffY = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {
                if (values[i][j] == CellV.WALL) {
                    double blockX = j * 45;
                    double blockY = i * 45;

                    boolean[] nearestWallY = new boolean[]{
                            blockX == iv.getX() && iv.getY() > blockY && (iv.getY() - blockY < closestYWallDiff[upDownIndex]),
                            blockX == iv.getX() && iv.getY() < blockY && (blockY - iv.getY() < closestYWallDiff[upDownIndex])
                    };

                    if (blockX == iv.getX() && iv.getY() > blockY && upDownIndex == 0) {
                        diffY = iv.getY() - blockY;
                    }
                    if (blockX == iv.getX() && iv.getY() < blockY && upDownIndex == 1) {
                        diffY = blockY - iv.getY();
                    }
                    if (nearestWallY[upDownIndex]) {
                        closestYWallDiff[upDownIndex] = diffY;
                        closestYBlock = blockY;
                    }
                }
            }
        }

        double blockX = 0;
        double blockY = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {
                if (values[i][j] == CellV.WALL) {

                    blockX = j * 45;
                    blockY = i * 45;

                    boolean[] charUpDown = new boolean[]{iv.getY() - 45 != closestYBlock && iv.getY() - blockY != 45 && iv.getY() > blockY,
                            iv.getY() + 45 != closestYBlock && blockY - iv.getY() != 45 && diffY != 45 && iv.getY() < blockY};

                    if (charUpDown[upDownIndex] && iv.getX() == blockX ) {
                        iv.setY(charUpDownNew[upDownIndex]);

                        if (upDownIndex == 1) {
                            direction = slayerDirection.DOWN;
                        } else {
                            direction = slayerDirection.UP;
                        }
                    } else if (charUpDownCheck[upDownIndex] == blockY && (iv.getX() == blockX)) {
                        upDownIndex++;
                        if (upDownIndex == 1) {
                            closestYWallBelowDiff = 810 - iv.getY();
                            closestYBlock = 810;
                        }

                        if (upDownIndex == 2) {
                            upDownIndex = (int) (Math.random() * 2);
                        }
                        break;

                    }
                }
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {
                if (values[i][j] == CellV.WALL) {
                    blockX = j * 45;
                     blockY = i * 45;

                    boolean[] nearestWallX = new boolean[]{
                            blockY == iv.getY() && iv.getX() > blockX && (iv.getX() - blockX < closestXWallDiff[otherIndex]),
                            blockY == iv.getY() && iv.getX() < blockX && (blockX - iv.getX() < closestXWallDiff[otherIndex])
                    };

                    double[] diff = new double[]{iv.getX() - blockX, blockX - iv.getX()};

                    if (nearestWallX[otherIndex]) {
                        closestXWallDiff[otherIndex] = diff[otherIndex];
                        closestXBlock = blockX;
                    }
                }
            }
        }

        boolean[] charLeftRight = new boolean[2];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {
                if (values[i][j] == CellV.WALL || values[i][j] == CellV.DEADEND) {
                    blockX = j * 45;
                    blockY = i * 45;

                    if (values[i][j] == CellV.DEADEND && otherIndex == 0 && closestXBlock == 0
                    && iv.getY() == blockY) {
                        closestXBlock = -45;

                    } else if (values[i][j] == CellV.DEADEND && otherIndex == 1 && closestXBlock == 900
                            && iv.getY() == blockY) {
                        closestXBlock = 945;
                    }



                    charLeftRight = new boolean[]{
                            iv.getX() - 45 != closestXBlock && iv.getX() > blockX
                            ,
                            iv.getX() + 45 != closestXBlock && iv.getX() < blockX
                    };

                    if ((charLeftRight[otherIndex]) && (iv.getY() == blockY)) {

                        iv.setX(charLeftRightNew[otherIndex]);

                        if (otherIndex == 1) {
                            if (iv.getX() == 900) {
                                iv.setX(0);
                            }
                            iv.setScaleX(-1);
                        } else {
                            if (iv.getX() == 0) {
                                iv.setX(900);
                            }
                            iv.setScaleX(1);
                        }
                    } else if (charLeftRightCheck[otherIndex] == blockX && (iv.getY() == blockY)) {
                        otherIndex++;
                        if (otherIndex == 1) {
                            closestXWallRightDiff = 900 - iv.getX();
                            closestXBlock = 900;
                        }
                        if (otherIndex == 2) {
                            otherIndex = (int) (Math.random() * 2);
                        }
                        break;
                    }
                }
            }
        }
        return false;
    }


    static int scoreLvl() {
        return numScore;
    }

    static int level() {
        return lvlNum;
    }


    // Prompts a window displaying level reached and score when either q key is pressed or
           //when the player loses.
    public void quitLossWindow() {

        VBox finish = new VBox(60);
        finish.setAlignment(Pos.CENTER);
        Text finalScore = new Text(String.format("Final Score: %d", scoreLvl()));
        Text finalLevel = new Text(String.format("Levels Reached: %d", level()));
        Text gameOver = new Text("Game Over");
        Text thanks = new Text("Thank you for playing!");
        Text[] texts = {finalScore, finalLevel, thanks, gameOver};

        for (Text text : texts) {
            text.setFont(GameLaunch.font);
            text.setFill(Color.web("#e3a619"));
        }

        Platform.runLater(()-> {
                    finish.getChildren().addAll(finalScore, finalLevel, thanks);
                });
        finish.setLayoutX(210);
        finish.setLayoutY(190);

        if (checkContact) {
            Platform.runLater(() -> {
                        finish.getChildren().add(0, gameOver);
                    });
            finish.setSpacing(30);
        }

        Platform.runLater(() -> {
                    getChildren().add(finish);
                });

        stop();
    }

    // Checks if the player or the ghost have come into contact with each other.
    public boolean checkContact(ImageView ghost, ImageView slayer) {
        if ((ghost.getX() == slayer.getX() || Math.abs(ghost.getX()-slayer.getX()) < 25) && (ghost.getY() == slayer.getY() || Math.abs(ghost.getY()-slayer.getY()) < 25) && !immune) {
            checkContact = true;
            quitLossWindow();
            return true;
        }
        checkContact = false;
        return false;
    }


    FireBallDirection directionFireBall = null;

    // Launches a fireball towards the player when Nezuko is chosen.
    public void launchFireBall(ImageView ghost, ImageView nezuko) {

        if (ghost.getX() == nezuko.getX() && ghost.getY() < nezuko.getY() && !fireBall.isVisible()
        && direction == slayerDirection.UP) {
            fireBall.setX(slayerPick[1].getX());
            fireBall.setY(slayerPick[1].getY() - 45);
            fireBall.setScaleY(-1);
            fireBall.setRotate( 0);
            fireBall.setVisible(true);
            directionFireBall = FireBallDirection.UP;
        }

        if (directionFireBall == FireBallDirection.UP) {
            if (fireBall.getY() != 45) {
                fireBall.setY(fireBall.getY() - 3);
            } else {
                fireBall.setVisible(false);
            }
        }

        if (ghost.getX() == nezuko.getX() && ghost.getY() > nezuko.getY() && !fireBall.isVisible()
                && direction == slayerDirection.DOWN) {
            fireBall.setX(slayerPick[1].getX());
            fireBall.setY(slayerPick[1].getY() + 45);
            fireBall.setScaleY(1);
            fireBall.setRotate( 0);
            fireBall.setVisible(true);
            directionFireBall = FireBallDirection.DOWN;
        }
        if (directionFireBall == FireBallDirection.DOWN) {
            if (fireBall.getY() != 765) {
                fireBall.setY(fireBall.getY() + 3);
            } else {
                fireBall.setVisible(false);
            }
        }


        if (ghost.getX() > nezuko.getX() && ghost.getY() == nezuko.getY() && !fireBall.isVisible() && nezuko.getScaleX() == -1) {

            fireBall.setX(slayerPick[1].getX() + 45);
            fireBall.setY(slayerPick[1].getY());
            fireBall.setRotate( 270);
            fireBall.setVisible(true);
            fireBall.setScaleX(1);
            fireBall.setScaleY(1);
            directionFireBall = FireBallDirection.RIGHT;
        }

        if (directionFireBall == FireBallDirection.RIGHT) {
            if (fireBall.getX() != 855 && fireBall.getX() != 900 ) {
                fireBall.setX(fireBall.getX() + 3);
            } else {
                fireBall.setVisible(false);
            }
        }

        if (ghost.getX() < nezuko.getX() && ghost.getY() == nezuko.getY() && !fireBall.isVisible() && nezuko.getScaleX() == 1) {
            fireBall.setX(slayerPick[1].getX() - 45);
            fireBall.setY(slayerPick[1].getY());
            fireBall.setRotate( 90);
            fireBall.setScaleX(1);
            fireBall.setScaleY(1);
            fireBall.setVisible(true);
            directionFireBall = FireBallDirection.LEFT;
        }
        if (directionFireBall == FireBallDirection.LEFT) {
            if (fireBall.getX() != 45 && fireBall.getX() != 0) {
                fireBall.setX(fireBall.getX() - 3);
            } else {
                fireBall.setVisible(false);
            }
        }

    }

           // Init fireball
        public void fireball () {
            fireBall.setFitHeight(45);
            fireBall.setFitWidth(45);
            fireBall.setX(slayerPick[1].getX() + 45);
            fireBall.setY(slayerPick[1].getY() + 45);
            fireBall.setPreserveRatio(true);
            fireBall.setVisible(false);
            Platform.runLater(() -> {
                getChildren().add(fireBall);
            });
        }

        public void checkFireBallContact(ImageView ghost, ImageView fireBall) {


            if ((ghost.getX() == fireBall.getX() || Math.abs(ghost.getX()-fireBall.getX()) < 45)  && ghost.getY() == fireBall.getY() && !immune && fireBall.isVisible()) {

                ghost.setX(45 * charCol(ghostCol));
                ghost.setY(45 * charRow(ghostRow));

            }
    }



     // If the slayer and the player are in the same row or column  with no walls between them,
           // the slayer will move towards the player.
    public void check(ImageView slayer, ImageView ghost) {
       int wallsYBetween = 0;
        int wallsXLeftBetween = 0;
        int wallsXRightBetween = 0;
        int wallsXBetween = 0;

        int rolY = ((int)(slayer.getY()/45));
        int colX = ((int)(slayer.getX()/45));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {
                if (values[i][j] == CellV.WALL) {
                    double wallX = j * 45;
                    double wallY = i * 45;

                    if (slayer.getX() == ghost.getX() && (Math.abs(ghost.getX() - wallX ) >= 0 &&
                            Math.abs(ghost.getX() - wallX ) < 45 ) &&
                            (wallY < ghost.getY() && wallY > slayer.getY() || wallY < slayer.getY() && wallY > ghost.getY())) {
                        wallsYBetween++;
                    }
                    if (slayer.getY() == ghost.getY() && (Math.abs(ghost.getY() - wallY ) >= 0 &&
                            Math.abs(ghost.getY() - wallY ) < 45 ) &&
                            ((wallX < slayer.getX() && wallX > ghost.getX()) ||(wallX > slayer.getX() && wallX < ghost.getX()) )) {
                        wallsXBetween++;
                    }

                }
            }
        }

            if (slayer.getX() == ghost.getX() && wallsYBetween == 0) {

                if ( rolY - 1 != -1 && !Objects.equals(values[rolY][colX], (CellV.WALL))
                       && ghost.getY() < slayer.getY() && slayer.getX() == ghost.getX()) {
                    slayer.setY(slayer.getY() - slayerSpeed);
                    sameCol = true;
                }
                    if (rolY + 1 != 21 &&!Objects.equals(values[rolY + 1][colX], (CellV.WALL))
                            && ghost.getY() > slayer.getY() && slayer.getX() == ghost.getX()) {
                        slayer.setY(slayer.getY() + slayerSpeed);
                        sameCol = true;
                }
            } else {
                sameCol = false;
            }
                if (slayer.getY() == ghost.getY()) {

                    if (colX -1 != -1 && !Objects.equals(values[rolY][colX], (CellV.WALL))
                            && ghost.getX() < slayer.getX() && slayer.getY() == ghost.getY()
                    &&  wallsXBetween == 0) {
                        slayer.setScaleX(1);
                        slayer.setX(slayer.getX() - slayerSpeed);
                        sameRow = true;
                    }
                    if (colX +1 != 21 && !Objects.equals(values[rolY][colX + 1], (CellV.WALL))
                            && ghost.getX() > slayer.getX() && slayer.getY() == ghost.getY()
                            &&  wallsXBetween == 0) {
                        slayer.setX(slayer.getX() + slayerSpeed);
                        slayer.setScaleX(-1);
                        sameRow = true;
                    }

                } else {
                    sameRow = false;
                }
    }


    public void checkBoar(ImageView slayer, ImageView ghost) {
        int rowIndex = ((int) (slayer.getY() / 45));
        int colIndex = ((int) (slayer.getX() / 45));
        int ghostYIndex = ((int) (ghost.getY() / 45));
        int ghostXIndex = ((int) (ghost.getX() / 45));
        if (slayer.getX() == ghost.getX() &&  ghost.getX() % 45 == 0) {
            if ( ghost.getY() < slayer.getY() && slayer.getX() == ghost.getX() ) {
                if (  (rowIndex  - 1) != 0 && Objects.equals(values[rowIndex  - 1][colIndex],
                        (CellV.WALL)) && ghostYIndex == rowIndex - 2) {
                    grid.getChildren().remove(ivArr[rowIndex  - 1][colIndex]);
                    ivArr[rowIndex - 1][colIndex ] = new ImageView();
                    ivArr[rowIndex - 1][colIndex ].setFitHeight(45);
                    ivArr[rowIndex - 1][colIndex ].setFitWidth(45);
                    ivArr[rowIndex - 1][colIndex].setPreserveRatio(true);
                    grid.add(ivArr[rowIndex  - 1][colIndex], rowIndex  - 1, colIndex);
                    values[rowIndex - 1][colIndex] = CellV.EMPTY;
                }
            }
            if ( ghost.getY() > slayer.getY() && slayer.getX() == ghost.getX()) {
                if ( rowIndex  + 1 != 20 && Objects.equals(values[rowIndex  + 1][colIndex],
                        (CellV.WALL)) &&  ghostYIndex == rowIndex + 2) {
                    grid.getChildren().remove(ivArr[rowIndex + 1][colIndex]);
                    ivArr[rowIndex + 1][colIndex ] = new ImageView();
                    ivArr[rowIndex + 1][colIndex ].setFitHeight(45);
                    ivArr[rowIndex + 1][colIndex ].setFitWidth(45);
                    ivArr[rowIndex + 1][colIndex ].setPreserveRatio(true);
                    grid.add(ivArr[ rowIndex  + 1][colIndex ], rowIndex + 1, colIndex );
                    values[rowIndex + 1][colIndex ] = CellV.EMPTY;
                }
            }
        }
        if (slayer.getY() == ghost.getY() &&  ghost.getY() % 45 == 0) {
            if ( ghost.getX() < slayer.getX() && slayer.getY() == ghost.getY()) {

                if (colIndex - 1 != 0 &&Objects.equals(values[rowIndex ][colIndex-1], (CellV.WALL))
                        && ghostXIndex == colIndex - 2) {
                    grid.getChildren().remove(ivArr[rowIndex][colIndex - 1]);
                    ivArr[ rowIndex ][colIndex - 1] = new ImageView();
                    ivArr[ rowIndex ][colIndex - 1].setFitHeight(45);
                    ivArr[ rowIndex ][colIndex - 1].setFitWidth(45);
                    ivArr[ rowIndex ][colIndex - 1].setPreserveRatio(true);
                    grid.add(ivArr[ rowIndex ][colIndex - 1],  rowIndex , colIndex - 1);
                    values[ rowIndex ][colIndex - 1] = CellV.EMPTY;
                }
            }
            if ( ghost.getX() > slayer.getX() && slayer.getY() == ghost.getY()) {
                if ( (colIndex + 1) != 20 && Objects.equals(values[rowIndex ][colIndex+1], (CellV.WALL))
                        && ghostXIndex == colIndex + 2) {
                    grid.getChildren().remove(ivArr[rowIndex ][colIndex + 1]);
                    ivArr[rowIndex ][colIndex + 1] = new ImageView();
                    ivArr[rowIndex ][colIndex + 1].setFitHeight(45);
                    ivArr[rowIndex ][colIndex + 1].setFitWidth(45);
                    ivArr[rowIndex ][colIndex + 1].setPreserveRatio(true);
                    grid.add(ivArr[rowIndex ][colIndex + 1], rowIndex , (colIndex + 1));
                    values[rowIndex ][colIndex + 1] = CellV.EMPTY;
                }
            }
        }
    }


         } // DemoGame
