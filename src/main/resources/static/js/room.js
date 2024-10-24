// ----------------------- Attendee management -------------------------
const CheckInStatus = {
    OUT_OF_ROOM: 0,
    WAITING: 1,
    ACCEPTED: 2,
    REJECTED: 3
};

const tableBody = document.querySelector('table tbody');

function setAllRowsStatus() {
    const rows = tableBody.querySelectorAll('tr');
    room.attendees.forEach((e, i) => {
        setRowStatus(rows[i], e.checkInStatus);
    });
}

function setRowStatus(row, status) {
    debugger
    const cols = row.querySelectorAll('td');
    const attendeeId = row.getAttribute('data-id');
    switch (status) {
        case CheckInStatus.OUT_OF_ROOM:
            cols[3].innerText = 'Chưa vào phòng';
            cols[4].innerHTML = '';
            row.className = 'bg-white text-dark';
            row.setAttribute("data-status", CheckInStatus.OUT_OF_ROOM);
            break;

        case CheckInStatus.WAITING:
            cols[3].innerText = 'Chờ duyệt';
            row.className = 'bg-secondary text-white';
            row.setAttribute("data-status", CheckInStatus.WAITING);

            cols[4].innerHTML = '';
            const approvalBtn = document.createElement('button');
            approvalBtn.className = 'btn btn-sm btn-success py-0';
            approvalBtn.textContent = "Duyệt";
            approvalBtn.onclick = () => setStatus(attendeeId, CheckInStatus.ACCEPTED);
            cols[4].appendChild(approvalBtn);
            break;

        case CheckInStatus.ACCEPTED:
            cols[3].innerText = 'Đã duyệt';
            row.className = 'bg-success text-white';
            row.setAttribute("data-status", CheckInStatus.ACCEPTED);

            cols[4].innerHTML = '';
            const cancelBtn = document.createElement('button');
            cancelBtn.className = 'btn btn-sm btn-danger py-0';
            cancelBtn.textContent = "Hủy duyệt";
            cancelBtn.onclick = () => setStatus(attendeeId, CheckInStatus.WAITING);
            cols[4].appendChild(cancelBtn);
            break;

        case CheckInStatus.REJECTED:
            cols[3].innerText = 'Từ chối';
            cols[4].innerHTML = '';
            row.className = 'bg-danger text-white';
            row.setAttribute("data-status", CheckInStatus.REJECTED);
            break;
    }
}

window.onload = e => {
    setAllRowsStatus();
}

//----------------- WEBSOCKET -------------------------
function calculateDistance(lat1, lon1, lat2, lon2) {
    const DEG_TO_RAD = Math.PI / 180;
    const R = 63710088; // Bán kính Trái Đất chính xác hơn (m)
    
    const dLat = (lat2 - lat1) * DEG_TO_RAD;
    const dLon = (lon2 - lon1) * DEG_TO_RAD;

    const lat1Rad = lat1 * DEG_TO_RAD;
    const lat2Rad = lat2 * DEG_TO_RAD;

    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(lat1Rad) * Math.cos(lat2Rad) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c; // Khoảng cách tính bằng m
}

const MessageType = {
    ATTENDEE_STATUS: 0,
    ROOM_STATUS: 1,
}

var socket = new SockJS("/ws");
var stompClient = Stomp.over(socket);
stompClient.connect({}, function(frame) {
    stompClient.subscribe("/topic/rooms/" + room.id, next => {
        /**next.body = {
         *     type: MessageType
         *     data: any
         * }
         */
        debugger
        const message = JSON.parse(next.body);
        if(message.type === MessageType.ATTENDEE_STATUS) {
            const targetRow = Array.from(tableBody.rows).find(e => e.getAttribute('data-id') == message.data.attendeeId);
            setRowStatus(targetRow, message.data.attendeeStatus);
        }
    });
});

function setStatus(id, status) {
    const json = {
        type: MessageType.ATTENDEE_STATUS,
        roomId: room.id,
        data : {
            attendeeId: id,
            attendeeStatus: status
        }
    };
    stompClient.send(`/app/setAttendeeStatus`, {}, JSON.stringify(json));
}

function setCloseStatus() {

}

// ----------------- Notification management -------------------------
const notification = document.getElementById('notification');
const content = document.getElementById('content');
const action = document.getElementById('action');

function showNotification(msg, actions) {
    content.innerText = msg;
    action.innerHTML = '';
    if(actions) {
        actions.forEach(e => {
            const btn = document.createElement('button');
            btn.className = "btn btn-outline-secondary btn-sm me-2";
            btn.onclick = e.action;
            btn.innerText = e.name;
            action.appendChild(btn);
        });
    } else {
        const accept = document.createElement('button');
        accept.className = "btn btn-outline-success btn-sm me-2";
        accept.onclick = () => {
            notification.hidden = true;
        }
        accept.innerText = 'OK';
        accept.style.width = '100px';
        action.appendChild(accept);
    }
    notification.hidden = false;
}

function showConfirm(msg, onConfirm, onCancel) {
    content.innerText = msg;
    action.innerHTML = '';

    const cancel = document.createElement('button');
    cancel.className = "btn btn-outline-secondary btn-sm me-2";
    cancel.onclick = () => {
        notification.hidden = true;
        if(onCancel) onCancel();
    }
    cancel.innerText = 'Hủy';
    cancel.style.width = '100px';
    action.appendChild(cancel);


    const accept = document.createElement('button');
    accept.className = "btn btn-outline-success btn-sm me-2";
    accept.onclick = () => {
        notification.hidden = true;
        onConfirm();
    }
    accept.innerText = 'Xác nhận';
    accept.style.width = '100px';
    action.appendChild(accept);

    
    notification.hidden = false;
}

// ----------------- OTHER --------------------
function closeRoom() {
    showNotification(
        "Xác nhận đóng phòng điểm danh?",
        [
            { name: 'Hủy', action: () => notification.hidden = true },
            { name: 'Xác nhận', action: () => {
                notification.hidden = true;
                window.location.href = '/rooms/result?roomId=' + room.id;
            }}
        ]
    )
}

function acceptAll() {
    showConfirm("Xác nhận duyệt tất cả?", () => {
        const rows = tableBody.querySelectorAll('tr')
        rows.forEach(e => {
            if(e.getAttribute('data-status') == '1')
                setStatus(e.getAttribute('data-id'), CheckInStatus.ACCEPTED);
        });
    }, null);
}

function copyCheckInUrl() {
    navigator.clipboard.writeText(room.url).then(() => {
        showNotification('Đã sao chép link tham gia', null);
    }).catch(err => {
        showNotification('Sao chép link thất bại (err)', null);
    });
}

function copyRoomCode() {
    navigator.clipboard.writeText(room.code).then(() => {
        showNotification('Đã sao chép mã phòng', null);
    }).catch(err => {
        showNotification('Sao chép mã phòng thất bại (err)', null);
    });
}

function generateQRCode() {
    const qrImage = document.getElementById('qrImage');
    const apiUrl = `https://api.qrserver.com/v1/create-qr-code/?data=${encodeURIComponent(room.url)}&size=300x300`;
    qrImage.src = apiUrl;
}