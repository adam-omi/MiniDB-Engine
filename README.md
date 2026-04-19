# MiniDB Engine - Lightweight Java Database

## Overview
MiniDB is a lightweight, file-based database engine written in Java. It allows users to manage data using basic SQL-like syntax. The engine stores data in JSON format, making it easy to read and port. This project was developed as an academic exercise to understand the principles of database engine architecture and SQL parsing.

## Features
- **SQL Support**: Supports fundamental SQL commands: `CREATE`, `DROP`, `SELECT`, `INSERT`, `UPDATE`, and `DELETE`.
- **JSON Storage**: Utilizes the Jackson library for efficient serialization and deserialization of database records into JSON files.
- **Client-Server Architecture**: Separated logic between the database engine (`MiniDBEngine`) and the interactive command-line client (`MiniDBClient`).
- **Regex Parsing**: Custom-built `SQLParser` using regular expressions to interpret and validate user queries.

## Tech Stack
- **Language**: Java
- **Libraries**: Jackson (Annotations, Core, Databind) for JSON processing.

## Project Structure
- `MiniDBEngine.java`: The core logic responsible for database operations and file management.
- `SQLParser.java`: A utility class that parses raw SQL strings into actionable commands.
- `MiniDBClient.java`: An interactive CLI tool for user interaction with the database.

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher.
- Jackson library JARs (included in the `libs` folder).

### Running the application
1. Compile the source files with Jackson in the classpath:
   ```bash
   javac -cp ".;jackson-databind-2.15.2.jar;jackson-core-2.15.2.jar;jackson-annotations-2.15.2.jar" *.java
