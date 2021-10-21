# 4IFA-PR-http-server
---

## Configurations

Serveur : localhost

Port : 3001

---

## URLs valides :

***Depuis le navigateur :***

* (GET) http://localhost:3001 : renvoie le fichier index.html
* (GET) http://localhost:3001/<nom_de_laressource_sur_le_serveur> : renvoie le fichier (html, json, txt, jpeg,...) indiqué (ou page d'erreur si non trouvé)

***Depuis POSTMAN :***
* GET http://localhost:3001 : renvoie le fichier index.html
* GET http://localhost:3001/<nom_de_laressource_sur_le_serveur> : renvoie le fichier (html, json, txt, jpeg,...) indiqué (ou page d'erreur si non trouvé)
* DELETE http://localhost:3001/<nom_de_laressource_sur_le_serveur> : supprime le fichier (html, json, txt, jpeg,...) indiqué sur le serveur
* PUT http://localhost:3001/<nom_de_laressource_sur_le_serveur> [body=binary --> télécharger le fichier modifié] : met à jour le fichier (html, json, txt) indiqué sur le serveur. Le nom du fichier sur le serveur n'est pas modifié, seul le contenu du fichier est mis à jour.
* POST http://localhost:3001 : ajoute le fichier (html, json, txt, jpeg,...) sur le serveur avec un nom unique. 


---

## Test du serveur 

***Après avoir démarré le serveur :***

* dans un terminal avec telnet :
```telnet localhost 3001
Trying ::1...
Connected to localhost.
Escape character is '^]'.
GET / HTTP/1.1
Host: localhostHTTP/1.1 200 OK
ContentType: text/htmlIt works!Connection closed by foreign host.
```
* depuis le navigateur :

`http://localhost:3000/`

* depuis Postman :
`GET localhost:3001`


---
