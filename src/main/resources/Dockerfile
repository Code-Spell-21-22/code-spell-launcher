FROM ubuntu:20.04
ARG codeUniqueId
COPY ~/Code_Spell/Launcher/Temp/$codeUniqueId /code_repository
WORKDIR /code_repository/code-spell-code-executor/
RUN ["mvn", "package"]
WORKDIR /code_repository/code-spell-code-executor/target/
ENTRYPOINT ["java", "-jar", "code-spell-code-executor-1.0-SNAPSHOT.jar"]