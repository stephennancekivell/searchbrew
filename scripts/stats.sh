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
	echo "\nServer"
	tail -n 5 /home/searchbrew/logs/application.log | cut -b 1-100
	echo "\nloadavg "
	cat /proc/loadavg

	service searchbrew status
EOF
