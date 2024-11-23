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

function getCurrentDateFormatted() {
    const date = new Date();
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}-${month}-${year}`;
}

async function downResult() {
    const roomName = room.roomName;
    const currentDate = getCurrentDateFormatted();

    await downloadFile(
        '/rooms/export-data',
        `Kết quả điểm danh - ${roomName} - ${currentDate}.xlsx`
    );
    await downloadFile(
        '/rooms/export-log',
        `Lịch sử phòng - ${roomName} - ${currentDate}.xlsx`
    );
}

window.onload = async e => await downResult();
