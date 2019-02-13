package GameEngine;

import com.sun.javafx.iio.ImageStorage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Random;

public class GameLoopTest extends JFrame implements ActionListener {

    private GamePanel gamePanel = new GamePanel();
    private boolean running = true;
    private boolean paused = false;
    private int fps = 60;
    private int frameCount = 0;

    public GameLoopTest() {
        super("Fixed Timestep Game Loop Test");
        Container cp = getContentPane();
        cp.add(gamePanel, BorderLayout.CENTER);
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        GameLoopTest glt = new GameLoopTest();
        glt.setVisible(true);
        glt.runGameLoop();
    }

    public void actionPerformed(ActionEvent e) {
    }

    //Starts a new thread and runs the game loop in it.
    public void runGameLoop() {
        Thread loop = new Thread() {
            public void run() {
                gameLoop();
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
        gamePanel.repaint();
    }

    private class GamePanel extends JPanel {

        float interpolation;
        float ballX, ballY, lastBallX, lastBallY;
        int ballWidth, ballHeight;
        float ballXVel, ballYVel;
        float ballSpeed;

        int lastDrawX, lastDrawY;

        public GamePanel() {
            ballX = lastBallX = 100;
            ballY = lastBallY = 100;
            ballWidth = 25;
            ballHeight = 25;
            ballSpeed = 25;
            ballXVel = (float) Math.random() * ballSpeed * 2 - ballSpeed;
            ballYVel = (float) Math.random() * ballSpeed * 2 - ballSpeed;
        }

        public void setInterpolation(float interp) {
            interpolation = interp;
        }

        public void update() {
            lastBallX = ballX;
            lastBallY = ballY;

            ballX += ballXVel;
            ballY += ballYVel;

            if (ballX + ballWidth / 2 >= getWidth()) {
                ballXVel *= -1;
                ballX = getWidth() - ballWidth / 2;
                ballYVel = (float) Math.random() * ballSpeed * 2 - ballSpeed;
            } else if (ballX - ballWidth / 2 <= 0) {
                ballXVel *= -1;
                ballX = ballWidth / 2;
            }

            if (ballY + ballHeight / 2 >= getHeight()) {
                ballYVel *= -1;
                ballY = getHeight() - ballHeight / 2;
                ballXVel = (float) Math.random() * ballSpeed * 2 - ballSpeed;
            } else if (ballY - ballHeight / 2 <= 0) {
                ballYVel *= -1;
                ballY = ballHeight / 2;
            }
        }

        public void paintComponent(Graphics g) {
          Random random = new Random();
            int w = (int) getWidth();
            int h = (int) getHeight();
//            Image image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
//            Graphics ig = image.getGraphics();
            
            for (int i = 0; i < w; i+=1) {
                for (int j = 0; j < h; j+=1) {
//                    ig.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
//                    ig.fillRect(i, j, i+1, j+1);
                    g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
                    g.fillRect(i, j, i+1, j+1);
                }
            }
//            g.drawImage(image, 0, 0, null);
            
//         BS way of clearing out the old rectangle to save CPU.
//         g.setColor(getBackground());
//         g.fillRect(lastDrawX-1, lastDrawY-1, ballWidth+2, ballHeight+2);
//         
//         g.setColor(Color.RED);
//         int drawX = (int) ((ballX - lastBallX) * interpolation + lastBallX - ballWidth/2);
//         int drawY = (int) ((ballY - lastBallY) * interpolation + lastBallY - ballHeight/2);
//         g.fillOval(drawX, drawY, ballWidth, ballHeight);
//         
//         lastDrawX = drawX;
//         lastDrawY = drawY;

         g.setColor(getBackground());
         g.fillRect(5, 0, 75, 30);
         g.setColor(Color.BLACK);
         g.drawString("FPS: " + fps, 5, 10);
         
         frameCount++;
        }
    }

    private class Ball {

        float x, y, lastX, lastY;
        int width, height;
        float xVelocity, yVelocity;
        float speed;

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

        public void draw(Graphics g) {

        }
    }
}
