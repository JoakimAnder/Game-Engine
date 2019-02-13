package GameEngine;

import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TestGame2 extends GameEngine {

    public TestGame2(int width, int height, int pixelWidth, int pixelHeight) {
        super(width, height, pixelWidth, pixelHeight);
    }
    public TestGame2(int width, int height) {
        super(width, height);
    }

    @Override
    boolean onUserCreate() {
        return true;
    }
    
    double eTime = 0;

    @Override
    boolean onUserUpdate(double elapsedTime) {
            Random rand = new Random();
            for (int x = 0; x < getScreenWidth(); x++) {
                for (int y = 0; y < getScreenHeight(); y++) {
                    draw(x, y, Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
//                    draw(x,y,Color.CYAN);
                }
            }
        return true;
    }

    @Override
    void onUserRender(GraphicsContext g) {
    }

//    @Override
//    void onUserRender(Graphics g) {
////        eTime += elapsedTime;
////        
////        double tTime = 0.5;
////            clear(Color.BLACK);
//        
////        if (eTime >= tTime) {
////            eTime-=tTime;
////        }
////                    for (int i = 0; i < 100; i++) {
////            fillRect(i, 0, 1, getScreenHeight(), Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
////        }
//    }

}
