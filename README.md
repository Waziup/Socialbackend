
Social Backend
==============

The Social Backend is a component of Waziup.
It is meant to provide a unified interface to social networks such as Facebook, Twitter, SMS...


Install
-------

First, you should have installed:
- JDK 1.8
- Wildfly 10

```
sudo apt-get install openjdk-8-jre
```
Download Wildfly from http://www.wildfly.org/downloads/
Unzip it and start:
```
cd bin
./standalone
```

Run with:
```
mvn clean install wildfly:deploy
```


Test
----

API can be tested with:
```
curl -X POST -d "user_id=gilbikelenter&channel=twitter&message=Yibeogo Ouaga" http://localhost:8080/SocialBackend/socials
```



