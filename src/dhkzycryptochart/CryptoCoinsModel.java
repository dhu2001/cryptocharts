/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dhkzycryptochart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import org.json.*;
import java.util.Formatter;
/**
 *
 * @author darianhu
 * 
 * @references 
 *      API USED: https://www.coingecko.com/en
 *      json library used https://github.com/stleary/JSON-java 
 * 
 */
public class CryptoCoinsModel extends AbstractModel implements Coin{
    private int cryptoPrice; 
    public Timeline timeline;
    public KeyFrame keyFrame;
    private static HttpURLConnection connection;
    private BufferedReader reader;
    private String line;
    private int buyAmount;
    private double shares;
    private double marketPrice;
    private double netChange;
    private int index;
    private int rescaleTopY;
    private int rescaleBottomY;
    private int initialPrice;
    private int lowestPrice;
    private int highestPrice;
    
    
    public CryptoCoinsModel(){
        super();
        cryptoPrice = 0;
        buyAmount = 0;
        shares = 0;
        marketPrice = 0;
        netChange = 0;
        index = 0;
        rescaleTopY = 0;
        rescaleBottomY = 0;
        lowestPrice = 0;
        highestPrice = 0;
    }
    
    @Override
    public void updatePrice(){
        doPrice();
        initialPrice = cryptoPrice;
        timeline = new Timeline(new KeyFrame(Duration.millis(10000), (ActionEvent) -> {
            doPrice();
            index++;
            rescaleTopY = getHighestPrice();
            rescaleBottomY = getLowestPrice();
            firePropertyChange("RescaleTop", null, rescaleTopY);
            firePropertyChange("RescaleBottom", null, rescaleBottomY);
            firePropertyChange("BitcoinPrice", null, cryptoPrice);
            firePropertyChange("PriceGraph", null, cryptoPrice);
            doMarketPrice();
            doNetChange();
        }));
        
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    
    public void doPrice(){
       // code from https://www.youtube.com/watch?v=qzRKa8I36Ww
       // code start
        StringBuffer responseContent = new StringBuffer();
        try {
            URL url = new URL("https://api.coingecko.com/api/v3/simple/price?ids=Bitcoin&vs_currencies=USD");
            connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int status = connection.getResponseCode();
            //System.out.println(status);
            
            if (status > 299){
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();
            }
            
            try {
                System.out.println(responseContent.toString());
                parseJson(responseContent.toString());
            } catch (Exception ex) {
                Logger.getLogger(CryptoCoinsModel.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(CryptoCoinsModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CryptoCoinsModel.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            connection.disconnect();
        }
        // code stop 
    }
    
    public void parseJson(String responseBody) {
        if (responseBody == null || responseBody == "") return;
        
        //https://stackoverflow.com/questions/2591098/how-to-parse-json-in-java
        // code start
        JSONObject obj = new JSONObject(responseBody);
        cryptoPrice = obj.getJSONObject("bitcoin").getInt("usd");
        System.out.println(cryptoPrice);
        // code stop
    }
    
    public int getLowestPrice(){
        lowestPrice = initialPrice;
        if (lowestPrice > cryptoPrice){
            lowestPrice = cryptoPrice;
        }
        return (lowestPrice - 5);
    }
    
    public int getHighestPrice(){
        highestPrice = initialPrice;
        if (highestPrice < cryptoPrice){
            highestPrice = cryptoPrice;
        }
        return (highestPrice + 5);
    }
    
    @Override
    public void doShares(){
        shares = (double) buyAmount / cryptoPrice;
        doMarketPrice();
        doNetChange();
        // formatter code form website linked in doNetChange()
        // code start
        Formatter formatterShares = new Formatter();
        formatterShares.format("%.2f", shares);
        // code stop
        firePropertyChange("Shares", null, formatterShares);
//        System.out.printf("Shares = %.2f\n", shares);
    }
    
    public void doNetChange(){
        netChange = marketPrice - buyAmount;
        // code from https://java2blog.com/format-double-to-2-decimal-places-java/#:~:text=decimal%20places%3A%202.46-,Using%20BigDecimal,RoundingMode%20to%20specify%20rounding%20behavior.
        // code start
        Formatter formatter = new Formatter();
        formatter.format("%.2f", netChange);
        // code stop
//        System.out.printf("netChange = %.2f\n", netChange);
        
        firePropertyChange("Netchange", null, formatter);
    }
    
    
    
    public void doMarketPrice(){
        marketPrice = shares * cryptoPrice;
        // formatter code form website linked in doNetChange()
        // code start
        Formatter formatterMarket = new Formatter();
        formatterMarket.format("%.2f", marketPrice);
        // code stop
//        System.out.printf("marketPrice = %.2f\n", marketPrice);
        firePropertyChange("Marketprice", null, formatterMarket);
    }
    
    
    public int getPrice(){
        return this.cryptoPrice;
    }
    
    
    public void setBuyAmount(int buyAmount){
        this.buyAmount = buyAmount;
        firePropertyChange("Buyamount", null, buyAmount);
//        System.out.println("buyAmount = " + this.buyAmount);
    }
    
    public void setShares(double shares){
        this.shares = shares;
        // formatter code form website linked in doNetChange()
        // code start
        Formatter formatterShares = new Formatter();
        formatterShares.format("%.2f", shares);
        // code stop
        firePropertyChange("Shares", null, formatterShares);
        doMarketPrice();
        doNetChange();
        
    }
    
    public int getIndex(){
        return this.index;
    }
    
    public double getShares(){
        return this.shares;
    }
    
    public int getBuyAmount(){
        return this.buyAmount;
    }
}
