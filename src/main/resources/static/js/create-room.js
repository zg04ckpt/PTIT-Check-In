// --- Upload list from excel
const getListCloseBtn = document.getElementById('getListCloseBtn')
const getListFromExcelBtn = document.getElementById('getListFromExcelBtn')
const previewTable = document.getElementById('previewTable')
const count = document.querySelector('#count b')
const saveBtn = document.getElementById('saveBtn')
const successLabel = document.getElementById('successLabel')
const list = document.getElementById('list')
let previewData = [];

document.getElementById('updateListBtn').onclick = e => {
    e.preventDefault()
    document.getElementById('uploadListDialog').hidden = false;
}

getListFromExcelBtn.onclick = e => {
    e.target.value = '';
    if(!idColumnInput.value || !nameColumnInput.value || !startRowInput.value) {
        showNotification("Vui lòng điền vị trí cột, dòng");
        e.preventDefault();
    }
}


getListFromExcelBtn.onchange = e => {
    const file = e.target.files[0];
    resetPreviewTable();
    if(file) {
        const reader = new FileReader();
        reader.onload = e => {
            const data = new Uint8Array(e.target.result);
            const workbook = XLSX.read(data, { type: 'array' });

            const sheetName = workbook.SheetNames[0];
            const sheet = workbook.Sheets[sheetName];

            const rowData = XLSX.utils.sheet_to_json(sheet, { header: 1 });
            debugger
            const idCol = document.getElementById('idColumnInput').value - 1;
            const nameCol = document.getElementById('nameColumnInput').value - 1;
            const startRow = document.getElementById('startRowInput').value - 1;
            let cnt = 0;
            for(let i = startRow; i < rowData.length; i++) {
                if (rowData[i][idCol] == null && rowData[i][nameCol] == null) {
                    continue;
                }
                cnt++;
                previewTable.querySelector('tbody').replaceChildren();
                const row = previewTable.insertRow();
                row.insertCell(0).textContent = cnt;
                row.insertCell(1).textContent = rowData[i][idCol];
                row.insertCell(2).textContent = rowData[i][nameCol];
                previewData.push({
                    id: rowData[i][idCol], 
                    name: rowData[i][nameCol]
                });
            }
            
            count.innerText = cnt;
        }
        reader.readAsArrayBuffer(file);
    }
}

function resetPreviewTable() {
    previewData = [];
    while (previewTable.rows.length > 1) {
        previewTable.deleteRow(1);
    }
}

saveBtn.onclick = e => {
    e.preventDefault();

    if(previewData.length == 0) {
        showNotification("Danh sách trống");
        return;
    }

    for (const e of previewData) {
        if (e['id'] === undefined || e['name'] === undefined) {
            showNotification("Danh sách không hợp lệ!");
            return;
        }
    }

    list.replaceChildren();
    previewData.forEach((e, index) => {
        const id = document.createElement('input');
        id.type = 'hidden'
        id.name = 'attendees[' + index + '].checkInCode';
        id.value = e.id;
        
        const name = document.createElement('input');
        name.type = 'hidden'
        name.name = 'attendees[' + index + '].name';
        name.value = e.name;

        list.appendChild(id);
        list.appendChild(name);
    });

    document.getElementById('successMess').hidden = false;
    document.getElementById('emptyMess').hidden = true;
    document.getElementById('uploadListDialog').hidden = true;
}

// --- Notification
function showNotification(msg) {
    const notification = document.getElementById('notification');
    document.getElementById('noti-msg').innerText = msg;
    notification.hidden = false;
}

// --- Get location
const getLocationMethodSelect = document.getElementById('getLocationMethodSelect')
const locationSelect = document.getElementById('locationSelect')
const positionInput = document.getElementById('positionInput')
const QR = document.getElementById('QR')
const latitudeInp = document.getElementById('latitude')
const longitudeInp = document.getElementById('longitude')

function saveLocation() {
    if(!latitudeInp.value || !longitudeInp.value) {
        showNotification("Lấy mốc vị trí thất bại");
        return;
    }
    // tạm thời
    latitudeInp.value = 20.98113532126582;
    longitudeInp.value = 105.78753244306769;
    showNotification("Lấy mốc vị trí thành công");
    document.getElementById('getLocationDialog').hidden = true;
}

getLocationMethodSelect.onchange = e => {
    const select = e.target.value; 
    if(select == 1) {
        locationSelect.hidden = false;
        positionInput.hidden = true;
        latitudeInp.value = 20.98113532126582;
        longitudeInp.value = 105.78753244306769;
        QR.hidden = true;
    } else if(select == 2) {
        locationSelect.hidden = true;
        positionInput.hidden = false;
        latitudeInp.value = null;
        longitudeInp.value = null;
        QR.hidden = true;
    } else {
        locationSelect.hidden = true;
        positionInput.hidden = true;
        latitudeInp.value = null;
        longitudeInp.value = null;
        QR.hidden = false;
    }
}

document.getElementById('getLocationBtn').onclick = e => {
    e.preventDefault();
    document.getElementById('getLocationDialog').hidden = false
}

// --- other
//fix lỗi quay lại trang cữ có form ko hợp lệ
window.history.pushState({}, window.location.href);
window.addEventListener('popstate', (event) => {
    event.preventDefault();
    window.location.href = '/';
});

window.onload = e => {
    document.querySelector('#successMess').hidden = data.attendees.length == 0
    document.querySelector('#emptyMess').hidden = data.attendees.length != 0

    const loc = document.getElementById('locationContainer');
    loc.querySelector('button').hidden = !data.requireCheckLocation;

    const start = document.getElementById('startContainer');
    start.querySelector('input').checked = data.startTime != null;
    start.querySelector('#startTimeInput').hidden = data.startTime == null;
    start.querySelector('#startTimeInput').value = data.startTime;
    start.querySelector('p').hidden = data.startTime != null;

    const end = document.getElementById('endContainer');
    end.querySelector('input').checked = data.endTime != null;
    end.querySelector('#endTimeInput').hidden = data.endTime == null;
    end.querySelector('#endTimeInput').value = data.endTime;
    end.querySelector('p').hidden = data.endTime != null;
}

document.querySelector("#backBtn").onclick = e => {
    e.preventDefault();
    window.location.href = '/'
}