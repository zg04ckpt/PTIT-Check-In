// ------------------------------- GET LIST ----------------------------
console.log("script works!");

const getListBtn = document.getElementById('getListBtn')
const getListDialog = document.getElementById('getListDialog')
const getListCloseBtn = document.getElementById('getListCloseBtn')
const getListFromExcelBtn = document.getElementById('getListFromExcelBtn')
const previewTable = document.getElementById('previewTable')
const count = document.querySelector('#count b')
const saveBtn = document.getElementById('saveBtn')
const successLabel = document.getElementById('successLabel')
const list = document.getElementById('list')
let previewData = [];

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

    successLabel.hidden = false;
    console.log(data);
}

// ----------------------------- GET LOCATION -----------------------
const getLocationDialog = document.getElementById('getLocationDialog')
const getLocationBtn = document.getElementById('getLocationBtn')
const getLocationCloseBtn = document.getElementById('getLocationCloseBtn')
const getLocationMethodSelect = document.getElementById('getLocationMethodSelect')
const locationSelect = document.getElementById('locationSelect')
const positionInput = document.getElementById('positionInput')
const QR = document.getElementById('QR')
const saveLocationBtn = document.getElementById('saveLocationBtn')
const rangeSelect = document.getElementById('rangeSelect')
const latitudeInp = document.getElementById('latitude')
const longitudeInp = document.getElementById('longitude')

function saveLocation() {
    const selection = getLocationMethodSelect.value;
    if(selection == 1) {
        // Tạm thời
        latitudeInp.value = 20.98113532126582;
        longitudeInp.value = 105.78753244306769;
    }
    showNotification("Lấy vị trí thành công");
}

getLocationMethodSelect.onchange = e => {
    const select = e.target.value; 
    if(select == 1) {
        locationSelect.hidden = false;
        positionInput.hidden = true;

        QR.hidden = true;
    } else if(select == 2) {
        locationSelect.hidden = true;
        positionInput.hidden = false;
        QR.hidden = true;
    } else {
        locationSelect.hidden = true;
        positionInput.hidden = true;
        QR.hidden = false;
    }
}

// -------------------------- OTHER -----------------------------
const startTimeInput = document.getElementById('check2')
const endTimeInput = document.getElementById('check3')

startTimeInput.onchange = e => {
    data.startTime = startTimeInput.value;
}

endTimeInput.onchange = e => {
    data.endTime = endTimeInput.value;
}

function showNotification(msg) {
    const notification = document.getElementById('notification');
    document.getElementById('noti-msg').innerText = msg;
    notification.hidden = false;
}


// --------------------------- Hưng --------------------------------
const checkbox1 = document.getElementById('check1');
const additionalInfo1 = document.getElementById('info1');
checkbox1.addEventListener('change', () => {
    additionalInfo1.hidden = !additionalInfo1.hidden;
});

const checkbox2 = document.getElementById('check2');
const additionalInfo2 = document.getElementById('info2');
checkbox2.addEventListener('change', () => {
    additionalInfo2.hidden = !additionalInfo2.hidden;
    const lb2 = additionalInfo2.parentElement.querySelector('p');
    lb2.hidden = !lb2.hidden;
});

const checkbox3 = document.getElementById('check3');
const additionalInfo3 = document.getElementById('info3');
checkbox3.addEventListener('change', () => {
    additionalInfo3.hidden = !additionalInfo3.hidden;
    const lb3 = additionalInfo3.parentElement.querySelector('p');
    lb3.hidden = !lb3.hidden;
});

document.getElementById('getListBtn').onclick = e => {
    e.preventDefault();
}

document.getElementById('check7').onclick = e => {
    e.preventDefault();
}

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
window.onload = e => {
    additionalInfo1.hidden = !data.requireCheckLocation;

    additionalInfo2.hidden = data.startTime == null;
    additionalInfo2.querySelector('input').value = data.startTime;
    additionalInfo2.parentElement.querySelector('p').hidden = !additionalInfo2.hidden;
    checkbox2.checked = data.startTime != null;

    additionalInfo3.hidden = data.endTime == null;
    additionalInfo3.querySelector('input').value = data.endTime;
    additionalInfo3.parentElement.querySelector('p').hidden = !additionalInfo3.hidden;
    checkbox3.checked = data.endTime != null;
}

