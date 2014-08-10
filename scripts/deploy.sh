#!/bin/bash

SCRIPT_DIR=`dirname $0`

SERVER=new.searchbrew.com
TARGET=/home/searchbrew
SERVICE_SERVER=searchbrew
INIT_SERVER_CONF=searchbrew.conf
APACHE_CONF=searchbrew.com.conf
USER=searchbrew
NEWRELIC_PKG=newrelic-java-3.7.2.zip

cd $SCRIPT_DIR/../server
#sbt clean stage
cd ..

ssh $SERVER <<EOF
	sudo service $SERVICE_SERVER stop

	# if [ ! -f $TARGET ]; then
	# 	sudo adduser $USER
	# 	sudo mkdir $TARGET/.ssh
	# 	sudo cp ~/.ssh/authorized_keys $TARGET/.ssh/
	# 	sudo chown -R $USER:$USER $TARGET/.ssh
	# 	sudo sh -c "echo 'searchbrew.com' > /etc/hostname"
	# fi

	# install required
	sudo add-apt-repository ppa:webupd8team/java
	#sudo apt-get update
	sudo apt-get -y install apache2 unattended-upgrades oracle-java7-installer oracle-java7-set-default git unzip

	sudo sh <<EOF
		echo deb http://apt.newrelic.com/debian/ newrelic non-free > /etc/apt/sources.list.d/newrelic.list
		wget -O- https://download.newrelic.com/548C16BF.gpg | apt-key add -
		apt-get update
		apt-get install newrelic-sysmond
		#nrsysmond-config --set license_key=
		/etc/init.d/newrelic-sysmond start
	EOF

	sudo a2enmod proxy_http
	sudo a2enmod rewrite
EOF

ssh $USER@$SERVER <<EOF
	mkdir -p $TARGET/logs
	mkdir -p $TARGET/server/dist
EOF

rsync -v --recursive --exclude git.repo --delete --compress $SCRIPT_DIR/../server/target/universal/stage/ $USER@$SERVER:/$TARGET/server/dist/

ssh $USER@$SERVER <<EOF
	ln -s $TARGET/logs $TARGET/server/dist
EOF

scp $SCRIPT_DIR/$INIT_SERVER_CONF $SERVER:.
scp $SCRIPT_DIR/$APACHE_CONF $SERVER:.
scp $SCRIPT_DIR/$NEWRELIC_PKG $SERVER:.

ssh $SERVER <<EOF
	sudo mv $INIT_SERVER_CONF /etc/init/
	sudo mv $APACHE_CONF /etc/apache2/sites-available
	sudo a2dissite 000-default
	sudo a2ensite $APACHE_CONF
	sudo service apache2 reload
	sudo service $SERVICE_SERVER start

	unzip $NEWRELIC_PKG -d $TARGET/server/newrelic
EOF
