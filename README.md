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

## API contract (run reset.sql first, then api.http in order)

| # | Request | Expected |
|---|---------|----------|
| 1 | GET /api/rooms | 200 |
| 2 | GET /api/rooms?minCapacity=6 | 200 |
| 3 | GET /api/rooms?keyword=pod | 200 |
| 4 | GET /api/rooms?minCapacity=6&keyword=roof | 200 |
| 5 | GET /api/rooms?sort=capacity | 200 |
| 6 | GET /api/rooms/2 | 200 |
| 7 | GET /api/rooms/99 | 404 |
| 8 | PUT /api/rooms/1 | 200 |
| 9 | PUT /api/rooms/1 (capacity 0) | 400 |
| 10 | PUT /api/rooms/99 | 404 |
| 11 | POST /api/rooms | 201 |
| 12 | POST /api/rooms (capacity 25) | 400 |
| 13 | DELETE /api/rooms/3 | 204 |
| 14 | GET /api/rooms/count | 200 |
| 15 | GET /api/rooms/1/reservations | 200, 3 items |
| 16 | GET /api/rooms/99/reservations | 404 |
| 17 | POST /api/rooms/1/reservations (Kim, 14 Oct 10–11) | 201, /api/reservations/6 |
| 18 | POST /api/rooms/1/reservations (6 Oct 10:30–11:30) | 409 |
| 19 | POST /api/rooms/1/reservations (6 Oct 11:00–12:00) | 201, /api/reservations/7 |
| 20 | POST /api/rooms/1/reservations (end before start) | 400 |
| 21 | POST /api/rooms/99/reservations | 404 |
| 22 | GET /api/reservations?reservedBy=Mina | 200, 2 items |
| 23 | DELETE /api/rooms/1 | 409 |
| 24 | GET /api/reservations/6 | 200 |
| 25 | GET /api/reservations/99 | 404 |
| 26 | DELETE /api/reservations/7 | 204 |
| 27 | DELETE /api/reservations/99 | 404 |

## Endpoints

| Method | Path | Status |
|---|---|---|
| GET | /api/rooms | 200 |
| GET | /api/rooms/count | 200 |
| GET | /api/rooms/{id} | 200 / 404 |
| POST | /api/rooms | 201 / 400 |
| PUT | /api/rooms/{id} | 200 / 400 / 404 |
| DELETE | /api/rooms/{id} | 204 / 404 / 409 |
| GET | /api/rooms/{roomId}/reservations | 200 / 404 |
| POST | /api/rooms/{roomId}/reservations | 201 / 400 / 404 / 409 |
| GET | /api/reservations?reservedBy= | 200 |
| GET | /api/reservations/{id} | 200 / 404 |
| DELETE | /api/reservations/{id} | 204 / 404 |

## Why request 19 succeeds

Two bookings overlap only if new start < old end AND new end > old start; request 19 starts
at 11:00, exactly when Mina's 10:00–11:00 booking ends, and 11:00 > 11:00 is false, so the
bookings only touch and no overlap is counted.

## 409: two rules, two approaches

Deleting a room uses approach A: the database's foreign key refuses the delete and
RoomExceptionHandler turns that into 409, because the database already enforces this rule.
The overlap rule uses approach B: the service checks first and throws ConflictException,
because no database constraint can express "two time ranges must not overlap".

## AI use

NO AI used.