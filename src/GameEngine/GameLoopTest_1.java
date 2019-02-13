package GameEngine;

import java.awt.Panel;
import java.nio.ByteBuffer;
import java.util.Random;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameLoopTest_1 extends Stage {

    private GamePanel gamePanel = new GamePanel(this);
    private boolean running = true;
    private boolean paused = false;
    private int fps = 60;
    private int frameCount = 0;

    public GameLoopTest_1() {
        setScene(new Scene(new Group(gamePanel)));
        setTitle("Unfixed Timestep Game Loop Test");
        
        setWidth(500);
        setHeight(500);
        
        setOnCloseRequest(e -> running = false);
    }

    public void actionPerformed(ActionEvent e) {
    }

    //Starts a new thread and runs the game loop in it.
    public void runGameLoop() {
        Thread loop = new Thread() {
            public void run() {
                gameLoop();
                Thread.yield();
            }
        };
        loop.start();
    }

    //Only run this in another Thread!
    private void gameLoop() {
        //This value would probably be stored elsewhere.
        final double GAME_HERTZ = 30.0;
        //Calculate how many ns each frame should take for our target game hertz.
        final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
        //At the very most we will update the game this many times before a new render.
        //If you're worried about visual hitches more than perfect timing, set this to 1.
        final int MAX_UPDATES_BEFORE_RENDER = 5;
        //We will need the last update time.
        double lastUpdateTime = System.nanoTime();
        //Store the last time we rendered.
        double lastRenderTime = System.nanoTime();

        //If we are able to get as high as this FPS, don't render again.
        final double TARGET_FPS = 60;
        final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

        //Simple way of finding FPS.
        int lastSecondTime = (int) (lastUpdateTime / 1000000000);

        while (running) {
            double now = System.nanoTime();
            int updateCount = 0;

            if (!paused) {
                //Do as many game updates as we need to, potentially playing catchup.
                while (now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER) {
                    updateGame();
                    lastUpdateTime += TIME_BETWEEN_UPDATES;
                    updateCount++;
                }

                //If for some reason an update takes forever, we don't want to do an insane number of catchups.
                //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
                if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
                    lastUpdateTime = now - TIME_BETWEEN_UPDATES;
                }

                //Render. To do so, we need to calculate interpolation for a smooth render.
                float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES));
                drawGame(interpolation);
                lastRenderTime = now;

                //Update the frames we got.
                int thisSecond = (int) (lastUpdateTime / 1000000000);
                if (thisSecond > lastSecondTime) {
                    System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
                    fps = frameCount;
                    frameCount = 0;
                    lastSecondTime = thisSecond;
                }

                //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
                while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
                    Thread.yield();

                    //This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
                    //You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
                    //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                    }

                    now = System.nanoTime();
                }
            }
        }
    }

    private void updateGame() {
        gamePanel.update();
    }

    private void drawGame(float interpolation) {
        gamePanel.setInterpolation(interpolation);
//        gamePanel.repaint();
        gamePanel.paintComponent(gamePanel.getGraphicsContext2D());
    }

    private class GamePanel extends Canvas {

        double interpolation;
        double ballX, ballY, lastBallX, lastBallY;
        int ballWidth, ballHeight;
        double ballXVel, ballYVel;
        double ballSpeed;
        Stage stage;
        GraphicsContext gc;
        PixelWriter p;
        WritablePixelFormat<ByteBuffer> byteBgraInstance;

        int lastDrawX, lastDrawY;

        public GamePanel(Stage stage) {
            this.stage = stage;
            ballX = lastBallX = 100;
            ballY = lastBallY = 100;
            ballWidth = 25;
            ballHeight = 25;
            ballSpeed = 25;
            ballXVel = (float) Math.random() * ballSpeed * 2 - ballSpeed;
            ballYVel = (float) Math.random() * ballSpeed * 2 - ballSpeed;
            gc = getGraphicsContext2D();
            p = gc.getPixelWriter();
            byteBgraInstance = PixelFormat.getByteBgraPreInstance();
        }

        public void setInterpolation(float interp) {
            interpolation = interp;
        }

        public void update() {
            if (getWidth() != stage.getWidth() || getHeight() != stage.getHeight()) {
                setWidth(stage.getWidth());
                setHeight(stage.getHeight());
//                paintComponent(getGraphicsContext2D());
            }
//            lastBallX = ballX;
//            lastBallY = ballY;
//
//            ballX += ballXVel;
//            ballY += ballYVel;
//
//            if (ballX + ballWidth / 2 >= getWidth()) {
//                ballXVel *= -1;
//                ballX = getWidth() - ballWidth / 2;
//                ballYVel = (float) Math.random() * ballSpeed * 2 - ballSpeed;
//            } else if (ballX - ballWidth / 2 <= 0) {
//                ballXVel *= -1;
//                ballX = ballWidth / 2;
//            }
//
//            if (ballY + ballHeight / 2 >= getHeight()) {
//                ballYVel *= -1;
//                ballY = getHeight() - ballHeight / 2;
//                ballXVel = (float) Math.random() * ballSpeed * 2 - ballSpeed;
//            } else if (ballY - ballHeight / 2 <= 0) {
//                ballYVel *= -1;
//                ballY = ballHeight / 2;
//            }
            
        }

        public void paintComponent(GraphicsContext g) {
            Random random = new Random();
            
            int w = (int) getWidth();
            int h = (int) getHeight();
            if(w <= 0 || h <= 0)
                return;
            WritableImage image = new WritableImage(w, h);
            PixelWriter pw = image.getPixelWriter();
            for (int i = 0; i < w; i+=1) {
                for (int j = 0; j < h; j+=1) {
                    
                        pw.setColor(i, j, Color.rgb(random.nextInt(200),random.nextInt(200),random.nextInt(200)));
//                        pw.setColor(i, j, Color.gray(random.nextDouble()));
                        
//                    g.setFill(Color.gray(random.nextDouble()));
//                    g.fillRect(i, j, i+1, j+1);
                }
            }
            g.drawImage(image, 0, 0);
            //BS way of clearing out the old rectangle to save CPU.
//            g.clearRect(lastDrawX - 1, lastDrawY - 1, ballWidth + 2, ballHeight + 2);
//            g.clearRect(5, 0, 75, 30);
////            g.clearRect(0, 0, getWidth(), getHeight());
//
//            g.setFill(Color.RED);
//            int drawX = (int) ((ballX - lastBallX) * interpolation + lastBallX - ballWidth / 2);
//            int drawY = (int) ((ballY - lastBallY) * interpolation + lastBallY - ballHeight / 2);
//            g.fillOval(drawX, drawY, ballWidth, ballHeight);
//
//            lastDrawX = drawX;
//            lastDrawY = drawY;
//
            g.clearRect(5, 0, 75, 30);
            g.setStroke(Color.BLACK);
            g.strokeText("FPS: " + fps, 5, 10);
            frameCount++;
        }
        
        private Color getBackground() {return Color.GHOSTWHITE;}
    }

    private class Ball {

        double x, y, lastX, lastY;
        int width, height;
        double xVelocity, yVelocity;
        double speed;

        public Ball() {
            width = (int) (Math.random() * 50 + 10);
            height = (int) (Math.random() * 50 + 10);
            x = (float) (Math.random() * (gamePanel.getWidth() - width) + width / 2);
            y = (float) (Math.random() * (gamePanel.getHeight() - height) + height / 2);
            lastX = x;
            lastY = y;
            xVelocity = (float) Math.random() * speed * 2 - speed;
            yVelocity = (float) Math.random() * speed * 2 - speed;
        }

        public void update() {
            lastX = x;
            lastY = y;

            x += xVelocity;
            y += yVelocity;

            if (x + width / 2 >= gamePanel.getWidth()) {
                xVelocity *= -1;
                x = gamePanel.getWidth() - width / 2;
                yVelocity = (float) Math.random() * speed * 2 - speed;
            } else if (x - width / 2 <= 0) {
                xVelocity *= -1;
                x = width / 2;
            }

            if (y + height / 2 >= gamePanel.getHeight()) {
                yVelocity *= -1;
                y = gamePanel.getHeight() - height / 2;
                xVelocity = (float) Math.random() * speed * 2 - speed;
            } else if (y - height / 2 <= 0) {
                yVelocity *= -1;
                y = height / 2;
            }
        }

        public void draw(GraphicsContext g) {

        }
    }
}
