FROM eclipse-temurin:17-alpine
RUN mkdir /opt/app
COPY build/install/questbot /opt/app

WORKDIR "/opt/app/bin/"
COPY bagpipes.mp3 /opt/app/bin/bagpipes.mp3


CMD ["/opt/app/bin/questbot"]
