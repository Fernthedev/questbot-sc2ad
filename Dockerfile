FROM eclipse-temurin:17-alpine
RUN mkdir /opt/app
COPY build/install/questbot /opt/app

EXPOSE 443/tcp
EXPOSE 443/udp

WORKDIR "/opt/app/bin/"
CMD ["/opt/app/bin/questbot"]