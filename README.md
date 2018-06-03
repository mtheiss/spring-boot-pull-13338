# spring-boot-pull-13338
Demo project for Pull Request #13338

Create a docker container for terracotta server.
```
docker container run --rm -p 9410:9410 -p 9430:9430 terracotta/terracotta-server-oss:5.4.1
```

Configure url in src/main/resources/ehcache.xml

New feature can be simulated with setting jcache.beanclassloader in application.properties to either true or false.
