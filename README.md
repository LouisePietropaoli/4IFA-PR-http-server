4IFA-PR-http-server

Help tuto :
https://dev.to/mateuszjarzyna/build-your-own-http-server-in-java-in-less-than-one-hour-only-get-method-2k02


Après avoir démarré le serveur :


Test dans un terminal avec telnet :

telnet localhost 3000
Trying ::1...
Connected to localhost.
Escape character is '^]'.
GET / HTTP/1.1
Host: localhost

HTTP/1.1 200 OK
ContentType: text/html

<b>It works!</b>

Connection closed by foreign host.


Test depuis le navigateur :

http://localhost:3000/


Test depuis Postman :
GET localhost:3000