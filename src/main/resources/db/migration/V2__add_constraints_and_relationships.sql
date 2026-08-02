CREATE TABLE posts_new (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(200) NOT NULL UNIQUE,
    content VARCHAR(10000)
);

INSERT INTO posts_new (id, title, content)
SELECT id, title, content FROM posts;

CREATE TABLE tags_new (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO tags_new (id, name)
SELECT id, name FROM tags;

CREATE TABLE post_tags_new (
    post_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts_new(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags_new(id) ON DELETE CASCADE
);

INSERT INTO post_tags_new (post_id, tag_id)
SELECT post_id, tag_id FROM post_tags;

DROP TABLE post_tags;
DROP TABLE posts;
DROP TABLE tags;

ALTER TABLE posts_new RENAME TO posts;
ALTER TABLE tags_new RENAME TO tags;
ALTER TABLE post_tags_new RENAME TO post_tags;
