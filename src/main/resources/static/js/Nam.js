
function toggleEditListAndSetup(button) {
    const blockElement = document.querySelector('.block');
    blockElement.style.display = blockElement.style.display === 'block' ? 'none' : 'block';
}

function toggleQRCode() {
    const qrCodeElement = document.querySelector('.qr-code-container');
    qrCodeElement.style.display = qrCodeElement.style.display === 'block' ? 'none' : 'block';
}

function toggleAutoCheckIn(button) {
    button.textContent = button.textContent === 'Auto điểm danh' ? 'Auto ❌' : 'Auto điểm danh';
}

function toggleConfirmation() {
    const confirmationElement = document.querySelector('.confirmation');
    confirmationElement.style.display = confirmationElement.style.display === 'block' ? 'none' : 'block';
}

function toggleAttendance(button, row) {
    const statusCell = row.cells[3];
    const checkButtonCell = row.cells[4];
    const resultCell = row.cells[5];

    if (statusCell.textContent.trim() === 'Chờ điểm danh') {
        statusCell.textContent = 'Đã điểm danh';
        button.textContent = 'Hủy điểm danh';
        button.classList.remove('checkin');
        button.classList.add('cancel-checkin');
    }
    else {
        statusCell.textContent = 'Chờ điểm danh';
        button.textContent = 'Điểm danh';
        button.classList.remove('cancel-checkin');
        button.classList.add('checkin');

    }

    updateRowColors();
}
function updateRowColors() {
    const table = document.getElementById('attendanceTable');
    const rows = table.querySelectorAll('tbody tr');

    rows.forEach(row => {
        const status = row.children[3].textContent.trim();
        const button = row.children[4].querySelector('button');
        const result = row.children[5].textContent.trim();


        row.classList.remove('green', 'red', 'white', 'gray');

        if (status === 'Đã điểm danh' && button && button.textContent.trim() === 'Hủy điểm danh' && result.includes('Vị trí hợp lệ')) {
            row.classList.add('green');
        } else if (result.includes('Vượt quá phạm vi')) {
            row.classList.add('red');
        } else if (status === 'Đã điểm danh' && button && button.textContent.trim() === 'Điểm danh' && result.includes('Vị trí hợp lệ')) {
            row.classList.add('white');
        } else if (status === 'Chờ điểm danh') {
            row.classList.add('gray');
        }
    });
}

function addCheckinEvent() {
    const rows = document.querySelectorAll('#attendanceTable tbody tr');

    rows.forEach(row => {
        const checkinButton = row.cells[4].querySelector('button');
        if (checkinButton) {
            checkinButton.addEventListener('click', function() {
                toggleAttendance(checkinButton, row);
            });
        }
    });
}

window.onload = function() {
    addCheckinEvent();
    updateRowColors();
};
