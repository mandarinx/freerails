/*
 * Created on 30-Jul-2003
 *
 */
package jfreerails.server;

import java.util.Iterator;

import jfreerails.move.AddTransactionMove;
import jfreerails.world.accounts.Receipt;
import jfreerails.world.cargo.CargoBatch;
import jfreerails.world.cargo.CargoBundle;
import jfreerails.world.common.Money;
import jfreerails.world.station.StationModel;
import jfreerails.world.top.KEY;
import jfreerails.world.top.World;

/** This class Generates Moves that pay the player for delivering the cargo.
 * 
 * @author Luke Lindsay
 *
 */
public class ProcessCargoAtStationMoveGenerator {
	
	public static AddTransactionMove processCargo(World w, CargoBundle cargoBundle, int stationID){			
		StationModel thisStation = (StationModel)w.get(KEY.STATIONS, stationID);		
		Iterator batches = cargoBundle.cargoBatchIterator();
		int amountOfCargo = 0;
		double amount = 0;
		while(batches.hasNext()){
			CargoBatch batch = (CargoBatch)batches.next();		
			int distanceSquared = (batch.getSourceX() -  thisStation.x) * (batch.getSourceX() -  thisStation.x) * (batch.getSourceY() -  thisStation.y) * (batch.getSourceY() -  thisStation.y);
			double dist	= Math.sqrt(distanceSquared);	 			
			amount += cargoBundle.getAmount(batch) * dist * 10;
		}		
		Receipt receipt = new Receipt(new Money((long)amount));
		return new AddTransactionMove(0, receipt);
	}

}
