DROP TABLE IF EXISTS Epics_Users;
DROP TABLE IF EXISTS Epics;
DROP TABLE IF EXISTS Tasks;
DROP TABLE IF EXISTS Users;

CREATE TABLE IF NOT EXISTS Users (
    id VARCHAR(36) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Tasks (
    id VARCHAR(36) NOT NULL,
    description TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL,
    CONSTRAINT pk_task PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Epics (
    id VARCHAR(36)  NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    task_id VARCHAR(36),
    CONSTRAINT pk_epic PRIMARY KEY (id),
    CONSTRAINT fk_task_id FOREIGN KEY (task_id) REFERENCES Tasks(id)
);

CREATE TABLE IF NOT EXISTS Epics_Users (
    epic_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_epic_user PRIMARY KEY (epic_id, user_id),
    CONSTRAINT fk_epic_id FOREIGN KEY (epic_id) REFERENCES Epics(id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES Users(id)
);