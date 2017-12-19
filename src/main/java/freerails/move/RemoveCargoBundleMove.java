/*
 * Created on 26-May-2003
 *
 */
package freerails.move;

import freerails.world.cargo.ImmutableCargoBundle;
import freerails.world.player.FreerailsPrincipal;
import freerails.world.top.KEY;

/**
 * This move removes a cargo bundle from the cargo bundle list.
 *
 */
public class RemoveCargoBundleMove extends RemoveItemFromListMove {
    private static final long serialVersionUID = 3762247522239723316L;

    /**
     *
     * @param i
     * @param item
     * @param p
     */
    public RemoveCargoBundleMove(int i, ImmutableCargoBundle item,
                                 FreerailsPrincipal p) {
        super(KEY.CARGO_BUNDLES, i, item, p);
    }
}