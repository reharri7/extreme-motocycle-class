# extreme-motorcycle-class


## Compile:

### Windows (Command Prompt):

Inside Extreme-motorcycle-class folder:

```
javac src/emc/*.java -d classes/
```

Inside project root folder:

```
javac Extreme_Motorcycle_Class/src/emc/*.java -d Extreme_Motorcycle_Class/classes/
```

## Run:

### Windows (Command Prompt):

Inside Extreme-motorcycle-class folder:

```
java -cp lib/mysql-connector-j-8.0.32.jar;classes emc/Main "jdbc:mysql://localhost:3306/extreme-motorcycle-class?autoReconnect=true&useSSL=false&useLegacyDatetimeCode=false&&serverTimezone=America/New_York" root password "com.mysql.cj.jdbc.Driver"
```

Inside project root folder:

```
java -cp Extreme_Motorcycle_Class/lib/mysql-connector-j-8.0.32.jar;Extreme_Motorcycle_Class/classes emc/Main "jdbc:mysql://localhost:3306/extreme-motorcycle-class?autoReconnect=true&useSSL=false&useLegacyDatetimeCode=false&&serverTimezone=America/New_York" root password "com.mysql.cj.jdbc.Driver"
```
