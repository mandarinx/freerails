/*
 * $Id$
 */

#ifndef __GAMEMAPVIEW_H__
#define __GAMEMAPVIEW_H__

#include "GameMainWindow.h"
#include "WorldMap.h"
#include "GuiEngine.h"
#include "MapField.h"

/*#include "TrackController.h"
  #include "StationController.h"
  #include "Track.h"
  #include "Station.h"*/

#include <paragui_types.h>
#include <pgthemewidget.h>
#include <pgrect.h>
#include <pgimage.h>
#include <pgscrollbar.h>

class GameMapView: public PG_ThemeWidget {

  public:
  
    enum MouseType {normal=0,
                    buildTrack=10, buildStation, buildSignal};
    /**  */
    GameMapView(GameMainWindow* parent, int x, int y, int w, int h, GuiEngine* _guiEngine);
    /**  */
    ~GameMapView();
    
    void setMouseType(MouseType type);

  private:
    GuiEngine* guiEngine;
    
    SDL_Surface* tilesImage;
    SDL_Surface* trackImage;
    
    PG_ScrollBar* verticalScrollBar;
    PG_ScrollBar* horizontalScrollBar;
    
    SDL_Surface* imageSurface;
    PG_Image* view;
    int mouseType;

    int mouseOldMapX;
    int mouseOldMapY;
    
    PG_Point viewPos;
    
    /* TrackController* trackcontroller;
       StationController* stationcontroller; */
    
    void getMapImage(SDL_Surface* surface, int offsetX, int offsetY, int x, int y);
    int getImagePos(int x, int y, MapField::FieldType type);
    int getRiverImagePos(int x, int y);
    int get3DImagePos(int x, int y, MapField::FieldType type);
    
    void regenerateTile(int x, int y); // x and y are the position of the tile on which the mouse is now.
    void showTrack(int x, int y,int tracktileX, int tracktileY);

    void eventMouseLeave();
    bool eventMouseButtonDown(const SDL_MouseButtonEvent* button);
    bool eventMouseMotion(const SDL_MouseMotionEvent* motion);

    bool eventScrollPos(int id, PG_Widget* widget,unsigned long data);
    bool eventScrollTrack(int id, PG_Widget* widget,unsigned long data);
    
    void moveXto(unsigned long data);
    void moveYto(unsigned long data);
    
    void redrawMap(int x, int y, int w, int h);

};

#endif
