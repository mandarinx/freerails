package freerails.move;

import freerails.model.player.Player;
import freerails.model.train.motion.TrainMotion;
import freerails.model.world.World;
import org.jetbrains.annotations.NotNull;

// TODO undo not implemented
/**
 *
 */
public class MoveTrainActivityMove implements Move {

    private final Player player;
    private final int trainId;
    private final TrainMotion trainMotion;


    public MoveTrainActivityMove(@NotNull Player player, int trainId, @NotNull  TrainMotion trainMotion) {
        this.player = player;
        this.trainId = trainId;
        this.trainMotion = trainMotion;
    }

    @Override
    public Status tryDoMove(World world, Player player) {
        return Status.OK;
    }

    @Override
    public Status tryUndoMove(World world, Player player) {
        return Status.OK;
    }

    @Override
    public Status doMove(World world, Player player) {
        world.addActivity(this.player, trainId, trainMotion);
        return Status.OK;
    }

    @Override
    public Status undoMove(World world, Player player) {
        return Status.OK;
    }
}
