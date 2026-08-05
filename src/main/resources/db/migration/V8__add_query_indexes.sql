CREATE INDEX idx_posts_status_updated_at ON posts (status, updated_at DESC);
CREATE INDEX idx_post_tags_tag_post ON post_tags (tag_id, post_id);
