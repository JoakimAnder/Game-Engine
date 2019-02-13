package GameEngine;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class GraphicsWriter {

    private final PixelWriter p;
    WritableImage image;
    private final double pixelWidth;
    private final double pixelHeight;

    public GraphicsWriter(Canvas canvas, WritableImage image, double pixelWidth, double pixelHeight) {
        p = image.getPixelWriter();
        this.image = image;
//        System.out.printf("img(%f, %f), calculated(%f, %f)", image.getWidth(), image.getHeight());
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
    }

    public void draw(int x, int y, Color c) {
        for (int i = (int) (x*pixelWidth); i < x*pixelWidth + pixelWidth; i++) {
            for (int j = (int) (y*pixelHeight); j < y*pixelHeight + pixelHeight; j++) {
                if (i >= image.getWidth() || j >= image.getHeight() || i < 0 || j < 0)
                    continue;
                p.setColor(i, j, c);
            }
        }
    }
    
    public void drawLine(int x1, int y1, int x2, int y2, Color c) {
        int dx, dy, dx1, dy1, px, py, xe, ye;
        dx = x2 - x1; dy = y2 - y1;
        
        if(dx == 0) { // Vertical
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                draw(x1, y, c);
            }
            return;
        }
        
        if(dy == 0) { // Horizontal
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                draw(x, y1, c);
            }
            return;
        }
        
        int x,y;
        
        dx1 = Math.abs(dx); dy1 = Math.abs(dy);
        px = 2 * dy1 - dx1; py = 2 * dx1 - dy1;
        
        if (dy1 <= dx1) {
            if (dx >= 0) {
                x = x1; y = y1; xe = x2;
            } else {
                x = x2; y = y2; xe = x1;
            }
            draw(x,y,c);
            
            for (int i = 0; x < xe; i++) {
                x++;
                if (px < 0)
                    px = px + 2 * dy1;
                else {
                    y = ((dx<0 && dy<0) || (dx>0 && dy>0))? y+1 : y-1;
                    px = px + 2 * (dy1 - dx1);
                }
                draw(x, y, c);
            }
        } else {
            if (dy >= 0) {
                x = x1; y = y1; ye = y2;
            } else {
                x = x2; y = y2; ye = y1;
            }
            draw(x,y,c);
            
            for (int i = 0; y < ye; i++) {
                y++;
                if (py < 0)
                    py = py + 2 * dx1;
                else {
                    x = ((dx<0 && dy<0) || (dx>0 && dy>0))? x+1 : x-1;
                    py = py + 2 * (dx1 - dy1);
                }
                draw(x, y, c);
            }
        }
    }
    
    public void drawCircle(int x, int y, int radius, Color c) {
        int x0 = 0;
        int y0 = radius;
        int d = 3-2*radius;
        if (radius == 0) return;
        
        while (y0 >= x0) {
            draw(x-x0, y-y0, c);
            draw(x-y0, y-x0, c);
            draw(x+y0, y-x0, c);
            draw(x+x0, y-y0, c);
            draw(x-x0, y+y0, c);
            draw(x-y0, y+x0, c);
            draw(x+y0, y+x0, c);
            draw(x+x0, y+y0, c);
            if (d < 0)
                d +=4 * x0++ + 6;
            else
                d +=4 * (x0++ - y0--) +10;
        }
    }
    
    public void fillCircle(int x, int y, int radius, Color c) {
        int x0 = 0;
        int y0 = radius;
        int d = 3-2*radius;
        if (radius == 0) return;
        
        
        while (y0 >= x0) {
            drawHorizontalLine(x-x0, x+x0, y-y0, c);
            drawHorizontalLine(x-y0, x+y0, y-x0, c);
            drawHorizontalLine(x-x0, x+x0, y+y0, c);
            drawHorizontalLine(x-y0, x+y0, y+x0, c);
            if (d < 0)
                d +=4 * x0++ + 6;
            else
                d +=4 * (x0++ - y0--) +10;
        }
    }
    
    public void drawRect(int x, int y, int w, int h, Color c) {
        drawLine(x,y,x+w,y,c);
        drawLine(x+w,y,x+w,y+h,c);
        drawLine(x+w,y+h,x,y+h,c);
        drawLine(x,y+h,x,y,c);
    }
    
    public void fillRect(int x, int y, int w, int h, Color c) {
        int x2 = x+w;
        int y2 = y+h;
        
        for (int i = x; i <= x2; i++) {
            for (int j = y; j <= y2; j++) {
                draw(i, j, c);
            }
        }
    }
    
    public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Color c) {
        drawLine(x1, y1, x2, y2, c);
        drawLine(x2, y2, x3, y3, c);
        drawLine(x3, y3, x1, y1, c);
    }
    
    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Color c) {
        
        int temp;
        if (y1 > y2) {
            temp = y1;
            y1 = y2; y2 = temp;
            
            temp = x1;
            x1 = x2; x2 = temp;
        }
        if (y1 > y3) {
            temp = y1;
            y1 = y3; y3 = temp;
            
            temp = x1;
            x1 = x3; x3 = temp;
        }
        if (y2 > y3) {
            temp = y2;
            y2 = y3; y3 = temp;
            
            temp = x2;
            x2 = x3; x3 = temp;
        }
        
        if (y1 == y2) {
            flatTop(x1,y1, x2,y2, x3,y3, c);
        } else if(y2 == y3) {
            flatBottom(x1,y1, x2,y2, x3,y3, c);
        } else {
            int x4 = (int)(x1 + ((y2 - y1) / (float)(y3 - y1)) * (x3 - x1));
            int y4 = y2;
            flatTop(x2,y2, x4,y4, x3,y3, c);
            flatBottom(x1,y1, x2,y2, x4,y4, c);
        }
    }
    
    private void flatTop(int x1,int y1,int x2,int y2,int x3,int y3, Color c) {
        float invslope1 = (x3 - x1) / (float) (y3 - y1);
        float invslope2 = (x3 - x2) / (float) (y3 - y2);
        
        float curx1 = x3;
        float curx2 = x3;
        
        for (int scanlineY = y3; scanlineY > y1; scanlineY--) {
            drawHorizontalLine((int)curx1, (int)curx2, scanlineY, c);
            curx1 -= invslope1;
            curx2 -= invslope2;
        }
    }
    private void flatBottom(int x1,int y1,int x2,int y2,int x3,int y3, Color c) {
        float invslope1 = (x2 - x1) / (float) (y2 - y1);
        float invslope2 = (x3 - x1) / (float) (y3 - y1);
        
        float curx1 = x1;
        float curx2 = x1;
        
        for (int scanlineY = y1; scanlineY <= y2; scanlineY++) {
            drawHorizontalLine((int)curx1, (int)curx2, scanlineY, c);
            curx1 += invslope1;
            curx2 += invslope2;
        }
    }
    
    private void drawHorizontalLine(int sx, int ex, int ny, Color c) {
        for (int i = Math.min(sx, ex); i <= Math.max(sx, ex); i++) {
            draw(i, ny, c);
        }
    }
}
