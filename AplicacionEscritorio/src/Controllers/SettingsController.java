package Controllers;

import Util.Preferencias;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SettingsController {
    @FXML
    private JFXTextField textIP;
    @FXML
    private Button saveIP;

    private MyCallback myCallback;
    @FXML
    private FontAwesomeIcon closeImage;
    public void initData() {
        closeImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Node node = (Node)event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();
            }
        });
    }
    public void handleClicks(ActionEvent actionEvent){
        if(actionEvent.getSource()==saveIP){
            if(!textIP.getText().equals("")){

                myCallback.callback(textIP.getText());
            }
        }

    }
    public void setCallbackPerfomed(MyCallback myCallback){
        this.myCallback=myCallback;
    }
}
