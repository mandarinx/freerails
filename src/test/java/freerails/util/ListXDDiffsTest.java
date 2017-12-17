/*
 * Created on 27-Jul-2005
 *
 */
package freerails.util;

import java.util.SortedMap;
import java.util.TreeMap;

public class ListXDDiffsTest extends ListXDTest {

    enum listid {
        list1, list2, list3
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        SortedMap<ListKey, Object> map = new TreeMap<>();
        list1d = new List1DDiff<>(map, list1d, listid.list1);
        list2d = new List2DDiff<>(map, list2d, listid.list2);
        list3d = new List3DDiff<>(map, list3d, listid.list3);
    }

}