/** $Id$
  */

#include "Engine.h"
#include <iostream>

Engine::Engine(WorldMap* _worldMap, Player* _player) {

  worldMap = _worldMap;
  isSingle = true;
  isClient = false;
  isServer = false;
  
  Init(_player);

  std::cerr << "engine(alone) inited" << std::endl;
}

Engine::Engine(WorldMap* _worldMap, Player* _player, Server* _server) {

  gameState = Initializing;

  worldMap = _worldMap;
  server=_server;
  isSingle = false;
  isClient = false;
  isServer = true;
  
  Init(_player);
  
   std::cerr << "engine(Server) inited" << std::endl;
}

Engine::Engine(Player* _player, Client* _client) {

  gameState = Initializing;

  worldMap = NULL;
  client=_client;
  isSingle = false;
  isClient = true;
  isServer = false;
  
  Init(_player);
  
   std::cerr << "engine(Client) inited" << std::endl;
}

void Engine::Init(Player* _player) {

  lastmsec = 0;
  frame = 0;
  gui2engine=new MessageQueue();
  engine2gui=new MessageQueue();

  controllerDispatcher = new ControllerDispatcher();
  
  controllerDispatcher->addController(new PlayerController());
  
  controllerDispatcher->getController(1)->addGameElement(_player);

  gameState = Waiting;

  gameCon=new GameController("default",1900,1,1);
}

Engine::~Engine() {

}

void Engine::sendMsg(Message* msg) {

  gui2engine->addMsg(msg);
}

bool Engine::haveMsg() {

  return engine2gui->hasMoreElements();
}

Message* Engine::getMsg() {

  return engine2gui->getMsg();
}

void Engine::checkNet() {
  if (isServer) {
      // Server.check();
  } else
  if (isClient) {
    //Client.check();
  }
}

void Engine::checkNext(int msec) {

  if (gameState==Running)
  {
    if ((msec-lastmsec)>10)
    { lastmsec=msec;
      frame++;
      process();
      std::cerr << frame << std::endl;
    }
  }
  while (gui2engine->hasMoreElements()) {
    Message* msg = gui2engine->getMsg();
    processMsg(msg);
    delete msg;
  }

}

void Engine::process() {

  std::cerr << ".";
  if(isServer) { }

}

void Engine::processMsg(Message* msg) {

  switch (msg->getType()) {
    case Message::addElement: addElementToGame(msg);
    case Message::stateOfGame: changeStateOfGame(msg);
  }
}

void Engine::addElementToGame(Message* msg) {

  GameElement* element = (GameElement *)msg->getData();
  Controller* elementController = controllerDispatcher->getController(element->getTypeID());
  if (isServer) {
    if (elementController->canBuildElement(element)) {
      elementController->addGameElement(element);
      Message* msg = new Message(Message::addElement, 0, element);
      SendAll(msg);
    }
  } else {
    elementController->addGameElement(element);
  }
}

void Engine::changeStateOfGame(Message* msg) {

  GameState state = *(GameState *)msg->getData();
  Message* Msg = new Message(Message::stateOfGame,0,&state);
  if (gameState==Waiting && state==Running)
  {
    gameState=state;
    SendAll(Msg);
  }
  if (gameState==Running && state==Pausing)
  {
    gameState=state;
    SendAll(Msg);
  }
  if (gameState==Pausing && state==Running)
  {
    gameState=state;
    SendAll(Msg);
  }
  if ((gameState==Pausing || gameState==Running) && state==Stopping)
  {
    gameState=state;
    SendAll(Msg);
  }
}

void Engine::SendAll(Message* msg) {

  if (isServer) {
    // Server
  } else {
    engine2gui->addMsg(msg);
  }
}