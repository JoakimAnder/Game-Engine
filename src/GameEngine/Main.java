package GameEngine;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        System.out.println("test");
//        GameEngine game = new TestGame2(100,100);
////        Thread loop = new Thread() {
////            @Override
////            public void run() {
//                game.Start();
////            }
////        };
////        loop.start();
//        System.out.println("testEnd");

//        GameLoopTest_1 glt = new GameLoopTest_1();
//        glt.show();
//        glt.runGameLoop();
        primaryStage.close();

        TestGame3 game = new TestGame3();
        game.start();
    }
    
}
