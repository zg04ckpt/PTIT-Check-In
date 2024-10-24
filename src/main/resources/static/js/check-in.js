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



