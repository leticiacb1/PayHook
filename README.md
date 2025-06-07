## 💰  PayHook


### Description

The project aims to integrate a payment gateway into an existing web commerce system. 

The chosen gateway uses a Webhook-based architecture, where a POST request with payment details is automatically sent to a configured webhook URL upon payment confirmation (e.g., via PayPal or MercadoPago).

> [!NOTE]
> 
> A webhook is a communication mechanism between systems that allows one server to automatically notify another when a specific event occurs, eliminating the need for constant polling. Technically, it is an HTTP request (usually POST) sent to a preconfigured endpoint triggered by an event.
>

<img src="media/Webhook.png" width="600">


Format of the payload that the webhook will receive (POST) for validation :

```bash
{ 
  "event": "payment_success",
  "transaction_id": "abc123",
  "amount": 49.90,
  "currency": "BRL",
  "timestamp": "2025-05-11T16:00:00Z" 
}  
```

Expected handling by the webhook :

| Transaction Condition                         | Return Status | Observation                                      |
| --------------------------------------------- | ------------- | ------------------------------------------------ |
| Valid transaction                             | 200           | Make a request to the `/confirmation` route      |
| Contains incorrect information                | Not 400       | Cancel the transaction by making a request       |
| Missing information (except `transaction_id`) | -             | Cancel the transaction by making a request       |
| Invalid token                                 | -             | This is a fake transaction and should be ignored |


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
$ sbt clean compile
$ sbt run

# Access : http://localhost:8080/docs
```

#### After a dependence add or update

Change on file `webhook/project/Dependencies.scala` and in `webhook/build.sbt` :

```bash
$ cd webhook/
$ sbt reload
$ sbt update
```

For run the tests:

```bash
$ cd webhook/
$ python3 -m venv venv
$ pip install -r requirements.txt

# --- Run tests ---

# Terminal 1
$ sbt clean compile
$ sbt run

# Terminal 2 
$ cd src/test/python/
$ python3 test_webhook.py
```

References:

https://index.scala-lang.org/swagger-akka-http/swagger-akka-http

https://doc.akka.io/libraries/akka-http/current/server-side/index.html

https://github.com/pjfanning/swagger-akka-http-sample/tree/main

https support: https://doc.akka.io/libraries/akka-http/current/server-side/server-https-support.html