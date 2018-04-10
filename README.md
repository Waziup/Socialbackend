
Social Backend
==============

The Social Backend is a component of Waziup.
It is meant to provide a unified interface to social networks such as Facebook, Twitter, SMS...

Install
-------

To create the image based on the Dockerfile of the API:

```
docker build -t waziup/socials .
docker run -it -P 9123:9123 waziup/socials
```

The API source contains a properties file ( **src/main/resources/parameters.properties**) to set a set of parameters such as the twitter sender authentication keys ,  the sender phone number, the database URI and collections.

Usage
-----

**Send a Facebook page feed message**

```
curl -v -H "Content-Type: application/json" -d '{"channel":"facebook","message":"thisistestfromsocialsservicewaziup 4"}' http://localhost:9123/api/v1/domains/waziup/socials
```

In case of duplicate message, social services will provide the following HTTP code:`500 Internal Server Error` and in case of succesfful delivery of message:`202 Accepted`.
 
**Send a Twitter message**

```
curl -H "Content-Type : application/json" -X POST -d '{"user_id": "test", "channel":"twitter","message":"Test", "username":"Pandaconstantin"}' http://localhost:9123/api/v1/domains/waziup/socials
```


**Send a SMS message**

```
curl -H "Content-Type : application/json" -X POST -d '{"user_id": "+22678012589", "channel":"sms","message":"Yibeogo Ouaga","username":"Pandaconstantin"}'  http://localhost:9123/api/v1/domains/waziup/socials
```


**Send a voice message**

```
curl -H "Content-Type : application/json" -X POST -d '{"user_id": "+22678012589", "channel":"voice","message":"Yibeogo Ouaga","username":"Pandaconstantin"}' http://localhost:9123/api/v1/domains/waziup/socials
```

**Retrieve notifications**

```
curl -H "Content-Type: application/json" -X GET http://localhost:9123/api/v1/domains/waziup/socials
```

**Retrieve a specific notification**

```
curl -H "Content-Type: application/json" -X GET http://localhost:9123/api/v1/domains/waziup/socials/59f871cb40416d2aa1b80cac
```

**Delete all the notifications**

```
curl -H "Content-Type: application/json" -X DELETE http://localhost:9123/api/v1/domains/waziup/socials
```

**Delete a specific notification**

```
curl -H "Content-Type: application/json" -X DELETE http://localhost:9123/api/v1/domains/waziup/socials/59f0da9f584ade1f320c8d4a
```

