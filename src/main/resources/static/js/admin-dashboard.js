function showDetails(td){
    debugger
    const row =td.parentElement.parentElement;
    const data_id=row.getAttribute("data-id");
    const room = rooms.find(e => e.id === data_id);
    document.getElementById("name").innerText = room.name;
    document.getElementById("createBy").innerText = room.createBy;
    // document.getElementById("latitude").innerText = room.latitude;
    // document.getElementById("longitude").innerText = room.longitude;
    // document.getElementById("acceptRange").innerText = room.acceptRange;
    document.getElementById("startTime").innerText = room.startTime;
    document.getElementById("endTime").innerText = room.endTime;
    // document.getElementById("isEnded").innerText = room.isEnded;
    hiddenFalse();
}

function deleteRoom(de){
    //sử dụng th:attr="data-id=${attendee.id} và các câu lệnh để lấy ra id
    debugger
    const row =de.parentElement.parentElement;
    const id=row.getAttribute("data-id");
    showConfirm("Xác nhận xóa phòng này?", () => {
        fetch(`dashboard/${id}/delete`, {method: 'DELETE'})
        .then(res => {
            debugger
            if(res.ok) {
                showNotification("Xóa phòng thành công", [{name: 'OK', action: () => window.location.reload()}]);
            } else {
                showNotification("Xóa phòng thất bại", null)
            }
        });
    }, null);
}

window.onload = evt => {
    debugger
    const rows = document.querySelectorAll('tbody tr');
    rooms.forEach((e, index) => {
        const td = rows[index].querySelectorAll('td')[3];
        if(e.status == 0) {
            td.innerText = 'Chờ mở phòng';
        } else if(e.status == 1) {
            td.innerText = 'Đang mở';
        } else {
            td.innerText = 'Đã đóng';
            td.className = 'text-danger';
        }
        td.classList.add('text-center');
    });
}

// --------------------------- LOG ---------------------------------
const log = document.getElementById('log');

function showLogAboutRoom() {
    debugger
    log.querySelector("#log-title").innerText = `Lịch sử hoạt động của web`;
    fetch(`/logs/all`)
    .then(res => res.json())
    .then(data => loadLogTable(data));
}

function deleteAllSystemLog() {
    showConfirm("Xác nhận xóa hết log hệ thống", () => {
        fetch(`/admin/logs`, { method: 'DELETE' })
        .then(res => {
            if(res.ok) {
                showNotification("Xóa log thành công", null);
            } else {
                howNotification("Xóa log thất bại", null);
            }
        })
    }, null);
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

//----------------------MESSAGE ---------------------------
const notification = document.getElementById('notification');
const content = document.getElementById('content');
const action = document.getElementById('action');
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