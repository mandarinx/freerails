/*
 * DialogueBoxController.java
 *
 * Created on 29 December 2002, 02:05
 */

package jfreerails.client.view;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.border.LineBorder;

import jfreerails.client.common.MyGlassPanel;
import jfreerails.client.renderer.ViewLists;
import jfreerails.move.ChangeProductionAtEngineShopMove;
import jfreerails.move.Move;
import jfreerails.move.MoveStatus;
import jfreerails.world.station.ProductionAtEngineShop;
import jfreerails.world.station.StationModel;
import jfreerails.world.top.KEY;
import jfreerails.world.top.NonNullElements;
import jfreerails.world.top.World;
import jfreerails.world.top.WorldIterator;
import jfreerails.world.train.Schedule;
import jfreerails.world.train.TrainModel;

/**	This class is responsible for displaying dialogue boxes, adding borders to them as appropriate, and 
 *  returning focus to the last focus owner after a dialogue box has been closed.  Currently dialogue boxes
 * are not separate windows.  Instead, they are drawn on the modal layer of the main JFrames LayerPlane.  This 
 * allows dialogue boxes with transparent regions to be used. 
 *
 * @author  lindsal8 
 */
public class DialogueBoxController {

	private SelectEngineJPanel selectEngine;
	private MyGlassPanel glassPanel;
	private NewsPaperJPanel newspaper;
	private SelectWagonsJPanel selectWagons;
	private TrainScheduleJPanel trainSchedule;
        private GameControlsJPanel showControls;

	private World w;

	private Component defaultFocusOwner = null;

	private LineBorder defaultBorder =
		new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3);

	/** Use this ActionListener to close a dialogue without performing any other action. */
	private ActionListener closeCurrentDialogue = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			closeContent();
		}
	};

	/** Creates new DialogueBoxController */
	public DialogueBoxController(JFrame frame, World world, ViewLists vl) {
		this.w = world;

		//Setup glass panel..    	    	    
		glassPanel = new MyGlassPanel();
		frame.getLayeredPane().add(glassPanel, JLayeredPane.MODAL_LAYER);

		//We need to resize the glass panel when its parent resizes.
		frame.getLayeredPane().addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent evt) {
				glassPanel.setSize(glassPanel.getParent().getSize());
				glassPanel.validate();
			}
		});
		glassPanel.setVisible(false);

		//Setup the various dialogue boxes.
                
                // setup the 'show controls' dialogue
                showControls = new GameControlsJPanel();
                showControls.setup(w, vl, this.closeCurrentDialogue);

		//Set up train orders dialogue
		trainSchedule = new TrainScheduleJPanel();
		trainSchedule.setup(w, vl, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int trainNumber = trainSchedule.getTrainNumber();
				Schedule schedule = trainSchedule.getNewSchedule();
				TrainModel train = (TrainModel)w.get(KEY.TRAINS,trainNumber);
				train.setSchedule(schedule);
				closeContent();
			}

		});

		
		//Set up select engine dialogue.
		selectEngine = new SelectEngineJPanel(this);
		selectEngine.setup(w, vl, new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				showSelectWagons();
			}

		});
		newspaper = new NewsPaperJPanel();
		newspaper.setup(w, vl, closeCurrentDialogue);

		selectWagons = new SelectWagonsJPanel();
		selectWagons.setup(w, vl, new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				WorldIterator wi = new NonNullElements(KEY.STATIONS, w);
				if (wi.next()) {

					StationModel station = (StationModel) wi.getElement();

					ProductionAtEngineShop before = station.getProduction();
					int engineType = selectEngine.getEngineType();
					int[] wagonTypes = selectWagons.getWagons();
					ProductionAtEngineShop after =
						new ProductionAtEngineShop(engineType, wagonTypes);

					Move m = new ChangeProductionAtEngineShopMove(before, after, wi.getIndex());
					MoveStatus ms = m.doMove(w);
					if (!ms.ok) {
						System.out.println(
							"Couldn't change production at station: " + ms.toString());
					} else {
						System.out.println("Production at station changed.");
					}
				}
				closeContent();
			}

		});
	}

	public void showNewspaper(String headline) {
		newspaper.setHeadline(headline);
		showContent(newspaper);
	}

	public void showTrainOrders() {
		WorldIterator wi = new NonNullElements(KEY.TRAINS, w);
		if (!wi.next()) {
			System.out.println("Cannot show train orders since there are no trains!");
		} else {
			trainSchedule.displayFirst();
			showContent(trainSchedule);
		}
	}

	public void showSelectEngine() {
		WorldIterator wi = new NonNullElements(KEY.STATIONS, w);
		if (!wi.next()) {
			System.out.println("Can't build train since there are no stations");
		} else {
			System.out.println("showSelectEngine()");
			showContent(selectEngine);
		}
	}
        
        public void showGameControls(){
            System.out.println("showGameControls");
            showContent(this.showControls);
        }

	public void showSelectWagons() {
		System.out.println("showSelectWagons");
		showContent(selectWagons);
	}

	public void showContent(JComponent component) {
		component.setBorder(defaultBorder);
		//		if(!glassPanel.isVisible()){					
		//			KeyboardFocusManager keyboardFocusManager =
		//			KeyboardFocusManager.getCurrentKeyboardFocusManager();
		//			lastFocusOwner = keyboardFocusManager.getFocusOwner();
		//		}
		glassPanel.showContent(component);
		glassPanel.validate();
		glassPanel.setVisible(true);
	}
	public void closeContent() {
		glassPanel.setVisible(false);
		if (null != defaultFocusOwner) {
			defaultFocusOwner.requestFocus();
		}
	}

	public Component getDefaultFocusOwner() {
		return defaultFocusOwner;
	}

	public void setDefaultFocusOwner(Component defaultFocusOwner) {
		this.defaultFocusOwner = defaultFocusOwner;
	}

        
}
