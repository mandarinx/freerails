/*
 * Created on 28-Jun-2003
 *
 */
package jfreerails.server;

import jfreerails.move.Move;
import jfreerails.move.MoveStatus;
import jfreerails.world.accounts.BankAccount;
import jfreerails.world.cargo.CargoBatch;
import jfreerails.world.cargo.CargoBundle;
import jfreerails.world.cargo.CargoBundleImpl;
import jfreerails.world.cargo.CargoType;
import jfreerails.world.station.DemandAtStation;
import jfreerails.world.station.StationModel;
import jfreerails.world.top.KEY;
import jfreerails.world.top.World;
import jfreerails.world.top.WorldImpl;
import jfreerails.world.train.TrainModel;
import junit.framework.TestCase;

/** This Junit TestCase tests whether a train picks up and drops off the right cargo at a station.
 * @author Luke Lindsay
 *
 */
public class DropOffAndPickupCargoMoveGeneratorTest extends TestCase {

	private World w;
	private final CargoBatch cargoType0FromStation2 =
		new CargoBatch(0, 0, 0, 0, 2);
	private final CargoBatch cargoType1FromStation2 =
		new CargoBatch(1, 0, 0, 0, 2);
	private final CargoBatch cargoType0FromStation0 =
		new CargoBatch(0, 0, 0, 0, 0);
	private CargoBundle emptyCargoBundle;

	protected void setUp() throws Exception {
		//Set up an empty cargobundle
		emptyCargoBundle = new CargoBundleImpl();

		//Set up the world object with three cargo types, one station, and one train.		
		w = new WorldImpl();

		//set up the cargo types.
		w.add(KEY.CARGO_TYPES, new CargoType(0, "Mail", "Mail"));
		w.add(KEY.CARGO_TYPES, new CargoType(0, "Passengers", "Passengers"));
		w.add(KEY.CARGO_TYPES, new CargoType(0, "Goods", "Goods"));

		//Set up station
		int x = 10;
		int y = 10;
		int stationCargoBundleId =
			w.add(KEY.CARGO_BUNDLES, new CargoBundleImpl());
		String stationName = "Station 1";
		StationModel station =
			new StationModel(
				x,
				y,
				stationName,
				w.size(KEY.CARGO_TYPES),
				stationCargoBundleId);
		w.add(KEY.STATIONS, station);

		//Set up train
		int trainCargoBundleId =
			w.add(KEY.CARGO_BUNDLES, new CargoBundleImpl());

		//3 wagons to carry cargo type 0.
		int[] wagons = new int[] { 0, 0, 0 };
		TrainModel train = new TrainModel(wagons, trainCargoBundleId);
		w.add(KEY.TRAINS, train);
		
		w.add(KEY.BANK_ACCOUNTS, new BankAccount());
	}

	/** Tests picking up cargo from a station. */
	public void testPickUpCargo1() {

		//Set up the variables for this test.		

		CargoBundle cargoBundleWith2CarloadsOfCargo0 = new CargoBundleImpl();
		//cargoBundleWith2CarloadsOfCargo0.setAmount(cargoType0FromStation2, 2);
		cargoBundleWith2CarloadsOfCargo0.setAmount(cargoType0FromStation2, 80);

		//Get the station from the world object.
		StationModel station = (StationModel) w.get(KEY.STATIONS, 0);

		assertEquals(
			"There shouldn't be any cargo at the station yet",
			emptyCargoBundle,
			getCargoAtStation());
		assertEquals(
			"There shouldn't be any cargo on the train yet",
			emptyCargoBundle,
			getCargoOnTrain());

		//Now add 2 carloads of cargo type 0 to the station.				
		//getCargoAtStation().setAmount(cargoType0FromStation2, 2);
		getCargoAtStation().setAmount(cargoType0FromStation2, 80);

		//The train should pick up this cargo, since it has three wagons capable of carrying cargo type 0.
		stopAtStation();

		//The train should now have the two car loads of cargo and there should be no cargo at the station.
		assertEquals(
			"There should no longer be any cargo at the station",
			emptyCargoBundle,
			getCargoAtStation());
		assertEquals(
			"The train should now have the two car loads of cargo",
			cargoBundleWith2CarloadsOfCargo0,
			getCargoOnTrain());

	}

	/** Tests picking up cargo when the there is too much cargo at the station for the train to carry.*/
	public void testPickUpCargo2() {
		getCargoAtStation().setAmount(this.cargoType0FromStation2, 200);

		stopAtStation();

		//The train has 3 wagons, each wagon carries 40 units of cargo, so
		//the train should pickup 120 units of cargo.
		CargoBundle expectedOnTrain = new CargoBundleImpl();
		expectedOnTrain.setAmount(this.cargoType0FromStation2, 120);

		//The remaining 80 units of cargo should be left at the station.
		CargoBundle expectedAtStation = new CargoBundleImpl();
		expectedAtStation.setAmount(this.cargoType0FromStation2, 80);

		//Test the expected values against the actuals..
		assertEquals(expectedOnTrain, getCargoOnTrain());
		assertEquals(expectedAtStation, getCargoAtStation());
	}

	/** Tests that a train takes into account how much cargo it 
	 * already has and the type of wagons it has when it is picking up cargo.
	 */
	public void testPickUpCargo3() {
		//Set wagons on train.	
		TrainModel train = (TrainModel) w.get(KEY.TRAINS, 0);
		int[] wagons = new int[] { 0, 0, 2, 2 };
		//2 wagons for cargo type 0; 2 wagons for cargo type 2.
		train = addWagons(wagons);

		//Set cargo on train.
		getCargoOnTrain().setAmount(this.cargoType0FromStation2, 30);

		//Set cargo at station.
		getCargoAtStation().setAmount(this.cargoType0FromStation0, 110);

		//Check that station does not demand cargo type 0.
		StationModel station = (StationModel) w.get(KEY.STATIONS, 0);
		assertFalse(station.getDemand().isCargoDemanded(0));

		//Stop at station.
		stopAtStation();

		/* The train has 2 wagons for cargo type 0 but had 30 units of
		 * cargo type 0 before stopping so it can only pick up 50 units.
		 */
		CargoBundle expectedAtStation = new CargoBundleImpl();
		expectedAtStation.setAmount(cargoType0FromStation0, 60);

		CargoBundle expectedOnTrain = new CargoBundleImpl();
		expectedOnTrain.setAmount(this.cargoType0FromStation2, 30);
		expectedOnTrain.setAmount(this.cargoType0FromStation0, 50);

		assertEquals(expectedAtStation, getCargoAtStation());
		assertEquals(expectedOnTrain, getCargoOnTrain());
	}

	/** Tests that a train drops of cargo that a station demands and does not drop off cargo that is not
	 * demanded unless it has to.
	 */
	public void testDropOffCargo() {
		//Set the station to demand cargo type 0.
		StationModel station = (StationModel) w.get(KEY.STATIONS, 0);
		DemandAtStation demand =
			new DemandAtStation(new boolean[] { true, false, false, false });
		station.setDemand(demand);

		//Check that the station demadns what we think it does.		
		assertTrue(
			"The station should demand cargo type 0.",
			station.getDemand().isCargoDemanded(0));
		assertFalse(
			"The station shouldn't demand cargo type 1.",
			station.getDemand().isCargoDemanded(1));

		//Add 2 wagons for cargo type 0 and 1 for cargo type 1 to train.
		int[] wagons = new int[] { 0, 0, 1, 1 };
		TrainModel train = (TrainModel) w.get(KEY.TRAINS, 0);
		train = addWagons(wagons);

		//Add quantities of cargo type 0 and 2 to the train.
		getCargoOnTrain().setAmount(this.cargoType0FromStation2, 50);
		getCargoOnTrain().setAmount(this.cargoType1FromStation2, 40);

		stopAtStation();

		/* The train should have dropped of the 50 units cargo of type 0 since the 
		 * station demands it but not the 40 units of cargo type 1 which is does not
		 * demand.
		 */
		CargoBundle expectedOnTrain = new CargoBundleImpl();
		expectedOnTrain.setAmount(this.cargoType1FromStation2, 40);

		assertEquals(expectedOnTrain, getCargoOnTrain());
		assertEquals(emptyCargoBundle, getCargoAtStation());

		//Now remove the wagons from the train.
		removeAllWagonsFromTrain();
		stopAtStation();

		/*This time the train has no wagons, so has to drop the 40 units
		 * of cargo type 1 even though the station does not demand it.  Since
		 * ths station does not demand it, it is added to the cargo waiting at
		 * the station.
		 */
		CargoBundle expectedAtStation = new CargoBundleImpl();
		expectedAtStation.setAmount(this.cargoType1FromStation2, 40);

		assertEquals(expectedAtStation, getCargoAtStation());
		assertEquals(emptyCargoBundle, getCargoOnTrain());
	}

	/** Tests that a train does not drop cargo off at its station of origin unless it has to*/
	public void testDontDropOffCargo() {

		//Set station to 

		getCargoOnTrain().setAmount(cargoType0FromStation0, 50);
		getCargoOnTrain().setAmount(cargoType0FromStation2, 50);

		stopAtStation();

		//The train shouldn't have dropped anything off.
		CargoBundle expectedOnTrain = new CargoBundleImpl();
		expectedOnTrain.setAmount(cargoType0FromStation0, 50);
		expectedOnTrain.setAmount(cargoType0FromStation2, 50);

		assertEquals(expectedOnTrain, getCargoOnTrain());
		assertEquals(emptyCargoBundle, getCargoAtStation());

		//Now remove the wagons from the train.
		removeAllWagonsFromTrain();

		stopAtStation();

		/*The train now has no wagons, so must drop off the cargo whether the 
		station demands it or not.  Since the station does not demand it, the 
		cargo should get added to the cargo waiting at the station. */

		CargoBundle expectedAtStaton = new CargoBundleImpl();
		expectedAtStaton.setAmount(cargoType0FromStation0, 50);
		expectedAtStaton.setAmount(cargoType0FromStation2, 50);

		assertEquals(expectedAtStaton, getCargoAtStation());
		assertEquals(emptyCargoBundle, getCargoOnTrain());
	}

	/**  Tests that a train drops off any cargo before picking
	 * up cargo.
	 */
	public void testPickUpAndDropOffSameCargoType() {
		//Set cargo at station and on train.
		getCargoOnTrain().setAmount(this.cargoType0FromStation2, 120);
		getCargoAtStation().setAmount(this.cargoType0FromStation0, 200);

		//Set station to demand cargo 0.
		StationModel station = (StationModel) w.get(KEY.STATIONS, 0);
		DemandAtStation demand =
			new DemandAtStation(new boolean[] { true, false, false, false });
		station.setDemand(demand);

		assertTrue(station.getDemand().isCargoDemanded(0));
		stopAtStation();

		CargoBundle expectedOnTrain = new CargoBundleImpl();
		expectedOnTrain.setAmount(this.cargoType0FromStation0, 120);

		CargoBundle expectedAtStation = new CargoBundleImpl();
		expectedAtStation.setAmount(this.cargoType0FromStation0, 80);

		assertEquals(expectedAtStation, getCargoAtStation());
		assertEquals(expectedOnTrain, getCargoOnTrain());
	}

	private void removeAllWagonsFromTrain() {
		addWagons(new int[] {});
	}
	
	private TrainModel addWagons(int[] wagons){
		TrainModel train = (TrainModel) w.get(KEY.TRAINS, 0);
		TrainModel newTrain = train.getNewInstance(train.getEngineType(), wagons);
		w.set(KEY.TRAINS, 0, newTrain);
		return newTrain;
	}

	private void stopAtStation() {
		DropOffAndPickupCargoMoveGenerator moveGenerator =
			new DropOffAndPickupCargoMoveGenerator(0, 0, w);
		Move m = moveGenerator.generateMove();
		MoveStatus ms = m.doMove(w);
		assertTrue(ms.isOk());
	}

	/** Retrieves the cargo bundle that is waiting at the station from the world object.*/
	private CargoBundle getCargoAtStation() {
		StationModel station = (StationModel) w.get(KEY.STATIONS, 0);
		CargoBundle cargoAtStation =
			(CargoBundle) w.get(
				KEY.CARGO_BUNDLES,
				station.getCargoBundleNumber());
		return cargoAtStation;
	}

	/** Retrieves the cargo bundle that the train is carrying from the world object */
	private CargoBundle getCargoOnTrain() {
		TrainModel train = (TrainModel) w.get(KEY.TRAINS, 0);
		CargoBundle cargoOnTrain =
			(CargoBundle) w.get(
				KEY.CARGO_BUNDLES,
				train.getCargoBundleNumber());
		return cargoOnTrain;
	}

	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite() {
		junit.framework.TestSuite testSuite =
			new junit.framework.TestSuite(
				DropOffAndPickupCargoMoveGeneratorTest.class);
		return testSuite;
	}

}