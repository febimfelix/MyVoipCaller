package com.febi.myvoipcaller;

/**
 * Created by Febi.M.Felix on 2/11/16.
 */

public class ContactInfoStructure {
    private int contactId;
    private String contactName;
    private String phoneNumber;

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
