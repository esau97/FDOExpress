package Entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class Vehicle extends RecursiveTreeObject<Vehicle>{
    StringProperty carRegistration ;
    StringProperty datePurchase;
    StringProperty dateRevision;
    StringProperty dataName;

    public Vehicle(String carRegistration, String datePurchase, String dateRevision, String dataName) {
        this.carRegistration = new SimpleStringProperty(carRegistration);
        this.datePurchase = new SimpleStringProperty(datePurchase);
        this.dateRevision = new SimpleStringProperty(dateRevision);
        this.dataName = new SimpleStringProperty(dataName);
    }

    public ObservableValue<String> getCarRegistration() {
        return carRegistration;
    }

    public StringProperty carRegistrationProperty() {
        return carRegistration;
    }

    public void setCarRegistration(String carRegistration) {
        this.carRegistration.set(carRegistration);
    }

    public ObservableValue<String> getDatePurchase() {
        return datePurchase;
    }

    public StringProperty datePurchaseProperty() {
        return datePurchase;
    }

    public void setDatePurchase(String datePurchase) {
        this.datePurchase.set(datePurchase);
    }

    public ObservableValue<String> getDateRevision() {
        return dateRevision;
    }

    public StringProperty dateRevisionProperty() {
        return dateRevision;
    }

    public void setDateRevision(String dateRevision) {
        this.dateRevision.set(dateRevision);
    }

    public ObservableValue<String> getDataName() {
        return dataName;
    }

    public StringProperty dataNameProperty() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName.set(dataName);
    }
}
