# extreme-motorcycle-class

This program relies on the extreme-motorcycle-class mySQL database. Prior to running,
`Script-3.sql` from the repository should be run to build the database and `sample.sql` to fill
the database with data. Once the program runs, users may interact with the command line interface (CLI)
to administer the database by entering option numbers and requested information.

The program will provide feedback for successful and unsuccessful queries, includes error handling, and
mirrors the queries created by Team 4 in prior deliverables.

## Compile:

Users can run the bash script `run.sh` to build and run the program, assuming they have the necessary folder structure.
Users may also manually run the commands below to achieve the same goal.

### Windows/Mac (Command Prompt/Terminal):

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

### Mac (Terminal):

Inside Extreme-motorcycle-class folder:

```
java -cp lib/mysql-connector-j-8.0.32.jar:classes emc/Main "jdbc:mysql://localhost:3306/extreme-motorcycle-class?autoReconnect=true&useSSL=false&useLegacyDatetimeCode=false&&serverTimezone=America/New_York" <username> <password> "com.mysql.cj.jdbc.Driver"
```

Inside project root folder:

```
java -cp Extreme_Motorcycle_Class/lib/mysql-connector-j-8.0.32.jar:Extreme_Motorcycle_Class/classes emc/Main "jdbc:mysql://localhost:3306/extreme-motorcycle-class?autoReconnect=true&useSSL=false&useLegacyDatetimeCode=false&&serverTimezone=America/New_York" <username> <password> "com.mysql.cj.jdbc.Driver"
```
