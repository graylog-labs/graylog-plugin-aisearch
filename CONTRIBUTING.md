# Contributing to ai-search

Official (and somewhat generic) documentation on writing plugins is available at https://docs.graylog.org/docs/plugins. This guide
offers copy-and-paste commands that are specific to this plugin, to hopefully save you time. 😀  

## Configuring Project Directory

```bash
cd $HOME/Projects
git clone git@github.com:graylog-labs/ai-search.git
(check out preferred branch)
```

⚠️ When using Docker Desktop on Mac, configure `$HOME/Projects/ai-search` as a virtual file share in Settings|Resources|File sharing. (this isn't necessary on Windows)


## Running Development Container

Start devtanker with default settings and directory mounts:
```bash
docker run -d --name devtanker -v $HOME/Projects/ai-search:/home/runtime/graylog-plugin-aisearch -v devtanker:/data -e GRAYLOG_DATANODE_INSECURE_STARTUP="true" -e GRAYLOG_DATANODE_PASSWORD_SECRET="somepasswordpeppersomepasswordpeppersomepasswordpeppersomepasswordpepper" -e GRAYLOG_HTTP_EXTERNAL_URI="http://localhost:9000/" -e GRAYLOG_PASSWORD_SECRET="somepasswordpeppersomepasswordpeppersomepasswordpeppersomepasswordpepper" -e GRAYLOG_ROOT_PASSWORD_SHA2="8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918" -e TZ=UTC -p 5044:5044/tcp -p 5140:5140/tcp -p 5140:5140/udp -p 9000:9000/tcp -p 12201:12201/tcp -p 12201:12201/udp -p 13301:13301/tcp -p 13302:13302/tcp robfromboulder/devtanker:6.1.4c
```
👆️ When using Docker Desktop on Windows, use PowerShell to launch `docker run` and **not** git bash shell. Also use Windows syntax (`-v C:\Users\Projects\...`) when mapping the directory. 

Start bash shell:
```bash
docker exec -it devtanker bash
```

👀 At this point, run `ll` and verify that your home directory includes `graylog-plugin-aisearch` as a subdirectory.
If this directory is missing, then stop the container and double-check that your directory mount syntax is correct.
If this directory is present but owned by `root`, then set these permissions manually by reconnecting as the root user
(`docker exec --user root -it devtanker bash`), fixing the directory owner (`chown -R runtime:runtime graylog-plugin-aisearch`),
and then restarting the bash shell as the `runtime` user.


## Generating Plugin Skeleton

⚠️ Skip this entire section unless initializing a new branch from scratch

Generate plugin scaffolding:
```bash
mvn archetype:generate -DarchetypeGroupId=org.graylog -DarchetypeArtifactId=graylog-plugin-archetype
```
👆 then provide the following parameters:
* pluginClassName = AISearch
* githubRepo = ai-search
* ownerName = Graylog
* ownerEmail = support@graylog.com
* groupId = org.graylog
* artifactId = graylog-plugin-aisearch
* package = org.graylog

Update web-parent version to `6.1.4`:
```bash
nano graylog-plugin-aisearch/pom.xml
```

```bash
BROKEN STEP -- add missing dependency to pom.xml as well

        <dependency>
            <groupId>com.google.inject</groupId>
            <artifactId>guice</artifactId>
            <version>7.0.0</version>
            <scope>provided</scope>
        </dependency>
```

## Building Plugin

Switch to the plugin directory:
```bash
cd ~/graylog-plugin-aisearch
```

Build plugin using Maven:
```bash
mvn -Dmaven.javadoc.skip=true -DskipTests -Dskip.web.build clean compile package
```


## Deploying Plugin

Copy plugin to local Graylog server and restart:
```bash
cp target/graylog-plugin-aisearch*.jar $GRAYLOG_PLUGIN_DIR; supervisorctl restart graylog
```

Find plugin messages in logs:
```bash
cat $LOGS_DIR/graylog-stdout* | grep -i aisearch
```

## Stopping Development Container

Quit the bash shell:
```bash
exit
```

Stop container but keep all data:
```bash
docker stop devtanker
```
👆 Use `docker start devtanker` when you're ready to resume.

Stop container and remove all data:
```bash
docker stop devtanker; docker rm devtanker; docker volume rm devtanker
```