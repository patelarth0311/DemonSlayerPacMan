package ds;


          import java.util.BitSet;
          import java.util.logging.Level;
          import java.util.logging.Logger;

          import javafx.animation.KeyFrame;
          import javafx.animation.Timeline;

         import javafx.geometry.Bounds;
        import javafx.geometry.BoundingBox;
         import javafx.scene.input.KeyEvent;
        import javafx.scene.input.KeyCode;

       import javafx.scene.layout.Region;
       import javafx.util.Duration;


         public abstract class Game extends Region {




             private final Bounds bounds;                     // game bounds
    private final Duration fpsTarget;                // target duration for game loop
     private final Timeline loop = new Timeline();    // timeline for main game loop
     private final BitSet keysPressed = new BitSet(); // set of currently pressed keys

             private boolean initialized = false;

             /**
       * @param width minimum game region width
      * @param height minimum game region height
       * @param fps target frames per second (FPS)
       */
            public Game(int width, int height, int fps) {
             super();
               setMinWidth(width);
                setMinHeight(height);
                this.bounds = new BoundingBox(0, 0, width, height);
                this.fpsTarget = Duration.millis(1000.0 / fps);
                addEventFilter(KeyEvent.KEY_PRESSED, event -> handleKeyPressed(event));
                addEventFilter(KeyEvent.KEY_RELEASED, event -> handleKeyReleased(event));
                initGameLoop();
             } // Game




           /**
       * Initialize the main game loop.
      */
            private void initGameLoop() {
                KeyFrame updateFrame = new KeyFrame(fpsTarget, event -> {
                       requestFocus();
            update();
        });
        loop.setCycleCount(Timeline.INDEFINITE);
        loop.getKeyFrames().add(updateFrame);
    } // initGameLoop

    protected void init() {

    }


    protected abstract void update();


    private void handleKeyPressed(KeyEvent event) {
       // logger.info(event.toString());
        keysPressed.set(event.getCode().getCode());
    } // handleKeyPressed


    private void handleKeyReleased(KeyEvent event) {

        keysPressed.clear(event.getCode().getCode());
    } // handleKeyReleased


    protected final boolean isKeyPressed(KeyCode key) {
        return keysPressed.get(key.getCode());
    } // isKeyPressed


    protected final boolean isKeyPressed(KeyCode key, Runnable handler) {
        if (isKeyPressed(key)) {
            handler.run();
            return true;
        } else {
            return false;
        }
    }


    public final void play() {
        if (!initialized) {
            init();
            initialized = true;
        }
        loop.play();
    }

    /**
     * Stop the main game loop.
     */
    public final void stop() {
        loop.stop();
    } // stop

    /**
     * Pause the main game loop.
     */
    public final void pause() {
        loop.pause();
    } // pause


} // Game
