# Room API

Spring Boot REST API for rooms, running on MySQL through Spring Data JPA.

## Database setup

Run as root once (the root password is not stored in the project):

```sql
CREATE DATABASE roomdb;
CREATE USER 'roomapp'@'localhost' IDENTIFIED BY 'roomapp1234';
GRANT ALL PRIVILEGES ON roomdb.* TO 'roomapp'@'localhost';
```

Before testing, reset the tables and sample data with `reset.sql`. Use either way:

**Terminal** (from the project folder):

```bash
mysql -u roomapp -p roomdb < reset.sql
```

On macOS, if you get `command not found: mysql`, use the full path:

```bash
/usr/local/mysql/bin/mysql -u roomapp -p roomdb < reset.sql
```

Enter `roomapp1234` when asked for the password.

**MySQL Workbench:**

1. Open a connection as `roomapp` (password `roomapp1234`).
2. Open `reset.sql` in any editor, copy everything, and paste it into a new SQL tab in Workbench.
3. Click the lightning bolt ⚡ (Execute) to run the whole script.
4. Check with `SELECT * FROM roomdb.room;`. It should show 3 rooms.

## How to run

Run `reset.sql` first, then:

```bash
./gradlew bootRun
```

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/rooms` | Retrieve all rooms |
| GET | `/api/rooms?minCapacity={n}` | Filter rooms by minimum capacity |
| GET | `/api/rooms?keyword={word}` | Search rooms by keyword |
| GET | `/api/rooms?sort=capacity` | Sort rooms by capacity |
| GET | `/api/rooms/{id}` | Retrieve a room by ID |
| POST | `/api/rooms` | Create a room |
| PUT | `/api/rooms/{id}` | Update a room |
| DELETE | `/api/rooms/{id}` | Delete a room |
| GET | `/api/rooms/count` | Count all rooms |
| GET | `/api/rooms/{id}/reservations` | Retrieve reservations for a room |
| POST | `/api/rooms/{id}/reservations` | Create a reservation |
| GET | `/api/reservations?reservedBy={name}` | Retrieve reservations by user |
| GET | `/api/reservations/{id}` | Retrieve a reservation by ID |

## Testing with api.http

The `api.http` file contains 43 HTTP requests for testing the API.

Run the requests in order after resetting the database.

### Requests 1-25: Room and Reservation Operations

| # | Test | Expected Status |
|---:|---|---:|
| 1 | Get all rooms | 200 |
| 2 | Filter by minimum capacity | 200 |
| 3 | Filter by keyword | 200 |
| 4 | Apply combined filters | 200 |
| 5 | Sort by capacity | 200 |
| 6 | Get room by ID | 200 |
| 7 | Get nonexistent room | 404 |
| 8 | Update room | 200 |
| 9 | Update with invalid capacity | 400 |
| 10 | Update nonexistent room | 404 |
| 11 | Create room | 201 |
| 12 | Create room with excessive capacity | 400 |
| 13 | Delete room without reservations | 204 |
| 14 | Count rooms | 200 |
| 15 | Get reservations for room 1 | 200 |
| 16 | Get reservations for nonexistent room | 404 |
| 17 | Create reservation | 201 |
| 18 | Create overlapping reservation | 409 |
| 19 | Create reservation starting when another ends | 201 |
| 20 | Create reservation with end before start | 400 |
| 21 | Create reservation for nonexistent room | 404 |
| 22 | Get Mina's reservations | 200 |
| 23 | Delete room with reservations | 409 |
| 24 | Get reservation by ID | 200 |
| 25 | Get nonexistent reservation | 404 |

### Requests 26-33: Week 9 - Dirty Checking and Error Responses

| # | Test | Expected Status |
|---:|---|---:|
| 26 | Update room with the same values | 200 |
| 27 | Update room with new values | 200 |
| 28 | Create room with blank name | 400 |
| 29 | Create room with capacity 0 | 400 |
| 30 | Create room with two invalid fields | 400 |
| 31 | Get nonexistent room | 404 |
| 32 | Delete room with reservations | 409 |
| 33 | Request nonexistent URL | 404 |

For request 26, check the console: the update should execute one SELECT query without an UPDATE statement.

For request 27, check the console: the application should execute a SELECT followed by an UPDATE statement.

### Requests 34–41: Week 9 Lab E - Reservation Validation

| # | Test | Expected Status |
|---:|---|---:|
| 34 | Create reservation with blank reservedBy | 400 |
| 35 | Create reservation without endTime | 400 |
| 36 | Create reservation with end before start | 400 |
| 37 | Create overlapping reservation | 409 |
| 38 | Get nonexistent reservation | 404 |
| 39 | Get reservations without required query parameter | 400 |
| 40 | Submit a non-numeric room capacity | 400 |
| 41 | Submit a non-numeric room ID | 400 |

### Requests 42–43: Additional Error Tests

| # | Test | Expected Status |
|---:|---|---:|
| 42 | Retrieve reservation using a non-numeric ID | 400 |
| 43 | Create room with a name exceeding 100 characters | 400 |

## Validation

The API validates incoming request data before processing it.

Examples include:

- Room names must not be blank.
- Room names must not exceed 100 characters.
- Room capacity must be within the permitted range.
- Reservation `reservedBy` must not be blank.
- Reservation start and end times are required.
- Reservation start time must be before end time.

Invalid request fields are returned through the `fieldErrors` property in the error response.

## Error Responses

The API uses a consistent `ApiError` response structure:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/rooms",
  "fieldErrors": {
    "name": "must not be blank"
  }
}
```

The response contains:

| Field | Description |
|---|---|
| `status` | HTTP status code |
| `error` | HTTP error description |
| `message` | Explanation of the error |
| `path` | Requested API endpoint |
| `fieldErrors` | Details of individual validation failures |

### HTTP Status Codes

| Status | Meaning |
|---:|---|
| 200 | Request successful |
| 201 | Resource created successfully |
| 204 | Resource deleted successfully |
| 400 | Invalid request or input validation failure |
| 404 | Requested resource or endpoint not found |
| 409 | Conflict, such as an overlapping reservation or deleting a room with reservations |
| 500 | Unexpected server error |

## AI use

NO AI used.