FROM eclipse-temurin:17-alpine
RUN mkdir /opt/app
COPY build/install/questbot /opt/app

WORKDIR "/opt/app/bin/"
CMD ["/opt/app/bin/questbot"]