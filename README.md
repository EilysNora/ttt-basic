# Tic-Tac-Toe Web App

A Tic-Tac-Toe game playable against the computer, built as a Java web application:
- Server: Java Servlets (Jakarta EE), see `src/main/java/com/tictactoe/web/`
- Client: plain HTML/CSS/JS, see `src/main/webapp/`
- API description: see `API.txt`

The computer always plays the first free cell, scanning left to right, top to bottom.

## Requirements

- JDK 17 or newer
- Apache Maven
- A servlet container supporting Jakarta EE (`jakarta.servlet` namespace) — **Tomcat 10 or newer**
  (Tomcat 9 and earlier use the old `javax.servlet` namespace and will NOT work with this WAR)

If your machine doesn't already have a working JDK/Tomcat, see [No JDK/Tomcat installed?](#no-jdktomcat-installed) below.

## 1. Build

From the project root:

```cmd
mvn clean package
```

This produces `target\tictactoe.war`.

## 2. Deploy

Copy the WAR into your Tomcat's `webapps` folder:

```cmd
copy /Y "target\tictactoe.war" "<CATALINA_HOME>\webapps\tictactoe.war"
```

Replace `<CATALINA_HOME>` with your Tomcat installation path.

## 3. Start Tomcat

```cmd
set "CATALINA_HOME=<CATALINA_HOME>"
"%CATALINA_HOME%\bin\startup.bat"
```

Wait a couple of seconds for Tomcat to finish deploying, then check `<CATALINA_HOME>\logs\catalina.<date>.log` if you want to confirm it started cleanly.

## 4. Play

Open in a browser:

```
http://localhost:8080/tictactoe/
```

Click **New Game** and click a cell to play. Game state is kept per browser session, so each new session/browser gets its own game.

## 5. Stop Tomcat

```cmd
"%CATALINA_HOME%\bin\shutdown.bat"
```

## No JDK/Tomcat installed?

If you don't have a working JDK 17+ or Tomcat 10+ handy, portable (no-install, no admin rights needed) copies were set up for this project under:

```
C:\Users\Tuyet Phuong\.jdks\jdk-17.0.19+10
C:\Users\Tuyet Phuong\.jdks\apache-tomcat-10.1.56
```

Use them by setting the environment variables before running the steps above:

```cmd
set "JAVA_HOME=C:\Users\Tuyet Phuong\.jdks\jdk-17.0.19+10"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "CATALINA_HOME=C:\Users\Tuyet Phuong\.jdks\apache-tomcat-10.1.56"
```

Then run the build/deploy/start commands from steps 1-3 as-is (they already reference `%CATALINA_HOME%`).

## Project structure

```
src/main/java/com/tictactoe/          Game logic (Board, Squareboard, Player, Computerplayer, GameConfig, ...)
src/main/java/com/tictactoe/web/      Servlets exposing the game over HTTP (NewGameServlet, MoveServlet, StateServlet)
src/main/webapp/                      Client: index.html, app.js, WEB-INF/web.xml
API.txt                               API reference (paths, request/response bodies)
```

> Note: `com.tictactoe.HttpServer`, `HttpClient`, `NioServer`, `Client`, `Server`, etc. at the top level of
> `com.tictactoe` are standalone console/socket exercises from earlier assignments and are unrelated to this
> web app; they are not used when running the WAR.
