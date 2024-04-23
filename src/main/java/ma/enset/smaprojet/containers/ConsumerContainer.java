package ma.enset.smaprojet.containers;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
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
import javafx.stage.Stage;
import ma.enset.smaprojet.agents.ConsumerAgent;

public class ConsumerContainer extends Application {

    protected ConsumerAgent consumerAgent;

    public void setConsumerAgent(ConsumerAgent consumerAgent) {
        this.consumerAgent = consumerAgent;
    }

    ObservableList<String> observableList;

    public static void main(String[] args)  {
            launch(args);
    }
    public  void startContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");

        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.createNewAgent("Consumer", "ma.enset.smaprojet.agents.ConsumerAgent", new Object[]{this});
        agentController.start();
    }
    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        stage.setTitle("Consumer ");
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane,600,400);
        stage.setScene(scene);
        stage.show();
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);
        Label label = new Label("Voiture  :");
        TextField textField = new TextField();
        Button acheterButton = new Button("Acheter");
        hBox.getChildren().addAll(label,textField,acheterButton);
        borderPane.setTop(hBox);
        observableList = FXCollections.observableArrayList();
        ListView<String> stringListView = new ListView<String>(observableList);

        HBox hboxL = new HBox();
        hboxL.setPadding(new Insets(10));
        hboxL.setSpacing(10);
        hboxL.getChildren().add(stringListView);

        borderPane.setCenter(hboxL);

        acheterButton.setOnAction(evt->{
            String voiture = textField.getText();
            //observableList.add(voiture);
            GuiEvent event = new GuiEvent(this,1);
            event.addParameter(voiture);
            consumerAgent.onGuiEvent(event);

        });


    }
    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableList.add(aclMessage.getContent()+" "+aclMessage.getSender().getName());

        });
    }
}
