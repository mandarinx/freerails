/** $Id$
  * Vertical MenuBar class
  */

#include "GameMenuBar.h"
#include "GameMenuBar.moc"

#include <qpixmap.h>
#include <stdio.h>


GameMenuBar::GameMenuBar(QWidget* parent, const char* name)
           : QWidget(parent, name) {
	setFixedWidth(50);
	initDone = false;
	init();
}

GameMenuBar::GameMenuBar(GameMainWindow* parent, const char* name)
           : QWidget(parent->getWidget(), name){
	setFixedWidth(50);
	initDone = false;
	init();
}


GameMenuBar::~GameMenuBar() {
	if(initDone) {
		delete bBuildRail;
		delete bBuildStation;
		delete bBulldoze;
		delete bPurchaseTrain;
		delete bMapOverview;
		delete bControllPanel;
		delete bOptions;
		delete bStockMarket;
		delete layout;
	}
}

void GameMenuBar::init() {
	layout = new QVBoxLayout(this);

	// TEMP just to show the pix ;-)
	bBuildRail = new QPushButton(QPixmap("./data/graphics/ui/buttons/rail.png"),0, this);
	bBuildStation = new QPushButton("Station", this);
	bBulldoze = new QPushButton("Bulldoze", this);
	bMapOverview = new QPushButton("Map", this);
	bPurchaseTrain = new QPushButton("Train", this);
	bStockMarket = new QPushButton("Market", this);
	bControllPanel = new QPushButton("Controll", this);
	bOptions = new QPushButton("Options", this);
	layout->addWidget(bBuildRail);
	layout->addWidget(bBuildStation);
	layout->addWidget(bBulldoze);
	layout->addWidget(bMapOverview);
	layout->addWidget(bPurchaseTrain);
	layout->addWidget(bStockMarket);
	layout->addWidget(bControllPanel);
	layout->addWidget(bOptions);

	initDone = true;
}

void GameMenuBar::Show() {
	show();
	bBuildRail->show();
	bBuildStation->show();
	bBulldoze->show();
	bMapOverview->show();
	bPurchaseTrain->show();
	bStockMarket->show();
	bControllPanel->show();
	bOptions->show();
}

