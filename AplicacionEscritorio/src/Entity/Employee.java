package Entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class Employee extends RecursiveTreeObject<Employee> {
    StringProperty name;
    StringProperty email;
    StringProperty address;
    StringProperty phone;
    StringProperty ruta;

    public Employee(String name, String email, String address, String phone, String ruta) {
        this.name = new SimpleStringProperty(name) ;
        this.email = new SimpleStringProperty(email);
        this.address = new SimpleStringProperty(address);
        this.phone = new SimpleStringProperty(phone);
        this.ruta = new SimpleStringProperty(ruta);

    }

    public ObservableValue<String> getRuta() {
        return ruta;
    }

    public StringProperty rutaProperty() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta.set(ruta);
    }

    public ObservableValue<String> getName() {
        return name;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ObservableValue<String> getEmail() {
        return email;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public ObservableValue<String> getAddress() {
        return address;
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public ObservableValue<String> getPhone() {
        return phone;
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }
}
