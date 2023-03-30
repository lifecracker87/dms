CREATE TABLE drones (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    serialno VARCHAR(128) NOT NULL UNIQUE,
    model VARCHAR(128) NOT NULL,
    weightlimit INTEGER NOT NULL,
    batterycapicity INTEGER default 0,
    state VARCHAR(128),
    currentwt INTEGER default 0,
    PRIMARY KEY (id)
);

CREATE TABLE medications (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    droneid INTEGER not null,
    name VARCHAR(128) NOT NULL,
    code VARCHAR(128) NOT NULL,
    weight INTEGER NOT NULL,
    image VARCHAR(128),
    unload INTEGER default 0,
    PRIMARY KEY (id)
);