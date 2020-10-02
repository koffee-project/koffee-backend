FROM openjdk:8-jre-alpine@sha256:b2ad93b079b1495488cc01375de799c402d45086015a120c105ea00e1be0fd52

ENV APPLICATION_USER ktor
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY ./build/libs/koffee-backend.jar /app/koffee-backend.jar
WORKDIR /app

CMD [ \
    "java", \
    "-server", \
    "-XX:+UnlockExperimentalVMOptions", \
    "-XX:+UseCGroupMemoryLimitForHeap", \
    "-XX:InitialRAMFraction=2", \
    "-XX:MinRAMFraction=2", \
    "-XX:MaxRAMFraction=2", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=100", \
    "-XX:+UseStringDeduplication", \
    "-jar", \
    "koffee-backend.jar" \
]
