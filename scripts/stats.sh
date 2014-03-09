#!/bin/bash
ssh searchbrew.com /bin/sh << EOF
	date
	grep "GET /search?q=" /var/log/apache2/searchbrew.com.access.log |\
		grep -v downnotifier |\
		tail -n 10 |\
		cut -d ' ' -f 1,4,7 |\
		tr -s ' ' '\t'
	echo "\nError"
	tail -n 5 /var/log/apache2/searchbrew.com.error.log
	echo "\n Server"
	tail -n 5 /home/searchbrew/logs/server.log | cut -b 1-100
	echo "\n Updater"
	tail -n 5 /home/searchbrew/logs/updater.log
	echo "\nloadavg "
	cat /proc/loadavg

	service searchbrew status
	service searchbrew.updater status
	sudo service elasticsearch status
EOF
