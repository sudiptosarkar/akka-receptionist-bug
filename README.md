# Akka Receptionist Bug

## Akka Receptionist and messageAdapter bug

Steps to observe the problem:

* Run `org.coreops.akka.bug.Main`.
* Notice the log statements generated from line # 75 of [ProcessorParent](src/main/java/org/coreops/akka/bug/actors/ProcessorParent.java).
* Notice the log statements generated from line # 57 of [ProcessorParent](src/main/java/org/coreops/akka/bug/actors/ProcessorParent.java).
