CREATE TABLE synonyms_related (
    main_id MEDIUMINT,
    related_term varchar(60)  CHARACTER SET utf8 COLLATE utf8_bin,
    communication TINYINT,
    status TINYINT,
    INDEX MAIN_ID_INDEX(main_id),
    FOREIGN KEY (MAIN_id) REFERENCES synonyms_main(id) on DELETE CASCADE
);