package Entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class Provider extends RecursiveTreeObject<Provider> {
    StringProperty companyName;
    StringProperty companyEmail;
    StringProperty companyAddress;
    StringProperty companyPhone;

    public Provider(String companyName, String companyEmail, String companyAddress, String companyPhone) {
        this.companyName = new SimpleStringProperty(companyName) ;
        this.companyEmail = new SimpleStringProperty(companyEmail);
        this.companyAddress = new SimpleStringProperty(companyAddress);
        this.companyPhone = new SimpleStringProperty(companyPhone);
    }

    public ObservableValue<String> getCompanyName() {
        return companyName;
    }

    public StringProperty companyNameProperty() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName.set(companyName);
    }

    public ObservableValue<String> getCompanyEmail() {
        return companyEmail;
    }

    public StringProperty companyEmailProperty() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail.set(companyEmail);
    }

    public ObservableValue<String> getCompanyAddress() {
        return companyAddress;
    }

    public StringProperty companyAddressProperty() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress.set(companyAddress);
    }

    public ObservableValue<String> getCompanyPhone() {
        return companyPhone;
    }

    public StringProperty companyPhoneProperty() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone.set(companyPhone);
    }
}
