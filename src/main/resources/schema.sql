CREATE TABLE IF NOT EXISTS schedules
(
    id
    SERIAL
    NOT
    NULL,
    name
    CHARACTER
    VARYING
(
    255
),
    CONSTRAINT pk_schedule_id PRIMARY KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS sessions
(
    id
    SERIAL
    NOT
    NULL,
    duration_minutes
    INT
    NOT
    NULL,
    start_time
    TIMESTAMP
    NOT
    NULL,
    close_time
    TIMESTAMP,
    is_active
    BOOLEAN
    NOT
    NULL,
    id_schedule
    SERIAL
    NOT
    NULL,
    CONSTRAINT
    pk_session_id
    PRIMARY
    KEY
(
    id
),
    CONSTRAINT fk_session_schedule FOREIGN KEY
(
    id_schedule
) REFERENCES schedules
(
    id
)
    );

CREATE TABLE IF NOT EXISTS associates
(
    id
    SERIAL
    NOT
    NULL,
    name
    CHARACTER
    VARYING
(
    255
),
    cpf CHARACTER VARYING
(
    11
),
    CONSTRAINT pk_associate_id PRIMARY KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS votes
(
    id
    BIGSERIAL
    NOT
    NULL,
    id_associate
    BIGINT
    NOT
    NULL,
    option
    CHARACTER
    VARYING
(
    3
),
    id_session SERIAL NOT NULL,
    creation_time TIMESTAMP NOT NULL,
    CONSTRAINT pk_vote_id PRIMARY KEY
(
    id
),
    CONSTRAINT fk_vote_session FOREIGN KEY
(
    id_session
) REFERENCES sessions
(
    id
),
    CONSTRAINT uc_vote_associate_session UNIQUE
(
    id_associate,
    id_session
)
    )