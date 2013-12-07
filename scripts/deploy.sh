#!/bin/bash

SCRIPT_DIR=`dirname $0`

SERVER=searchbrew.com
TARGET=/home/searchbrew/server
SERVICE=searchbrew
INIT_CONF=searchbrew.conf
APACHE_CONF=searchbrew.com
USER=searchbrew

cd $SCRIPT_DIR/..
play compile stage

ssh $SERVER sudo service $SERVICE stop

rsync -v --recursive --delete --compress $SCRIPT_DIR/../target/universal/stage $USER@$SERVER:/$TARGET/dist/

scp $SCRIPT_DIR/$INIT_CONF $SERVER:.
scp $SCRIPT_DIR/$APACHE_CONF $SERVER:.

ssh $SERVER <<EOF
	sudo mv $INIT_CONF /etc/init/
	sudo mv $APACHE_CONF /etc/apache2/sites-available
	sudo service apache2 reload
	sudo service $SERVICE start
EOF