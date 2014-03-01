<VirtualHost *:80>
    ServerName new.searchbrew.com
    ServerAdmin webmaster@localhost
    RewriteEngine on

    ErrorLog ${APACHE_LOG_DIR}/searchbrew.com.error.log

    # Possible values include: debug, info, notice, warn, error, crit,
    # alert, emerg.
    LogLevel warn

    CustomLog ${APACHE_LOG_DIR}/searchbrew.com.access.log combined

    RewriteRule ^/(.*)$ http://127.0.0.1:9000/$1 [P]
    ProxyPassReverse / http://127.0.0.1:9000
</VirtualHost>
