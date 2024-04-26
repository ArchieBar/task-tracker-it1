CREATE TABLE IF NOT EXISTS Users (
    id UUID NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Tasks (
    id UUID NOT NULL,
    description TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL,
    CONSTRAINT pk_task PRIMARY KEY (id),
);

CREATE TABLE IF NOT EXISTS Epics (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    CONSTRAINT pk_epic PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Epic_Task (
    epic_id UUID NOT NULL,
    task_id UUID NOT NULL,
    CONSTRAINT pk_epic_task PRIMARY KEY (epic_id, task_id),
    CONSTRAINT fk_epic_id FOREIGN KEY (epic_id) REFERENCES Epics(id),
    CONSTRAINT fk_task_id FOREIGN KEY (task_id) REFERENCES Tasks(id)
)

CREATE TABLE IF NOT EXISTS Epic_User (
    epic_id UUID NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT pk_epic_user PRIMARY KEY (epic_id, user_id),
    CONSTRAINT fk_epic_id FOREIGN KEY (epic_id) REFERENCES Epics(id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES Users(id)
)