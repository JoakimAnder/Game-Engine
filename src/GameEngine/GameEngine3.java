package GameEngine;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

// Thank you Majoolwip & OneLoneCoder!
public abstract class GameEngine3 implements Runnable {

    Stage stage;
    Scene scene;
    Canvas canvas;
    private Thread thread;
    public boolean running = false;
    private final double UPDATE_CAP = 1.0 / 60.0;
    private float pixelWidth, pixelHeight;
    private String title;

    public GameEngine3(double width, double height, float pixelWidth, float pixelHeight) {
        this("GameEngine", width, height, pixelWidth, pixelHeight);
    }

    public GameEngine3(String title, double width, double height, float pixelWidth, float pixelHeight) {
        this.title = title;
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;

        canvas = new Canvas(width*pixelWidth, height*pixelHeight);
        scene = new Scene(new AnchorPane(canvas));

        stage = new Stage();
        stage.setWidth(width*pixelWidth);
        stage.setHeight(height*pixelHeight);
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> stop());
        stage.setTitle(title);
        stage.setScene(scene);

    }

    public abstract boolean create();

    public abstract boolean update(double elapsedTime);

    public abstract void render(GraphicsWriter g);

    public void scale(double scale) {
        canvas.setScaleX(scale);
        canvas.setScaleY(scale);

    }

    public void start() {
        if (!create()) {
            Platform.exit();
        }
        stage.show();
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        running = false;
    }

    public void run() {
        running = true;

        boolean render;
        double firstTime;
        double elapsedTime;
        double lastTime = System.nanoTime() / 1000000000.0;
        float unprocessedTime = 0f;
        float frameTime = 0f;
        short frames = 0;
        short fps;
        WritableImage img = new WritableImage(1, 1);

        while (running) {
            render = false;

            firstTime = System.nanoTime() / 1000000000.0;
            elapsedTime = firstTime - lastTime;
            lastTime = firstTime;

            unprocessedTime += elapsedTime;
            frameTime += elapsedTime;

            while (unprocessedTime >= UPDATE_CAP) {
                unprocessedTime -= UPDATE_CAP;
                render = true;

                if (frameTime >= 1.0) {
                    fps = frames;
                    System.out.printf("FPS: %d\n", fps);
                    frames = 0;
                    frameTime = 0;
                }
                running = update(elapsedTime);
            }

            if (render) {
                frames++;
                img.cancel();
                img = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                GraphicsWriter gw = new GraphicsWriter(canvas, img, pixelWidth, pixelHeight);
                render(gw);
                canvas.getGraphicsContext2D().drawImage(img, 0, 0, canvas.getWidth(), canvas.getHeight());
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        dispose();
    }

    public void dispose() {
        try {
            thread.join(5);
        } catch (InterruptedException ex) {
        }
    }

    public double getWidth() {
        return canvas.getWidth()/pixelWidth;
    }

    public void setWidth(double width) {
        canvas.setWidth(width);
        stage.setWidth(width + 7);
    }

    public double getHeight() {
        return canvas.getHeight()/pixelWidth;
    }

    public void setHeight(double height) {
        canvas.setHeight(height);
        stage.setHeight(height + 30);
    }

    public float getPixelWidth() {
        return pixelWidth;
    }

    public void setPixelWidth(float pixelWidth) {
        this.pixelWidth = pixelWidth;
    }

    public float getPixelHeight() {
        return pixelHeight;
    }

    public void setPixelHeight(float pixelHeight) {
        this.pixelHeight = pixelHeight;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
