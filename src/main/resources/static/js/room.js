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
    const cols = row.querySelectorAll('td');
    switch (status) {
        case CheckInStatus.OUT_OF_ROOM:
            cols[3].innerText = 'Chưa vào phòng';
            row.className = 'bg-white text-dark';
            break;

        case CheckInStatus.WAITING:
            cols[3].innerText = 'Chờ duyệt';
            row.className = 'bg-secondary text-white';

            cols[4].innerHTML = '';
            const approvalBtn = document.createElement('button');
            approvalBtn.className = 'btn btn-sm btn-success py-0';
            approvalBtn.textContent = "Duyệt";
            cols[4].appendChild(approvalBtn);
            break;

        case CheckInStatus.ACCEPTED:
            cols[3].innerText = 'Đã duyệt';
            row.className = 'bg-success text-white';

            cols[4].innerHTML = '';
            const cancelBtn = document.createElement('button');
            cancelBtn.className = 'btn btn-sm btn-danger py-0';
            cancelBtn.textContent = "Hủy duyệt";
            cols[4].appendChild(cancelBtn);
            break;

        case CheckInStatus.REJECTED:
            cols[3].innerText = 'Từ chối';
            row.className = 'bg-danger text-white';
            break;
    }

    //change data value:
    debugger
    const realIndex = row.getAttribute('data-index');
    // room.attendees[realIndex].checkInStatus = status;
    changeData(`attendees[${realIndex}].checkInStatus`, status);
}

const resultData = document.getElementById('result-data');
function changeData(key, value) {
    resultData.elements[key].value = value;
}

function insertRowToBottomOfInRoomStatus(row) {
    const copy = row.cloneNode(true);
    tableBody.removeChild(row);
    var target = Array.from(tableBody.rows).find(e => e.querySelectorAll('td')[3].innerText == 'Chưa vào phòng');
    if(target != null) {
        tableBody.insertBefore(copy, target);
    } else {
        tableBody.appendChild(copy);
    }
}

function insertRowToTop(row) {
    const copy = row.cloneNode(true);
    tableBody.removeChild(row);
    tableBody.insertBefore(copy, tableBody.firstElementChild);
}

function insertRowToBottom(row) {
    const copy = row.cloneNode(true);
    tableBody.removeChild(row);
    tableBody.appendChild(copy);
}

tableBody.addEventListener('click', (event) => {
    if (event.target.classList.contains('btn-success')) {
        const row = event.target.closest('tr');
        setRowStatus(row, CheckInStatus.ACCEPTED);
        insertRowToBottomOfInRoomStatus(row);
        refreshOrder();
    } else if (event.target.classList.contains('btn-danger')) {
        const row = event.target.closest('tr');
        setRowStatus(row, CheckInStatus.WAITING);
        insertRowToTop(row);
        refreshOrder();
    }
});

function refreshOrder() {
    //chỉnh lại số thứ tự
    debugger
    const rows = tableBody.querySelectorAll('tr');
    rows.forEach((e, index) => {
        e.querySelector('td').innerText = (index + 1);
    });
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

var socket = new SockJS("/ws");
var stompClient = Stomp.over(socket);
stompClient.connect({}, function(frame) {
    stompClient.subscribe("/topic/room/" + room.id, data => {
        //data.body : là checkInId
        debugger
        var idx = Array.from(room.attendees).findIndex(e => e.checkInId == data.body);
        room.attendees[idx].checkInStatus = CheckInStatus.WAITING;

        var currentRow = Array.from(tableBody.rows).find(e => e.querySelectorAll('td')[1].innerText == data.body);
        var copy = currentRow.cloneNode(true);
        tableBody.removeChild(currentRow);

        //đổi button
        const buttons = copy.querySelectorAll('button');
        buttons[0].classList.remove('d-none');

        //đổi message trạng thái
        copy.querySelectorAll('td')[3].innerText = "Chờ duyệt";

        //move element to top
        tableBody.insertBefore(copy, tableBody.firstElementChild);
    });
});

// ----------------- Notification management -------------------------
const notification = document.getElementById('notification');
const content = document.getElementById('content');
const action = document.getElementById('action');

function showNotification(msg, actions) {
    content.innerText = msg;
    action.innerHTML = '';
    actions.forEach(e => {
        const btn = document.createElement('button');
        btn.className = "btn btn-outline-secondary btn-sm me-2";
        btn.onclick = e.action;
        btn.innerText = e.name;
        action.appendChild(btn);
    });
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
                debugger
                document.querySelector('form button').click();
            }}
        ]
    )
}
