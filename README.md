# MyWallet

## What is it?
`TODO TODO TODO TODO TODO`

## How to run it?
### Bitnami WildFly stack installation
Simply download installation package from https://bitnami.com/stack/wildfly and install it.  
**Notice**: Don't forget usernames and passwords, they will be used later in WildFly and MySQL administation consoles.

### MySQL
Create mywallet database in MySQL admin  
`CREATE DATABASE mywallet;`  
And that's it :)

### WildFly
#### Datasource configuration
  - Log in to management console (http://localhost:9990/console/App.html)
  - Configuration -> Subsystems -> Datasources -> Non-XA -> Add -> MySQL Datasource
  - Name: `MyWalletDS`  
    JNDI Name: `java:/jboss/datasources/MyWalletDS`
  - Detected driver -> mysql-connector-java-5.1.37-bin.jar_com.mysql.jdbc.Driver_5_1
  - Connection URL: `jdbc:mysql://localhost:3306/mywallet`  
    Username: `root`  
    Password: `changeme`
  - Test connection - *"Successfully created JDBC connection"* message should appear

If you remove default datasource it might be necessary to use this command:
`/subsystem=ee/service=default-bindings:undefine-attribute(name=datasource)`

#### Application deployment
1. Build .war file
 - go to mywallet server directory (where the pom.xml file is) 
 - run `mvn clean package`
 - `mywallet.war` should appear in the `target` directory
2. Deploy .war file
 - go to `bin` in under WildFly root directory
 - connect to JBoss Comman Line by typing `jboss-cli.bat --connect`
 - after connecting to jboss-cli type `deploy C:\path\to\mywallet.war`

## How to use it?
### API
#### Entities
##### Entry
- id - *Number* (used only in queried entities)
- createDate - *Date* (used only in queried entities)
- value - *Number*
- description - *String*
- tags - *Set of Strings*

##### Tag
- id - *Number* - (used only in queried entities)
- name - *String* - name without hash character (#)
- description - *String* - Optional
- entries - *Set of Numbers* - ids of all entries assigned to this tag

#### Endpoints
##### All entries
**GET** on `/mywallet/entries`

##### Account balance (sum of all entry values)
**GET** on `/mywallet/entries/balance`

##### Entries by tag
**GET** on `/mywallet/entries/{tagName}`

##### Sum of entry values by tag
**GET** on `/mywallet/entries/{tagName}/sum`

##### Add entry
**POST** on `/mywallet/entries` with *Entry* DTO in request's body. For example:
```javascript
{
  "value": 54.2,
  "description": "tankowanie bp #paliwo",
  "tags": [
    "paliwo"
  ]
}
```

`TODO TODO TODO TODO TODO`
