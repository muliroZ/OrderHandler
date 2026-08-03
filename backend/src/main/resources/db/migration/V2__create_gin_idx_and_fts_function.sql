CREATE INDEX IF NOT EXISTS idx_item_search ON items
    USING gin((
                  setweight(to_tsvector('portuguese', name), 'A') ||
                  setweight(to_tsvector('portuguese', coalesce(description, '')), 'B')
                  ));

CREATE FUNCTION fts_match(tsvector, tsquery)
    RETURNS BOOLEAN AS $$
SELECT $1 @@ $2;
$$ LANGUAGE sql IMMUTABLE;