## 💰  PayHook


### Dependencies

#### Install Scala and Package Manager

**SBT** native packager lets you build application packages in native formats and offers different archetypes for common configurations, such as simple Java apps or server applications.

```bash
# Linux/Debian
$ echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
$ curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" | sudo apt-key add
$ sudo apt update
# Install Java (sbt dependencie) : 
# $ sudo apt install openjdk-17-jdk
$ sudo apt install sbt
```

```bash
# Create a sbt project
$ sbt new scala/scala-seed.g8
```

See more, [here](https://www.scala-sbt.org/sbt-native-packager/introduction.html)

### Run

```bash
$ cd webhook/
$ sbt run
```