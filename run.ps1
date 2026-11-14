# starts the app with the local JDK 17 + Maven
$env:JAVA_HOME = "$env:USERPROFILE\devtools\jdk-17.0.21+9"
$env:Path = "$env:JAVA_HOME\bin;$env:USERPROFILE\devtools\apache-maven-3.9.9\bin;$env:Path"
mvn spring-boot:run
