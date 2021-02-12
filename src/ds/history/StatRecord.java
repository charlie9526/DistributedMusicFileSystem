package ds.history;

import ds.credential.Credential;

import java.util.Date;

public class StatRecord {
    private String searchQuery;
    private Date triggeredTime;
    private Date deliveryTime;
    private int hopsRequired;
    private Credential servedNode;

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Date getTriggeredTime() {
        return triggeredTime;
    }

    public void setTriggeredTime(Date triggeredTime) {
        this.triggeredTime = triggeredTime;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public int getHopsRequired() {
        return hopsRequired;
    }

    public void setHopsRequired(int hopsRequired) {
        this.hopsRequired = hopsRequired;
    }

    public Credential getServedNode() {
        return servedNode;
    }

    public void setServedNode(Credential servedNode) {
        this.servedNode = servedNode;
    }
}
