/*
 * $Id$
 */

//#include <stdio.h>
//#include <unistd.h>
//#include <iostream>

#include <qapplication.h>
#include <qlabel.h>
#include <qpixmap.h>
#include <qstring.h>

#include "Engine.h"
#include "GameApplication.h"
#include "GameMainWindow.h"
#include "singlegameoptiondialog.h"

GameApplication::GameApplication(int argc, char *argv[]) :
    BaseApplication(argc, argv)
{
  application = new QApplication(argc,argv);
  mW = 0l;
  splash = 0l;
}

GameApplication::~GameApplication()
{
  if(splash)
    delete splash;
}

int GameApplication::run()
{
  // Show the splash
  showSplash();
  application->processEvents();
  sleep(1);

  hideSplash();
  application->processEvents();

  // FIXME: should be done in constructor with parameters from argc, argv
  mW = new GameMainWindow(0, 0, 800, 600);

  // FIXME: versionstring for caption
  mW->setCaption("Freerails");

  //setMainWindow(mW);
  application->setMainWidget((QWidget*)mW->getWidget());
  application->processEvents();
  hideSplash();

  // Show dialog menu for game mode
  GameModeSelector::GameMode mode = mW->askGameMode();
  qDebug("mW->askGameMode() result=%i\n", (int)mode);

  // Show mainwindow
  application->processEvents();

  // If user wants to quit, then quit
  if(mode == GameModeSelector::Quit)
  {
    application->exit(0);
    exit(0);
  }

  #warning complete me
  QString tmp_name;
  int tmp_width, tmp_height;
  int status;
  SingleGameOptionDialog *sgoDlg = new SingleGameOptionDialog(mW);
  status = sgoDlg->exec();
  if (status == QDialog::Accepted)
  {
    tmp_name = sgoDlg->getName();
    tmp_width = sgoDlg->getWidth();
    tmp_height = sgoDlg->getHeight();
  }
  delete sgoDlg;

  if (status == QDialog::Accepted)
  {  
    initSingleGame(std::string(tmp_name), tmp_width, tmp_height, 0);

    engine = new Engine(worldMap, playerSelf);
    CHECK_PTR(engine);


    // Construct playfield (map, panel, buttons)
    mW->setEngine(engine);
    mW->constructPlayField();

    return application->exec();
  }
  return 1;
}

void GameApplication::showSplash()
{
  // Lots of code in this method is taken from Qt Designer's code
  splash = new QLabel(0, "Splash screen", Qt::WDestructiveClose |
      Qt::WStyle_Customize | Qt::WStyle_NoBorder | Qt::WX11BypassWM |
      Qt::WStyle_StaysOnTop);
  splash->setFrameStyle(QFrame::WinPanel | QFrame::Raised);
  // New, better and probably a bit smaller splashscreen picture wanted!
  splash->setPixmap(QPixmap(QString("data/graphics/ui/title.png")));
  splash->adjustSize();
  splash->move((QApplication::desktop()->width() - splash->width()) / 2,
      (QApplication::desktop()->height() - splash->height()) / 2);
  splash->show();
  splash->repaint(false);
  QApplication::flushX();
}

void GameApplication::hideSplash()
{
  if(splash)
    splash->hide(); // deleted splash (WDestructiveClose flag in splash!!)
}

// Obsolote, because we call directly application->setMainWidget()
// It crashed, too, but don't know why
void GameApplication::setMainWindow(GameMainWindow* mw)
{
  application->setMainWidget((QWidget*)mw->getWidget());
}
