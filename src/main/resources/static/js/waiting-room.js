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
function registerSocket() {
    stompClient.connect({}, frame => {
        stompClient.subscribe(`/topic/rooms/${data.roomId}`, next => {
            const message = JSON.parse(next.body);
            if(message.roomId != data.roomId) {
                return;
            }

            debugger
            if(message.type === MessageType.ATTENDEE_STATUS && message.data.attendeeId === data.attendeeId) {
                if(message.data.attendeeStatus === CheckInStatus.OUT_OF_ROOM) {
                    window.location.href = '/';
                }
            } else if(message.type === MessageType.CLOSE_ROOM) {
                const result = message.data[data.attendeeId];
                if(result.success) {
                    showNotification("Điểm danh thành công!", [{name: 'OK', action: () => window.location.href = '/'}])
                    
                } else {
                    showNotification("Điểm danh thất bại!", [{name: 'OK', action: () => window.location.href = '/'}])
                }
                notification.querySelector('.btn-close').onclick = () => window.location.href = '/';
            }
        });
    });
}

registerSocket()

function outRoom() {
    const json = {
        type: MessageType.ATTENDEE_STATUS,
        roomId: data.roomId,
        data: {
            attendeeId: data.attendeeId,
            attendeeStatus: CheckInStatus.OUT_OF_ROOM
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