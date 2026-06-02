// Tenant management JavaScript
// Handles CRUD operations, search filtering, and tenant form interactions.

const apiBase = '/tenants';

const tenantForm = document.getElementById('tenantForm');
const tenantIdInput = document.getElementById('tenantId');
const tenantNameInput = document.getElementById('tenantName');
const tenantPhoneInput = document.getElementById('tenantPhone');
const tenantEmailInput = document.getElementById('tenantEmail');
const tenantRoomIdInput = document.getElementById('tenantRoomId');
const tenantsContainer = document.getElementById('tenantsContainer');
const messageEl = document.getElementById('message');
const refreshBtn = document.getElementById('refreshBtn');
const cancelBtn = document.getElementById('cancelBtn');
const searchBtn = document.getElementById('searchBtn');
const filterNameInput = document.getElementById('filterName');
const filterPhoneInput = document.getElementById('filterPhone');
const filterEmailInput = document.getElementById('filterEmail');
const filterRoomIdInput = document.getElementById('filterRoomId');
let currentEditTenantId = null;

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

function clearForm() {
    currentEditTenantId = null;
    tenantIdInput.value = '';
    tenantIdInput.readOnly = false;
    tenantNameInput.value = '';
    tenantPhoneInput.value = '';
    tenantEmailInput.value = '';
    tenantRoomIdInput.value = '';
    showMessage('Ready to create a new tenant.');
}

function getQueryUrl() {
    const params = new URLSearchParams();
    const name = filterNameInput.value.trim();
    const phone = filterPhoneInput.value.trim();
    const email = filterEmailInput.value.trim();
    const roomId = filterRoomIdInput.value.trim();

    if (name) params.append('name', name);
    if (phone) params.append('phoneNumber', phone);
    if (email) params.append('email', email);
    if (roomId) params.append('roomId', roomId);

    return apiBase + (params.toString() ? `/search?${params}` : '');
}

async function fetchTenants() {
    const url = getQueryUrl();
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('Could not load tenants.');
        }
        const tenants = await response.json();
        renderTenants(tenants);
        showMessage(`Loaded ${tenants.length} tenant(s)`);
    } catch (error) {
        showMessage(error.message, 'error');
    }
}

function renderTenants(tenants) {
    tenantsContainer.innerHTML = '';
    if (!tenants || tenants.length === 0) {
        tenantsContainer.innerHTML = '<div class="feedback">No tenants found. Use the form to add a new tenant.</div>';
        return;
    }

    tenants.forEach(tenant => {
        const card = document.createElement('div');
        card.className = 'tenant-card';

        const header = document.createElement('div');
        header.className = 'tenant-card-header';
        header.innerHTML = `
            <div>
                <h3 class="tenant-card-title">${tenant.name || 'Unnamed tenant'}</h3>
                <p>ID: ${tenant.id || 'N/A'}</p>
            </div>
            <div class="tenant-room">Room ${tenant.room?.roomId || '—'}</div>
        `;
        card.appendChild(header);

        const meta = document.createElement('div');
        meta.className = 'tenant-card-meta';
        meta.innerHTML = `
            <span>Phone: ${tenant.phoneNumber || '—'}</span>
            <span>Email: ${tenant.email || '—'}</span>
        `;
        card.appendChild(meta);

        const actions = document.createElement('div');
        actions.className = 'tenant-actions';

        const editButton = document.createElement('button');
        editButton.type = 'button';
        editButton.className = 'action-btn';
        editButton.textContent = 'Edit';
        editButton.addEventListener('click', () => populateForm(tenant));

        const deleteButton = document.createElement('button');
        deleteButton.type = 'button';
        deleteButton.className = 'action-btn';
        deleteButton.textContent = 'Delete';
        deleteButton.addEventListener('click', () => removeTenant(tenant.id));

        actions.append(editButton, deleteButton);
        card.appendChild(actions);
        tenantsContainer.appendChild(card);
    });
}

function populateForm(tenant) {
    currentEditTenantId = tenant.id;
    tenantIdInput.value = tenant.id || '';
    tenantIdInput.readOnly = true;
    tenantNameInput.value = tenant.name || '';
    tenantPhoneInput.value = tenant.phoneNumber || '';
    tenantEmailInput.value = tenant.email || '';
    tenantRoomIdInput.value = tenant.room?.roomId || '';
    const roomMsg = tenant.room?.roomId ? ' Note: The assigned room status is automatically managed based on tenant assignments.' : '';
    showMessage(`Editing tenant ID ${tenant.id}. Save to update or cancel to reset.${roomMsg}`);
}

async function removeTenant(id) {
    if (!id) return;
    if (!confirm('Delete this tenant permanently? If this is the last tenant in the room, the room status will automatically become vacant.')) {
        return;
    }

    try {
        const response = await fetch(`${apiBase}/${id}`, { method: 'DELETE' });
        if (!response.ok) {
            throw new Error('Unable to delete tenant.');
        }
        await fetchTenants();
        showMessage('Tenant deleted successfully. Room status has been automatically updated if applicable.', 'success');
    } catch (error) {
        showMessage(error.message, 'error');
    }
}

tenantForm.addEventListener('submit', async event => {
    event.preventDefault();

    const tenantIdValue = tenantIdInput.value.trim();
    const roomIdValue = tenantRoomIdInput.value.trim();
    const payload = {
        id: currentEditTenantId ? currentEditTenantId : (tenantIdValue ? parseInt(tenantIdValue, 10) : null),
        name: tenantNameInput.value.trim(),
        phoneNumber: tenantPhoneInput.value.trim(),
        email: tenantEmailInput.value.trim(),
        room: {
            roomId: roomIdValue ? parseInt(roomIdValue, 10) : null
        }
    };

    if (!payload.id || !payload.name || !payload.room.roomId) {
        showMessage('Please provide Tenant ID, Name, and Room ID.', 'error');
        return;
    }

    try {
        const isEditing = currentEditTenantId !== null;
        const method = isEditing ? 'PUT' : 'POST';
        const url = isEditing ? `${apiBase}/${currentEditTenantId}` : apiBase;
        const response = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error(isEditing ? 'Failed to update tenant.' : 'Failed to create tenant.');
        }

        await fetchTenants();
        clearForm();
        const statusMsg = isEditing ? 
            'Tenant updated successfully. Room status has been automatically updated if a room was assigned.' : 
            'Tenant created successfully. Room status has been automatically set to occupied.';
        showMessage(statusMsg, 'success');
    } catch (error) {
        showMessage(error.message, 'error');
    }
});

cancelBtn.addEventListener('click', event => {
    event.preventDefault();
    clearForm();
});

refreshBtn.addEventListener('click', fetchTenants);
searchBtn.addEventListener('click', fetchTenants);

window.addEventListener('load', () => {
    clearForm();
    fetchTenants();
});
