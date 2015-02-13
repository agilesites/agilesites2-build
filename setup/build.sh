#!/bin/bash
export BASE=$HOME/Local/AgileSitesInstaller
mkdir -p $BASE/repo
cp -Rvf bin $BASE
if ! test -d $BASE/demo/.git
then git clone https://github.com/agilesites/agilesites2-demo $BASE/demo
fi
cd $BASE/demo
git pull origin master
sh $BASE/bin/mkagilesites.sh $(/usr/libexec/java_home) $BASE/demo/agilesites.sh
cd -
ant



