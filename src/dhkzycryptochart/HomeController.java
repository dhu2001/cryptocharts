/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dhkzycryptochart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author darianhu
 * @references:
 * many functions taken from Wergeles Lecture 32 
 * handleSave
 * handleOpen
 * displayExceptionAlert
 * confirmContinueOnUnsavedData
 * ready
 */
public class HomeController extends Switchable implements Initializable, PropertyChangeListener {

    @FXML
    private Text currentPrice;
    @FXML
    private TextField priceField;
    @FXML
    private Text netChangeText;
    @FXML
    private Text sharesText;
    @FXML
    private Text buyAmountText;
    @FXML
    private Text marketPriceText;
    @FXML
    private AreaChart<String, Number> areaChart;
    @FXML
    private NumberAxis priceY;
    @FXML
    private CategoryAxis priceX;
    
    private XYChart.Series<String, Number> series;
    
    private Stage stage;
    
    public CryptoCoinsModel bitcoin;
    
    public CryptoInfo infoHolder;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeCharts();
        bitcoin = new CryptoCoinsModel();
        infoHolder = new CryptoInfo(0,0);
        bitcoin.updatePrice();
        currentPrice.setText("$" + bitcoin.getPrice());
        bitcoin.addPropertyChangeListener(this);
        
    }    

    @FXML
    private void HandleGoToAbout(MouseEvent event) {
        Switchable.switchTo("About");
    }

    @FXML
    private void confirmButton(MouseEvent event) {
        try{
             if (Integer.parseInt(priceField.getText()) > 0){
                if (Integer.parseInt(priceField.getText()) > bitcoin.getPrice()/100) {
                    bitcoin.setBuyAmount(Integer.parseInt(priceField.getText()));  
                    bitcoin.doShares();
                }else{
                        Alert a = new Alert(AlertType.ERROR);
                        a.setContentText("Error: Please enter a number greater than 1% of bitcoin's current price");
                        a.show();
                    }
             
                }else{
                    Alert a = new Alert(AlertType.ERROR);
                    a.setContentText("Error: Please enter a number greater than 0");
                    a.show();
                }
        } catch (Exception e){
            Alert a = new Alert(AlertType.ERROR);
            a.setContentText("Error: Please enter a Integer value if what you entered was an Integer please make it a smaller value");
            a.show();
        }
         
    }
    
    public void initializeCharts(){
        series = new XYChart.Series();
        areaChart.getData().add(series);
        areaChart.setAnimated(false);
        priceY.setAutoRanging(false);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("Netchange")){
            netChangeText.setText("$" + evt.getNewValue());
        }
        else if (evt.getPropertyName().equals("BitcoinPrice")){
            currentPrice.setText("$" + evt.getNewValue());
        }
        else if (evt.getPropertyName().equals("Shares")){
            sharesText.setText(evt.getNewValue().toString());
        }
        else if (evt.getPropertyName().equals("Buyamount")){
            buyAmountText.setText("$" + evt.getNewValue());
        }
        else if (evt.getPropertyName().equals("Marketprice")){
            marketPriceText.setText("$" + evt.getNewValue());
        }
        else if (evt.getPropertyName().equals("PriceGraph")){
            series.getData().add(new XYChart.Data(Integer.toString(bitcoin.getIndex()), evt.getNewValue()));
        }
        else if (evt.getPropertyName().equals("RescaleTop")){
            priceY.setUpperBound((int) evt.getNewValue());
        }
        else if (evt.getPropertyName().equals("RescaleBottom")){
            priceY.setLowerBound((int) evt.getNewValue());
        }
    }
    
    public void ready(Stage stage) {
        this.stage = stage;
        
        stage.setOnCloseRequest((WindowEvent we) ->{
            if (!(bitcoin.getBuyAmount() == 0)) {
                if (!confirmContinueOnUnsavedData()) {
                    return;
                }
            }
        });
    }

    @FXML
    private void handleSave(MouseEvent event) {
        infoHolder = new CryptoInfo(bitcoin.getShares(), bitcoin.getBuyAmount());
        if (infoHolder == null) {
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
               
               String jsonString = infoHolder.toJsonString();
               
               PrintWriter out = new PrintWriter(file.getPath());
               out.print(jsonString);
               out.close();
               
            } catch(IOException ioex) {
                String message = "Exception occurred while saving to " + file.getPath();
                displayExceptionAlert(message, ioex);
            } 
        }  
    }

    @FXML
    private void handleOpen(MouseEvent event) {
        if (!(bitcoin.getBuyAmount() == 0)) {
            if (!confirmContinueOnUnsavedData()) {
                return;
            }
        }
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                
                FileReader fileReader = new FileReader(file.getPath());
                BufferedReader bufferedReader = new BufferedReader(fileReader);
 
                String json = "";
                String line = null;
                while((line = bufferedReader.readLine()) != null) {
                    json += line;
                }
                bufferedReader.close();
                fileReader.close();
                
                infoHolder.initFromJsonString(json);
                
            } catch(IOException ioex) {
               String message = "Exception occurred while opening " + file.getPath();
               displayExceptionAlert(message, ioex);
            }

        }
        bitcoin.setBuyAmount(infoHolder.getBuyAmount());
        bitcoin.setShares(infoHolder.getShares());
    }
    
    private void displayExceptionAlert(String message, Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText(ex.getClass().getSimpleName());      
        alert.setContentText(message + "\n\n" + ex.getMessage());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
    
    private boolean confirmContinueOnUnsavedData() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potential Unsaved Data");
        alert.setHeaderText("Double check to make sure a file has been saved onto your desktop.");
        alert.setContentText("Are you sure you want to continue?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // ... user chose OK
            return true;
        } else {
            // ... user chose CANCEL or closed the dialog
            return false;
        }
    } 
}
