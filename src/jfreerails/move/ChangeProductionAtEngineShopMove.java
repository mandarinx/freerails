/*
 * Created on 28-Mar-2003
 *
 */
package jfreerails.move;

import jfreerails.world.player.FreerailsPrincipal;
import jfreerails.world.player.Player;
import jfreerails.world.station.ProductionAtEngineShop;
import jfreerails.world.station.StationModel;
import jfreerails.world.top.KEY;
import jfreerails.world.top.World;


/**
 * This Move changes what is being built
 * at an engine shop - when a client wants to build a train, it
 * should send an instance of this class to the server.
 *
 * @author Luke
 *
 */
public class ChangeProductionAtEngineShopMove implements Move {
    final ProductionAtEngineShop before;
    final ProductionAtEngineShop after;
    final int stationNumber;

    public ChangeProductionAtEngineShopMove(ProductionAtEngineShop b,
        ProductionAtEngineShop a, int station) {
        this.before = b;
        this.after = a;
        this.stationNumber = station;
    }

    public MoveStatus tryDoMove(World w, FreerailsPrincipal p) {
        return tryMove(w, before);
    }

    private MoveStatus tryMove(World w, ProductionAtEngineShop stateA) {
        //Check that the specified station exists.
        if (!w.boundsContain(KEY.STATIONS, this.stationNumber,
                    Player.TEST_PRINCIPAL)) {
            return MoveStatus.moveFailed(this.stationNumber + " " +
                Player.TEST_PRINCIPAL);
        }

        StationModel station = (StationModel)w.get(KEY.STATIONS, stationNumber,
                Player.TEST_PRINCIPAL);

        if (null == station) {
            return MoveStatus.moveFailed(this.stationNumber + " " +
                Player.TEST_PRINCIPAL + " is does null");
        }

        //Check that the station is building what we expect.					
        if (null == station.getProduction()) {
            if (null == stateA) {
                return MoveStatus.MOVE_OK;
            } else {
                return MoveStatus.moveFailed(this.stationNumber + " " +
                    Player.TEST_PRINCIPAL);
            }
        } else {
            if (station.getProduction().equals(stateA)) {
                return MoveStatus.MOVE_OK;
            } else {
                return MoveStatus.moveFailed(this.stationNumber + " " +
                    Player.TEST_PRINCIPAL);
            }
        }
    }

    public MoveStatus tryUndoMove(World w, FreerailsPrincipal p) {
        return tryMove(w, after);
    }

    public MoveStatus doMove(World w, FreerailsPrincipal p) {
        MoveStatus status = tryDoMove(w, p);

        if (status.isOk()) {
            StationModel station = (StationModel)w.get(KEY.STATIONS,
                    stationNumber, Player.TEST_PRINCIPAL);
            station = new StationModel(station, this.after);
            w.set(KEY.STATIONS, stationNumber, station, Player.TEST_PRINCIPAL);
        }

        return status;
    }

    public MoveStatus undoMove(World w, FreerailsPrincipal p) {
        MoveStatus status = tryUndoMove(w, p);

        if (status.isOk()) {
            StationModel station = (StationModel)w.get(KEY.STATIONS,
                    stationNumber, Player.TEST_PRINCIPAL);
            station = new StationModel(station, this.before);
            w.set(KEY.STATIONS, stationNumber, station, Player.TEST_PRINCIPAL);
        }

        return status;
    }

    public boolean equals(Object o) {
        if (o instanceof ChangeProductionAtEngineShopMove) {
            ChangeProductionAtEngineShopMove arg = (ChangeProductionAtEngineShopMove)o;
            boolean stationNumbersEqual = (this.stationNumber == arg.stationNumber);
            boolean beforeFieldsEqual = (before == null ? arg.before == null
                                                        : before.equals(arg.before));
            boolean afterFieldsEqual = (after == null ? arg.after == null
                                                      : after.equals(arg.after));

            if (stationNumbersEqual && beforeFieldsEqual && afterFieldsEqual) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}