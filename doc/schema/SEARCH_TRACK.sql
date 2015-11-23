CREATE TABLE search_track (
    date datetime,
    ip char(15),
    ori_keyword varchar(500),
    keyword varchar(500),
    filter varchar(100),
    price_lower MEDIUMINT,
    price_upper MEDIUMINT,
    page MEDIUMINT,
    size MEDIUMINT,
    platform char(6),
    response_status varchar(30),
    total MEDIUMINT,took INT
);