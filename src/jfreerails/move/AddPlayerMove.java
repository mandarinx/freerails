package jfreerails.move;

import jfreerails.world.accounts.Receipt;
import jfreerails.world.accounts.Transaction;
import jfreerails.world.common.Money;
import jfreerails.world.player.FreerailsPrincipal;
import jfreerails.world.player.Player;
import jfreerails.world.player.PlayerPrincipal;
import jfreerails.world.top.ReadOnlyWorld;
import jfreerails.world.top.World;


/**
 * Adds a player to the world
 */
public class AddPlayerMove implements Move, ServerMove {
    private final Player player2add;

    private AddPlayerMove(Player p) {
        this.player2add = p;
    }

    public static AddPlayerMove generateMove(ReadOnlyWorld w, Player player) {
        /**
         * create a new player with a corresponding Principal
         */
        Player player2add = new Player(player.getName(), player.getPublicKey(),
                w.getNumberOfPlayers());
        PlayerPrincipal tmpPlayer = new PlayerPrincipal(w.getNumberOfPlayers());

        return new AddPlayerMove(player2add);
    }

    public MoveStatus tryDoMove(World w, FreerailsPrincipal p) {
        // TODO Auto-generated method stub
        return MoveStatus.MOVE_OK;
    }

    public MoveStatus tryUndoMove(World w, FreerailsPrincipal p) {
        // TODO Auto-generated method stub
        return MoveStatus.MOVE_OK;
    }

    public MoveStatus doMove(World w, FreerailsPrincipal p) {
        w.addPlayer(this.player2add, Player.AUTHORITATIVE);

        Transaction t = new Receipt(new Money(w.getNumberOfPlayers() * 100000));
        w.addTransaction(t, player2add.getPrincipal());

        return MoveStatus.MOVE_OK;
    }

    public MoveStatus undoMove(World w, FreerailsPrincipal p) {
        // TODO Auto-generated method stub
        return MoveStatus.MOVE_OK;
    }
}