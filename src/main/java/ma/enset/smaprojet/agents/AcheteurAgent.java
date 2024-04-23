package ma.enset.smaprojet.agents;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class AcheteurAgent extends GuiAgent {


    protected AcheteurGui acheteurGui;
    protected AID[] vendeurs;

    @Override
    protected void setup() {
        if(getArguments().length==1){
            acheteurGui = (AcheteurGui) getArguments()[0];
            acheteurGui.acheteurAgent=this;

        }
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        addBehaviour(new TickerBehaviour(this,5000) {
            @Override
            protected void onTick() {
                // il attend a chaque fois 5 sec , il va récupérer la list
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                //càd est ce qu'on veut chercher le service par nom ou bien  ....

                serviceDescription.setType("transaction");
                serviceDescription.setName("vente-voitures");
                dfAgentDescription.addServices(serviceDescription);
                try {
                    // rs récupér les services de types vendeurs
                    DFAgentDescription[] rs = DFService.search(myAgent,dfAgentDescription);
                    //pour récupérer la listes des agents
                    vendeurs = new AID[rs.length];
                    for (int i = 0; i < vendeurs.length; i++) {
                        vendeurs[i] = rs[i].getName();
                    }
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            private int cpt=0;
            private List<ACLMessage> replies = new ArrayList<ACLMessage>();
            @Override
            public void action() {
                // Message Template pour que l'agent n'accepte que ces messages
                MessageTemplate messageTemplate = MessageTemplate.or
                                 (MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                   MessageTemplate.or(
                                           MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                                           MessageTemplate.or(
                                                   MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                                                   MessageTemplate.MatchPerformative(ACLMessage.REFUSE)


                                           )
                                   ));
                ACLMessage aclMessage = receive(messageTemplate);//on attend le msg
                if(aclMessage!=null){

                    switch (aclMessage.getPerformative()){
                        // çad je reçoit le message du consomateur
                        case  ACLMessage.REQUEST :
                            String voiture = aclMessage.getContent();
                            // càd il envoie un appel pour proposition
                                ACLMessage aclMessage1 = new ACLMessage(ACLMessage.CFP);
                                aclMessage1.setContent(voiture);
                            for(AID aid : vendeurs){
                                aclMessage1.addReceiver(aid);
                            }
                            // maintenant on a envoyer le message aux agents
                            send(aclMessage1);
                            break;
                        case  ACLMessage.PROPOSE:
                            //Lorsqu'il reçoit des propositions des vendeurs,
                            // il sélectionne la meilleure offre et envoie une confirmation au consommateur.
                            ++cpt;
                            replies.add(aclMessage);
                            // si le nombre des réponses == les nombres des vendeurs
                            if(cpt==vendeurs.length){
                                ACLMessage meilleurOffre = replies.get(0);
                                // pour savoir le meilleur offre
                                double min=Double.parseDouble(meilleurOffre.getContent());
                                for(ACLMessage offre : replies){
                                    double price = Double.parseDouble(offre.getContent());
                                    if (price < min) {
                                            meilleurOffre = offre;
                                            min=price;
                                    }
                                }
                                // Correction de l'erreur ici : utilisez meilleurOffre au lieu de aclMessage
                                ACLMessage offreAccept = meilleurOffre.createReply();
                                offreAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                offreAccept.setContent(meilleurOffre.getContent());

                                send(offreAccept);
                            }

                            break;
                        case  ACLMessage.AGREE:
                            ACLMessage response = aclMessage.createReply(ACLMessage.CONFIRM);
                            response.addReceiver(new AID("Consumer",AID.ISLOCALNAME));
                            response.setContent(aclMessage.getContent());
                            send(response);




                            break;
                        case ACLMessage.REFUSE:
                            break;
                        default: break;
                    }
                    String voiture = aclMessage.getContent();
                    acheteurGui.logMessage(aclMessage);
                    ACLMessage reply = aclMessage.createReply();
                    reply.setContent("Voici le prix  : "+aclMessage.getContent());
                    send(reply);
                    ACLMessage aclMessage1 = new ACLMessage(ACLMessage.CFP); // call for proposal
                    aclMessage1.setContent(voiture);
                    aclMessage1.addReceiver(new AID("VENDEUR",AID.ISLOCALNAME));
                    send(aclMessage1);

                }else{
                    block();
                }
            }
        });
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}
