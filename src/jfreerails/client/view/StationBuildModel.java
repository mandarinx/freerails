/**
 * provides the models for the TrackMoveProducer build mode
 */
package jfreerails.client.view;

import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import java.util.Vector;

import jfreerails.client.renderer.TrackPieceRendererList;
import jfreerails.client.renderer.TrackPieceRenderer;
import jfreerails.client.renderer.ViewLists;
import jfreerails.controller.StationBuilder;
import jfreerails.world.top.ReadOnlyWorld;
import jfreerails.world.top.KEY;
import jfreerails.world.track.TrackConfiguration;
import jfreerails.world.track.TrackRule;

/**
 * This class provides the UI model for building a station.
 * The mode of operation is as follows:
 * <ol>
 * <li>Select a station to build by calling ActionPerformed() on the choose
 * Action.
 * <li>Set the position to build.
 * <li>call actionPerformed on the build Action
 * <li> alternatively, call actionPerformed on the cancel Action
 * </ol>
 */
public class StationBuildModel {
    /*
     * 100 010 001 = 0x111
     */
    private static final int trackTemplate =
        TrackConfiguration.getFlatInstance(0x111).getTemplate();

    /**
     * Vector of StationBuildAction.
     * Actions which represent stations which can be built
     */
    private Vector stationChooseActions = new Vector();

    private StationBuildAction stationBuildAction = new StationBuildAction();

    private StationCancelAction stationCancelAction = new StationCancelAction();
    
    private ReadOnlyWorld world;

    private StationBuilder stationBuilder;
 
    public StationBuildModel(StationBuilder sb, ReadOnlyWorld
	    world, ViewLists vl) {
	stationBuilder = sb;
	this.world = world;
	TrackPieceRendererList trackPieceRendererList =
	    vl.getTrackPieceViewList();
	for (int i = 0; i < world.size(KEY.TRACK_RULES); i++) {
	    TrackRule trackRule = (TrackRule)world.get(KEY.TRACK_RULES, i);
	    if (trackRule.isStation()) {
		TrackPieceRenderer renderer =
		    trackPieceRendererList.getTrackPieceView(i);
		StationChooseAction action = new StationChooseAction(i);
		String trackType = trackRule.getTypeName();
		action.putValue(Action.SHORT_DESCRIPTION, trackType);
		action.putValue(Action.NAME, "Build " + trackType);
		action.putValue(Action.SMALL_ICON, new
			ImageIcon(renderer.getTrackPieceIcon(trackTemplate)));
		stationChooseActions.add(action);	
	    }
	}
    }

    public Action[] getStationChooseActions() {
	return (Action[]) stationChooseActions.toArray(new Action[0]);
    }

    private class StationChooseAction extends AbstractAction {
	private int actionId;

	public StationChooseAction(int actionId) {
	    this.actionId = actionId;
	}

	public void actionPerformed(
		java.awt.event.ActionEvent actionEvent) {
	    stationBuilder.setStationType(actionId);
	    TrackRule trackRule = (TrackRule) world.get(KEY.TRACK_RULES,
		    actionId);
	    //Show the relevant station radius when the station type's menu item
	    //gets focus.
	    stationBuildAction.putValue(StationBuildAction.STATION_RADIUS_KEY,
		    new Integer(trackRule.getStationRadius()));
	    stationBuildAction.setEnabled(true);    
	}
    }

    private class StationCancelAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    stationBuildAction.setEnabled(false);
	}
    }

    /**
     * This action builds the station.
     */
    public class StationBuildAction extends AbstractAction {
	/**
	 * This key can be used to set the position where the station is to be
	 * built as a Point object. 
	 */
	public final static String STATION_POSITION_KEY =
	    "STATION_POSITION_KEY";

	/**
	 * This key can be used to retrieve the radius of the currently selected
	 * station as an Integer value. Don't bother writing to it!
	 */
	public final static String STATION_RADIUS_KEY = "STATION_RADIUS_KEY";

	StationBuildAction () {
	    setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
	    stationBuilder.buildStation((Point)
		    stationBuildAction.getValue(
			StationBuildAction.STATION_POSITION_KEY));
	    setEnabled(false);		
	}
    }

    public boolean canBuildStationHere() {
	Point p = (Point) stationBuildAction.getValue(
		    StationBuildAction.STATION_POSITION_KEY);
	return stationBuilder.canBuiltStationHere(p);
    }

    public Action getStationCancelAction() {
	return stationCancelAction;
    }

    public StationBuildAction getStationBuildAction() {
	return stationBuildAction;
    }
}
