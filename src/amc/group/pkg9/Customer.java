package amc.group.pkg9;

public class Customer {
    private String customerId,userId,contactInfo;

    public Customer(String customerId, String userId, String contactInfo) {
        this.customerId = customerId;
        this.userId = userId;
        this.contactInfo = contactInfo;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public String toString() {
        return customerId+"|"+userId+"|"+contactInfo;
    }
}
