# Tenant Functionality Documentation

## Overview
This document describes the tenant management functionality for the rental application. It explains the frontend tenant UI, the backend mapping, REST endpoints, and how the tenant user interface follows the same design principles as room management.

---

## 1. Tenant Entity Mapping (`Tenant.java`)

The `Tenant` entity is stored in the `tenants` database table. The entity uses Java camelCase field names while the database columns are snake_case where needed.

Key fields:
- `id` → mapped to `id`
- `name` → mapped to `name`
- `phoneNumber` → mapped to `phone_number`
- `email` → mapped to `email`
- `balance` → mapped to `balance`
- `room` → mapped to `room_id` via a `ManyToOne` relationship

### Why this matters
Spring Data JPA repository method names use Java property names, not column names. The `Tenant` entity must expose the correct Java properties so service and controller logic can pass the expected fields.

Example mapping:
```java
@Column(name = "phone_number")
private String phoneNumber;
```

This keeps the entity property as `phoneNumber` while the database column remains `phone_number`.

---

## 2. Repository Methods (`TenantRepository.java`)

The tenant repository supports search operations by tenant fields and room assignment.

Common query methods:
- `findByName(String name)`
- `findByPhoneNumber(String phoneNumber)`
- `findByEmail(String email)`
- `findByBalance(Double balance)`
- `findByRoom_RoomId(Long roomId)`

### Purpose of each method
- `findByName(...)`: search tenants by name
- `findByPhoneNumber(...)`: search tenants by phone
- `findByEmail(...)`: search tenants by email
- `findByBalance(...)`: search tenants by balance
- `findByRoom_RoomId(...)`: search tenants assigned to a specific room

---

## 3. Business Logic (`TenantService.java`)

### Create tenant
`createTenant(Tenant tenant)` persists a new tenant record. The frontend sends required fields and includes `room.roomId` so the entity relationship is satisfied.

### Update tenant
`updateTenant(Long id, Tenant tenantUpdate)` updates tenant fields while preserving the tenant ID. It updates:
- `name`
- `phoneNumber`
- `email`
- `balance`
- `room`

### Delete tenant
`deleteTenant(Long id)` removes the tenant record if it exists.

### Search tenants
`searchTenants(...)` returns tenants based on provided filters. The service supports search by any one of:
- `name`
- `phoneNumber`
- `email`
- `balance`
- `roomId`

If no filters are provided, it returns all tenants.

---

## 4. Controller Routing and Search Logic (`TenantController.java`)

### Endpoints
- `GET /tenants` — return all tenants
- `GET /tenants/{id}` — return tenant by ID
- `POST /tenants` — create a new tenant
- `PUT /tenants/{id}` — update an existing tenant
- `DELETE /tenants/{id}` — delete an existing tenant
- `GET /tenants/search` — search tenants by filters

### Search endpoint logic
The `/tenants/search` endpoint handles the following optional filter parameters:
- `name`
- `phoneNumber`
- `email`
- `balance`
- `roomId`

The endpoint delegates to service search methods and returns filtered tenant results.

---

## 5. Frontend UI Form and Script (`Tenant.html`, `tenant.css`, `tenant.js`)

### Tenant.html structure
The UI follows the same two-column layout as the room page:
- left panel: tenant create/edit form
- right panel: tenant list with search filters

Form fields:
- `Tenant ID`
- `Name`
- `Phone number`
- `Email`
- `Balance`
- `Assigned room ID`

Search filters:
- `Filter by name`
- `Filter by phone`
- `Filter by email`
- `Room ID`

### `tenant.js` behavior
The script manages tenant CRUD and search operations.

#### Create vs Update behavior
A JavaScript state variable, `currentEditTenantId`, tracks whether the form is in create or edit mode.
- When creating a tenant:
  - `tenantId` is editable
  - the request sends `POST /tenants`
- When editing a tenant:
  - `tenantId` becomes read-only
  - the request sends `PUT /tenants/{id}`

#### Payload shape
The frontend sends tenant objects shaped like:
```json
{
  "id": 2001,
  "name": "Jane Doe",
  "phoneNumber": "555-1234",
  "email": "jane@example.com",
  "balance": 120.5,
  "room": {
    "roomId": 101
  }
}
```

This matches the backend `Tenant` entity relationship to `Room`.

#### Search query construction
The search UI builds the query string from filter inputs and calls `/tenants/search` when any filter is present.

#### List rendering
Each tenant card displays:
- tenant name
- tenant ID
- assigned room ID
- phone number
- email
- balance

Cards include edit/delete controls.

### tenant.css styling
The CSS mirrors the room page style with:
- clean shell layout
- white panels and card UI
- responsive grid behavior
- primary, secondary, and action button styles

Responsive behavior:
- single-column layout below 940px
- stacked search fields below 520px

---

## 6. Important UI and Backend Details

### 1. Tenant ID handling
Locking the `tenantId` field during edits prevents accidental creation of a new record when updating.

### 2. Room association
The form sends room assignment as nested JSON with `room.roomId`, which is required by the `Tenant` entity's `ManyToOne` relation.

### 3. Search filtering
The UI supports filtering by tenant fields and room ID, keeping the backend search endpoint aligned with repository methods.

### 4. Status messaging
The frontend provides instant feedback using a status message area for success and error notifications.

---

## 7. What to watch for next
To improve the tenant feature further, consider:
- validating email and phone formats on form submit
- returning validation error details from the API rather than only HTTP status codes
- supporting combined filter searches across multiple fields
- adding room availability validation before assigning a tenant

---

## Summary
The tenant UI is built to mirror the room feature document style and uses a consistent CRUD pattern:
- the frontend form and list match the room UI layout
- the API routes follow the same REST design
- tenant data is passed as JSON with a nested room reference
- search, edit, delete, and create flows are handled similarly to room management

This documentation now aligns with the style and structure of `ROOM_FEATURE_DOC.md` while explaining tenant-specific implementation details.
