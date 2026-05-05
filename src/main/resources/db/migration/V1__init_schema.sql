CREATE TABLE IF NOT EXISTS urls (
                                    id BIGSERIAL PRIMARY KEY,
                                    long_url TEXT NOT NULL,
                                    short_code VARCHAR(10) NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    click_count INTEGER DEFAULT 0
    );