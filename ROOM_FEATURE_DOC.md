# Room Functionality Documentation

## Overview
This document describes the room management functionality in the rental application. It explains the entity mapping, REST endpoints, UI behavior, data validation, and the recent fixes that made room creation, updating, deletion, and searching work correctly.

---

## 1. Room Entity Mapping (`Room.java`)

The `Room` entity is stored in the `rooms` database table. The application uses Java field names in camelCase, while the database columns remain snake_case.

Key fields:
- `roomId` → mapped to `room_id`
- `roomType` → mapped to `room_type`
- `roomDescription` → mapped to `room_description`
- `status` → mapped to `status`
- `roomPrice` → mapped to `room_price`

### Why this matters
Spring Data JPA repository method names use the Java property names, not the column names. If the entity fields were left as snake_case, repository methods like `findByRoomType` would fail with a JPQL grammar error.

Example mapping:
```java
@Column(name = "room_type", nullable = false)
private String roomType;
```

This means the entity property is `roomType`, while the actual database column is still `room_type`.

---

## 2. Repository Methods (`RoomRepository.java`)

The repository exposes query methods that support room filtering:
- `findByRoomType(String roomType)`
- `findByRoomPrice(Double roomPrice)`
- `findByRoomPriceBetween(Double minPrice, Double maxPrice)`
- `findByStatus(String status)`
- `existsByRoomId(Long roomId)`

### Purpose of each method
- `findByRoomType(...)`: search rooms by type
- `findByRoomPriceBetween(...)`: search rooms in a price range
- `findByStatus(...)`: search rooms by occupancy status

---

## 3. Business Logic (`RoomService.java`)

### Create room
The `createRoom(Room room)` method now:
- requires `room.getRoomId()` to be provided explicitly
- throws an error if the room already exists
- sets a default `status` of `vacant` if none is provided

### Update room
The `updateRoom(Long id, Room room)` method updates:
- `roomType`
- `roomDescription`
- `status`
- `roomPrice`

It preserves the `roomId` of the existing record.

### Delete room
The `deleteRoom(Long id)` method removes the room if it exists.

### Search methods
The service exposes:
- `searchByType(String type)`
- `searchByPrice(Double roomPrice)`
- `searchByPriceRange(Double minPrice, Double maxPrice)`
- `searchByStatus(String status)`

This supports both exact and range filtering.

---

## 4. Controller Routing and Search Logic (`RoomController.java`)

### Endpoints
- `GET /rooms` — return all rooms
- `GET /rooms/{id}` — return room by ID
- `POST /rooms` — create a new room
- `PUT /rooms/{id}` — update an existing room
- `DELETE /rooms/{id}` — delete an existing room
- `GET /rooms/search` — search rooms

### Search endpoint logic
The `/rooms/search` endpoint now evaluates filters in the correct order:
1. If both `minPrice` and `maxPrice` are provided, do a price range search.
2. Otherwise, if `type` is provided, search by type.
3. Otherwise, if `status` is provided, search by status.
4. If none of these filters are provided, return all rooms.

This ordering fixes the bug where `type` or `status` would prevent price range searches from running.

---

## 5. Frontend UI Form and Script (`Room.html` and `room.js`)

### Status field
The room form now includes a status dropdown with only two valid values:
- `vacant`
- `occupied`

The backend also supports this status field and stores it in the `status` column.

### Create vs Update behavior
A JavaScript flag named `currentEditRoomId` is used to determine whether the form is creating a new room or editing an existing one.

- When creating a room:
  - `roomId` is editable
  - the request is sent as `POST /rooms`
- When editing a room:
  - `roomId` is locked
  - the request is sent as `PUT /rooms/{id}`

### Search filters
The UI builds the search query using these fields:
- `type`
- `status`
- `minPrice`
- `maxPrice`

This means users can filter by room type, occupancy status, or price range from the frontend.

---

## 6. Important Fixes That Made Room Functionality Successful

### 1. Property naming alignment
Changing the entity fields to camelCase while keeping `@Column(name = ...)` fixed repository method resolution.

### 2. Search priority correction
Reordering search logic in `RoomController` ensured price range filtering works correctly before other filters.

### 3. Create/update form handling
Using `currentEditRoomId` prevented accidental update-mode behavior during room creation.

### 4. Status field consistency
Adding `status` to the model, service, controller, and UI ensures rooms can be either `vacant` or `occupied` consistently.

### 5. Default status on create
Defaulting new rooms to `vacant` when no status is provided prevents null or invalid state.

---

## 7. What to watch for next
If you want to improve the system further, consider:
- enforcing `status` values in Java with an enum
- supporting combined search filters (`type` + `status` + price range)
- adding validation errors in the API response body instead of only HTTP status codes
- migrating the database if the column names or types change

---

## Summary
The room functionality now works because:
- the entity matches the repository method naming conventions,
- search filtering order is correct,
- the frontend sends the right payload and query parameters,
- and `status` is explicitly supported as `vacant` or `occupied`.

These changes make room creation, update, deletion, and search reliable across backend and frontend.
