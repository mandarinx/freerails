package jfreerails.client.common;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JToggleButton;

/**
 * Provides a mapping from a set of ButtonModels or a ComboBoxModel to a set of
 * Actions.
 * To use with a set of buttons, call elements() to obtain the set of
 * ButtonModels to apply to the buttons, and add each button in the enumeration
 * to a ButtonGroup. Listeners should listen for changes to the model and not to
 * any events from UI components, although UI components may call setAction() in
 * order to receive property change updates and to set icons etc.
 */
public class ActionAdapter extends DefaultComboBoxModel {
    /**
     * The set of actions which each button / menu item correspond to
     */
    private Action[] actions;

    /**
     * The set of MappedButtonModels corresponding to the actions
     */
    private Vector buttonModels;

    /**
     * @param actions An array of the actions to be used. The ComboBoxModel
     * objects are taken from the NAME property of the Action. The ButtonModel
     * icons are obtained from the SMALL_ICON property.
     */
    public ActionAdapter(Action[] actions) {
	super();
	this.actions = actions;
	buttonModels = new Vector();
	for (int i = 0; i < actions.length; i++) {
	    buttonModels.add(new MappedButtonModel(actions[i]));
	    addElement(actions[i].getValue(Action.NAME));
	}
    }

    /**
     * @return an enumeration of Action
     */
    public Enumeration getActions() {
	return new Enumeration() {
	    private int i = 0;

	    public boolean hasMoreElements() {
		return (i < actions.length);
	    }

	    public Object nextElement() {
		return actions[i++];
	    }
	};
    }
    
    /**
     * @return an enumeration of MappedButtonModel
     */
    public Enumeration getButtonModels() {
	return buttonModels.elements();
    }

    /**
     * @param item The NAME of the Action selected
     */
    public void setSelectedItem(Object item) {
	super.setSelectedItem(item);
	for (int i = 0; i < buttonModels.size(); i++) {
	    MappedButtonModel bm = (MappedButtonModel) buttonModels.get(i);
	    if (bm.actionName.equals(item)) {
		bm.setSelected(true);
	    }
	}
	for (int i = 0; i < actions.length; i++) {
	    if (actions[i].getValue(Action.NAME).equals(item)) {
		actions[i].actionPerformed(new ActionEvent(this,
		ActionEvent.ACTION_PERFORMED,
		(String) actions[i].getValue(Action.ACTION_COMMAND_KEY)));
	    }
	}
    }

    public class MappedButtonModel extends JToggleButton.ToggleButtonModel {
	/**
	 * The NAME of the Action to which this ButtonModel is mapped
	 */
	public final String actionName;
	
	public MappedButtonModel(Action action) {
	    actionName = (String) action.getValue(Action.NAME);
	}
	
	public void setSelected(boolean b) {
	    if (isSelected() != b) {
		super.setSelected(b);
		if (b)
		    ActionAdapter.this.setSelectedItem(actionName);
	    }
	}
    }
}
