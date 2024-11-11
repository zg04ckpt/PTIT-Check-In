document.getElementById("backBtn").onclick = e => {
    e.preventDefault();
    window.location.href='/';
}

//fix lỗi quay lại trang cữ có form ko hợp lệ
window.history.pushState({}, window.location.href);
window.addEventListener('popstate', (event) => {
    event.preventDefault();
    window.location.href = '/';
});


document.getElementById("getLocationBtn").onclick = e => {
    e.preventDefault();
    getLocation();
}
function getLocation() {
    debugger
    navigator.geolocation.getCurrentPosition(
        position => {
            document.getElementById("latitude").value = position.coords.latitude;
            document.getElementById("longitude").value = position.coords.longitude;
            showNotification("Lấy vị trí thành công", null);
        }, 
        error => showNotification("Lấy vị trí thất bại", null)
    );
}

// ------------------------ Notification ------------------------------
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



