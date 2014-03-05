DROP TABLE if exists users;
DROP TABLE if exists projects;
DROP TABLE if exists fields;
DROP TABLE if exists records;
DROP TABLE if exists batches;

CREATE TABLE Batches (Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
					FilePath TEXT NOT NULL , 
					ProjectId INTEGER NOT NULL, 
					UserId INTEGER, 
					IsIndexed BOOL DEFAULT FALSE);

CREATE TABLE Fields (Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, 
					ProjectId INTEGER NOT NULL, 
					Name TEXT NOT NULL, 
					XCoord INTEGER NOT NULL, 
					Width INTEGER NOT NULL, 
					HelpHtml TEXT, 
					KnownData TEXT);

CREATE TABLE Projects (Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
					Title TEXT NOT NULL  UNIQUE,
					RecordsPerImage INTEGER NOT NULL,
					FirstYCoord INTEGER NOT NULL,
					RecordHeight INTEGER NOT NULL);

CREATE TABLE Records (Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, 
					Value TEXT NOT NULL , 
					FieldId INTEGER NOT NULL , 
					BatchId INTEGER NOT NULL );

CREATE TABLE Users (Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
					UserName TEXT NOT NULL  UNIQUE , 
					Password TEXT NOT NULL , 
					FirstName TEXT, 
					LastName TEXT, 
					Email TEXT, 
					IndexedRecords INTEGER DEFAULT 0, 
					BatchID INTEGER);