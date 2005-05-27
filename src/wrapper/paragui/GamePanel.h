/*
 * $Id$
 */

#ifndef __GAMEPANEL_H__
#define __GAMEPANEL_H__

#include "GameMainWindow.h"
#include "TerrainInfoPane.h"
#include "TerrainBuildPane.h"

#include <pgthemewidget.h>
#include <pgradiobutton.h>
#include <pgbutton.h>
#include <pgimage.h>
#include <pgrect.h>
#include <pgwidgetlist.h>

#include "GuiEngine.h"
#include "Message.h"
#include "Station.h"
#include "Train.h"

class GamePanel: public PG_ThemeWidget {//, public PG_EventObject {

  public:
    enum WidgetID {ViewStations = 10000, ViewTrains, BuildTrack, BuildStation, BuildTrain };
    /**  */
    GamePanel(GameMainWindow* parent, int x, int y, int w, int h, GuiEngine* _engine, MapHelper* mapHelper);
    /**  */
    ~GamePanel();
    
    void addStation(Station* station);
    void addTrain(Train* train);
    
    bool eventMouseMotion(const SDL_MouseMotionEvent* motion) { return true; };
    bool eventMouseButtonDown(const SDL_MouseButtonEvent* button) { return true; };
    bool eventMouseButtonUp(const SDL_MouseButtonEvent* button) { return true; };

  protected:
    bool handleViewButtonClick(PG_Button* button);
    bool handleGameButtonClick(PG_Button* button);
    
  private:
  
    void releaseAllBuildButtons(PG_Button* button);
    void releaseAllViewButtons(PG_Button* button);
    void releaseAllViews();

    PG_Button* terrainViewButton;
    PG_Button* buildViewButton;
    PG_Button* stationViewButton;
    PG_Button* trainViewButton;
    
    PG_WidgetList* stationList;
    PG_WidgetList* trainList;
    
    int stationListSize;
    int trainListSize;

    PG_Button* pauseButton;
    PG_Button* quitButton;
    
    PG_RadioButton* stationSignal;
    PG_RadioButton* stationSmall;
    PG_RadioButton* stationMedium;
    PG_RadioButton* stationBig;
    
    GuiEngine* guiEngine;
    GameMainWindow* my_parent;
    
    TerrainInfoPane* infoPane;
    TerrainBuildPane* buildPane;
};

#endif
