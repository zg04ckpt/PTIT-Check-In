function getCurrentDateFormatted() {
    const date = new Date();
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}-${month}-${year}`;
}

function down() {
    let filename;
    fetch(
        '/rooms/export',
        { method: 'GET' }
    )
    .then(res => {
        debugger
        if(!res.ok) {
            throw new Error('Lỗi tải file');
        }
        filename = room.roomName + "_" + getCurrentDateFormatted() + ".xlsx";
        return res.blob();
    })
    .then(blob => {
        debugger
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    })
}

down()


// ------------------------- WEB SOCKET --------------------------------
