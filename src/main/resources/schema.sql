CREATE TABLE IndexInfo (
                           info_id BIGINT PRIMARY KEY,
                           index_classification VARCHAR(100),
                           index_name VARCHAR(255),
                           employed_items_count INT,
                           basepoint_intime TIMESTAMPTZ,
                           base_index DECIMAL,
                           source_type VARCHAR(20),
                           favorite BOOLEAN,
                           created_at TIMESTAMPTZ,
                           updated_at TIMESTAMPTZ,
                           enabled BOOLEAN
);

CREATE TABLE IndexData (
                           id BIGINT PRIMARY KEY,
                           indexInfoId BIGINT NOT NULL,
                           base_date TIMESTAMPTZ,
                           source_type VARCHAR(20),
                           market_price DECIMAL,
                           closing_price DECIMAL,
                           high_price DECIMAL,
                           low_price DECIMAL,
                           versus DECIMAL,
                           fluctuation_rate DECIMAL,
                           trading_quantity INT,
                           trading_price INT,
                           market_total_amount BIGINT,
                           created_at TIMESTAMPTZ,
                           enabled BOOLEAN,

                           CONSTRAINT fk_indexdata_indexinfo FOREIGN KEY (indexInfoId)
                               REFERENCES IndexInfo(info_id)
                               ON DELETE CASCADE
);

CREATE TABLE Integration (
                             id BIGINT PRIMARY KEY,
                             indexInfoId BIGINT NOT NULL,
                             data_id BIGINT,
                             job_type VARCHAR(100),
                             base_date TIMESTAMPTZ,
                             worker VARCHAR(100),
                             job_time TIMESTAMPTZ,
                             result VARCHAR(20),

                             CONSTRAINT fk_integration_indexinfo FOREIGN KEY (indexInfoId)
                                 REFERENCES IndexInfo(info_id)
                                 ON DELETE CASCADE,

                             CONSTRAINT fk_integration_data FOREIGN KEY (data_id)
                                 REFERENCES IndexData(id)
                                 ON DELETE CASCADE
);