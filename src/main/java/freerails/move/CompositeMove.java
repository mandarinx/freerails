/*
 * FreeRails
 * Copyright (C) 2000-2018 The FreeRails Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * Created on 26-May-2003
 *
 */
package freerails.move;

import freerails.util.ImList;
import freerails.world.World;
import freerails.world.player.FreerailsPrincipal;

import java.util.List;

/**
 * This Move may be subclassed to create a move composed of a number of
 * component Moves where atomicity of the move is required. This class defines a
 * number of methods which may not be subclassed - all changes must be
 * encapsulated as sub-moves of this move.
 */
public class CompositeMove implements Move {
    private static final long serialVersionUID = 3257289149391517489L;

    private final ImList<Move> moves;

    /**
     * @param movesArrayList
     */
    public CompositeMove(List<Move> movesArrayList) {

        moves = new ImList<>(movesArrayList);
    }

    /**
     * @param moves
     */
    public CompositeMove(Move... moves) {
        this.moves = new ImList<>(moves);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CompositeMove))
            return false;

        final CompositeMove compositeMove = (CompositeMove) o;

        return moves.equals(compositeMove.moves);
    }

    /**
     * This method lets sub classes look at the moves.
     */
    final Move getMove(int i) {
        return moves.get(i);
    }

    @Override
    public int hashCode() {
        // This will do for now.
        return moves.size();
    }

    /**
     * @return
     */
    public final ImList<Move> getMoves() {
        return moves;
    }

    public MoveStatus tryDoMove(World world, FreerailsPrincipal principal) {
        // Since whether a move later in the list goes through could
        // depend on whether an ealier move has been executed, we need
        // actually execute moves, then undo them to test whether the
        // array of moves can be excuted ok.
        MoveStatus ms = doMove(world, principal);

        if (ms.ok) {
            // We just wanted to see if we could do them so we undo them again.
            undoMoves(world, moves.size() - 1, principal);
        }

        // If its not ok, then doMove would have undone the moves so we don't
        // need to undo them.
        return ms;
    }

    public MoveStatus tryUndoMove(World world, FreerailsPrincipal principal) {
        MoveStatus ms = undoMove(world, principal);

        if (ms.isOk()) {
            redoMoves(world, 0, principal);
        }

        return ms;
    }

    public MoveStatus doMove(World world, FreerailsPrincipal principal) {
        MoveStatus ms = compositeTest(world, principal);

        if (!ms.ok) {
            return ms;
        }

        for (int i = 0; i < moves.size(); i++) {
            ms = moves.get(i).doMove(world, principal);

            if (!ms.ok) {
                // Undo any moves we have already done.
                undoMoves(world, i - 1, principal);

                return ms;
            }
        }

        return ms;
    }

    public MoveStatus undoMove(World world, FreerailsPrincipal principal) {
        MoveStatus ms = MoveStatus.MOVE_OK;

        for (int i = moves.size() - 1; i >= 0; i--) {
            ms = moves.get(i).undoMove(world, principal);

            if (!ms.ok) {
                // Redo any moves we have already undone.
                redoMoves(world, i + 1, principal);

                return ms;
            }
        }

        return ms;
    }

    private void undoMoves(World w, int number, FreerailsPrincipal p) {
        for (int i = number; i >= 0; i--) {
            MoveStatus ms = moves.get(i).undoMove(w, p);

            if (!ms.ok) {
                throw new IllegalStateException(ms.message);
            }
        }
    }

    private void redoMoves(World w, int number, FreerailsPrincipal p) {
        for (int i = number; i < moves.size(); i++) {
            MoveStatus ms = moves.get(i).doMove(w, p);

            if (!ms.ok) {
                throw new IllegalStateException(ms.message);
            }
        }
    }

    /**
     * Subclasses may override this method to perform tests which pass or fail
     * depending on the combination of moves making up this composite move.
     */
    MoveStatus compositeTest(World w, FreerailsPrincipal p) {
        return MoveStatus.MOVE_OK;
    }

    /**
     * @return
     */
    public int size() {
        return moves.size();
    }

    @Override
    public final String toString() {
        String s = "";

        for (int i = 0; i < moves.size(); i++) {
            s += moves.get(i).toString() + ((i > 0) ? ", " : "");
        }

        return s;
    }
}