#!/bin/bash
ssh searchbrew.com /bin/sh << EOF
	date
	grep "GET /search?q=" /var/log/apache2/searchbrew.com.access.log |\
		grep -v downnotifier |\
		tail -n 25 |\
		cut -d ' ' -f 1,4,7 |\
		tr -s ' ' '\t'
	echo "\nError"
	tail -n 5 /var/log/apache2/searchbrew.com.error.log
	echo "\nloadavg "
	cat /proc/loadavg
EOF
