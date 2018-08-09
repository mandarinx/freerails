package freerails.move;

import freerails.model.player.Player;
import freerails.model.station.Station;
import freerails.model.world.World;
import org.jetbrains.annotations.NotNull;

// TODO hashcode, equals, try, undo ...
/**
 *
 */
public class AddStationMove implements Move {

    private final Player player;
    private final Station station;

    public AddStationMove(@NotNull Player player, @NotNull Station station) {
        this.player = player;
        this.station = station;
    }

    public Station getStation() {
        return station;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddStationMove)) return false;
        final AddStationMove other = (AddStationMove) o;

        return player.equals(other.player) && station.equals(other.station);
    }

    @Override
    public int hashCode() {
        int result = player.hashCode();
        result = 29 * result + station.hashCode();
        return result;
    }

    @Override
    public Status tryDoMove(World world, Player player) {
        if (World.contains(this.station.getId(), world.getStations(this.player))) {
            return Status.moveFailed("Station with id already existing");
        }
        return Status.OK;
    }

    @Override
    public Status tryUndoMove(World world, Player player) {
        if (!World.contains(this.station.getId(), world.getStations(this.player))) {
            return Status.moveFailed("Station with id not existing");
        }
        return Status.OK;
    }

    @Override
    public Status doMove(World world, Player player) {
        if (World.contains(this.station.getId(), world.getStations(this.player))) {
            return Status.moveFailed("Station with id already existing");
        }
        world.addStation(this.player, station);
        return Status.OK;
    }

    @Override
    public Status undoMove(World world, Player player) {
        if (!World.contains(this.station.getId(), world.getStations(this.player))) {
            return Status.moveFailed("Station with id not existing");
        }
        world.removeStation(this.player, station.getId());
        return Status.OK;
    }
}
