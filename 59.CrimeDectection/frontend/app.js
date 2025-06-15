// Set your backend API base URL here
const API_BASE = 'http://localhost:8080'; // Change if your backend runs elsewhere

// --- Utility function to escape HTML ---
function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>"']/g, function(m) {
        return ({
            '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
        })[m];
    });
}

// --- Load all cases ---
async function loadCases(query = "") {
    try {
        const res = await fetch(`${API_BASE}/api/cases`);
        if (!res.ok) throw new Error('HTTP error ' + res.status);
        let cases = await res.json();
        if (query) {
            cases = cases.filter(c => c.title.toLowerCase().includes(query.toLowerCase()));
        }
        const caseList = document.getElementById('caseList');
        caseList.innerHTML = "";
        cases.forEach(c => {
            const li = document.createElement('li');
            li.className = "list-group-item list-group-item-action";
            li.textContent = c.title + " (" + c.status + ")";
            li.onclick = () => showCaseDetails(c.caseId);
            caseList.appendChild(li);
        });
    } catch (err) {
        console.error('Failed to load cases:', err);
        document.getElementById('caseList').innerHTML = `<li class="list-group-item text-danger">Could not load cases.</li>`;
    }
}

// --- Show case details ---
async function showCaseDetails(caseId) {
    try {
        const res = await fetch(`${API_BASE}/api/cases/${caseId}`);
        if (!res.ok) throw new Error('HTTP error ' + res.status);
        const c = await res.json();
        const details = document.getElementById('caseDetails');
        details.classList.remove('d-none');
        details.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="case-title">${escapeHtml(c.title)}</span>
                <span class="badge bg-${c.status === 'Closed' ? 'secondary' : (c.status === 'Open' ? 'success' : 'warning')}">${escapeHtml(c.status)}</span>
            </div>
            <div class="case-meta mb-2">Reported: ${escapeHtml(c.dateReported)}</div>
            <p>${escapeHtml(c.description)}</p>
            <hr>
            <h6>Suspects</h6>
            <ul>
                ${c.suspects && c.suspects.length ? c.suspects.map(s => `<li>${escapeHtml(s.name)} (${escapeHtml(s.gender)}, ${escapeHtml(s.age)})</li>`).join('') : '<li>No suspects</li>'}
            </ul>
            <h6>Evidence</h6>
            <ul>
                ${c.evidenceList && c.evidenceList.length ? c.evidenceList.map(e => `<li>${escapeHtml(e.type)}: ${escapeHtml(e.description)} (${escapeHtml(e.dateCollected)})</li>`).join('') : '<li>No evidence</li>'}
            </ul>
        `;
    } catch (err) {
        console.error('Failed to load case details:', err);
        const details = document.getElementById('caseDetails');
        details.classList.remove('d-none');
        details.innerHTML = `<div class="alert alert-danger">Could not load case details.</div>`;
    }
}

// --- Add case ---
document.getElementById('addCaseForm').onsubmit = async function(e) {
    e.preventDefault();
    const data = {
        title: document.getElementById('caseTitle').value,
        description: document.getElementById('caseDescription').value,
        status: document.getElementById('caseStatus').value,
        dateReported: document.getElementById('caseDate').value
    };
    try {
        const res = await fetch(`${API_BASE}/api/cases`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!res.ok) throw new Error('HTTP error ' + res.status);
        document.getElementById('addCaseForm').reset();
        bootstrap.Modal.getInstance(document.getElementById('addCaseModal')).hide();
        loadCases();
    } catch (err) {
        alert('Failed to add case: ' + err.message);
    }
};

// --- Search ---
document.getElementById('search').oninput = function() {
    loadCases(this.value);
};

// --- Initial load ---
loadCases();
