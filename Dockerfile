From openjdk:11
ENV APP_HOME /jfrog
RUN mkdir $APP_HOME
WORKDIR $APP_HOME
ADD . $APP_HOME
CMD ["./run.sh"]