package GameEngine;

import javafx.scene.paint.Color;

public class TestGame extends GameEngine {

    public TestGame(double width, double height) {
        super(width, height);
    }

    double eT = 0;
    double eT1 = 0;
    double eT2 = 0;
    boolean flip = true;
    boolean flip2 = true;
    double x1 = 1;
    double y1 = 1;
    double x2 = 1;
    double y2 = 1;

    @Override
    boolean onUserCreate() {
        return true;
    }

    @Override
    boolean onUserUpdate(double elapsedTime) {
        draw(1,1,Color.RED);
        
        eT1 += elapsedTime;
        if (eT1 >= 2) {
            eT1 -= 2;
         eT += elapsedTime;
         eT2 += elapsedTime;
        }

        if (eT2 >= 1 / 60.0) {
            eT2 = 0;

            double x = 196 + 128 * Math.cos(eT);
            double y = 196 + 128 * Math.sin(eT);
            if (flip) {
                x1 = 196 + 128 * Math.cos(eT - 0.4);
                y1 = 196 + 128 * Math.sin(eT - 0.4);
                flip = false;
                if (flip2) {
                    x2 = 196 + 128 * Math.cos(eT);
                    y2 = 196 + 128 * Math.sin(eT);
                    flip2 = false;
                } else {
                    flip2 = true;
                }
            } else {
                flip = true;
            }

            // Clear the canvas
            this.clear(Color.BLACK);
            // background image clears canvas
            this.drawCircle(x, y, 50, Color.GREEN);
            this.drawCircle(x1, y1, 50, Color.YELLOW);
//            this.drawCircle(x2, y2, 50, Color.RED);
        }

        return true;
    }

}
