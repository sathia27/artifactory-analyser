#!/bin/bash
cd $APP_HOME
echo "Running gradle"
./gradlew clean
./gradlew build
echo "Gradle run"
./gradlew bootRun