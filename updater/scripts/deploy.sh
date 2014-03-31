#!/bin/bash

SCRIPT_DIR=`dirname $0`

SERVER=searchbrew.com
TARGET=/home/searchbrew
SERVICE_UPDATER=searchbrew.updater
INIT_UPDATER_CONF=searchbrew.updater.conf
USER=searchbrew

cd $SCRIPT_DIR/..
sbt clean dist

ssh $SERVER <<EOF
	sudo service $SERVICE_UPDATER stop

	# install required
	sudo add-apt-repository ppa:webupd8team/java
	sudo apt-get -y install unattended-upgrades oracle-java7-installer oracle-java7-set-default git
EOF

ssh $USER@$SERVER <<EOF
	mkdir -p $TARGET/logs
	mkdir -p $TARGET/git.repo
	mkdir -p $TARGET/updater/dist
EOF

rsync -v --recursive --delete --compress $SCRIPT_DIR/../target/dist/ $USER@$SERVER:/$TARGET/updater/dist/

ssh $USER@$SERVER <<EOF
	ln -s $TARGET/logs $TARGET/updater
	ln -s $TARGET/git.repo $TARGET/updater
EOF

scp $SCRIPT_DIR/$INIT_UPDATER_CONF $SERVER:.

ssh $SERVER <<EOF
	sudo mv $INIT_UPDATER_CONF /etc/init/
	sudo service $SERVICE_UPDATER start
EOF
