/*


*/

--HISTORICO
CREATE TABLE IF NOT EXISTS History(
id varchar(50) NOT NULL PRIMARY KEY,
timestampHistory timestamp, 
description varchar(50),
state varchar(50),
refId varchar(50)
);

--CLIENT
create table if not exists Client(
id varchar(50) NOT NULL PRIMARY KEY,
idstr varchar(50) NOT NULL,
version	int, 
enabled boolean,
enabledMultiAccountPay boolean
);

--ACCOUNT
create TABLE IF NOT exists Account(
id varchar(50) NOT NULL PRIMARY KEY,
idstr varchar(50) NOT NULL,
ccc varchar(50),
importValue float
); 

--CLIENT_ACCOUNT
CREATE TABLE IF NOT EXISTS client_account(
client_id varchar(50),
account_id varchar(50),
foreign key (client_id) references Client(id),
foreign key (account_id) references Account(id)
);


--TRANSACTION
CREATE TABLE IF NOT EXISTS TRANSACTIONS(
id varchar(50) NOT NULL PRIMARY KEY,
idstr varchar(50) NOT NULL,
version int,
state varchar(50),
importValue float
);

--TRANSACTION_ACCOUNT
CREATE TABLE IF NOT EXISTS TRANSACTION_ACCOUNT(
transaction_id varchar(50),
account_id varchar(50),
FOREIGN KEY (transaction_id) REFERENCES Transactions(id),
FOREIGN KEY (account_id) REFERENCES Account(id)
);

--TRANSACTION_CLIENT
CREATE TABLE IF NOT EXISTS TRANSACTION_CLIENT(
transaction_id varchar(50),
client_id varchar(50),
FOREIGN KEY (transaction_id) REFERENCES Transactions(id),
FOREIGN KEY (client_id) REFERENCES Client(id)
);