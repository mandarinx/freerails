/*
 * $Id$
 */

#ifndef __GAMEMAPVIEW_H__
#define __GAMEMAPVIEW_H__

#include "GameMainWindow.h"
#include "WorldMap.h"
#include "MapField.h"

#include <paragui_types.h>
#include <pggradientwidget.h>
#include <pgrect.h>
#include <pgimage.h>
#include <pgwidgetlist.h>

class GameMapView: public PG_GradientWidget {

  public:
  
    enum MouseType {normal=0,
                    buildTrack=10, buildStation, buildSignal};
    /**  */
    GameMapView(GameMainWindow* parent, int x, int y, int w, int h, WorldMap* _worldMap);
    /**  */
    ~GameMapView();
    
    void setMouseType(MouseType type);

  private:
    WorldMap* worldMap;
    
    SDL_Surface* tilesImage;
    SDL_Surface* trackImage;
    SDL_Surface* imageSurface;
    PG_WidgetList* WidgetList;
    PG_Image* view;
    int mouseType;
    int mouseOldX;
    int mouseOldY;
    
    void getMapImage(SDL_Surface* surface,int x, int y);
    int getImagePos(int x, int y, MapField::FieldType type);
    int getRiverImagePos(int x, int y);
    int get3DImagePos(int x, int y, MapField::FieldType type);
    
    bool isOnNewTile(const SDL_MouseMotionEvent* motion);

    void eventMouseLeave();
    bool eventMouseButtonDown(const SDL_MouseButtonEvent* button);
    bool eventMouseMotion(const SDL_MouseMotionEvent* motion);

};

#endif