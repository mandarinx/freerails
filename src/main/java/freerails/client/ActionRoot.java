/*
 * FreeRails
 * Copyright (C) 2000-2018 The FreeRails Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package freerails.client;

import freerails.client.model.ServerControlModel;
import freerails.client.model.StationBuildModel;
import freerails.client.renderer.RendererRoot;
import freerails.client.view.DialogueBoxController;
import freerails.move.StationBuilder;
import freerails.controller.TrackMoveProducer;
import freerails.model.world.UnmodifiableWorld;

import javax.swing.*;

// TODO what is this good for, probably can move to somewhere else
/**
 * Provides access to Actions change the game state and the GUI.
 */
public class ActionRoot {

    private final ServerControlModel serverControls;
    private DialogueBoxController dialogueBoxController;
    private StationBuildModel stationBuildModel;
    private TrackMoveProducer trackMoveProducer;

    /**
     * @param serverControls
     */
    public ActionRoot(ServerControlModel serverControls) {
        this.serverControls = serverControls;
    }

    /**
     * @return
     */
    public Action getBuildTrainDialogAction() {
        return new BuildTrainDialogAction(this);
    }

    /**
     * @return
     */
    public DialogueBoxController getDialogueBoxController() {
        return dialogueBoxController;
    }

    /**
     * @param dialogueBoxController
     */
    public void setDialogueBoxController(DialogueBoxController dialogueBoxController) {
        this.dialogueBoxController = dialogueBoxController;
    }

    /**
     * @return
     */
    public ServerControlModel getServerControls() {
        return serverControls;
    }

    /**
     * @return
     */
    public StationBuildModel getStationBuildModel() {
        return stationBuildModel;
    }

    /**
     * @return
     */
    public TrackMoveProducer getTrackMoveProducer() {
        return trackMoveProducer;
    }

    /**
     * Call this method when a new game is started or a game is loaded.
     */
    public void setup(ModelRootImpl modelRoot, RendererRoot rendererRoot) {
        serverControls.setup(modelRoot, dialogueBoxController);

        UnmodifiableWorld world = modelRoot.getWorld();

        if (world.getTrackTypes().size() > 0) {
            trackMoveProducer = new TrackMoveProducer(modelRoot);
            stationBuildModel = new StationBuildModel(new StationBuilder(modelRoot), rendererRoot, modelRoot);
        }
    }

}