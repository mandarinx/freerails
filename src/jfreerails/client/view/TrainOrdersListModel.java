/*
 * TrainOrdersListModel.java
 *
 * Created on 23 August 2003, 17:49
 */

package jfreerails.client.view;
import javax.swing.*;
import jfreerails.world.top.*;
import jfreerails.world.train.*;

/**
 *
 * @author  Luke Lindsay
 */

public class TrainOrdersListModel extends AbstractListModel {
    
   	
    private int trainNumber;     
    
	private ReadOnlyWorld w;
    
    public static final int DONT_GOTO = 0;
    public static final int GOTO_NOW = 1;
    public static final int GOTO_AFTER_PRIORITY_ORDERS = 2;
    
    
    /** This class holds the values that are needed by the ListCellRender. TrainOrdersListModel.getElementAt(int index) returns
     an instance of this class. */
    public static class TrainOrdersListElement{        
        public final boolean isPriorityOrder; 
        public final int gotoStatus;
        public final TrainOrdersModel order;
        public final int trainNumber;
        public TrainOrdersListElement(boolean isPriorityOrder, int gotoStatus, TrainOrdersModel order, int trainNumber){
            this.isPriorityOrder = isPriorityOrder; 
            this.gotoStatus = gotoStatus;
            this.order = order;      
            this.trainNumber = trainNumber;
        }
    }
    
    /** Creates a new instance of TrainOrdersListModel */
    public TrainOrdersListModel(ReadOnlyWorld w, int trainNumber) {
       
        this.trainNumber = trainNumber;
		this.w = w;
    }
    
    public Object getElementAt(int index) {
        Schedule s = getSchedule();
        int gotoStatus;
        if(s.getNextScheduledOrder() == index){
            if(s.hasPriorityOrders()){
                gotoStatus = GOTO_AFTER_PRIORITY_ORDERS;
            }else{
                gotoStatus = GOTO_NOW;
            }
        }else{
            if(s.hasPriorityOrders() && 0 == index){
                //These orders are the priority orders.
                gotoStatus = GOTO_NOW;
            }else{
                gotoStatus = DONT_GOTO;
            }
        }
        
       
        boolean isPriorityOrders = 0 == index && s.hasPriorityOrders();
        TrainOrdersModel order =  getSchedule().getOrder(index);
        return new TrainOrdersListElement(isPriorityOrders, gotoStatus, order, trainNumber);
    }
    
    public int getSize() {
        return getSchedule().getNumOrders();
    }
    
    public void fireRefresh(){
        super.fireContentsChanged(this, 0, getSize());
    }
    
    private Schedule getSchedule(){
		TrainModel train = (TrainModel)w.get(KEY.TRAINS, trainNumber);		
		return (ImmutableSchedule)w.get(KEY.TRAIN_SCHEDULES, train.getScheduleID());		
    }    		
}