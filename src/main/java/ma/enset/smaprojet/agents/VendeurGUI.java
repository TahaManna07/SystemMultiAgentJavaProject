package ma.enset.smaprojet.agents;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VendeurGUI extends Application {
    ObservableList<String> observableList;

    protected VendeurAgent vendeurAgent;
    AgentContainer agentContainer;


    public static void main(String[] args) {
    launch(args);

    }
    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        stage.setTitle("Vendeur");
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);
        Label label = new Label("Agent Name : ");
        TextField textField = new TextField();
        Button buttonDeploy = new Button("deploy");
        hBox.getChildren().addAll(label,textField,buttonDeploy);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(hBox);
        VBox vBox = new VBox();
        observableList= FXCollections.observableArrayList();
        ListView<String> listView = new ListView<String>(observableList);
        vBox.getChildren().add(listView);
        borderPane.setCenter(vBox);
        Scene scene = new Scene(borderPane,400,300);
        stage.setScene(scene);
        stage.show();
        buttonDeploy.setOnAction((evt)->{
            // a chaque fois je click sur le button je deploy un nouveau agent
            try {
                String name = textField.getText();
                AgentController agentController = agentContainer.createNewAgent(name,"ma.enset.smaprojet.agents.VendeurAgent",new Object[]{this} );
                agentController.start();
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }

        });
    }
    public void startContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        agentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.createNewAgent("VENDEUR","ma.enset.smaprojet.agents.VendeurAgent",new Object[]{this} );
       // agentController.start();
    }
    public void logMessage(ACLMessage aclMessage){
        //observableList.add(aclMessage.getSender().getName()+"=>"+aclMessage.getContent());
        Platform.runLater(()->{
            observableList.add(aclMessage.getContent()+" "+aclMessage.getSender().getName());
        });
    }
}
