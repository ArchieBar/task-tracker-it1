DROP TABLE IF EXISTS Boards_Users;
DROP TABLE IF EXISTS Epics_Users;
DROP TABLE IF EXISTS Comments;
DROP TABLE IF EXISTS Tasks;
DROP TABLE IF EXISTS Epics;
DROP TABLE IF EXISTS Entitlements;
DROP TABLE IF EXISTS Invitations;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Boards;

CREATE TABLE IF NOT EXISTS Boards (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT pk_board PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Users (
    id VARCHAR(36) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    logon BOOLEAN NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Invitations (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    board_id VARCHAR(36) NOT NULL,
    confirmed BOOLEAN NOT NULL,
    CONSTRAINT pk_invitation PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (board_id) REFERENCES Boards(id)
);

CREATE TABLE IF NOT EXISTS Entitlements (
    id VARCHAR(36) NOT NULL,
    board_id VARCHAR(36) NOT NULL,
    entitlement VARCHAR(255) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_entitlement PRIMARY KEY (id),
    FOREIGN KEY (board_id) REFERENCES Boards(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE IF NOT EXISTS Epics (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(255) NOT NULL,
    created_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    board_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_epic PRIMARY KEY (id),
    FOREIGN KEY (board_id) REFERENCES Boards(id)
);

CREATE TABLE IF NOT EXISTS Tasks (
    id VARCHAR(36) NOT NULL,
    description TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL,
    epic_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_task PRIMARY KEY (id),
    FOREIGN KEY (epic_id) REFERENCES Epics(id)
);

CREATE TABLE IF NOT EXISTS Comments (
    id VARCHAR(36) NOT NULL,
    text VARCHAR(1000),
    created_time TIMESTAMP NOT NULL,
    epic_id VARCHAR(36) NOT NULL,
    author_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    FOREIGN KEY (author_id) REFERENCES Users(id),
    FOREIGN KEY (epic_id) REFERENCES Epics(id)
);

CREATE TABLE IF NOT EXISTS Boards_Users (
    board_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_board_task PRIMARY KEY (board_id, user_id),
    FOREIGN KEY (board_id) REFERENCES Boards(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE IF NOT EXISTS Epics_Users (
    epic_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_epic_user PRIMARY KEY (epic_id, user_id),
    FOREIGN KEY (epic_id) REFERENCES Epics(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);