#!/bin/bash

SCRIPT_DIR=`dirname $0`

SERVER=new.searchbrew.com
TARGET=/home/searchbrew
SERVICE_SERVER=searchbrew
SERVICE_UPDATER=searchbrew.updater
INIT_SERVER_CONF=searchbrew.conf
INIT_UPDATER_CONF=searchbrew.updater.conf
APACHE_CONF=searchbrew.com
USER=searchbrew
ES_DOWNLOAD=https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-1.0.1.deb
ES_FILE=elasticsearch-1.0.1.deb

cd $SCRIPT_DIR/../server
sbt clean stage
cd ..

cd $SCRIPT_DIR/../updater
sbt clean dist
cd ..

ssh $SERVER <<EOF
	sudo service $SERVICE_SERVER stop
	sudo service $SERVICE_UPDATER stop

	# install required
	# sudo add-apt-repository ppa:webupd8team/java
	sudo apt-get -y install apache2 unattended-upgrades oracle-java7-installer oracle-java7-set-default git xserver-xorg
	sudo a2enmod proxy_http
	sudo a2enmod rewrite

	if [ ! -f $ES_FILE ]; then
		wget $ES_DOWNLOAD
		sudo dpkg -i $ES_FILE
	fi
EOF

ssh $USER@$SERVER <<EOF
	mkdir -p $TARGET/logs
	mkdir -p $TARGET/git.repo
	mkdir -p $TARGET/server/dist
	mkdir -p $TARGET/updater/dist
	ln -s $TARGET/logs $TARGET/server/dist
	ln -s $TARGET/logs $TARGET/updater/dist
	ln -s $TARGET/git.repo $TARGET/updater/dist
EOF

rsync -v --recursive --delete --compress $SCRIPT_DIR/../server/target/universal/stage/ $USER@$SERVER:/$TARGET/server/dist/
rsync -v --recursive --delete --compress $SCRIPT_DIR/../updater/target/dist/ $USER@$SERVER:/$TARGET/updater/dist/

ssh $USER@$SERVER <<EOF
	ln -s $TARGET/logs $TARGET/server/dist
	ln -s $TARGET/logs $TARGET/updater/dist
	ln -s $TARGET/git.repo $TARGET/updater/dist
EOF

scp $SCRIPT_DIR/$INIT_SERVER_CONF $SERVER:.
scp $SCRIPT_DIR/$INIT_UPDATER_CONF $SERVER:.
scp $SCRIPT_DIR/$APACHE_CONF $SERVER:.

ssh $SERVER <<EOF
	sudo mv $INIT_SERVER_CONF /etc/init/
	sudo mv $INIT_UPDATER_CONF /etc/init/
	sudo mv $APACHE_CONF /etc/apache2/sites-available
	sudo a2ensite $APACHE_CONF
	sudo service apache2 reload
	sudo service elasticsearch start
	sudo service $SERVICE_SERVER start
	sudo service $SERVICE_UPDATER start
EOF