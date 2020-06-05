package Entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class City extends RecursiveTreeObject<City> {
    StringProperty orderCity;
    StringProperty cityQuantityEmployees;
    StringProperty cityQuantityOrder;


    public City(String orderCity, String cityQuantityEmployees, String cityQuantityOrder) {
        this.orderCity = new SimpleStringProperty(orderCity) ;
        this.cityQuantityEmployees = new SimpleStringProperty(cityQuantityEmployees);
        this.cityQuantityOrder = new SimpleStringProperty(cityQuantityOrder);

    }

    public ObservableValue<String> getOrderCity() {
        return orderCity;
    }

    public StringProperty orderCityProperty() {
        return orderCity;
    }

    public void setOrderCity(String orderCity) {
        this.orderCity.set(orderCity);
    }

    public ObservableValue<String> getCityQuantityEmployees() {
        return cityQuantityEmployees;
    }

    public StringProperty cityQuantityEmployeesProperty() {
        return cityQuantityEmployees;
    }

    public void setCityQuantityEmployees(String cityQuantityEmployees) {
        this.cityQuantityEmployees.set(cityQuantityEmployees);
    }

    public ObservableValue<String> getCityQuantityOrder() {
        return cityQuantityOrder;
    }

    public StringProperty cityQuantityOrderProperty() {
        return cityQuantityOrder;
    }

    public void setCityQuantityOrder(String cityQuantityOrder) {
        this.cityQuantityOrder.set(cityQuantityOrder);
    }

}
