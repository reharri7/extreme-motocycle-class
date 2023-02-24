<<<<<<< HEAD
#!/bin/bash

# This script compiles the source code of the project
echo "This script compiles the source code of the project"

echo Root Password: # wait for user input
stty -echo # disable echo
read -r root_password # read the password
stty echo # enable echo

echo "Compiling the source code of the project"

javac Extreme_Motorcycle_Class/src/emc/*.java -d Extreme_Motorcycle_Class/classes/

echo "Running the project"
=======
#!/bin/bash

# This script compiles the source code of the project
echo "This script compiles the source code of the project"

echo Root Password: # wait for user input
stty -echo # disable echo
read -r root_password # read the password
stty echo # enable echo

echo "Compiling the source code of the project"

javac Extreme_Motorcycle_Class/src/emc/*.java -d Extreme_Motorcycle_Class/classes/

echo "Running the project"
>>>>>>> a437b829abc496f765242b7b315105025f526189
java -cp Extreme_Motorcycle_Class/lib/mysql-connector-j-8.0.32.jar:Extreme_Motorcycle_Class/classes emc/Main "jdbc:mysql://localhost:3306/extreme-motorcycle-class?autoReconnect=true&useSSL=false&useLegacyDatetimeCode=false&&serverTimezone=America/New_York" root "$root_password" "com.mysql.cj.jdbc.Driver"