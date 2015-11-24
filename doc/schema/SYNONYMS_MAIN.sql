CREATE TABLE synonyms_main (
    id MEDIUMINT not null auto_increment,
    original_term varchar(60),
    status TINYINT,
    PRIMARY KEY (id)
);