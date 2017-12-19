/*
 * Created on 05-Jan-2005
 *
 */
package freerails.controller;

import freerails.client.common.ModelRootImpl;
import freerails.server.MapFixtureFactory2;
import freerails.world.common.ImPoint;
import freerails.world.common.Step;
import freerails.world.top.World;
import junit.framework.TestCase;

import java.util.Arrays;

import static freerails.world.common.Step.*;

/**
 */
public class PathOnTrackFinderTest extends TestCase {

    World w;

    TrackMoveProducer producer;

    PathOnTrackFinder pathFinder;

    StationBuilder stationBuilder;

    BuildTrackStrategy bts;

    /**
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        w = MapFixtureFactory2.getCopy();
        MoveExecutor me = new SimpleMoveExecutor(w, 0);
        ModelRoot mr = new ModelRootImpl();
        producer = new TrackMoveProducer(me, w, mr);
        pathFinder = new PathOnTrackFinder(w);
        stationBuilder = new StationBuilder(me);
        bts = BuildTrackStrategy.getDefault(w);
    }

    /**
     *
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     *
     */
    public void testPathAsVectors1() {
        Step[] path = {EAST, EAST, SOUTH_EAST};
        ImPoint start = new ImPoint(5, 5);
        ImPoint end = Step.move(start, path);
        producer.buildTrack(start, path);
        try {
            pathFinder.setupSearch(start, end);
            pathFinder.search(-1);
            assertEquals(IncrementalPathFinder.PATH_FOUND, pathFinder
                    .getStatus());
            Step[] pathFound = pathFinder.pathAsVectors();
            assertTrue(Arrays.equals(path, pathFound));
        } catch (PathNotFoundException e) {
            fail();
        }
    }

    /**
     *
     */
    public void testPathAsVectors2() {
        Step[] path = {EAST, EAST, SOUTH_EAST, EAST, EAST, NORTH_EAST};
        ImPoint start = new ImPoint(5, 5);
        ImPoint end = Step.move(start, path);
        producer.buildTrack(start, path);
        try {
            pathFinder.setupSearch(start, end);
            pathFinder.search(-1);
            assertEquals(IncrementalPathFinder.PATH_FOUND, pathFinder
                    .getStatus());
            Step[] pathFound = pathFinder.pathAsVectors();
            assertTrue(Arrays.equals(path, pathFound));
        } catch (PathNotFoundException e) {
            fail();
        }
    }

    /**
     *
     */
    public void testSetupSearch() {
        Step[] path = {EAST, EAST, SOUTH_EAST};
        ImPoint start = new ImPoint(5, 5);
        ImPoint end = Step.move(start, path);
        producer.buildTrack(start, path);
        try {
            pathFinder.setupSearch(start, end);
        } catch (PathNotFoundException e) {
            fail("Track at both of the points so no excepton should be thrown");
        }
        try {
            pathFinder.setupSearch(start, new ImPoint(10, 10));
            fail("No track at one of the points so an excepton should be thrown");
        } catch (PathNotFoundException e) {

        }
        try {
            pathFinder.setupSearch(new ImPoint(10, 10), end);
            fail("No track at one of the points so an excepton should be thrown");
        } catch (PathNotFoundException e) {

        }
    }

}
