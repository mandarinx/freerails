/*
 * Created on 19-Aug-2005
 *
 */
package freerails.world.track;

import freerails.server.MapFixtureFactory2;
import freerails.util.Utils;
import freerails.world.common.Step;
import freerails.world.top.SKEY;
import freerails.world.top.World;
import junit.framework.TestCase;

/**
 *
 */
public class TrackPieceImplTest extends TestCase {

    private World w;

    /**
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        w = MapFixtureFactory2.getCopy();
    }

    /**
     *
     */
    public void testEqualsObject() {
        TrackConfiguration tc1 = TrackConfiguration.getFlatInstance(Step.NORTH);

        TrackRule rule0 = (TrackRule) w.get(SKEY.TRACK_RULES, 0);
        TrackRule rule4 = (TrackRule) w.get(SKEY.TRACK_RULES, 4);

        TrackPieceImpl tp1 = new TrackPieceImpl(tc1, rule0, 0, 0);
        assertEquals(tp1, tp1);
        TrackPieceImpl tp2 = new TrackPieceImpl(tc1, rule4, 0, 4);
        assertFalse(tp1.equals(tp2));
        TrackPieceImpl tp1Clone = (TrackPieceImpl) Utils
                .cloneBySerialisation(tp1);
        assertEquals(tp1, tp1Clone);
    }

}
