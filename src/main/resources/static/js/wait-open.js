var h, m, s;
var sub;
const time = document.getElementById('time');

window.onload = e => {
    h = Math.floor(remaining / 3600);
    m = Math.floor((remaining % 3600) / 60); 
    s = remaining % 60;

    sub = setInterval(update, 1000);
}

function update() {
    time.innerText = `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
    s--;
    if(s < 0) {
        m--;
        if(m < 0) {
            h--;
            if(h < 0) {
                openRoom();
            }
            m = 59
        }
        s = 59;
    }
}

function openRoom() {
    clearInterval(sub);
    debugger
    fetch('/rooms/open-room', {
        method: 'post',
        headers: {
            'Content-Type': 'application/json'
        },
        body: roomId
    })
    .then(response => {
        debugger
        if (response.redirected) {
            window.location.href = response.url; // Redirect to the new URL
        } else {
            return response.json();
        }
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

function showOpenRoomConfirm() {
    showConfirm("Xác nhận mở phòng ngay lúc này?", () => openRoom(), null)
}

function showCloseRoomConfirm() {
    showConfirm("Xác nhận muốn xóa phòng hiện tại?", () => {}, null)
}


// ----------------- Notification management -------------------------
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
