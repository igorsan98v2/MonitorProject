package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Array;

public class RegistrationForm {
    private int id;
    private Stage stage;

    public RegistrationForm(int id){
        this.id =id;
        stage = new Stage();
        stage.setTitle("Введите данные по пользователю");

        try {
            Parent root = FXMLLoader.load(getClass().getResource("modal.fxml"));
            Label label=(Label)root.lookup("#id");
            JFXButton button = (JFXButton)root.lookup("#input");
            JFXTextField firstName = (JFXTextField)root.lookup("#firstname");
            JFXTextField secondName = (JFXTextField)root.lookup("#secondname");
            JFXTextField patronymic = (JFXTextField)root.lookup("#patronymic");
            if(label!=null) {
                label.setText(label.getText()+" "+id);
            }
            if(button!=null){
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        try {
                            SQLDriver db = new SQLDriver();

                            String name =  firstName.getText();
                            String scndName = secondName.getText();
                            String ptrn = patronymic.getText();
                            if(name!=""&&scndName!=""&&ptrn!=""){
                                String fullname[] = {name,scndName,ptrn};
                                db.addPatient(id,fullname);

                            }
                        }catch (Exception exep){
                            exep.printStackTrace();
                        }

                    }
                });
            }
            /*

           */
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }



    }
}
