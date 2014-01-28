#!/bin/sh

CURRENT=`pwd`

sudo security authorizationdb write system.privilege.taskport < $CURRENT/system.privilege.taskport.plist
sudo security authorizationdb write com.apple.dt.instruments.process.analysis <  $CURRENT/com.apple.dt.instruments.process.analysis.plist
sudo security authorizationdb write com.apple.dt.instruments.process.kill <  $CURRENT/com.apple.dt.instruments.process.kill.plist