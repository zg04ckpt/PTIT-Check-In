

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
    initRemainingTime();
    fetch('/attendees/get-ip')
        .then(res => res.text())
        .then(ip => connectSocket(ip));
}

//----------------- WEBSOCKET -------------------------

const MessageType = {
    ATTENDEE_STATUS: 0,
    ROOM_STATUS: 1,
}

var socket = new SockJS("/ws");
var stompClient = Stomp.over(socket);

function connectSocket(ip) {
    const headers = {
        isRoomOwner: true,
        ip: ip
    }
    
    stompClient.connect(headers, function(frame) {
        stompClient.subscribe("/topic/rooms/" + room.id, next => {
            debugger
            const message = JSON.parse(next.body);
            if(message.type === MessageType.ATTENDEE_STATUS) {
                const targetRow = Array.from(tableBody.rows).find(e => e.getAttribute('data-id') == message.data.attendeeId);
                setRowStatus(targetRow, message.data.attendeeStatus);

                const name = room.attendees.find(e => e.id).name;
                console.log(name);
                if(message.data.attendeeStatus == CheckInStatus.WAITING) {
                    showToast(`${name} đã vào phòng.`)
                }
            }
        });
    });
}

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

// --------------------------- LOG ---------------------------------
const log = document.getElementById('log');

function showLogAboutAttendee(index) {
    debugger
    const attendee = room.attendees[index];
    log.querySelector("#log-title").innerText = `Lịch sử hoạt động của ${attendee.name}`;
    const content = log.querySelector('tbody');
    content.innerHTML = ''
    fetch(`/logs/attendees/${attendee.id}`)
    .then(res => res.json())
    .then(data => loadLogTable(data));
}

function showLogAboutRoom() {
    debugger
    log.querySelector("#log-title").innerText = `Lịch sử hoạt động của phòng`;
    fetch(`/logs/room`)
    .then(res => res.json())
    .then(data => loadLogTable(data));
}

function loadLogTable(logs) {
    const content = log.querySelector('tbody');
    content.innerHTML = ''
    logs.forEach((e, index) => {
        debugger
        const row = document.createElement('tr');

        const order = document.createElement('td');
        order.innerText = index + 1;
        row.appendChild(order);

        const time = document.createElement('td');
        time.innerText = e.time;
        row.appendChild(time);

        const ip = document.createElement('td');
        ip.innerText = e.ip;
        row.appendChild(ip);

        const description = document.createElement('td');
        description.innerText = e.description;
        row.appendChild(description);

        content.appendChild(row);
    });
    log.hidden = false;
}

// --------------------------- DETAIL -------------------------------
const detail = document.getElementById('detail');

function showDetail() {
    loadDetailTable(room.attendees);
    detail.hidden = false;
}

function reloadAdditionalInfo() {
    fetch("/rooms/attendees-info")
    .then(res => res.json())
    .then(data => {
        debugger
        room.attendees = data;
        loadDetailTable(room.attendees);
        showNotification("Reload thành công", null)
    })
    .catch(error => showNotification("Reload thất bại", null));
}

function getViolation() {
    debugger
    const range = Number(detail.querySelector("#range").value);
    const device = detail.querySelector('#device').value;
    const ip = detail.querySelector('#ip').value;
    if(range != 0 || device != 'Any' || ip != '') {
        let filteredList = room.attendees;
        if(range != 0) {
            filteredList = filteredList.filter(e => e.range != '--' && e.distance > range);
        }
        if(device != 'Any') {
            filteredList = filteredList.filter(e => e.device != '--' && e.device != device);
        }
        if(ip != '') {
            filteredList = filteredList.filter(e => e.ip != '--' && e.ip != ip);
        }
        loadDetailTable(filteredList);
        detail.querySelector("#resetBtn").hidden = false;
        detail.querySelector("#filterBtn").hidden = true;
    }
}

function loadDetailTable(attendees) {
    debugger
    const content = detail.querySelector('tbody');
    content.innerHTML = ''
    attendees.forEach((e, index) => {
        const row = document.createElement('tr');

        const order = document.createElement('td');
        order.innerText = index + 1;
        row.appendChild(order);

        const name = document.createElement('td');
        name.innerText = e.name;
        row.appendChild(name);

        const checkInCode = document.createElement('td');
        checkInCode.innerText = e.checkInCode;
        row.appendChild(checkInCode);

        const status = document.createElement('td');
        if(e.checkInStatus == CheckInStatus.OUT_OF_ROOM) {
            status.innerText = 'Chưa vào';
        } else if(e.checkInStatus == CheckInStatus.WAITING) {
            status.innerText = 'Chờ duyệt';
        } else if(e.checkInStatus == CheckInStatus.ACCEPTED) {
            status.innerText = 'Đã duyệt';
        } else {
            status.innerText = 'Cấm';
        }
        row.appendChild(status);

        const device = document.createElement('td');
        device.innerText = e.device;
        row.appendChild(device);

        const browser = document.createElement('td');
        browser.innerText = e.browser;
        row.appendChild(browser);

        const distance = document.createElement('td');
        distance.innerText = e.distance == -1 ? '--' : e.distance.toFixed(3) + 'km';
        row.appendChild(distance);

        const ip = document.createElement('td');
        ip.innerText = e.ip;
        row.appendChild(ip);

        const attendOn = document.createElement('td');
        attendOn.innerText = e.attendOn;
        row.appendChild(attendOn);

        const action = document.createElement('td');
        action.className = "text-center";
        action.innerHTML = `<div class="dropdown">
                    <i class="bx bx-dots-vertical-rounded" data-bs-toggle="dropdown"></i>
                    <div class="dropdown-menu rounded-0 py-0" style="font-size: 12px;">
                      <button class="dropdown-item" onclick="showLogAboutAttendee(${index})">Xem log</button>
                      <a class="dropdown-item" onclick="showRealityLocation(${index})">Xem vị trí (GG map)</a>
                    </div>
                  </div>`
        row.appendChild(action);

        content.appendChild(row);
    });
}

function resetFilter() {
    detail.querySelector("#range").value = 0;
    detail.querySelector('#device').value = 'Any';
    detail.querySelector('#ip').value = '';
    loadDetailTable(room.attendees);
    detail.querySelector("#resetBtn").hidden = true;
    detail.querySelector("#filterBtn").hidden = false;
}

function showRealityLocation(index) {
    showNotification("Tính năng chưa phát triển", null);
    // const attendeeId = room.attendees[index].id;
    // fetch(`/rooms/get-ggmap-url?attendeeId=${attendeeId}`).then(res => {
    //     debugger
    //     if(res.redirected) {
    //         window.open(res.url, "_blank");
    //     }
    // });
}

// ----------------- OTHER --------------------
var h, m, s;
var sub;
const remainingTime = document.getElementById("remainingTime");

function initRemainingTime() {
    if(room.endTime) {
        const remaining = Math.floor((new Date(room.endTime) - new Date()) / 1000);
        remainingTime.hidden = false;
        h = Math.floor(remaining / 3600);
        m = Math.floor((remaining % 3600) / 60); 
        s = remaining % 60;
        sub = setInterval(updateRemainingTime, 1000);
    }
}

function updateRemainingTime() {
    remainingTime.querySelector('b').innerText = `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
    s--;
    if(s < 0) {
        m--;
        if(m < 0) {
            h--;
            if(h < 0) {
                window.location.href = '/rooms/result';
                clearInterval(sub);
            }
            m = 59
        }
        s = 59;
    }
}

function closeRoom() {
    showNotification(
        "Xác nhận đóng phòng điểm danh?",
        [
            { name: 'Hủy', action: () => notification.hidden = true },
            { name: 'Xác nhận', action: () => {
                notification.hidden = true;
                downResult();
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

async function downloadFile(url, filename) {
    try {
        const res = await fetch(url, { method: 'GET' });
        if (!res.ok) {
            throw new Error(`Failed to download file from ${url}`);
        }
        const blob = await res.blob();
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    } catch (error) {
        console.error('Download error:', error);
        alert(`Error: ${error.message}`);
    }
}

async function downResult() {
    const roomName = room.name;
    const currentDate = getCurrentDateFormatted();

    const tasks = [
        downloadFile(
            '/rooms/export-data',
            `${roomName}_${currentDate}.xlsx`
        ),
        downloadFile(
            '/rooms/export-log',
            `${roomName}_log_${currentDate}.xlsx`
        )
    ];

    await Promise.all(tasks);

    window.location.href = '/rooms/result';
}

function getCurrentDateFormatted() {
    const date = new Date();
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}-${month}-${year}`;
}

function showToast(msg) {
    const content = document.querySelector('#toast');
    content.querySelector('.toast-body').innerText = msg;
    content.classList.toggle('show');
    setTimeout(() => content.classList.toggle('show'), 2000);
}
