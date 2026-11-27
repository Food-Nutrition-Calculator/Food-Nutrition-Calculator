/**
 * NutriTrack - Web Client Application (Member 1 Food Module)
 */

document.addEventListener('DOMContentLoaded', () => {
    // Use the Java server when this page is opened through Live Server or
    // directly from disk. When served by NutriTrackServer, keep same-origin.
    const API_BASE = window.location.port === '8081' ? '' : 'http://localhost:8081';

    // State variables for searching and sorting
    let currentSearch = '';
    let currentSort = 'NAME';
    let isAscending = true;

    // DOM Elements
    const foodTableBody = document.getElementById('foodTableBody');
    const foodCount = document.getElementById('foodCount');
    const foodSearch = document.getElementById('foodSearch');
    const foodSort = document.getElementById('foodSort');
    const btnToggleOrder = document.getElementById('btnToggleOrder');
    const sortOrderIcon = document.getElementById('sortOrderIcon');
    const addFoodForm = document.getElementById('addFoodForm');
    const toast = document.getElementById('toast');
    const navButtons = document.querySelectorAll('.nav-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    // --- Tab Navigation ---
    navButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const targetTab = btn.getAttribute('data-tab');
            navButtons.forEach(b => b.classList.remove('active'));
            tabContents.forEach(tab => tab.classList.remove('active'));

            btn.classList.add('active');
            const activeSection = document.getElementById(`tab-${targetTab}`);
            if (activeSection) {
                activeSection.classList.add('active');
            }
        });
    });

    window.addEventListener('message', (event) => {
        if (event.origin !== window.location.origin || event.data?.type !== 'meal-saved') return;
        ['tab-dashboard', 'tab-history'].forEach(id => {
            const frame = document.querySelector(`#${id} iframe`);
            if (frame) frame.contentWindow.location.reload();
        });
    });

    // --- Load Food Catalog from Java API ---
    async function loadFoods() {
        try {
            const url = `${API_BASE}/api/foods?search=${encodeURIComponent(currentSearch)}&sortBy=${currentSort}&asc=${isAscending}`;
            const response = await fetch(url);
            
            if (!response.ok) {
                throw new Error(`Server returned HTTP ${response.status}`);
            }

            const foods = await response.json();
            renderFoodTable(foods);
        } catch (error) {
            console.error('Error fetching foods:', error);
            foodTableBody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-muted" style="color: #ef4444 !important;">
                        ⚠️ Could not connect to Java API. Ensure NutriTrackServer is running in VS Code.
                    </td>
                </tr>
            `;
            foodCount.textContent = '0';
        }
    }

    // --- Render Foods Table ---
    function renderFoodTable(foods) {
        foodTableBody.innerHTML = '';
        foodCount.textContent = foods.length;

        if (foods.length === 0) {
            foodTableBody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-muted">
                        No food items found matching your criteria. Add one on the left!
                    </td>
                </tr>
            `;
            return;
        }

        foods.forEach(food => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td><strong>#${food.foodId}</strong></td>
                <td><strong>${escapeHtml(food.foodName)}</strong></td>
                <td>${food.defaultServingSize} ${escapeHtml(food.servingUnit)}</td>
                <td><span class="badge-cal">${Number(food.calories).toFixed(1)} kcal</span></td>
                <td><span class="badge-pro">${Number(food.protein).toFixed(1)}g</span></td>
                <td><span class="badge-carb">${Number(food.carbohydrates).toFixed(1)}g</span></td>
                <td><span class="badge-fat">${Number(food.fats).toFixed(1)}g</span></td>
                <td>
                    <button class="btn-danger-sm btn-delete" data-id="${food.foodId}" data-name="${escapeHtml(food.foodName)}">
                        Delete
                    </button>
                </td>
            `;
            foodTableBody.appendChild(row);
        });

        // Attach delete listeners
        document.querySelectorAll('.btn-delete').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const id = btn.getAttribute('data-id');
                const name = btn.getAttribute('data-name');
                confirmDeleteFood(id, name);
            });
        });
    }

    // --- Add Food Form Handler ---
    addFoodForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const payload = {
            foodName: document.getElementById('foodName').value.trim(),
            servingUnit: document.getElementById('servingUnit').value,
            defaultServingSize: parseFloat(document.getElementById('defaultServingSize').value),
            calories: parseFloat(document.getElementById('calories').value),
            protein: parseFloat(document.getElementById('protein').value),
            carbohydrates: parseFloat(document.getElementById('carbohydrates').value),
            fats: parseFloat(document.getElementById('fats').value)
        };

        try {
            const response = await fetch(`${API_BASE}/api/foods`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const result = await response.json();

            if (response.ok && result.success) {
                showToast(`Food '${payload.foodName}' added successfully!`, 'success');
                addFoodForm.reset();
                document.getElementById('defaultServingSize').value = '100';
                loadFoods();
            } else {
                showToast(result.message || 'Error adding food item.', 'error');
            }
        } catch (error) {
            console.error('Submission error:', error);
            showToast('Failed to connect to Java server.', 'error');
        }
    });

    // --- Delete Food Handler ---
    async function confirmDeleteFood(foodId, foodName) {
        if (!confirm(`Are you sure you want to delete '${foodName}'?`)) {
            return;
        }

        try {
            const response = await fetch(`${API_BASE}/api/foods?id=${foodId}`, {
                method: 'DELETE'
            });

            const result = await response.json();

            if (response.ok && result.success) {
                showToast(`'${foodName}' was deleted.`, 'success');
                loadFoods();
            } else {
                showToast(result.message || 'Cannot delete food item.', 'error');
            }
        } catch (error) {
            console.error('Delete error:', error);
            showToast('Error communicating with server.', 'error');
        }
    }

    // --- Search & Sort Event Listeners ---
    let searchDebounceTimeout;
    foodSearch.addEventListener('input', (e) => {
        clearTimeout(searchDebounceTimeout);
        searchDebounceTimeout = setTimeout(() => {
            currentSearch = e.target.value.trim();
            loadFoods();
        }, 200);
    });

    foodSort.addEventListener('change', (e) => {
        currentSort = e.target.value;
        loadFoods();
    });

    btnToggleOrder.addEventListener('click', () => {
        isAscending = !isAscending;
        sortOrderIcon.textContent = isAscending ? '↑ Asc' : '↓ Desc';
        loadFoods();
    });

    // --- Toast Notifications ---
    function showToast(message, type = 'success') {
        toast.textContent = message;
        toast.className = `toast ${type}`;
        setTimeout(() => {
            toast.className = 'toast hidden';
        }, 3500);
    }

    function escapeHtml(text) {
        if (!text) return '';
        const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
        return text.replace(/[&<>"']/g, m => map[m]);
    }

    // Initial Load on page startup
    loadFoods();
});
