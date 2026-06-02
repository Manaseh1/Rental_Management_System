// Room management JavaScript
// This script handles CRUD operations for rooms, as well as filtering and searching.

//api base URL for room-related endpoints
const apiBase = '/rooms';

// DOM elements for form inputs, buttons, and containers
const roomForm = document.getElementById('roomForm');
const roomIdInput = document.getElementById('roomId');
const roomTypeInput = document.getElementById('roomType');
const roomDescriptionInput = document.getElementById('roomDescription');
const roomStatusInput = document.getElementById('roomStatus');
const roomPriceInput = document.getElementById('roomPrice');
const roomsContainer = document.getElementById('roomsContainer');
const messageEl = document.getElementById('message');
const refreshBtn = document.getElementById('refreshBtn');
const cancelBtn = document.getElementById('cancelBtn');
const searchBtn = document.getElementById('searchBtn');
const filterTypeInput = document.getElementById('filterType');
const filterStatusInput = document.getElementById('filterStatus');
const minPriceInput = document.getElementById('minPrice');
const maxPriceInput = document.getElementById('maxPrice');
let currentEditRoomId = null;

// Utility function to display status messages to the user
function showMessage(text, type = 'info') {
    if (!messageEl) return;
    messageEl.textContent = text;
    messageEl.className = 'status-message';
    if (type === 'error') {
        messageEl.style.color = '#dc2626';
    } else if (type === 'success') {
        messageEl.style.color = '#16a34a';
    } else {
        messageEl.style.color = '#2563eb';
    }
}

// Clears the form and resets it to default state for creating a new room
function clearForm() {
    currentEditRoomId = null;
    roomIdInput.value = '';
    roomIdInput.readOnly = false;
    roomTypeInput.value = '';
    roomDescriptionInput.value = '';
    roomStatusInput.value = 'vacant';
    roomStatusInput.readOnly = false;
    roomPriceInput.value = '';
    showMessage('Ready to create a new room.');
}

//Constructs the query for fetching rooms based on filter inputs
function getQueryUrl() {
    const params = new URLSearchParams();
    const type = filterTypeInput.value.trim();
    const status = filterStatusInput.value;
    const minPrice = minPriceInput.value;
    const maxPrice = maxPriceInput.value;

    if (type) params.append('type', type);
    if (status) params.append('status', status);
    if (minPrice) params.append('minPrice', minPrice);
    if (maxPrice) params.append('maxPrice', maxPrice);

    return apiBase + (params.toString() ? `/search?${params}` : '');
}

async function fetchRooms() {
    const url = getQueryUrl();
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('Could not load rooms');
        }
        const rooms = await response.json();
        renderRooms(rooms);
        showMessage(`Loaded ${rooms.length} room(s)`);
    } catch (error) {
        showMessage(error.message, 'error');
    }
}

function renderRooms(rooms) {
    roomsContainer.innerHTML = '';
    if (!rooms || rooms.length === 0) {
        roomsContainer.innerHTML = '<div class="feedback">No rooms found. Use the form to add a new room.</div>';
        return;
    }

    rooms.forEach(room => {
        const card = document.createElement('div');
        card.className = 'room-card';

        const header = document.createElement('div');
        header.className = 'room-card-header';
        header.innerHTML = `
            <div>
                <h3 class="room-card-title">${room.roomType || 'Untitled room'}</h3>
                <p>ID: ${room.roomId || 'N/A'}</p>
            </div>
            <div class="room-price">$${room.roomPrice?.toFixed(2) || '0.00'}</div>
        `;
        card.appendChild(header);

        const description = document.createElement('p');
        description.textContent = room.roomDescription || 'No description available.';
        card.appendChild(description);

        const meta = document.createElement('div');
        meta.className = 'room-card-meta';
        meta.innerHTML = `<span>Type: ${room.roomType || '—'}</span><span>Status: ${room.status || '—'}</span><span>Price: $${room.roomPrice?.toFixed(2) || '0.00'}</span>`;
        card.appendChild(meta);

        const actions = document.createElement('div');
        actions.className = 'room-actions';

        const editButton = document.createElement('button');
        editButton.type = 'button';
        editButton.className = 'action-btn';
        editButton.textContent = 'Edit';
        editButton.addEventListener('click', () => populateForm(room));

        const deleteButton = document.createElement('button');
        deleteButton.type = 'button';
        deleteButton.className = 'action-btn';
        deleteButton.textContent = 'Delete';
        deleteButton.addEventListener('click', () => removeRoom(room.roomId));

        actions.append(editButton, deleteButton);
        card.appendChild(actions);
        roomsContainer.appendChild(card);
    });
}

function populateForm(room) {
    currentEditRoomId = room.roomId;
    roomIdInput.value = room.roomId || '';
    roomIdInput.readOnly = true;
    roomTypeInput.value = room.roomType || '';
    roomDescriptionInput.value = room.roomDescription || '';
    roomStatusInput.value = room.status || 'vacant';
    roomStatusInput.readOnly = room.status === 'occupied';
    roomPriceInput.value = room.roomPrice || '';
    const statusNote = room.status === 'occupied' ? ' Note: Room status is automatically managed by tenant assignments and cannot be manually changed while occupied.' : '';
    showMessage('Editing room ID ' + room.roomId + '. Save to update or cancel to reset.' + statusNote);
}

async function removeRoom(id) {
    if (!id) return;
    if (!confirm('Delete this room permanently?')) {
        return;
    }

    try {
        const response = await fetch(`${apiBase}/${id}`, { method: 'DELETE' });
        if (!response.ok) {
            throw new Error('Unable to delete room');
        }
        await fetchRooms();
        showMessage('Room deleted successfully.', 'success');
    } catch (error) {
        showMessage(error.message, 'error');
    }
}

roomForm.addEventListener('submit', async event => {
    event.preventDefault();

    const roomId = roomIdInput.value.trim();
    const payload = {
        roomId: currentEditRoomId ? currentEditRoomId : parseInt(roomId),
        roomType: roomTypeInput.value.trim(),
        roomDescription: roomDescriptionInput.value.trim(),
        status: roomStatusInput.value,
        roomPrice: parseFloat(roomPriceInput.value)
    };

    if (!roomId || !payload.roomType || Number.isNaN(payload.roomPrice)) {
        showMessage('Please provide Room ID, type, and price.', 'error');
        return;
    }

    try {
        const isEditing = currentEditRoomId !== null;
        const method = isEditing ? 'PUT' : 'POST';
        const url = isEditing ? `${apiBase}/${currentEditRoomId}` : apiBase;
        const response = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error(isEditing ? 'Failed to update room' : 'Failed to create room');
        }

        await fetchRooms();
        clearForm();
        const statusMsg = isEditing ? 
            'Room updated successfully. Room occupied status is automatically managed by tenant assignments.' : 
            'Room created successfully. Room will be marked occupied when a tenant is assigned.';
        showMessage(statusMsg, 'success');
    } catch (error) {
        showMessage(error.message, 'error');
    }
});

cancelBtn.addEventListener('click', event => {
    event.preventDefault();
    clearForm();
});

refreshBtn.addEventListener('click', fetchRooms);
searchBtn.addEventListener('click', fetchRooms);

window.addEventListener('load', () => {
    clearForm();
    fetchRooms();
});

// Auto-refresh rooms when page regains focus (user switches tabs/windows)
document.addEventListener('visibilitychange', () => {
    if (!document.hidden) {
        fetchRooms();
    }
});

// Auto-refresh rooms every 5 seconds to catch updates from tenant operations
setInterval(() => {
    if (!document.hidden) {
        fetchRooms();
    }
}, 5000);
