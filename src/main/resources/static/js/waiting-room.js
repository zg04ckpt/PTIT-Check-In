// ------------------------- WEB SOCKET -----------------------------
const RoomStatus = {
    PENDING: 0,
    OPENING: 1,
    CLOSED: 2
};

const MessageType = {
    ATTENDEE_STATUS: 0,
    ROOM_STATUS: 1,
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
            debugger
            if(message.type === MessageType.ATTENDEE_STATUS && message.data.attendeeId === data.attendeeId) {
                if(message.data.attendeeStatus === CheckInStatus.OUT_OF_ROOM) {
                    window.location.href = '/';
                }
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