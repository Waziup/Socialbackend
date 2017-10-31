
Social Backend
==============

The Social Backend is a component of Waziup.
It is meant to provide a unified interface to social networks such as Facebook, Twitter, SMS...



Requirements
---

First, you should have installed:

- Docker 

- MongoDB


Running the social backend API in a docker container
---


To create the image based on the Dockerfile of the API 

```
 docker build -t <IMAGE_NAME> .

```
Exemple :


```
docker build -t  waziupbackend .

```

To start a container based on the image : 

```
docker run  -d  <IMAGE_NAME>
```

Exemple : 

```
docker run -d  waziupbackend
```


MongoDB installation and launch
---

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



#WhatsApp notification parameters (You need to be registered on https://www.whatsmate.net/whatsapp-gateway-api.html  to get the parameters below)
INSTANCE_ID=<THE_INSTANCE_ID>
CLIENT_ID=<THE_SENDER_CLIENT_ID_ON_WHATSMATE>
CLIENT_SECRET=<THE_SENDER_CLIENT_SECRET_ON_WHATSMATE>
WA_GATEWAY_URL_INCOMPLETE=http://api.whatsmate.net/v3/whatsapp/single/text/message/


#SMS service parametersYou need to be registered on https://www.plivo.com/ to get the parameters)
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
curl  -H "Content-Type : application/json"  -X POST -d '{"user_id": "`<TWITTER_RECIPIENT>`", "channel":"twitter","message":"`<MESSAGE_TO_BE_SENT>`","username":"`<WAZIUP_USER_CONNECTED>`"} http://`<DOMAINE_OR_LOCAL_SERVER>`/api/v1/domains/waziup/socials
```

Example :

```
curl -H "Content-Type : application/json" -X POST -d '{"user_id": "gilbikelenter", "channel":"twitter","message":"Yibeogo Ouaga","username":"Pandaconstantin} http://172.17.0.2:9123/api/v1/domains/waziup/socials
```


- **Send a SMS message**

```
curl -H "Content-Type : application/json"  -X POST -d '{"user_id": "`<RECIPIENT_PHONE_NUMBER>`", "channel":"sms","message":"`<MESSAGE_TO_BE_SENT>`","username":"`<WAZIUP_USER_CONNECTED>`"} http://`<DOMAINE_OR_LOCAL_SERVER>`/api/v1/domains/waziup/socials
```

Example :
```
curl -H "Content-Type : application/json" -X POST -d '{"user_id": "+22678012589", "channel":"sms","message":"Yibeogo Ouaga","username":"Pandaconstantin} http://172.17.0.2:9123/api/v1/domains/waziup/socials
```

- **Retrieve notifications**

```
curl -H "Content-Type: application/json"  -X  GET   http://`<DOMAINE_OR_LOCAL_SERVER>`/api/v1/domains/waziup/socials

```
Example : 

```
curl -H "Content-Type: application/json"  -X  GET   http://172.17.0.2:9123/api/v1/domains/waziup/socials

```
- **Retrieve a specific notification**
```
curl -H "Content-Type: application/json"  -X  GET   http://`<DOMAINE_OR_LOCAL_SERVER>`/api/v1/domains/waziup/socials/<NOTIFICATION_ID>

```
Example : 

```
curl -H "Content-Type: application/json"  -X  GET   http://172.17.0.2:9123/api/v1/domains/waziup/socials/59f871cb40416d2aa1b80cac

```

-**Delete all the notifications**


```
curl -H "Content-Type: application/json"  -X  DELETE   http://`<DOMAINE_OR_LOCAL_SERVER>`/api/v1/domains/waziup/socials

```

Example : 

```
curl -H "Content-Type: application/json"  -X  DELETE   http://172.17.0.2:9123/api/v1/domains/waziup/socials

```


- **Delete a specific notification**

```
curl -H "Content-Type: application/json"  -X  DELETE   http://`<DOMAINE_OR_LOCAL_SERVER>`/api/v1/domains/waziup/socials/`<NOTIFICATION_ID>`

```
Example :

```
curl -H "Content-Type: application/json" -X DELETE  http://172.17.0.2:9123/api/v1/domains/waziup/socials/59f0da9f584ade1f320c8d4a

```

