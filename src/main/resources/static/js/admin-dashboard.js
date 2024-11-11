
function showDetails(td){
    debugger
    const row =td.parentElement.parentElement;
    const data_id=row.getAttribute("data-id");
    const room = rooms.find(e => e.id === data_id);

    document.getElementById("name").innerText = room.name;
    document.getElementById("createBy").innerText = room.createBy;
    document.getElementById("latitude").innerText = room.latitude;
    document.getElementById("longitude").innerText = room.longitude;
    document.getElementById("acceptRange").innerText = room.acceptRange;
    document.getElementById("startTime").innerText = room.startTime;
    document.getElementById("endTime").innerText = room.endTime;
    document.getElementById("isEnded").innerText = room.isEnded;
    hiddenFalse();
}
//hiện đi chi tiết phòng
function hiddenFalse(){
    document.getElementById("detail-box").hidden=false;
}
//ẩn đi chi tiết phòng
function hideDetails(){
    document.getElementById("detail-box").hidden=true;
}
function deleteRoom(de){
    //sử dụng th:attr="data-id=${attendee.id} và các câu lệnh để lấy ra id
    debugger
    const row =de.parentElement.parentElement;
    const id=row.getAttribute("data-id");
    //bắt sự kiện click để chuyển sang đường dẫn mới(truyền id động)
    document.querySelector("#confirmDelete").addEventListener('click',function(){
        window.location.href=`dashboard/${id}/delete`;
    });
    hideDeleteFalse();
}
//hiện ra thông báo xoóa
function hideDeleteFalse(){
    document.getElementById("delete-box").hidden=false;
}
//ẩn đi thông báo xóa
function hideDeleteTrue(){
    document.getElementById("delete-box").hidden=true;
}
