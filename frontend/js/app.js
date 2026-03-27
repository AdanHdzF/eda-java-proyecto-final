$(document).ready(function () {

    // ── Restaurant Menus ──────────────────────────────────
    const menus = {
        'La Pizzería de Mario': [
            { name: 'Margherita', price: 8.99 },
            { name: 'Pepperoni', price: 10.99 },
            { name: 'Pizza Imposible', price: 99.99, suspicious: true },
            { name: 'Calzone', price: 9.99 },
            { name: 'Tiramisú', price: 6.99 }
        ],
        'Sushi Tokio Express': [
            { name: 'California Roll', price: 12.99 },
            { name: 'Sashimi Mix', price: 18.99 },
            { name: 'Edamame', price: 5.99 },
            { name: 'Ramen Tonkotsu', price: 14.99 }
        ],
        'Tacos El Patrón': [
            { name: 'Tacos al Pastor (3)', price: 9.99 },
            { name: 'Burrito Supreme', price: 11.99 },
            { name: 'Quesadilla', price: 7.99 },
            { name: 'Nachos Loaded', price: 10.99 },
            { name: 'Churros', price: 5.99 }
        ],
        'Burger Palace': [
            { name: 'Classic Burger', price: 9.99 },
            { name: 'Bacon Deluxe', price: 13.99 },
            { name: 'Veggie Burger', price: 10.99 },
            { name: 'Cheese Fries', price: 6.99 }
        ],
        'Pasta Nonna': [
            { name: 'Spaghetti Carbonara', price: 12.99 },
            { name: 'Lasagna', price: 13.99 },
            { name: 'Risotto Funghi', price: 14.99 },
            { name: 'Bruschetta', price: 7.99 }
        ]
    };

    // State: item quantities keyed by item name
    let quantities = {};

    // ── Restaurant Selection ──────────────────────────────
    $('#restaurant').on('change', function () {
        const restaurant = $(this).val();
        quantities = {};
        if (!restaurant) {
            $('#menu-card').slideUp(200);
            updateSummary();
            return;
        }
        renderMenu(restaurant);
        $('#menu-card').slideDown(300);
        updateSummary();
    });

    // ── Render Menu ───────────────────────────────────────
    function renderMenu(restaurant) {
        const items = menus[restaurant];
        const $grid = $('#menu-grid').empty();
        $('#menu-title').text('📋 ' + restaurant);

        items.forEach(function (item) {
            const suspicious = item.suspicious ? ' suspicious' : '';
            const badge = item.suspicious
                ? '<span class="suspicious-badge">🔥 YOLO</span>'
                : '';
            const $el = $(`
                <div class="menu-item${suspicious}" data-name="${item.name}" data-price="${item.price}">
                    ${badge}
                    <div class="menu-item-name">${item.name}</div>
                    <div class="menu-item-price">$${item.price.toFixed(2)}</div>
                    <div class="qty-controls">
                        <button class="qty-btn btn-minus" type="button">−</button>
                        <span class="qty-value">0</span>
                        <button class="qty-btn btn-plus" type="button">+</button>
                    </div>
                </div>
            `);
            $grid.append($el);
        });
    }

    // ── Quantity Buttons (delegated) ──────────────────────
    $('#menu-grid').on('click', '.btn-plus', function () {
        const $item = $(this).closest('.menu-item');
        const name = $item.data('name');
        quantities[name] = (quantities[name] || 0) + 1;
        const $val = $item.find('.qty-value');
        $val.text(quantities[name]).addClass('has-items');
        updateSummary();
    });

    $('#menu-grid').on('click', '.btn-minus', function () {
        const $item = $(this).closest('.menu-item');
        const name = $item.data('name');
        if (!quantities[name]) return;
        quantities[name] = Math.max(0, quantities[name] - 1);
        const $val = $item.find('.qty-value');
        $val.text(quantities[name]);
        if (quantities[name] === 0) {
            $val.removeClass('has-items');
            delete quantities[name];
        }
        updateSummary();
    });

    // ── Update Summary ────────────────────────────────────
    function updateSummary() {
        const $content = $('#summary-content');
        const restaurant = $('#restaurant').val();
        const items = restaurant ? menus[restaurant] : [];
        const priceMap = {};
        items.forEach(function (i) { priceMap[i.name] = i.price; });

        const selected = Object.keys(quantities).filter(function (k) { return quantities[k] > 0; });

        if (selected.length === 0) {
            $content.html('<p class="empty-summary">Agrega productos de un restaurante para comenzar</p>');
            $('#btn-order').prop('disabled', true);
            return;
        }

        let total = 0;
        let html = '<ul class="summary-list">';
        selected.forEach(function (name) {
            const qty = quantities[name];
            const price = priceMap[name];
            const subtotal = qty * price;
            total += subtotal;
            html += `<li class="summary-item">
                <span>${qty}x ${name}</span>
                <span>$${subtotal.toFixed(2)}</span>
            </li>`;
        });
        html += '</ul>';
        html += `<div class="summary-total">
            <span>Total</span>
            <span class="price">$${total.toFixed(2)}</span>
        </div>`;

        $content.html(html);
        $('#btn-order').prop('disabled', false);
    }

    // ── Submit Order ──────────────────────────────────────
    $('#btn-order').on('click', function () {
        const customerName = $('#customer-name').val().trim();
        if (!customerName) {
            showToast('Por favor ingresa tu nombre', 'error');
            $('#customer-name').focus();
            return;
        }

        const restaurant = $('#restaurant').val();
        const items = menus[restaurant];
        const priceMap = {};
        items.forEach(function (i) { priceMap[i.name] = i.price; });

        const orderItems = [];
        Object.keys(quantities).forEach(function (name) {
            if (quantities[name] > 0) {
                orderItems.push({
                    productName: name,
                    quantity: quantities[name],
                    price: priceMap[name]
                });
            }
        });

        if (orderItems.length === 0) return;

        const total = orderItems.reduce(function (sum, i) { return sum + i.price * i.quantity; }, 0);

        const payload = {
            customerName: customerName,
            restaurantName: restaurant,
            items: orderItems
        };

        // Show loading
        $('#loading-overlay').addClass('active');
        $('#btn-order').prop('disabled', true);

        // Timer
        let elapsed = 0;
        const timerInterval = setInterval(function () {
            elapsed++;
            $('#loading-timer').text(elapsed + 's');
        }, 1000);

        $.ajax({
            url: '/api/orders',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(payload),
            timeout: 120000,
            success: function (data) {
                clearInterval(timerInterval);
                $('#loading-overlay').removeClass('active');
                const orderId = data.orderId || data.id;
                showToast('Pedido creado correctamente', 'success');

                // Show success, hide form
                $('#order-form').hide();
                $('#success-message').addClass('active');
                $('#tracking-link').attr('href', 'tracking.html?orderId=' + orderId);
            },
            error: function (xhr) {
                clearInterval(timerInterval);
                $('#loading-overlay').removeClass('active');
                $('#btn-order').prop('disabled', false);

                let msg = 'Error al procesar tu pedido';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    msg = xhr.responseJSON.message;
                }
                showToast(msg, 'error');
            }
        });
    });

    // ── Toast Helper ──────────────────────────────────────
    function showToast(message, type) {
        type = type || 'info';
        const icon = type === 'success' ? '✅' : type === 'error' ? '❌' : 'ℹ️';
        const $toast = $(`<div class="toast ${type}"><span>${icon}</span><span>${message}</span></div>`);
        $('#toast-container').append($toast);
        setTimeout(function () {
            $toast.css('animation', 'slideOut 0.3s ease-in forwards');
            setTimeout(function () { $toast.remove(); }, 300);
        }, 4000);
    }
});
