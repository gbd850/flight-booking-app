CREATE TABLE flight (
  id INT AUTO_INCREMENT NOT NULL,
   name VARCHAR(255) NOT NULL,
   price DOUBLE NOT NULL,
   start_date date NOT NULL,
   end_date date NULL,
   start_location VARCHAR(255) NOT NULL,
   end_location VARCHAR(255) NULL,
   is_available BIT(1) NOT NULL,
   CONSTRAINT pk_flight PRIMARY KEY (id)
);