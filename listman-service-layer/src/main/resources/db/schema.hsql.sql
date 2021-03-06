create table MAGNIFICENT_LIST (
 id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 name VARCHAR(32),
 description VARCHAR(1024),
 principal VARCHAR(32)
);

create table LIST_ITEM (
 id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 listId INT NOT NULL,
 name VARCHAR(32),
 description VARCHAR(1024),
 principal VARCHAR(32)
);


INSERT INTO MAGNIFICENT_LIST (id, name, description, principal) VALUES (NULL, 'first list', 'first list desc', NULL );
INSERT INTO MAGNIFICENT_LIST (id, name, description, principal) VALUES (NULL, 'second list', 'second list desc', NULL );

INSERT INTO LIST_ITEM (id, listid, name, description, principal) VALUES (NULL, 0, 'first item', 'first item desc', NULL );
INSERT INTO LIST_ITEM (id, listid, name, description, principal) VALUES (NULL, 0, 'second item', 'second item desc', NULL );