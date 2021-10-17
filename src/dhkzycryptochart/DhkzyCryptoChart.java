/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dhkzycryptochart;


import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
/**
 *
 * @author darianhu
 * @references:
 * CODE BY PROFESSOR WERGELES TAKEN FROM LECTURE 31
 * 
 * 
 */
public class DhkzyCryptoChart extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        // UI if SceneManager can't switch to a Scene
        HBox root = new HBox();
        root.setPrefSize(600, 400);
        root.setAlignment(Pos.CENTER);
        Text message = new Text("This is a failure!");
        message.setFont(Font.font(STYLESHEET_MODENA, 32));
        root.getChildren().add(message);
        
        // create Scene and init UI to show failure in case switch fails
        Scene scene = new Scene(root);
        
        Switchable.scene = scene;
        Switchable.switchTo("Home");

        
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        
        HomeController controller = (HomeController) Switchable.getControllerByName("Home");
        controller.ready(stage);
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
