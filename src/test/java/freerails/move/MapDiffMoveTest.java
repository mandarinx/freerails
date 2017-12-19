package freerails.move;

import freerails.world.top.World;
import freerails.world.top.WorldDiffs;
import freerails.world.track.FreerailsTile;

/**
 * JUnit test for MapDiffMove.
 *
 */
public class MapDiffMoveTest extends AbstractMoveTestCase {

    /**
     *
     */
    @Override
    public void testMove() {
        World world2 = this.getWorld();
        WorldDiffs worldDiff = new WorldDiffs(world2);

        FreerailsTile tile = (FreerailsTile) world2.getTile(2, 2);
        assertNotNull(tile);
        assertEquals(tile, worldDiff.getTile(2, 2));

        FreerailsTile newTile = FreerailsTile.getInstance(999);
        worldDiff.setTile(3, 5, newTile);
        assertEquals(newTile, worldDiff.getTile(3, 5));

        Move m = new WorldDiffMove(world2, worldDiff, WorldDiffMove.Cause.Other);
        this.assertDoMoveIsOk(m);
        this.assertUndoMoveIsOk(m);
        this.assertDoThenUndoLeavesWorldUnchanged(m);
        this.assertSurvivesSerialisation(m);
    }
}