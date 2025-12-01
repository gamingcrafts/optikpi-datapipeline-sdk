package com.optikpi.datapipeline;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Data container for batch operations
 */
public class BatchData {
    @JsonProperty("customers")
    private List<Object> customers;
    
    @JsonProperty("accountEvents")
    private List<Object> accountEvents;
    
    @JsonProperty("depositEvents")
    private List<Object> depositEvents;
    
    @JsonProperty("withdrawEvents")
    private List<Object> withdrawEvents;
    
    @JsonProperty("gamingEvents")
    private List<Object> gamingEvents;

    @JsonProperty("referFriendEvents")  
    private List<Object> referFriendEvents;
    
    @JsonProperty("walletBalanceEvents")  
    private List<Object> walletBalanceEvents;
    
    public BatchData() {}
    
    // Getters and Setters
    public List<Object> getCustomers() {
        return customers;
    }
    
    public void setCustomers(List<Object> customers) {
        this.customers = customers;
    }
    
    public List<Object> getAccountEvents() {
        return accountEvents;
    }
    
    public void setAccountEvents(List<Object> accountEvents) {
        this.accountEvents = accountEvents;
    }
    
    public List<Object> getDepositEvents() {
        return depositEvents;
    }
    
    public void setDepositEvents(List<Object> depositEvents) {
        this.depositEvents = depositEvents;
    }
    
    public List<Object> getWithdrawEvents() {
        return withdrawEvents;
    }
    
    public void setWithdrawEvents(List<Object> withdrawEvents) {
        this.withdrawEvents = withdrawEvents;
    }
    
    public List<Object> getGamingEvents() {
        return gamingEvents;
    }
    
    public void setGamingEvents(List<Object> gamingEvents) {
        this.gamingEvents = gamingEvents;
    }

      
    public List<Object> getReferFriendEvents() {
        return referFriendEvents;
    }
    
    public void setReferFriendEvents(List<Object> referFriendEvents) {
        this.referFriendEvents = referFriendEvents;
    }
    
    public List<Object> getWalletBalanceEvents() {
        return walletBalanceEvents;
    }
    
    public void setWalletBalanceEvents(List<Object> walletBalanceEvents) {
        this.walletBalanceEvents = walletBalanceEvents;
    }
}
