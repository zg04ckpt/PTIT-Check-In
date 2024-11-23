// ------------------------- WEB SOCKET -----------------------------
const RoomStatus = {
    PENDING: 0,
    OPENING: 1,
    CLOSED: 2
};

const MessageType = {
    ATTENDEE_STATUS: 0,
    ROOM_STATUS: 1,
    CLOSE_ROOM: 2,
}

const CheckInStatus = {
    OUT_OF_ROOM: 0,
    WAITING: 1,
    ACCEPTED: 2,
    REJECTED: 3
};

var socket = new SockJS('/ws');
var stompClient = Stomp.over(socket);
function connectSocket(ip) {
    // thông tin người tham gia gửi đi để đăng kí lắng nghe event
    const headers = {
        isRoomOwner: false,
        attendeeId: data.attendeeId,
        roomId: data.roomId,
        ip: ip,
    }

    stompClient.connect(headers, frame => {
        stompClient.subscribe(`/topic/rooms/${data.roomId}`, next => {
            const message = JSON.parse(next.body);
            if(message.roomId != data.roomId) {
                return;
            }

            debugger
            if(message.type === MessageType.ATTENDEE_STATUS && message.data.attendeeId === data.attendeeId) {
                if(message.data.attendeeStatus === CheckInStatus.REJECTED) {
                    window.location.href = '/';
                }
            } else if(message.type === MessageType.CLOSE_ROOM) {
                const result = message.data[data.attendeeId];
                if(result.success) {
                    showNotification("Điểm danh thành công!", [{name: 'OK', action: () => outRoom()}])
                    
                } else {
                    showNotification("Điểm danh thất bại!", [{name: 'OK', action: () => outRoom()}])
                }
                notification.querySelector('.btn-close').onclick = () => window.location.href = '/';
            }
        });
    });
}

function sendOutRoomMessage() {
    const json = {
        type: MessageType.ATTENDEE_STATUS,
        roomId: data.roomId,
        data: {
            attendeeId: data.attendeeId,
            attendeeStatus: CheckInStatus.OUT_OF_ROOM
        }
    }
    stompClient.send(`/app/setAttendeeStatus`, {}, JSON.stringify(json));
    console.log(json);
}

function sendOnRoomMessage() {
    const json = {
        type: MessageType.ATTENDEE_STATUS,
        roomId: data.roomId,
        data: {
            attendeeId: data.attendeeId,
            attendeeStatus: CheckInStatus.WAITING
        }
    }
    stompClient.send(`/app/setAttendeeStatus`, {}, JSON.stringify(json));
}

//----------------------MESSAGE ---------------------------
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
            btn.style.width = '100px';
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

// ----------------------- TIME REMAINING ----------------------------
var h, m, s;
var sub;
const remainingTime = document.getElementById("remainingTime");

function initRemainingTime() {
    if(data.end) {
        const remaining = Math.floor((new Date(data.end) - new Date()) / 1000);
        remainingTime.hidden = false;
        h = Math.floor(remaining / 3600);
        m = Math.floor((remaining % 3600) / 60); 
        s = remaining % 60;
        sub = setInterval(updateRemainingTime, 1000);
    }
}

function updateRemainingTime() {
    remainingTime.innerText = `Còn lại: ${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
    s--;
    if(s < 0) {
        m--;
        if(m < 0) {
            h--;
            if(h < 0) {
                clearInterval(sub);
            }
            m = 59
        }
        s = 59;
    }
}


// -------------------------------- OTHER --------------------------------
window.onload = e => {
    initRemainingTime();
    fetch('/attendees/get-ip')
        .then(res => res.text())
        .then(ip => connectSocket(ip));
}

function outRoom() {
    fetch(`/attendees/clear-session`)
    .then(res => {
        if(res.redirected) {
            window.location.href = res.url;
        }
    });
}

document.addEventListener('visibilitychange', function() {
    if (document.hidden) {
        sendOutRoomMessage();
    } else {
        sendOnRoomMessage();
    }
});