package GameEngine;

import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class TestGame3 implements Game {
    
    MyGameEngine game = new MyGameEngine();
    
    @Override
    public void start() {
        game.start();
    }
    
    private class MyGameEngine extends GameEngine3 {
        
        Random rand = new Random();
        
        public MyGameEngine() {
            super((short) 1280/4, (short) 720/4, (short) 4, (short) 4);
        }
        
        @Override
        public boolean create() {
            return true;
        }
        
        @Override
        public boolean update(double elapsedTime) {
            return true;
        }
        
            boolean step1 = true;
            boolean step2 = false;
            int x = 5;
            int y = 5;
            
        @Override
        public void render(GraphicsWriter g) {
            
            g.draw(x++, y++, Color.rgb(50, 50, 50));
            if(step1) {
                
                if(step2){
                    step1 = false;
                    step2 = false;
                }
                step2 = true;
            }
            
//            g.draw(5, 5, Color.RED);
//            g.drawLine(5, 10, 20, 5, Color.RED);
//            g.drawLine(5, 12, 20, 12, Color.RED);
//            g.drawLine(5, 14, 20, 19, Color.RED);
//            g.drawLine(5, 21, 5, 30, Color.RED);
//            g.drawCircle(10, 37, 5, Color.RED);
//            g.fillCircle(10, 50, 5, Color.RED);
            
//            g.drawRect(30, 5, 5, 5, Color.RED);
//            g.fillRect(42, 5, 5, 5, Color.RED);
//
//            
//            g.draw(30, 15, Color.BLACK);
//            g.draw(26, 20, Color.BLACK);
//            g.draw(40, 12, Color.BLACK);
//            g.draw(30, 15+5, Color.BLACK);
//            g.draw(26, 20+5, Color.BLACK);
//            g.draw(40, 12+5, Color.BLACK);
//            
//            g.drawTriangle(30, 15, 26, 20, 40, 12, Color.RED);
//            g.drawTriangle(30, 15+5, 26, 20+5, 40, 12+5, Color.RED);
//            g.fillTriangle(30, 15+5, 26, 20+5, 40, 12+5, Color.RED);
//            g.fillTriangle(30, 15+10, 26, 20+10, 40, 12+10, Color.RED);
            
//            WritableImage image = new WritableImage((int)getWidth(), (int)getHeight());
//            PixelWriter p = image.getPixelWriter();

//            for (int y = 0; y < getHeight(); y++) {
//                for (int x = 0; x < getWidth(); x++) {
////                    Color c = Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
//                    g.draw(x, y, Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
//                    p.setColor(x, y, c);
//                    p.setColor(x+1, y, c);
//                    p.setColor(x, y+1, c);
//                    p.setColor(x+1, y+1, c);
                    
                    
//                    g.setFill(c);
//                    g.fillRect(x, y, 2, 2);
//                }
//            }
//            g.drawImage(image, 0, 0);
            

        }
        
    }
    
}
