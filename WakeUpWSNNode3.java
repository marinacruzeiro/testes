package eboracum.wsn.network.node.sensor;

import java.util.List;


//import eboracum.wsn.network.node.sensor.cpu.SimpleFIFOBasedCPU;
import ptolemy.actor.NoRoomException;
import ptolemy.actor.NoTokenException;
import ptolemy.data.BooleanToken;
import ptolemy.data.StringToken;
import ptolemy.data.expr.SingletonParameter;
import ptolemy.data.expr.StringParameter;
import ptolemy.domains.wireless.kernel.WirelessIOPort;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

//------------------------------------------------------------------------------------------------

public class WakeUpWSNNode extends  SimpleWSNNode {

    private static final long serialVersionUID = 1L;
    
    public String receivedWUSignal;
    
    protected boolean sleepFlag;
    
    protected WirelessIOPort outWU;
    protected WirelessIOPort inWU;
    
    //Para testes:
    public String nome;
    public int contador;
    //--
    
//------------------------------------------------------------------------------------------------
    
    public WakeUpWSNNode(CompositeEntity container, String name) throws IllegalActionException, NameDuplicationException {
        super(container, name);
        
        sleepFlag = true;
        
        //----------------------------------------------

        StringParameter wuSignalChannelName = new StringParameter(this,"WUSignalChannelName");
        wuSignalChannelName.setExpression("PowerLossChannel3");
        //

        outWU = new WirelessIOPort(this, "output3", false, true);
        outWU.outsideChannel.setExpression("$WUSignalChannelName");
        
        inWU = new WirelessIOPort(this, "input3", true, false);
        inWU.outsideChannel.setExpression("$WUSignalChannelName");
        
        try {
            (new SingletonParameter(outWU, "_hide")).setToken(BooleanToken.TRUE);
            (new SingletonParameter(inWU, "_hide")).setToken(BooleanToken.TRUE);
            } catch (NameDuplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // ----------------------------------------------
        
    }

//------------------------------------------------------------------------------------------------
    
    public void initialize() throws IllegalActionException {
        super.initialize();
        
    }      

//------------------------------------------------------------------------------------------------
    
    /*public void fire() throws NoTokenException, IllegalActionException {
        
        super.fire();
        
        
    }
    
    public boolean postfire() throws IllegalActionException{
        
        //this.receivedWUSignal = null;
        //inWU.sendClear(0);
        
        return super.postfire();
    }*/

//------------------------------------------------------------------------------------------------
  
    protected void eventDoneManager(List<Object> runReturn) throws NoRoomException, IllegalActionException{
        System.out.println(runReturn);
        // verify if the event must be send through the network (not ordinary)
        
        if (!this.eventOrdinaryMap.get(((String)runReturn.get(1)).split("_")[0])) {       
            
           sendManager(((String)runReturn.get(1)));
           
           this.numberOfSensoredEvents++; // collect the event for statistics //?
        }

    }
    
//------------------------------------------------------------------------------------------------
    
    protected void sendManager(String token) throws NoRoomException, IllegalActionException {
        
        //if (this.sendMessageToNeighbours("WakeUp", 0.0006)) {
        //if (this.sendMessageToNeighbours(token, 0.0006)) {
            this.sendMessageToNeighbours(token, 0.0006);
            this.sendMessageToSink(token);
        //}
        
    }
    
//------------------------------------------------------------------------------------------------
    
    // Descarga de bateria do valor de sleep do nodo - deve ser modificado ainda
    public void batteryDischarge() throws NumberFormatException, IllegalActionException {
        
        if (!this.timeControler.equals(this.getDirector().getModelTime())){
            if (this.synchronizedRealTime.getExpression().equals("true") && 
                            Double.parseDouble(battery.getValueAsString()) >= Double.parseDouble((this.idleEnergyCost.getExpression())))
                    battery.setExpression(Double.toString((Double.parseDouble(battery.getValueAsString())-Double.parseDouble(idleEnergyCost.getValueAsString()))));
            else {
                    battery.setExpression(Double.toString((
                                            Double.parseDouble(battery.getValueAsString())-
                                            ( (Double.parseDouble(idleEnergyCost.getValueAsString())*this.getDirector().getModelTime().subtract(this.newTimeControler).getDoubleValue() )   ))));
                    if ((((Double.parseDouble(battery.getValueAsString())/Double.parseDouble(idleEnergyCost.getExpression()))) > 0))
                        this.timeOfDeath = (this.getDirector().getModelTime().add(((Double.parseDouble(battery.getValueAsString())/Double.parseDouble(idleEnergyCost.getExpression())))));
                        //_fireAt((this.getDirector().getModelTime().add(round((Double.parseDouble(battery.getValueAsString())/Double.parseDouble(idleEnergyCost.getExpression()))))));
            }
        }   
    }
    
//------------------------------------------------------------------------------------------------
    
    
    public void messageManager() throws NumberFormatException, IllegalActionException {
        
        System.out.println(this.getName() + " entrou no messageManager()");
        
        //Esse funciona:
        /*if (inWU.hasToken(0)) {
            System.out.println("Entrou em inWU: " + this.inWU.get(0).toString());
            if(in.hasToken(0)){
                System.out.println("Entrou em in.hasToken()");
                // receives and decide what to do with the message, if it is for this node.
                if (this.receiveMessage(this.in.get(0).toString())) {
                        this.sendMessageToNeighbours(this.receivedMessage, 0.0006);
                        this.sendMessageToSink(this.receivedMessage);
                    
                }
            }
        }*/
        
        /*if (inWU.hasToken(0)) {
            System.out.println("Entrou em inWU: " + this.inWU.get(0).toString());
            if (this.handshakeWakeUp(this.inWU.get(0).toString())) {
            
                if(in.hasToken(0)){
                    System.out.println("Entrou em in.hasToken()");
                    //System.out.println("Entrou em inWU: " + this.inWU.get(0).toString());
                    // receives and decide what to do with the message, if it is for this node.
                    if (this.receiveMessage(this.in.get(0).toString())) {
                            this.sendMessageToNeighbours(this.receivedMessage, 0.0006);
                            this.sendMessageToSink(this.receivedMessage);
                    }
                }
                
            }
            
        }*/
        
        System.out.println(this.getName() + " entrou no messageManager()");
        if (inWU.hasToken(0)) {
            System.out.println(this.getName() + " entrou em inWU.hasToken()");
            
            if (this.handshakeWakeUp(this.inWU.get(0).toString())) {
                System.out.println("Fez handshake");
                if (in.hasToken(0)) {
                    System.out.println("Entrou em in.hasToken()");
                    if (this.receiveMessage(this.in.get(0).toString())) {
                        this.sendManager(this.receivedMessage);
                    } 
                //this.sendManager(this.receivedMessage);     
                    
                }
            } else {
                System.out.println("Não fez handshake e voltou");
            }

        }
        
   }
    
//------------------------------------------------------------------------------------------------
    
    public boolean receiveMessage(String tempMessage) throws NumberFormatException, IllegalActionException{
        
        tempMessage = tempMessage.substring(2, tempMessage.length()-2);
        
        this.numberOfReceivedMessages++;
        
        if (this.getName().equals(tempMessage.split(",")[1].split("=")[1])){
                this.receivedMessage = tempMessage.split(",")[0].split("=")[1];
                
                System.out.println(this.getName() + " recebeu mensagem ");
                
                return true;
        }
        else {
            System.out.println("Não era para " + this.getName());
            return false;
        }
        
    }

//------------------------------------------------------------------------------------------------
    
    public boolean handshakeWakeUp(String tempMessage) throws NumberFormatException, IllegalActionException{

        tempMessage = tempMessage.substring(2, tempMessage.length()-2);
        
        this.numberOfReceivedMessages++;
        
        if (this.getName().equals(tempMessage.split(",")[1].split("=")[1])){
                //this.receivedWUSignal = tempMessage.split(",")[0].split("=")[1];
                //this.receivedMessage = tempMessage.split(",")[0].split("=")[1];
            
                System.out.println(this.getName() + " fez handshake");
                
                return true;
        }
        else {
            System.out.println("Não fez handshake " + this.getName());
            return false;
        }
    }

//------------------------------------------------------------------------------------------------    
    
    public boolean sendMessageToNeighbours(String token, double neighboursCommCost) throws NoRoomException, IllegalActionException{
        
        String nodoNome = this.getName();
        
        //double commCost = this.eventCommCostMap.get(token.split("_")[0]);//neighboursCommCost;
        double commCost = this.eventCommCostMap.get(token.split("_")[0]);
        if ((!gateway.getExpression().equals("")&&!gateway.getExpression().equals("END")) && (Double.parseDouble(battery.getValueAsString()) >= commCost)){
            battery.setExpression(Double.toString( ( Double.parseDouble(battery.getValueAsString()) - commCost )));
            if (this.synchronizedRealTime.getExpression().equals("false"))
                    this.timeOfDeath = (this.getDirector().getModelTime().add(((Double.parseDouble(battery.getValueAsString())/Double.parseDouble(idleEnergyCost.getExpression())))));
           
            outWU.send(0, new StringToken("{event="+token+",gateway="+gateway.getExpression()+"}"));
            
            System.out.println(nodoNome + " mandou mensagem de WU para " + "{event="+token+",gateway="+gateway.getExpression()+"}");
            
            this.numberOfSentMessages++;
            return true;
        }
        else
            return false;
                
    }
    
  //------------------------------------------------------------------------------------------------
    
    // Aqui só pra poder fazer prints, talvez não seja necessário ficar
    protected boolean sendMessageToSink(String token) throws NoRoomException, IllegalActionException{
        
        String nodoNome = this.getName(); 
        
        double commCost = this.eventCommCostMap.get(token.split("_")[0]);
        if ((!gateway.getExpression().equals("")&&!gateway.getExpression().equals("END")) && (Double.parseDouble(battery.getValueAsString()) >= commCost)){
            battery.setExpression(Double.toString( ( Double.parseDouble(battery.getValueAsString()) - commCost )));
            if (this.synchronizedRealTime.getExpression().equals("false"))
                    this.timeOfDeath = (this.getDirector().getModelTime().add(((Double.parseDouble(battery.getValueAsString())/Double.parseDouble(idleEnergyCost.getExpression())))));
             
            //outWU.send(0, new StringToken("{event="+token+",gateway="+gateway.getExpression()+"}"));
            out.send(0, new StringToken("{event="+token+",gateway="+gateway.getExpression()+"}"));

            System.out.println(nodoNome +" mandou mensagem de evento para "+ "{event="+token+",gateway="+gateway.getExpression()+"}");
            this.numberOfSentMessages++;
            return true;
        }
        else
                return false;
        
    }
    
}
