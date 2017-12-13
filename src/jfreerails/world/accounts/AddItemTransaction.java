/*
 * Created on 02-Aug-2003
 *
 */
package jfreerails.world.accounts;

import jfreerails.world.common.GameTime;

/**
 * This Transaction represents the charge/credit for buying/selling an asset.
 *
 * @author Luke Lindsay
 *
 */
public class AddItemTransaction extends Transaction {
    /** Constants to specify the subcategory. */
    public static final int TRACK = 1;
    public static final int LAND = 2;
    public static final int BUILDING = 3;
    public static final int ROLLING_STOCK = 4;

    /** For example track. */
    private final int subcategory;

    /** For example, standard track. */
    private final int type;

    /** For example, 4 tiles. */
    private final int quantity;

    public final int getCategory() {
	return CATEGORY_CAPITAL_EXPENSE;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof AddItemTransaction) {
            AddItemTransaction test = (AddItemTransaction)obj;

            return super.equals(test) && this.type == test.type &&
            this.quantity == test.quantity;
        } else {
            return false;
        }
    }

    /**
     * @param amount Positive amounts represent a credit, negative is a debit.
     */
    public AddItemTransaction(GameTime time, int subcategory, int type, int
	    quantity, long amount) {
	super(time, amount);
        this.subcategory = subcategory;
        this.type = type;
        this.quantity = quantity;
    }

    public final int getSubcategory() {
        return subcategory;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getType() {
        return type;
    }

    public String toString() {
	return "AddItemTransaction: subcategory=" + subcategory + ", type=" +
	    type + ", quantity=" + quantity + ", value=" + getValue() +
	    ", category=" + getCategory();
    }
}
