/*
 * Copyright (C) 2003 Luke Lindsay
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/*
 * Created on 25-Aug-2003
 *
  */
package org.railz.move;

import org.railz.world.common.*;
import org.railz.world.player.*;
import org.railz.world.top.*;
import org.railz.world.train.*;

/**
 * This Move can change a train's engine and wagons.
 *
 * @author Luke Lindsay
 *
 */
public class ChangeTrainMove extends ChangeItemInListMove {
    private ChangeTrainMove(int id, TrainModel before, TrainModel
	    after, FreerailsPrincipal p) {
	super(KEY.TRAINS, id, before, after, p);
	System.out.println("creating ChangeTrainMove for train " + id + ": " +
		getBefore() + ", " + ", " + getAfter());
    }

    /**
     * Change trains scheduled stop
     */
    public static ChangeTrainMove generateMove(int id,
	    FreerailsPrincipal p, TrainModel before, ScheduleIterator
	    newScheduleIterator, GameTime t) {
	TrainModel after = new TrainModel(before, newScheduleIterator, t);
	return  new ChangeTrainMove(id, before, after, p);
    }

    /**
     * Change trains state
     */
    public static ChangeTrainMove generateMove(int id, FreerailsPrincipal p,
	    TrainModel before, int newState, GameTime now) {
	TrainModel after = new TrainModel(before, now, newState);
	return new ChangeTrainMove(id, before, after, p);
    }

    /**
     * Change trains priority
     */
    public static ChangeTrainMove generatePriorityMove(int id,
	    FreerailsPrincipal p, TrainModel before, int newPriority) {
	TrainModel after = before.setPriority(newPriority);
	return new ChangeTrainMove(id, before, after, p);
    }
    /**
     * Change path to destination.
     */
    public static ChangeTrainMove generateMove(int id, FreerailsPrincipal p,
	    TrainModel before, TrainPath pathToDestination, GameTime now) {
	TrainModel after = new TrainModel(before, pathToDestination, now);
	return new ChangeTrainMove(id, before, after, p);
    }

    /**
     * Change engine and wagons
     */
    public static ChangeTrainMove generateMove(int id, FreerailsPrincipal p,
	    TrainModel before, int newEngine, int[] newWagons) {
        TrainModel after = before.getNewInstance(newEngine, newWagons);

        return new ChangeTrainMove(id, before, after, p);
    }

    /**
     * Generate a move to set the trains position at a specific time.
     */
    public static ChangeTrainMove generateMove(int id, FreerailsPrincipal p,
	    TrainPath tp, GameTime t, ReadOnlyWorld w) {
	TrainModel tm = (TrainModel) w.get(KEY.TRAINS, id, p);
	int speed = ((EngineType) w.get(KEY.ENGINE_TYPES, tm.getEngineType(),
		Player.AUTHORITATIVE)).getMaxSpeed();

	TrainModel newTm = tm.setPosition(tp, t, speed);
	return new ChangeTrainMove(id, tm, newTm, p);
    }

    public static ChangeTrainMove generateBlockedMove(ObjectKey ok,
	    TrainModel tm, GameTime t, boolean blocked) {
	TrainModel newTm = tm.setBlocked(blocked, t);
	return new ChangeTrainMove(ok.index, tm, newTm, ok.principal);
    }

    public MoveStatus doMove(World w, FreerailsPrincipal p) {
	return super.doMove(w, p);
    }
}
