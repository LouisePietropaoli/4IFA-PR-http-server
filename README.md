4IFA-PR-http-server

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