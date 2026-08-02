CREATE TABLE posts (
    id INTEGER PRIMARY KEY,
    content VARCHAR(255),
    title VARCHAR(255) NOT NULL
);

CREATE TABLE tags (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE post_tags (
    post_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
    PRIMARY KEY (post_id, tag_id)
);
