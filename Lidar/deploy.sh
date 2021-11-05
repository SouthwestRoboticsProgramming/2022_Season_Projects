#!/bin/bash

RPI_ADDRESS=pi@robopi.local
JAR_NAME=Lidar-1.0-SNAPSHOT.jar

echo -e "\n--- Building ---\n"
mvn clean package

echo -e "\n--- Transferring ---\n"
scp target/$JAR_NAME $RPI_ADDRESS:~/

echo -e "\n--- Running ---\n"
ssh $RPI_ADDRESS java -jar $JAR_NAME
