# spring-boot-pull-13338
Demo project for Pull Request #13338

Create a docker container for terracotta server.
```
docker container run te --rm -p 9410:9410 -p 9430:9430 terracotta/terracotta-server-oss:5.4.1
```

Configure url in src/main/resources/ehcache.xml
