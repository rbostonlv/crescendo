FROM openjdk:8-jre-alpine

# Only for testing access to various URLs
#RUN apk add --no-cache bash
#RUN apk add --no-cache curl

ARG artifactId=test
ARG version=0.0.1-SNAPSHOT
ARG JAR_FILE=$artifactId-$version.jar
ENV APP_HOME=/usr/local/$artifactId

WORKDIR $APP_HOME

COPY $artifactId/target/$JAR_FILE $APP_HOME/app.jar
#COPY deploy/startService.sh $APP_HOME/startService.sh
#RUN chmod +x $APP_HOME/startService.sh

# We don't use expose because we explicitly publish ports thru port mappings

#ENTRYPOINT ["./startService.sh"]
#CMD
CMD java -jar app.jar
