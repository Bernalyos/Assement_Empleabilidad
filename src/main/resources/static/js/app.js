const API_BASE = '/api/v1';
let currentProjectId = null;

// --- Auth Logic ---
function toggleAuth() {
    const isLogin = !document.getElementById('login-form').classList.contains('hidden');
    if (isLogin) {
        document.getElementById('login-form').classList.add('hidden');
        document.getElementById('register-form').classList.remove('hidden');
        document.getElementById('auth-title').innerText = 'Crear Cuenta';
        document.getElementById('auth-switch-text').innerText = '¿Ya tienes cuenta?';
        document.getElementById('auth-toggle').innerText = 'Inicia Sesión';
    } else {
        document.getElementById('login-form').classList.remove('hidden');
        document.getElementById('register-form').classList.add('hidden');
        document.getElementById('auth-title').innerText = 'Bienvenido';
        document.getElementById('auth-switch-text').innerText = '¿No tienes cuenta?';
        document.getElementById('auth-toggle').innerText = 'Regístrate';
    }
}

async function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (res.ok) {
            const data = await res.json();
            localStorage.setItem('token', data.token);
            localStorage.setItem('username', username);
            initApp();
        } else {
            alert('Error en el login. Verifica tus credenciales.');
        }
    } catch (e) {
        console.error(e);
        alert('Error de conexión.');
    }
}

async function register() {
    const username = document.getElementById('reg-username').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;

    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });

        if (res.ok) {
            alert('Registro exitoso. Ahora inicia sesión.');
            toggleAuth();
        } else {
            alert('Error en el registro.');
        }
    } catch (e) {
        console.error(e);
    }
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    showView('auth');
    document.getElementById('nav-user').classList.add('hidden');
}

// --- API Helper ---
async function apiFetch(endpoint, options = {}) {
    const token = localStorage.getItem('token');
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
    
    if (res.status === 401 || res.status === 403) {
        logout();
        throw new Error('Sesión expirada');
    }
    
    return res;
}

// --- Navigation ---
function showView(viewId) {
    document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
    document.getElementById(`view-${viewId}`).classList.add('active');
    
    if (viewId === 'dashboard') loadProjects();
}

// --- App Logic ---
function initApp() {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    
    if (token) {
        document.getElementById('nav-user').classList.remove('hidden');
        document.getElementById('display-username').innerText = username;
        showView('dashboard');
    } else {
        showView('auth');
    }
    lucide.createIcons();
}

async function loadProjects() {
    const res = await apiFetch('/projects');
    const projects = await res.json();
    
    // Filter out deleted projects
    const activeProjects = projects.filter(p => !p.deleted);
    
    const list = document.getElementById('project-list');
    list.innerHTML = activeProjects.map(p => `
        <div class="glass-card project-card" onclick="viewProject('${p.id}', '${p.name}', '${p.status}')">
            <div class="flex justify-between items-start mb-2">
                <span class="status-badge status-${p.status.toLowerCase()}">${p.status}</span>
            </div>
            <h3 class="mb-2">${p.name}</h3>
            <p class="text-muted" style="font-size: 0.8rem;">ID: ${p.id.substring(0,8)}...</p>
        </div>
    `).join('');
    
    if (activeProjects.length === 0) {
        list.innerHTML = '<p class="text-center text-muted" style="grid-column: 1/-1;">No tienes proyectos aún.</p>';
    }
}

function showCreateProjectModal() {
    document.getElementById('modal-project').classList.add('active');
    document.getElementById('modal-project-name').focus();
}

function closeModal() {
    document.getElementById('modal-project').classList.remove('active');
    document.getElementById('modal-project-name').value = '';
}

async function submitCreateProject() {
    const name = document.getElementById('modal-project-name').value;
    if (!name) return;

    await apiFetch('/projects', {
        method: 'POST',
        body: JSON.stringify({ name })
    });
    closeModal();
    loadProjects();
}

async function viewProject(id, name, status) {
    currentProjectId = id;
    document.getElementById('detail-project-name').innerText = name;
    const statusBadge = document.getElementById('detail-project-status');
    statusBadge.innerText = status;
    statusBadge.className = `status-badge status-${status.toLowerCase()}`;
    
    const btnActivate = document.getElementById('btn-activate');
    if (status === 'DRAFT') btnActivate.classList.remove('hidden');
    else btnActivate.classList.add('hidden');

    showView('project-detail');
    loadTasks();
}

async function activateCurrentProject() {
    try {
        const res = await apiFetch(`/projects/${currentProjectId}/activate`, { method: 'PATCH' });
        if (res.ok) {
            alert('Proyecto activado con éxito.');
            viewProject(currentProjectId, document.getElementById('detail-project-name').innerText, 'ACTIVE');
        } else {
            const err = await res.json();
            alert(err.message || 'Error al activar el proyecto. Asegúrate de tener al menos una tarea.');
        }
    } catch (e) {
        alert('Error al activar.');
    }
}

async function deleteCurrentProject() {
    if (!confirm('¿Estás seguro de eliminar este proyecto?')) return;
    await apiFetch(`/projects/${currentProjectId}`, { method: 'DELETE' });
    showView('dashboard');
}

async function loadTasks() {
    const res = await apiFetch(`/projects/${currentProjectId}/tasks`);
    const tasks = await res.json();
    
    // Filter out deleted tasks
    const activeTasks = tasks.filter(t => !t.deleted);
    
    const list = document.getElementById('task-list');
    list.innerHTML = activeTasks.map(t => `
        <div class="task-item ${t.completed ? 'completed' : ''}">
            <div class="task-title">${t.title}</div>
            <div class="flex gap-2">
                ${!t.completed ? `
                    <button class="btn btn-outline" style="padding: 0.4rem;" onclick="completeTask('${t.id}')" title="Completar">
                        <i data-lucide="check" style="width: 16px; height: 16px;"></i>
                    </button>
                ` : ''}
                <button class="btn btn-danger" style="padding: 0.4rem;" onclick="deleteTask('${t.id}')" title="Eliminar">
                    <i data-lucide="trash-2" style="width: 16px; height: 16px;"></i>
                </button>
            </div>
        </div>
    `).join('');
    
    if (activeTasks.length === 0) {
        list.innerHTML = '<p class="text-center text-muted mt-4">Sin tareas.</p>';
    }
    lucide.createIcons();
}

async function createNewTask() {
    const title = document.getElementById('new-task-title').value;
    if (!title) return;

    await apiFetch(`/projects/${currentProjectId}/tasks`, {
        method: 'POST',
        body: JSON.stringify({ title })
    });
    document.getElementById('new-task-title').value = '';
    loadTasks();
}

async function completeTask(taskId) {
    await apiFetch(`/tasks/${taskId}/complete`, { method: 'PATCH' });
    loadTasks();
}

async function deleteTask(taskId) {
    if (!confirm('¿Eliminar tarea?')) return;
    await apiFetch(`/tasks/${taskId}`, { method: 'DELETE' });
    loadTasks();
}

// Initialize
initApp();
