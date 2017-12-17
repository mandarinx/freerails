/*
 * Created on 23-Mar-2003
 *
 */
package freerails.world.top;

import freerails.world.common.Money;
import freerails.world.train.EngineType;
import freerails.world.train.WagonType;

/**
 * This class adds hard coded wagon and engine types to the World. Later the
 * wagon and engine types will be defined in an xml file, but this will do for
 * now.
 *
 * @author Luke
 */
public class WagonAndEngineTypesFactory {
    public void addTypesToWorld(World w) {
        // Wagon types
        WagonType[] wagonTypes = new WagonType[]{
                new WagonType("Mail", WagonType.MAIL),
                new WagonType("Passengers", WagonType.PASSENGER),
                new WagonType("Livestock", WagonType.FAST_FREIGHT),
                new WagonType("Coffee", WagonType.SLOW_FREIGHT),
                new WagonType("Wood", WagonType.BULK_FREIGHT),};

        for (WagonType wagonType : wagonTypes) {
            w.add(SKEY.WAGON_TYPES, wagonType);
        }

        // Engine types
        EngineType[] engineTypes = new EngineType[]{
                new EngineType("Grasshopper", 1000, new Money(10000), 10,
                        new Money(100)),
                new EngineType("Norris", 1000, new Money(10000), 15, new Money(
                        100)),};

        for (EngineType engineType : engineTypes) {
            w.add(SKEY.ENGINE_TYPES, engineType);
        }
    }
}