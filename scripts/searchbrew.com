server {
  server_name searchbrew.com;
  listen 80;

  location / {
    proxy_pass "http://127.0.0.1:9000";
  }
}