const rentchargeApi = '/rentcharges';
const tenantApi = '/tenants';

const rentchargeForm = document.getElementById('rentchargeForm');
const tenantSelect = document.getElementById('tenantSelect');
const chargeAmountInput = document.getElementById('chargeAmountInput');
const dueDateInput = document.getElementById('dueDate');
const rentchargesContainer = document.getElementById('rentchargesContainer');
const messageEl = document.getElementById('message');
const refreshBtn = document.getElementById('refreshBtn');
const cancelBtn = document.getElementById('cancelBtn');
const searchBtn = document.getElementById('searchBtn');
const tenantFilter = document.getElementById('tenantFilter');
const dueDateFilter = document.getElementById('dueDateFilter');

let tenants = [];

function showMessage(text, type = 'info') {
  if (!messageEl) return;
  messageEl.textContent = text;
  messageEl.className = 'status-message';
  messageEl.style.color = type === 'error' ? '#dc2626' : type === 'success' ? '#16a34a' : '#2563eb';
}

async function fetchTenants() {
  try {
    const response = await fetch(tenantApi);
    if (!response.ok) throw new Error('Could not load tenants');
    tenants = await response.json();
    renderTenantOptions();
  } catch (error) {
    showMessage(error.message, 'error');
  }
}

function renderTenantOptions() {
  if (!tenantSelect || !tenantFilter) return;
  tenantSelect.innerHTML = '<option value="">Select tenant</option>';
  tenantFilter.innerHTML = '<option value="">All tenants</option>';
  tenants.forEach(tenant => {
    const option = document.createElement('option');
    option.value = tenant.id;
    option.textContent = `${tenant.name || 'Tenant'} (#${tenant.id}) - Room ${tenant.room?.roomId || '—'}`;
    tenantSelect.appendChild(option);

    const filterOption = option.cloneNode(true);
    tenantFilter.appendChild(filterOption);
  });
}

tenantSelect.addEventListener('change', () => {
  const selectedTenantId = tenantSelect.value;
  if (selectedTenantId) {
    const selectedTenant = tenants.find(t => t.id === parseInt(selectedTenantId, 10));
    if (selectedTenant?.room?.roomPrice) {
      chargeAmountInput.placeholder = `Leave blank for room price ($${selectedTenant.room.roomPrice.toFixed(2)})`;
      chargeAmountInput.value = '';
    }
  }
});

function getSearchUrl() {
  const tenantId = tenantFilter.value;
  const dueDate = dueDateFilter.value;
  const params = new URLSearchParams();
  if (tenantId) params.append('tenantId', tenantId);
  if (dueDate) params.append('dueDate', dueDate);
  return params.toString() ? `${rentchargeApi}/search?${params.toString()}` : rentchargeApi;
}

async function fetchRentcharges() {
  const url = getSearchUrl();
  try {
    const response = await fetch(url);
    if (!response.ok) throw new Error('Could not load rent charges');
    const rentcharges = await response.json();
    const filtered = filterRentcharges(rentcharges);
    renderRentcharges(filtered);
    showMessage(`Loaded ${filtered.length} charge(s)`);
  } catch (error) {
    showMessage(error.message, 'error');
  }
}

function filterRentcharges(items) {
  const tenantId = tenantFilter.value;
  const dueDate = dueDateFilter.value;
  return items.filter(rc => {
    const matchesTenant = !tenantId || rc.tenant?.id === parseInt(tenantId, 10);
    const matchesDate = !dueDate || rc.dueDate === dueDate;
    return matchesTenant && matchesDate;
  });
}

function renderRentcharges(rentcharges) {
  rentchargesContainer.innerHTML = '';
  if (!rentcharges || rentcharges.length === 0) {
    rentchargesContainer.innerHTML = '<div class="feedback">No rent charges found. Use the form to create one.</div>';
    return;
  }

  rentcharges.forEach(rc => {
    const card = document.createElement('div');
    card.className = 'charge-card';

    const header = document.createElement('div');
    header.className = 'charge-card-header';
    header.innerHTML = `
      <div>
        <h3>Charge #${rc.id ?? 'N/A'}</h3>
        <p>${rc.dueDate ? `Due ${rc.dueDate}` : 'No due date'}</p>
      </div>
      <div>${rc.tenant?.name || 'No tenant'}</div>
    `;

    const meta = document.createElement('div');
    meta.className = 'charge-card-meta';
    const roomInfo = rc.tenant?.room;
    meta.innerHTML = `
      <span><strong>Tenant:</strong> ${rc.tenant?.name || 'Unknown'} (#${rc.tenant?.id || '—'})</span>
      <span><strong>Room:</strong> #${roomInfo?.roomId || '—'} / ${roomInfo?.roomType || '—'}</span>
      <span><strong>Charge Amount:</strong> $${rc.chargeAmount?.toFixed(2) ?? '0.00'}</span>
      <span><strong>Due date:</strong> ${rc.dueDate || '—'}</span>
    `;

    const actions = document.createElement('div');
    actions.className = 'charge-actions';
    actions.innerHTML = `
      <button type="button" class="primary-btn" onclick="removeRentcharge(${rc.id})">Delete</button>
    `;

    card.appendChild(header);
    card.appendChild(meta);
    card.appendChild(actions);
    rentchargesContainer.appendChild(card);
  });
}

async function removeRentcharge(id) {
  if (!confirm('Delete this rent charge? This cannot be undone.')) return;

  try {
    const response = await fetch(`${rentchargeApi}/${id}`, { method: 'DELETE' });
    if (!response.ok) {
      throw new Error('Unable to delete rent charge.');
    }
    await fetchRentcharges();
    showMessage('Rent charge deleted successfully.', 'success');
  } catch (error) {
    showMessage(error.message, 'error');
  }
}

rentchargeForm.addEventListener('submit', async event => {
  event.preventDefault();
  const tenantId = tenantSelect.value;
  const dueDate = dueDateInput.value;
  const chargeAmount = chargeAmountInput.value ? parseFloat(chargeAmountInput.value) : null;

  if (!tenantId || !dueDate) {
    showMessage('Tenant and due date are required.', 'error');
    return;
  }

  const payload = {
    tenant: { id: parseInt(tenantId, 10) },
    dueDate
  };

  if (chargeAmount !== null) {
    payload.chargeAmount = chargeAmount;
  }

  try {
    const response = await fetch(rentchargeApi, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (!response.ok) {
      throw new Error('Failed to save rent charge');
    }
    await fetchRentcharges();
    clearForm();
    showMessage('Rent charge saved successfully.', 'success');
  } catch (error) {
    showMessage(error.message, 'error');
  }
});

function clearForm() {
  tenantSelect.value = '';
  chargeAmountInput.value = '';
  dueDateInput.value = '';
}

cancelBtn.addEventListener('click', event => {
  event.preventDefault();
  clearForm();
});

refreshBtn.addEventListener('click', fetchRentcharges);
searchBtn.addEventListener('click', fetchRentcharges);

window.addEventListener('load', async () => {
  await fetchTenants();
  fetchRentcharges();
});
