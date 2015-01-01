server {
  server_name searchbrew.com;
  listen 8080;

  location / {
    proxy_pass "http://127.0.0.1:9000";
  }
}