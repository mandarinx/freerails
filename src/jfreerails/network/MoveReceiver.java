package jfreerails.network;

import jfreerails.move.Move;


/**
 * Accepts a Move without the caller knowing where its going.
 * TODO replace with MoveExecutor where the moves are
 * expected to be executed.
 * @author Luke
 */
public interface MoveReceiver {
    public void processMove(Move move);
}