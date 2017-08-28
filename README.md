# invoice

Steps to do project setup
1. Download MySQL by going to https://dev.mysql.com/downloads/mysql/.
2. Download MySQL Workbench by going to https://dev.mysql.com/downloads/workbench/
3. Configure database by creating following tables:

CREATE TABLE `customer` (
  `customer_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `email` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `customer_id_UNIQUE` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=latin1

CREATE TABLE `txn` (
  `txn_id` int(11) NOT NULL AUTO_INCREMENT,
  `customer_id` int(11) DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `total_amount` decimal(8,2) DEFAULT NULL,
  `line_ids` text,
  PRIMARY KEY (`txn_id`),
  UNIQUE KEY `txn_id_UNIQUE` (`txn_id`),
  KEY `customer_id_idx` (`customer_id`),
  CONSTRAINT `customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1

CREATE TABLE `txnline` (
  `line_id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(1000) DEFAULT NULL,
  `amount` decimal(8,2) DEFAULT NULL,
  PRIMARY KEY (`line_id`),
  UNIQUE KEY `line_id_UNIQUE` (`line_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1

4. Import this project to IDE.

5. Update DB connection information by modifying ../src/test/java/TestDB.java file.

6. Build and run the project should work. 

