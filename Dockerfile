FROM debian:trixie

USER root
RUN apt update && apt install -y \
    openjdk-21-jdk \
    maven \
    unzip \
    wget \
    curl \
    telnet \
    iproute2 \
    && useradd -ms /bin/bash chepe

USER chepe
WORKDIR /home/chepe

# Descarga y descomprime Open Liberty
RUN wget https://public.dhe.ibm.com/ibmdl/export/pub/software/openliberty/runtime/release/25.0.0.8/openliberty-jakartaee10-25.0.0.8.zip \
    && unzip openliberty-jakartaee10-25.0.0.8.zip \
    && rm openliberty-jakartaee10-25.0.0.8.zip \
    && /home/chepe/wlp/bin/server create app

RUN wget -O /home/chepe/wlp/lib/postgresql-42.7.7.jar https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.7/postgresql-42.7.7.jar

# COPIAR POM y COMPILAR
COPY --chown=chepe:chepe pom.xml /home/chepe/app-source/pom.xml
WORKDIR /home/chepe/app-source
RUN mvn dependency:go-offline

COPY --chown=chepe:chepe src /home/chepe/app-source/src
RUN mvn package -DskipTests

# DESPLIEGUE
RUN cp target/*.war /home/chepe/wlp/usr/servers/app/dropins/
RUN cp src/main/liberty/config/server.xml /home/chepe/wlp/usr/servers/app/server.xml

EXPOSE 9080
CMD ["/home/chepe/wlp/bin/server", "run", "app"]

#COMANDOS PARA CORRER EL DOCKER
# docker build -t prn335-app:latest .
# docker run -d --name prn335_runtime_final -p 9080:9080 --network host prn335-app:latest
# docker start prn335_runtime_final