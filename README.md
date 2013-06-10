tl;dr
=====

This demo app demonstrates how to provide a seamless UX with chunked responses and XHR2 (a fallback to spinning/iframe is also implemented)

installation instructions:
==========================

* install and launch redis

* play run

* point your browser to 
http://127.0.0.1:9000/assets/xhr.html
http://127.0.0.1:9000/assets/spinning.html

* send real time messages
```redis 127.0.0.1:6379> PUBLISH "stream" "user a likes user b's item "```

* check the various outpots (stdout, javascript console and the page)