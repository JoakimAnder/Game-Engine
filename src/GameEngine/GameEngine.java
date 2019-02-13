package GameEngine;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public abstract class GameEngine {

    abstract boolean onUserCreate();
    abstract void onUserRender(GraphicsContext g);

    abstract boolean onUserUpdate(double elapsedTime);

    Stage stage;
    Canvas canvas;
    GraphicsContext g;
    
    String appName;
    int pixelWidth;
    int pixelHeight;
    
    long time;

    public GameEngine(int width, int height) {
        this(width, height, 4, 4);
    }
    
    public GameEngine(int width, int height, int pixelWidth, int pixelHeight) {
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
        
        canvas = new Canvas();
        g = canvas.getGraphicsContext2D();
        
        stage = new Stage();

        stage.setScene(new Scene(new Group(canvas)));
        stage.setWidth(width*pixelWidth);
        stage.setHeight(height*pixelHeight);

        appName = "GameEngine";
        stage.setTitle(appName);
        stage.setOnCloseRequest(e -> isRunning = false);
        
        stage.show();
    }
    
    private boolean isRunning;

    public void stop() {isRunning = false;}
    
    public void Start() {
        System.out.println("Game starting");

        isRunning = onUserCreate();
        
//        gameLoop();
//        System.out.println("Game Ending");
//        Thread.yield();
//        System.out.println("Thread Ending");
//        
//        
//        Platform.exit();
        
        

        time = System.nanoTime();

        Timeline gameLoop = new Timeline();
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        
        FrameCounter fc = new FrameCounter();

        KeyFrame kf = new KeyFrame (Duration.millis(10), (ActionEvent event) -> {
            updateCanvasSize();
            
            double elapsedTime = getElapsedTime();
            if (fc.addTime(elapsedTime))
                stage.setTitle(appName+" | FPS: "+fc.getFrames()+"  Target: "+gameLoop.getTargetFramerate());
            
            if (!onUserUpdate(elapsedTime))
                Platform.exit();
        });
        
        gameLoop.getKeyFrames().add(kf);
        
        gameLoop.play();
    }
    
    private void gameLoop() {
        updateCanvasSize();
        
        // Mine
        final double TARGET_FPS = 60;
        final double TARGET_UPDATE = 1000000000 / TARGET_FPS;

        long lastUpdate = System.nanoTime();

        double lastFpsCheck = 0;
        int fps = 0;

        while (isRunning) {
//            System.out.println("not sleeping");
            long now = System.nanoTime();
            if (now - lastUpdate > TARGET_UPDATE) {
                double elapsedTime = (now - lastUpdate) / 1000000000.0;
//                System.out.println(now+"-\n"+lastUpdate+"=\n"+(now-lastUpdate));
//                System.out.println(elapsedTime);
//                System.out.println((now - lastUpdate / 1000000));
                
                lastUpdate = now;

                lastFpsCheck += elapsedTime;
                fps++;

                if (lastFpsCheck > 1) {
                    //TODO show FPS
                    System.out.println("FPS: "+fps+" Time: "+lastFpsCheck);
                    lastFpsCheck = 0;
                    fps = 0;
                }

                if (onUserUpdate(elapsedTime))
                    onUserRender(g);
                else
                    isRunning = false;
                
            } else {
                try {
//                    System.out.println("sleeping");
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
//                    Platform.exit();
                    isRunning = false;
                }
            }
        }
//        long lastFpsTime = 0;
//        int fps = 0;
        
        // Variable timestep
//        long lastLoopTime = System.nanoTime();
//        final int TARGET_FPS = 60;
//        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
//        
//        while(isRunning) {
//            long now = System.nanoTime();
//            long updateLength = now - lastLoopTime;
//            lastLoopTime = now;
////            double delta = updateLength / ((double)OPTIMAL_TIME);
//            double elapsedTime = (double) updateLength;
//            
//            lastFpsTime += updateLength;
//            fps++;
//            
//            if(lastFpsTime >= 1000000000) {
//                System.out.println("FPS: "+fps);
//                lastFpsTime = 0;
//                fps = 0;
//            }
//            
////            onUserUpdate(delta);
//            onUserUpdate(elapsedTime);
//            
//            try {
//                long wait = (-lastLoopTime + System.nanoTime() - OPTIMAL_TIME) / 1000000;
//                System.out.printf("(-%d + %d - %d) / 1000000 = %d\n", lastLoopTime, System.nanoTime(), OPTIMAL_TIME, wait);
//                if (wait > 0)
//                    Thread.sleep(wait);
//                else
//                    Thread.sleep(1);
//            } catch (InterruptedException ex) {
//                isRunning = false;
//                break;
//            }
//        }
        
        
        // Fixed timestep
//        final double GAME_HERTZ = 30.0;
//        final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
//        
//        final int MAX_UPDATES_BEFORE_RENDER = 5;
//        
//        double lastUpdateTime = System.nanoTime();
//        double lastRenderTime = System.nanoTime();
//        
//        final double TARGET_FPS = 60;
//        final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;
//        
//        int lastSecondTime = (int) (lastUpdateTime / 1000000000);
//        
//        while (isRunning) {
//            double now = System.nanoTime();
//            int updateCount = 0;
//            
//            while (now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER) {
////                updateGame();
//                lastUpdateTime += TIME_BETWEEN_UPDATES;
//                updateCount++;
//            }
//            
//            if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
//                lastUpdateTime = now - TIME_BETWEEN_UPDATES;
//            }
//            
//            float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES));
////            drawGame(interpolation);
//            isRunning = onUserUpdate(interpolation);
//            lastRenderTime = now;
//            
//            int thisSecond = (int) (lastUpdateTime / 1000000000);
//            if (thisSecond > lastSecondTime) {
//                fps = updateCount;
//                System.out.println("NEW SECOND " + thisSecond + " " + updateCount);
//                updateCount = 0;
//                lastSecondTime = thisSecond;
//            }
//            
//            while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
//                try {Thread.sleep(2);} catch(Exception e){}
//                
//                now = System.nanoTime();
//            }
//        }
    }
    
    private void updateCanvasSize() {
        canvas.setWidth(stage.getWidth());
        canvas.setHeight(stage.getHeight());
    }
    
    private double getElapsedTime() {
        long then = time;
        time = System.nanoTime();
        
        return (time - then) / 1000000000.0;
    }
    
    public void setApplicationName(String name) {appName = name;}
    
    public boolean isFocused() {return stage.isFocused();}
    public double getScreenWidth() {return stage.getWidth() / pixelWidth;}
    public double getScreenHeight() {return stage.getHeight() / pixelHeight;}
    
    public void drawLine(double x1, double y1, double x2, double y2, Paint p) {
        g.setStroke(p);
        g.setLineWidth((pixelWidth + pixelHeight) / 2);
        g.strokeLine(x1*pixelWidth, y1*pixelHeight, x2*pixelWidth, y2*pixelHeight);
    }
    
    public void draw(double x, double y, Color p) {
        if ((pixelWidth + pixelHeight ) / 2 == 1) 
            g.getPixelWriter().setColor((int)x, (int)y, p);
        else {
            g.setFill(p);
            g.fillRect(x*pixelWidth, y*pixelHeight, pixelWidth, pixelHeight);
        }
        
    }
    
    public void drawCircle(double x, double y, double radius, Paint p) {
        g.setStroke(p);
        g.setLineWidth((pixelWidth + pixelHeight) / 2);
        g.strokeOval(x*pixelWidth, y*pixelHeight, radius*pixelWidth, radius*pixelHeight);
    }

    public void fillCircle(double x, double y, double radius, Paint p) {
        g.setFill(p);
        g.fillOval(x*pixelWidth, y*pixelHeight, radius*pixelWidth, radius*pixelHeight);
    }

    public void drawRect(double x, double y, double width, double height, Paint p) {
        g.setStroke(p);
        g.setLineWidth((pixelWidth + pixelHeight) / 2);
        g.strokeRect(x*pixelWidth, y*pixelHeight, width*pixelWidth, height*pixelHeight);
    }

    public void fillRect(double x, double y, double width, double height, Paint p) {
        g.setFill(p);
        g.fillRect(x*pixelWidth, y*pixelHeight, width*pixelWidth, height*pixelHeight);
    }

    public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Paint p) {
        g.setStroke(p);
        g.setLineWidth((pixelWidth + pixelHeight) / 2);
        g.strokeLine(x1*pixelWidth, y1*pixelHeight, x2*pixelWidth, y2*pixelHeight);
        g.strokeLine(x3*pixelWidth, y3*pixelHeight, x2*pixelWidth, y2*pixelHeight);
        g.strokeLine(x1*pixelWidth, y1*pixelHeight, x3*pixelWidth, y3*pixelHeight);
    }

    public void fillTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Paint p) {
        g.setFill(p);

        g.moveTo(x1*pixelWidth, y1*pixelHeight);
        g.beginPath();
        g.lineTo(x2*pixelWidth, y2*pixelHeight);
        g.lineTo(x3*pixelWidth, y3*pixelHeight);
        g.lineTo(x1*pixelWidth, y1*pixelHeight);
        g.fill();
        g.closePath();
    }

//    public void drawSprite(double x, double y, Sprite sprite, double scale) {}
    public void drawString(double x, double y, String text, Paint p, double scale) {
        g.setFont(Font.font(scale));
        g.strokeText(text, x*pixelWidth, y*pixelHeight);
    }

    public void clear(Paint p) {
        g.setFill(p);
        g.fillRect(0, 0, stage.getWidth(), stage.getHeight());
    }
    
    
    
    private class FrameCounter {
        long frameTimer = 0;
        int frameCount = 0;
        
        public boolean addTime(double time) {
            frameTimer += time;
            frameCount++;
            return frameTimer >= 1;
        }
        
        public int getFrames() {
            int frames = frameCount;
            frameTimer = 0;
            frameCount = 0;
            
            return frames;
        }
    }
}
