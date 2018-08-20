[![CircleCI](https://circleci.com/gh/aneecebanoun/SpringDataEmbeddedMongoDemo.svg?style=svg)](https://circleci.com/gh/aneecebanoun/SpringDataEmbeddedMongoDemo)

[![codebeat badge](https://codebeat.co/badges/29b096c4-e814-4d80-9940-32d276bd0aef)](https://codebeat.co/projects/github-com-aneecebanoun-springdataembeddedmongodemo-master)   [![Codacy Badge](https://api.codacy.com/project/badge/Grade/49deee0e144a4c94a0dd723fbf64767e)](https://www.codacy.com/app/java2ee5/SpringDataEmbeddedMongoDemo?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=aneecebanoun/SpringDataEmbeddedMongoDemo&amp;utm_campaign=Badge_Grade)


Spring boot taking enterprise software development into another level, removing a lots of boiler plate infrastructure oriented code by taking care of curated dependencies with auto-configuration straight out of the box; for some one getting used to traditional spring development will ask where everything gone? where are spring configuration file(s) or class(es)? we still could create these files to suite the business requirements if we want to but spring boot make it so easy to put specific configuration in application.properties and SPRING INITIALIZR provide these files bundled and jarred to pick the configuration you specify in application.properties; there is a lot more to spring boot and it is promising.

Spring Data development using spring boot got so easy and spring data was and still the most appealing especially with the new kid in the block (NOSQL Data Stores).

There are many Embedded Relational Databases like h2 or MariaDB or even emdedded derby shipped with java which are good for testing and development; for NOSQL Emedded MongoDB is an option alleviate the need to install and bloat your OS with yet another hammer; dockerized Mongo or Cassandra or Redis to name few still an option yet!

The focus of this exhibit mainly on using Spring Data with nosql Data store, so I did not pay much attention to develop fancy widget rich views rather I used String.format to make the view looks like CLI which does not scale well on mobile browser but it scales on desktop as in the screenshot above.

I believe spring boot, spring data and nosql technologies are a step in the right direction to bridge the traditional gap between Object Oriented Modelling Vs Relational Modelling which a lots of ORM tools (including spring data) trying to solve, with added bonus of allowing asynchronous & reactive programming goes deep into the bone structure of the back end which is a long waited and anticipated by java 8 streams and lambdas making such concept official in java 9.
