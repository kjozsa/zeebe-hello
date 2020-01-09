FROM adoptopenjdk/openjdk13
ADD target/zeebee-hello-0.0.1-SNAPSHOT.jar .
CMD java -jar zeebee-hello-0.0.1-SNAPSHOT.jar
