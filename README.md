# store-data-cli-app
This is a basic implementation to manage Transaction entities in a CLI tool. It uses the [Data Access Object](http://best-practice-software-engineering.ifs.tuwien.ac.at/patterns/dao.html) to reuse the same inteface without having to change the code that references it (relies on dependency injection as well).

# Prerequisites
 * jdk (>=1.8 required)
 * maven (>=3.0.5)

# To Compile this project
Run install through Maven
```
..store-data-cli-app/mvn install
```
# To Test this project
Run test through Maven
```
..store-data-cli-app/mvn test
```
# To execute this project

## Transaction commands

### Add Transaction
```
../java -jar ./<pathtothejarfile>/store-data-cli-app-1.0.0-SNAPSHOT.jar <user_id> add <transaction_json>
```
Sample add transaction Linux
```
java -jar ./store-data-cil-app/target/store-data-cli-app-1.0.0-SNAPSHOT.jar 12 add '{"user_id":12,"amount":12.60,"description":"the description","date":"2018-12-30"}'
```
Sample add transaction [Windows](https://blogs.msdn.microsoft.com/twistylittlepassagesallalike/2011/04/23/everyone-quotes-command-line-arguments-the-wrong-way/)
```
java -jar C:\Users\Ruben\Documents\store-data-cil-app\target\store-data-cli-app-1.0.0-SNAPSHOT.jar "12" "add" "{\"user_id\":12,\"amount\":12.60,\"description\":\"the description\",\"date\":\"2018-12-30\"}"
```
### Show Transaction
```
../java -jar ./<pathtothejarfile>/store-data-cli-app-1.0.0-SNAPSHOT.jar <user_id> <transaction_id>
```
### List Transactions
```
../java -jar ./<pathtothejarfile>/store-data-cli-app-1.0.0-SNAPSHOT.jar <user_id> list
```
### Sum Transactions
```
../java -jar ./<pathtothejarfile>/store-data-cli-app-1.0.0-SNAPSHOT.jar <user_id> sum
```
Note: If the user doesn't exists the app still returns an entry with the requested user_id (with amount 0.0).