#!/bin/bash

mvn clean package
java -jar target/ShuffleWood-1.0-SNAPSHOT.jar
