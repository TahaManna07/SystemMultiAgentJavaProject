package ma.enset.smaprojet.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class VendeurAgent extends GuiAgent {

    protected VendeurGUI vendeurGUI;

    @Override
    protected void setup() {
        if(getArguments().length==1){
            vendeurGUI = (VendeurGUI) getArguments()[0];
            vendeurGUI.vendeurAgent=this;

        }
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        // c'est là où on va publier le service
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                dfAgentDescription.setName(getAID());// récuperer le ID de l'agent
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("vente-voitures");
                dfAgentDescription.addServices(serviceDescription);
                try {
                    // pour publier le service
                    DFService.register(myAgent,dfAgentDescription);//this fait référebce vers OneShotB , donc  => myAgent
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();//on attend le msg
                if(aclMessage!=null){
                    //vendeurGUI.logMessage(aclMessage);
//                    ACLMessage reply = aclMessage.createReply();
//                    reply.setContent("Ok pour "+aclMessage.getContent());
//                    send(reply);
                    //cçd quand it reçoit un message il va le logger dans  l'interface
                    switch(aclMessage.getPerformative()){
                        case ACLMessage.CFP :
                            // on va faire un reply
                            ACLMessage reply = aclMessage.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(String.valueOf(9000+new Random().nextInt(1000)));
                            send(reply);
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            String voiture = aclMessage.getContent();

                            // s'il accepte la proposition il va faire un reply
                            ACLMessage aclMessage1 = aclMessage.createReply();
                            aclMessage1.setPerformative(ACLMessage.AGREE);
                            aclMessage1.setContent("voici le meilleur prix : "+voiture);

                            send(aclMessage1);
                            break;


                    }
                }else{
                    block();
                }
            }
        });
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    @Override
    protected void takeDown() {
        // pour supprimer tous les services publier pour cet agent
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }
}
