
Social Backend
==============

The Social Backend is a component of Waziup.
It is meant to provide a unified interface to social networks such as Facebook, Twitter, SMS...
**Note  : The instructions below concern a local test environment. Later will publish the containerized version of the API based on Swarm.**


Install
-------

First, you should have installed:
- JDK 1.8
- Wildfly 10
- Apache Maven 3.X.X

```
sudo apt-get install openjdk-8-jre
```
Download Wildfly from http://www.wildfly.org/downloads/
Unzip it and start:
```
cd bin
./standalone -Djboss.http.port=<YOUR_PORT>
```
Where `<YOUR_PORT>` is the port on which the Wildfly instance is running.

Run with:
```
mvn clean install wildfly:deploy
```


Test
-----

- ~~~~**Send a Twitter message**~~~~

```
curl  -H "Content-Type : application/json"  -X POST -d '{"user_id": "`<TWITTER_RECIPIENT>`", "channel":"twitter","message":"`<MESSAGE_TO_BE_SENT>`","username":"`<WAZIUP_USER_CONNECTED>`"} http://`<DOMAINE_OR_LOCAL_SERVER>`/SocialBackend/socials
```

Exemple :
```
curl -H "Content-Type : application/json" -X POST -d '{"user_id": "gilbikelenter", "channel":"twitter","message":"Yibeogo Ouaga","username":"Pandaconstantin} http://localhost:8080/SocialBackend/socials
```


- ~~~~**Send a SMS message**~~~~

```
curl -H "Content-Type : application/json"  -X POST -d '{"user_id": "`<RECIPIENT_PHONE_NUMBER>`", "channel":"sms","message":"`<MESSAGE_TO_BE_SENT>`","username":"`<WAZIUP_USER_CONNECTED>`"} http://`<DOMAINE_OR_LOCAL_SERVER>`/SocialBackend/socials
```

Exemple :
```
curl -H "Content-Type : application/json" -X POST -d '{"user_id": "+22678012589", "channel":"sms","message":"Yibeogo Ouaga","username":"Pandaconstantin} http://localhost:8080/SocialBackend/socials
```

- ~~~~**Retrieve notifications**~~~~

```
curl -H "Content-Type: application/json"  -X  GET   http://`<DOMAINE_OR_LOCAL_SERVER>`/SocialBackend/socials

```
Exemple : 

```
curl -H "Content-Type: application/json"  -X  GET   http://localhost:8080/SocialBackend/socials

```

- ~~~~**Delete all the notifications**~~~~


```
curl -H "Content-Type: application/json"  -X  DELETE   http://`<DOMAINE_OR_LOCAL_SERVER>`/SocialBackend/socials

```

Exemple : 

```
curl -H "Content-Type: application/json"  -X  DELETE   http://localhost:8080/SocialBackend/socials

```


- ~~~~**Delete a specific notification**~~~~

```
curl -H "Content-Type: application/json"  -X  DELETE   http://`<DOMAINE_OR_LOCAL_SERVER>`/SocialBackend/socials/`<NOTIFICATION_ID>`

```
Exemple :

```
curl -H "Content-Type: application/json" -X DELETE  http://localhost:8080/SocialAPI/socials/59f0da9f584ade1f320c8d4a

```

