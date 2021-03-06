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

import freerails.model.ModelConstants;
import freerails.util.Vec2D;

import java.awt.*;
import java.io.File;

/**
 * Important constants
 */
public final class ClientConstants {

    /**
     *
     */
    public static final String GRAPHICS_PATH = "/freerails/client/graphics/";
    /**
     *
     */
    public static final int MODE_START_NETWORK_GAME = 0;
    public static final int MODE_JOIN_NETWORK_GAME = 1;
    public static final int MODE_SERVER_ONLY = 2;

    /**
     *
     */
    private static final String PATH_SOUNDS = "/freerails/client/sounds/";

    /**
     *
     */
    private static final String PATH_VIEWS = "/freerails/client/view/";

    /**
     *
     */
    public static final String ICONS_FOLDER_NAME = "icons";

    /**
     *
     */
    private static final String PATH_ICONS = GRAPHICS_PATH + ICONS_FOLDER_NAME + '/';

    // Sound resource locations

    /**
     *
     */
    public static final String SOUND_CASH = PATH_SOUNDS + "cash.wav";

    /**
     *
     */
    public static final String SOUND_BUILD_TRACK = PATH_SOUNDS + "buildtrack.wav";

    /**
     *
     */
    public static final String SOUND_REMOVE_TRACK = PATH_SOUNDS + "removetrack.wav";

    /**
     *
     */
    public static final String SOUND_WHISTLE = PATH_SOUNDS + "whistle.wav";

    /**
     *
     */
    public static final String SOUND_TRAIN_CRASH = PATH_SOUNDS + "traincrash.wav";

    // View file locations

    /**
     *
     */
    public static final String VIEW_BALANCE_SHEET = PATH_VIEWS + "balance_sheet.htm";

    /**
     *
     */
    public static final String VIEW_BROKER = PATH_VIEWS + "Broker_Screen.html";

    /**
     *
     */
    public static final String VIEW_INCOME_STATEMENT = PATH_VIEWS + "income_statement.htm";

    /**
     *
     */
    public static final String VIEW_GAME_CONTROLS = PATH_VIEWS + "game_controls.html";

    /**
     *
     */
    public static final String VIEW_ABOUT = PATH_VIEWS + "about.htm";

    /**
     *
     */
    public static final String VIEW_HOW_TO_PLAY = PATH_VIEWS + "how_to_play.htm";

    /**
     *
     */
    public static final String ICON_FILE_EXTENSION = ".png";

    // Icon file locations

    /**
     *
     */
    public static final String ICON_TERRAIN_INFO = PATH_ICONS + "terrain_info.png";

    /**
     *
     */
    public static final String ICON_NEW_TRACK = PATH_ICONS + "track_new.png";

    /**
     *
     */
    public static final String ICON_TRAIN_LIST = PATH_ICONS + "train_list.png";

    /**
     *
     */
    public static final String ICON_STATION_LIST = PATH_ICONS + "station_info.png";

    /**
     *
     */
    public static final String ICON_ERROR = PATH_ICONS + "error.gif";

    /**
     *
     */
    public static final String ICON_WARNING = PATH_ICONS + "warning.gif";

    /**
     *
     */
    public static final String ICON_INFO = PATH_ICONS + "info.gif";

    /**
     *
     */
    public static final String GRAPHIC_ARROW_SELECTED = GRAPHICS_PATH + "selected_arrow.png";

    /**
     *
     */
    public static final String GRAPHIC_ARROW_DESELECTED = GRAPHICS_PATH + "deselected_arrow.png";
    public static final int FULL_SCREEN = 0;
    public static final int WINDOWED_MODE = 1;
    public static final int BIG_DOT_WIDTH = 12;
    public static final int SMALL_DOT_WIDTH = 6;
    public static final boolean LIMIT_FRAME_RATE = false;
    public static final int TARGET_FPS = 40;
    public static final int WAGON_IMAGE_HEIGHT = 10;
    public static final int SPACING = 3;
    public static final int MAX_HEIGHT = 5 * (WAGON_IMAGE_HEIGHT + SPACING);
    public static final int MAX_WIDTH = 80;
    /**
     * server sleeping time in ms (1000/SERVERUPDATE is the frame rate)
     */
    public static final int SERVERUPDATE = 50;

    public static final Vec2D TILE_SIZE = new Vec2D(ModelConstants.TILE_SIZE, ModelConstants.TILE_SIZE);
    /**
     * Affects scroll direction and scroll speed relative to the cursor.
     * Examples:
     *
     * 1 := grab map, move 1:1
     *
     * -2 := invert mouse, scroll twice as fast
     */
    public static final int MAP_SCROLL_SPEED = 2;
    public static final Font USER_MESSAGE_FONT = new Font("Arial", 0, 12);
    public static final Font LARGE_MESSAGE_FONT = new Font("Arial", 0, 24);

    public static final File USER_HOME_FOLDER = new File(System.getProperty("user.home"));
    public static final File FREERAILS_USER_FOLDER = new File(USER_HOME_FOLDER, "Freerails user data");
    public static final File OPTIONS_FILE = new File(FREERAILS_USER_FOLDER, "Freerails.options");


    private ClientConstants() {
    }
}
