## 🚀 Tecnologies

This project was developed using these technologies:

- [Clojure](https://https://clojure.org/)
- [Leiningen](https://leiningen.org/)
- [Clj-time](https://github.com/clj-time/clj-time)
- [Midje](https://github.com/marick/Midje)

## 💻 Project
The project was developed for the <strong>Nubank</strong> company as an exercise. The goal was to create an authorizer that can receive transactions and output the result of them.

## 🏃‍♂️ Running

```bash
# Navigate into the project directory
$ cd nubank-authorizer

# You can run the project building and running the docker image that's inside
$ docker build -t nubank-authorizer .
$ docker run -ti nubank-authorizer

### OR

# You can just run it via leiningen
$ lein run
```

## ✅ Tests
You can run the tests using the following command:
```bash
$ lein midje
```

## 👨🏽‍💻 How to use
At the start, the application will prompt the user for an operation
``` bash
Insert your operation:
```

The user must input an account or transaction operation
``` bash
Insert your operation:
{ "account": { "activeCard": true, "availableLimit": 100 } }
```

The application will return the current status of the account and possible violations that may occur in the business rules
``` bash
Insert your operation:
{ "account": { "activeCard": true, "availableLimit": 100 } }
{"account":{"activeCard":true,"availableLimit":100},"violations":[]}
```

After that, the user will be prompted again and can input another operation.

_P.S.: The user must input a account operation before he does any transaction_

Made with 💗 by [Yan Alves](https://www.linkedin.com/in/yan-alves-monteiro-b8743810a/)

