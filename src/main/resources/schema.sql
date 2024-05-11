DROP TABLE IF EXISTS Boards_Users;
DROP TABLE IF EXISTS Boards_Epics;
DROP TABLE IF EXISTS Epics_Users;
DROP TABLE IF EXISTS Epics_Tasks;
DROP TABLE IF EXISTS Boards;
DROP TABLE IF EXISTS Epics;
DROP TABLE IF EXISTS Tasks;
DROP TABLE IF EXISTS Users;

CREATE TABLE IF NOT EXISTS Users (
    id VARCHAR(36) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Tasks (
    id VARCHAR(36) NOT NULL,
    description TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL,
    CONSTRAINT pk_task PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Epics (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(255) NOT NULL,
    CONSTRAINT pk_epic PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Boards (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT pk_board PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Boards_Epics (
    board_id VARCHAR(36) NOT NULL,
    epic_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_board_epic PRIMARY KEY (board_id, epic_id),
    FOREIGN KEY (board_id) REFERENCES Boards(id),
    FOREIGN KEY (epic_id) REFERENCES Epics(id)
);

CREATE TABLE IF NOT EXISTS Boards_Users (
    board_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_board_task PRIMARY KEY (board_id, user_id),
    FOREIGN KEY (board_id) REFERENCES Boards(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE IF NOT EXISTS Epics_Tasks (
    epic_id VARCHAR(36) NOT NULL,
    task_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_epic_task PRIMARY KEY (epic_id, task_id),
    FOREIGN KEY (epic_id) REFERENCES Epics(id),
    FOREIGN KEY (task_id) REFERENCES Tasks(id)
);

CREATE TABLE IF NOT EXISTS Epics_Users (
    epic_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_epic_user PRIMARY KEY (epic_id, user_id),
    FOREIGN KEY (epic_id) REFERENCES Epics(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);