package ma.enset.smaprojet.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import ma.enset.smaprojet.containers.ConsumerContainer;

public class ConsumerAgent extends GuiAgent {
    private ConsumerContainer gui;

    @Override
    protected void setup() {

        // pour le tester de migration
        if (getArguments().length == 1) {
            gui = (ConsumerContainer) getArguments()[0];
            gui.setConsumerAgent(this);// c'est l'agent , ona a une association bidirectionnelle
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if(aclMessage!=null){
                        switch (aclMessage.getPerformative()){
                            case ACLMessage.CONFIRM:
                                gui.logMessage(aclMessage);
                                break;


                        }
                }else{
                    block();
                }

            }
        });
    }

    @Override
    public void onGuiEvent(GuiEvent params) {
        if (params.getType()==1){
            String voiture = (String) params.getParameter(0);
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);// c'est l'acte de communication (REQUEST)
            aclMessage.setContent(voiture);
            aclMessage.addReceiver(new AID("ACHETEUR",AID.ISLOCALNAME));
            send(aclMessage);


        }
    }
}
