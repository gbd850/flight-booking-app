CREATE TABLE customer (
  id INT AUTO_INCREMENT NOT NULL,
   username VARCHAR(255) NOT NULL,
   password VARCHAR(255) NOT NULL,
   `role` VARCHAR(255) NULL,
   CONSTRAINT pk_customer PRIMARY KEY (id)
);

CREATE TABLE customers_flights (
  customer_id INT NOT NULL,
   flight_id INT NOT NULL,
   CONSTRAINT pk_customers_flights PRIMARY KEY (customer_id, flight_id)
);

ALTER TABLE customer ADD CONSTRAINT uc_customer_username UNIQUE (username);

ALTER TABLE customers_flights ADD CONSTRAINT fk_cusfli_on_customer FOREIGN KEY (flight_id) REFERENCES customer (id);

ALTER TABLE customers_flights ADD CONSTRAINT fk_cusfli_on_flight FOREIGN KEY (customer_id) REFERENCES flight (id);