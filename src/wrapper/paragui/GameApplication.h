/*
 * $Id$
 */

#ifndef __GAMEAPPLICATION_H__
#define __GAMEAPPLICATION_H__
#include "BaseApplication.h"
#include "GameMainWindow.h"
#include "GameModeSelectDialog.h"
#include "GameMapView.h"
#include "GameNetView.h"
#include "GamePanel.h"
#include "SDL.h"

#include "Message.h"


#include <pgapplication.h>
#include <pggradientwidget.h>

#include <unistd.h>

#define WRAPPERTYPE_PARAGUI 2 

class GameApplication : public BaseApplication {

public:
    GameApplication(int argc, char *argv[]);
    /**  */
    ~GameApplication();

    bool initScreen(int x, int y, int w, int h);
    void setCaption(const char *title);
    int run();
    int wrapperType() { return WRAPPERTYPE_PARAGUI; };
    void showSplash();
    void hideSplash();
    void setMainWindow(GameMainWindow* mw);

private:
    struct PG_Application2* pGlobalApp;
    Uint32 screenFlags;
    int screenDepth;
    PG_GradientWidget* splash;
    GameMapView* mapView;
    GameNetView* netView;
    GamePanel* panel;
};

class PG_Application2: public PG_Application {

public:
  void setEngine(Engine* _engine) {
    engine=_engine;
  };
  virtual void eventIdle();
private:
  Engine* engine;

};

#endif