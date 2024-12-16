# Contributing to graylog-plugin-aisearch

Official (and somewhat generic) documentation on writing plugins is available at https://docs.graylog.org/docs/plugins. This guide
offers copy-and-paste commands that are specific to this plugin, to hopefully save you time. 😀  


## Configuring Project Directory

Switch to the directory where you keep dev projects:
```bash
cd $HOME/Projects
```

Clone the repo:
```bash
git clone git@github.com:graylog-labs/graylog-plugin-aisearch.git
```

⚠️ When using Docker Desktop on Mac, configure your `graylog-plugin-aisearch` directory as a virtual file share in Settings|Resources|File sharing.


## Running Development Container

Start devtanker with default settings and volume/directory mounts:
```bash
docker run -d --name devtanker -v $HOME/Projects/graylog-plugin-aisearch:/home/runtime/graylog-project-repos/graylog-plugin-aisearch -v devtanker:/data -e GRAYLOG_DATANODE_INSECURE_STARTUP="true" -e GRAYLOG_DATANODE_PASSWORD_SECRET="somepasswordpeppersomepasswordpeppersomepasswordpeppersomepasswordpepper" -e GRAYLOG_HTTP_EXTERNAL_URI="http://localhost:9000/" -e GRAYLOG_PASSWORD_SECRET="somepasswordpeppersomepasswordpeppersomepasswordpeppersomepasswordpepper" -e GRAYLOG_ROOT_PASSWORD_SHA2="8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918" -e TZ=UTC -p 5044:5044/tcp -p 5140:5140/tcp -p 5140:5140/udp -p 9000:9000/tcp -p 12201:12201/tcp -p 12201:12201/udp -p 13301:13301/tcp -p 13302:13302/tcp robfromboulder/devtanker:6.1.4c
```
👆️ When using Docker Desktop on Windows, use PowerShell to launch `docker run` and **not** git bash shell. Also use Windows syntax (`-v C:\Users\Projects\...`) when mapping the directory. 

Start bash shell:
```bash
docker exec -it devtanker bash
```

👀 Run `ll graylog-project-repos` and verify `graylog-plugin-aisearch` is present as a subdirectory.
If this directory is missing, then stop the container and double-check that your directory mount syntax is correct.
If this directory is present and owned by root (instead of the runtime user), then fix by reconnecting as root
(`docker exec --user root -it devtanker bash`), changing the directory owner (`chown -R runtime:runtime ~`),
and then restarting the bash shell as shown above.

Finish by compiling Graylog Server, which takes a few minutes:
```bash
cd ~/graylog-project && mvn compile
```


## Generating Plugin Skeleton

⚠️ Skip this entire section unless initializing a new branch from scratch or planning to re-merge changes against generated files.

Generate plugin scaffolding with default params:
```bash
cd ~/graylog-project-repos/graylog-plugin-aisearch && rm -rf .mvn/jvm.config build.config.js package.json pom.xml src webpack.config.js && cd ~/graylog-project-repos && mvn archetype:generate -DarchetypeGroupId=org.graylog -DarchetypeArtifactId=graylog-plugin-archetype -DpluginClassName=AISearch -DgithubRepo=graylog-labs/graylog-plugin-aisearch -DownerName=Graylog -DownerEmail=support@graylog.com -DgroupId=org.graylog -DartifactId=graylog-plugin-aisearch -Dpackage=org.graylog.aisearch -Dversion=6.1.0-SNAPSHOT
```

Update web-parent version to `6.1.4`:
```bash
nano ~/graylog-project-repos/graylog-plugin-aisearch/pom.xml
```


## Building and Deploying Plugin

Build plugin using Maven:
```bash
cd ~/graylog-project-repos/graylog-plugin-aisearch && mvn -Dmaven.javadoc.skip=true -DskipTests clean compile package
```

Copy plugin to local Graylog server and restart:
```bash
cp ~/graylog-project-repos/graylog-plugin-aisearch/target/graylog-plugin-aisearch*.jar $GRAYLOG_PLUGIN_DIR; supervisorctl restart graylog
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