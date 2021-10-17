/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dhkzycryptochart;

import java.io.Serializable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author darianhu
 * @references
 * toJsonString() and initFromJsonString(String jsonString) functions were taken from Professor Wergeles Lecture 32
 * json library also taken from Professor Wergeles Lecture 32
 */
public class CryptoInfo implements Serializable{
    private Double shares;
    private Integer buyAmount;
    
    public CryptoInfo(double shares, int buyAmount){
        this.shares = shares;
        this.buyAmount = buyAmount;
    }
    
    public double getShares(){
        return this.shares;
    }
    
    public int getBuyAmount(){
        return this.buyAmount;
    }
    
    public String toJsonString() {
        JSONObject obj = new JSONObject();
        if (shares != null) obj.put("shares", shares);
        if (buyAmount != null)  obj.put("buyAmount", buyAmount);
        return obj.toJSONString(); 
    }
    
    public void initFromJsonString(String jsonString) {
        shares = null;
        buyAmount = null;
        
        if (jsonString == null || jsonString == "") return;
        
        JSONObject jsonObj;
        try {
            jsonObj = (JSONObject)JSONValue.parse(jsonString);
        } catch (Exception ex) {
            return;
        }
        
        if (jsonObj == null) {
            return;
        }
        shares = (Double)jsonObj.getOrDefault("shares", null);
        
        
        Object buyObj = jsonObj.getOrDefault("buyAmount", null);
        if (buyObj != null) {
            if (buyObj instanceof Long) {
                Long longBuy = (Long)buyObj;
                buyAmount = new Integer(longBuy.intValue());
            } else {
                buyAmount = null;
            } 
        }
//        System.out.println("Shares =" + shares);
//        System.out.println("buy amount =" + buyAmount);
    }
}
