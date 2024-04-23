package ma.enset.smaprojet.agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.util.ExtendedProperties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AcheteurGui extends Application {
    ObservableList<String> observableList;

    protected AcheteurAgent acheteurAgent;



    public static void main(String[] args) {
    launch(args);

    }
    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        stage.setTitle("Acheteur");
        BorderPane borderPane = new BorderPane();
        VBox vBox = new VBox();
        observableList= FXCollections.observableArrayList();
        ListView<String> listView = new ListView<String>(observableList);
        vBox.getChildren().add(listView);
        borderPane.setCenter(vBox);
        Scene scene = new Scene(borderPane,400,300);
        stage.setScene(scene);
        stage.show();
    }
    public void startContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.
                createNewAgent("ACHETEUR","ma.enset.smaprojet.agents.AcheteurAgent",new Object[]{this} );
        agentController.start();
    }
    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(() -> {
            observableList.add(aclMessage.getContent()+" "+aclMessage.getSender().getName());
        });
    }

    // Méthode pour afficher le meilleur prix dans l'interface graphique


}
