/*
 * $Id$
 */

#ifndef __GAMEAPPLICATION_H__
#define __GAMEAPPLICATION_H__

#include "BaseApplication.h"
#include "GameMap.h"
#include "GamePanel.h"
#include "GameMenuBar.h"

#include <qlayout.h>


class QApplication;
class QLabel;
class QHBoxLayout;
class QVBoxLayout;
class GameMainWindow;
class GameMap;
class GamePanel;
class GameMenuBar;

#define WRAPPERTYPE_QT 2

/** Constucts QApplication */
class GameApplication : public BaseApplication {

public:
    /** Initializes GameApplication 
      * @param argc count of command-line parameters
      * @param argv array of command-line parameter
      */
    GameApplication(int argc, char *argv[]);
    /**  */
    ~GameApplication();
    /** Runs application (application->exec()) */
    int run();
    /** Returns number, that shows type of used wrapper
      * In this case, returns 2 (WRAPPERTYPE_QT)
      */
    int wrapperType() { return WRAPPERTYPE_QT; };
    /** Constructs and shows splash screen */
    void showSplash();
    /** Hides splash screen */
    void hideSplash();
    /** Sets main window to be @ref mw
      * Main window will be main widget. When it's destroyed, application
      * will exit
      */
    void setMainWindow(GameMainWindow* mw);

private:
	GameMainWindow* mW;
	GameMap* map;
	GamePanel* panel;
	GameMenuBar* menu;
    QApplication* application;
    QLabel* splash;
	QVBoxLayout* vertLayout;
	QHBoxLayout* horLayout;
};

#endif
