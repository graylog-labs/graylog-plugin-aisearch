# Contributing to ai-search

## Configuring project directory

```bash
cd $HOME/Projects
git clone git@github.com:graylog-labs/ai-search.git
(check out preferred branch)
```

In Docker Desktop, register `$HOME/Projects/ai-search` as a virtual file share.


## Running development container

Start ubertanker with default settings, and sharing `$HOME/Projects/ai-search` on host as `/home/runtime/myrepo` on the container:
```bash
docker run -d --name ubertanker -v $HOME/Projects/ai-search:/home/runtime/myrepo -v ubertanker:/data -e GRAYLOG_DATANODE_INSECURE_STARTUP="true" -e GRAYLOG_DATANODE_PASSWORD_SECRET="somepasswordpeppersomepasswordpeppersomepasswordpeppersomepasswordpepper" -e GRAYLOG_HTTP_EXTERNAL_URI="http://localhost:9000/" -e GRAYLOG_PASSWORD_SECRET="somepasswordpeppersomepasswordpeppersomepasswordpeppersomepasswordpepper" -e GRAYLOG_ROOT_PASSWORD_SHA2="8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918" -e TZ=UTC -p 5044:5044/tcp -p 5140:5140/tcp -p 5140:5140/udp -p 9000:9000/tcp -p 12201:12201/tcp -p 12201:12201/udp -p 13301:13301/tcp -p 13302:13302/tcp robfromboulder/ubertanker:6.1.4c
```

Graylog will be running on port 9000 within a few seconds.

See [ubertanker docs](https://github.com/robfromboulder/ubertanker/blob/v6.1.x/README.md) for more details on using this container. 


## Generating plugin skeleton

⚠️ This should only be necessary when initializing a new branch from scratch, or when the target version of Graylog changes. (v6.1 is used here)

Start a bash shell:
```bash
docker exec -it ubertanker bash
```

Change to the `myrepo` directory:
```bash
cd myrepo
```

Bootstrap the meta project:
```bash
graylog-project bootstrap https://github.com/Graylog2/graylog-project.git --manifest manifests/6.1.json
```

Generate plugin scaffolding:
```bash
mvn archetype:generate -DarchetypeGroupId=org.graylog -DarchetypeArtifactId=graylog-plugin-archetype
```
👆 then provide the following parameters:
* pluginClassName = AISearchPlugin
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

Copy the gitignore file from the project root to the plugin directory:
```bash
cp .gitignore graylog-plugin-aisearch
```

Quit the bash shell:
```bash
exit
```

## Building and testing plugin

Start a bash shell:
```bash
docker exec -it ubertanker bash
```

Switch to the plugin directory:
```bash
cd myrepo/graylog-plugin-aisearch
```

Build plugin using Maven:
```bash
mvn -Dmaven.javadoc.skip=true -DskipTests -Dskip.web.build compile
```

@todo deploy and test plugin

Quit the bash shell:
```bash
exit
```

## Using IDEA

Open the pom.xml file in `graylog-plugin-aisearch` to load this as a focused IDEA project that leaves out the other scaffolding.

## Stopping development container

```bash
docker stop ubertanker; docker rm ubertanker; docker volume rm ubertanker
```