
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
- MongoDB

- **JDK installation**

The first thing to do is installing the Java Developer Kit on your system. You can find the full documentation [here](http://openjdk.java.net/install/) .
Below the instruction to install it in Debian distributions environment ( Ubuntu). 
```
sudo apt-get install openjdk-8-jre
```

- ** Wildfly installation and launch **

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

- ** MongoDB installation and launch**

Download  MongoDB  from [here](https://www.mongodb.com/download-center?jmp=nav#community)  and install .

Start it with :

```
./mongod

```

**Note** :  Create a database named **waziup** and a collection named **Notification**


Configurations
---

The API source contains a properties file ( **src/main/resources/parameters.properties**) to set a set of parameters such as the twitter sender authentication keys ,  the sender phone number, the database URI and collections.

~~~properties
#
# Twitter notification parameters
OAuthConsumerKey=<OAuth Consumer Key>
OAuthConsumerSecret=<OAuth Consumer Secret>
OAuthAccessToken=<OAuth Access Token>
OAuthAccessTokenSecret=<OAuth Access Token Secret>


#Facebook notification  parameters



#WhatsApp notification parameters



#SMS service parameters
api_key=<API_KEY>
api_token=<API_TOKEN>
telephonesrc=<SENDER_PHONE_NUMBER>

#Databases connection parameters
#Local database instance
#mongoclouduri=mongodb://localhost:27017/
#cloud based database
mongoclouduri=mongodb://admin:oracle@ds157584.mlab.com:57584/waziup 
mongoclouddatabase=waziup
mongocloudcollection=Notification
~~~

Note: Change the key **mongoclouduri** value to point to your MongoDB database




Test
-----

- **Send a Twitter message**

```
curl  -H "Content-Type : application/json"  -X POST -d '{"user_id": "`<TWITTER_RECIPIENT>`", "channel":"twitter","message":"`<MESSAGE_TO_BE_SENT>`","username":"`<WAZIUP_USER_CONNECTED>`"} http://`<DOMAINE_OR_LOCAL_SERVER>`/SocialBackend/socials
```

Example :
```
curl -H "Content-Type : application/json" -X POST -d '{"user_id": "gilbikelenter", "channel":"twitter","message":"Yibeogo Ouaga","username":"Pandaconstantin} http://localhost:8080/SocialBackend/socials
```


- **Send a SMS message**

```
curl -H "Content-Type : application/json"  -X POST -d '{"user_id": "`<RECIPIENT_PHONE_NUMBER>`", "channel":"sms","message":"`<MESSAGE_TO_BE_SENT>`","username":"`<WAZIUP_USER_CONNECTED>`"} http://`<DOMAINE_OR_LOCAL_SERVER>`/SocialBackend/socials
```

Example :
```
curl -H "Content-Type : application/json" -X POST -d '{"user_id": "+22678012589", "channel":"sms","message":"Yibeogo Ouaga","username":"Pandaconstantin} http://localhost:8080/SocialBackend/socials
```

- **Retrieve notifications**

```
curl -H "Content-Type: application/json"  -X  GET   http://`<DOMAINE_OR_LOCAL_SERVER>`/SocialBackend/socials

```
Example : 

```
curl -H "Content-Type: application/json"  -X  GET   http://localhost:8080/SocialBackend/socials

```

-**Delete all the notifications**


```
curl -H "Content-Type: application/json"  -X  DELETE   http://`<DOMAINE_OR_LOCAL_SERVER>`/SocialBackend/socials

```

Example : 

```
curl -H "Content-Type: application/json"  -X  DELETE   http://localhost:8080/SocialBackend/socials

```


- **Delete a specific notification**

```
curl -H "Content-Type: application/json"  -X  DELETE   http://`<DOMAINE_OR_LOCAL_SERVER>`/SocialBackend/socials/`<NOTIFICATION_ID>`

```
Example :

```
curl -H "Content-Type: application/json" -X DELETE  http://localhost:8080/SocialAPI/socials/59f0da9f584ade1f320c8d4a

```

