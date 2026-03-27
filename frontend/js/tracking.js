$(document).ready(function () {

    const orderId = new URLSearchParams(window.location.search).get('orderId');

    if (!orderId) {
        $('#no-order').addClass('active');
        return;
    }

    $('#tracking-content').show();
    $('#order-id').text(orderId);

    // ── Status → step mapping ─────────────────────────────
    const statusMap = {
        'CREATED': 1,
        'KITCHEN_CONFIRMED': 2,
        'PAID': 3,
        'RIDER_ASSIGNED': 4,
        'NOTIFIED': 5,
        'TRACKING': 6,
        'CANCELLED': -1
    };

    let lastStep = 0;

    // ── Check Status ──────────────────────────────────────
    function checkStatus() {
        $.ajax({
            url: '/api/orders/' + orderId + '/status',
            method: 'GET',
            success: function (data) {
                updateTimeline(data);
            },
            error: function () {
                // silently retry on next interval
            }
        });
    }

    // ── Update Timeline ───────────────────────────────────
    function updateTimeline(data) {
        const currentStep = statusMap[data.status] || 0;

        // Handle cancellation
        if (data.status === 'CANCELLED') {
            $('#cancelled-banner').addClass('active');
            if (data.reason) {
                $('#cancel-reason').text(data.reason);
            }
            // Mark completed steps up to where it failed, then mark the next as cancelled
            for (let i = 1; i <= 6; i++) {
                const $step = $('#step-' + i);
                if ($step.hasClass('completed')) continue;
                // don't mark any more as completed
            }
            return;
        }

        // Animate newly completed steps
        for (let i = 1; i <= 6; i++) {
            const $step = $('#step-' + i);
            if (i <= currentStep && !$step.hasClass('completed')) {
                // Stagger animation for multiple steps completing at once
                (function (step, delay) {
                    setTimeout(function () {
                        $('#step-' + step).addClass('completed').removeClass('pending');
                    }, delay);
                })(i, (i - lastStep - 1) * 300);
            }
        }

        lastStep = currentStep;

        // Update rider name
        if (data.riderName) {
            $('#rider-name').text(data.riderName);
            $('#rider-detail').text('Rider: ' + data.riderName);
        }

        // Update tracking number
        if (data.trackingNumber) {
            $('#tracking-number').text(data.trackingNumber);
            $('#tracking-detail').text('Tracking: ' + data.trackingNumber);
        }
    }

    // ── Initial check ─────────────────────────────────────
    checkStatus();

    // ── Poll every 10 seconds — SLOW AND WASTEFUL ─────────
    // TODO: Replace with SSE (Server-Sent Events)
    setInterval(checkStatus, 10000);
});
