const STORAGE_KEY = "clinic-api-v1";
const screenEl = document.getElementById("screen");
const noticeEl = document.getElementById("notice");
let db = load();
let tab = "patients";
let noticeTimer = null;

document.getElementById("load-sample").addEventListener("click", () => {
    db = sample();
    save();
    notice("Sample clinic loaded");
    render();
});

document.querySelectorAll(".tabs button").forEach((button) => {
    button.addEventListener("click", () => {
        tab = button.dataset.tab;
        clearNotice();
        render();
    });
});

function sample() {
    return {
        nextPatientId: 3,
        nextDoctorId: 3,
        nextAppointmentId: 3,
        nextRecordId: 2,
        patients: [
            { id: 1, name: "Maya Chen", email: "maya.chen@example.com", phoneNumber: "07700 900123", address: "12 New Walk, Leicester" },
            { id: 2, name: "Omar Hassan", email: "omar.hassan@example.com", phoneNumber: "07700 900456", address: "4 London Road, Leicester" }
        ],
        doctors: [
            { id: 1, name: "Dr Shah", email: "shah@example.com", specialisation: "Cardiology", phoneNumber: "0116 496 0001" },
            { id: 2, name: "Dr Patel", email: "patel@example.com", specialisation: "General practice", phoneNumber: "0116 496 0002" }
        ],
        appointments: [
            { id: 1, appointmentDate: "2026-03-12T09:30", status: "scheduled", notes: "Blood pressure check", patientId: 1, doctorId: 1 },
            { id: 2, appointmentDate: "2026-03-12T11:00", status: "completed", notes: "Follow-up", patientId: 2, doctorId: 2 }
        ],
        records: [
            { id: 1, recordDate: "2026-03-12T11:20", diagnosis: "Seasonal allergy", treatment: "Antihistamine", notes: "Review in four weeks", appointmentId: 2 }
        ]
    };
}

function load() {
    try {
        const saved = JSON.parse(localStorage.getItem(STORAGE_KEY));
        if (saved && Array.isArray(saved.patients)) return saved;
    } catch (error) {
        /* use the sample */
    }
    return sample();
}

function save() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(db));
}

function notice(message, isError) {
    noticeEl.hidden = false;
    noticeEl.textContent = message;
    noticeEl.className = isError ? "error" : "";
    clearTimeout(noticeTimer);
    noticeTimer = setTimeout(clearNotice, 2600);
}

function clearNotice() {
    noticeEl.hidden = true;
}

function patientById(id) {
    return db.patients.find((person) => person.id === id);
}

function doctorById(id) {
    return db.doctors.find((person) => person.id === id);
}

function appointmentById(id) {
    return db.appointments.find((item) => item.id === id);
}

function recordForAppointment(id) {
    return db.records.find((record) => record.appointmentId === id);
}

function createPatient(fields) {
    if (!fields.name || !fields.email || !fields.phoneNumber || !fields.address) {
        return { error: "Missing required fields: name, email, phoneNumber, address" };
    }
    const patient = { id: db.nextPatientId++, ...fields };
    db.patients.push(patient);
    save();
    return { created: patient };
}

function updatePatient(id, fields) {
    const patient = patientById(id);
    if (!patient) return { error: "Patient with id " + id + " not found" };
    ["name", "email", "phoneNumber", "address"].forEach((key) => {
        if (fields[key]) patient[key] = fields[key];
    });
    save();
    return { patient };
}

function deletePatient(id) {
    if (!patientById(id)) return { error: "Patient with id " + id + " not found" };
    const appointmentIds = db.appointments.filter((item) => item.patientId === id).map((item) => item.id);
    db.records = db.records.filter((record) => !appointmentIds.includes(record.appointmentId));
    db.appointments = db.appointments.filter((item) => item.patientId !== id);
    db.patients = db.patients.filter((person) => person.id !== id);
    save();
    return { ok: true };
}

function createDoctor(fields) {
    if (!fields.name || !fields.email || !fields.specialisation) {
        return { error: "Missing required fields: name, email, specialisation" };
    }
    const doctor = { id: db.nextDoctorId++, phoneNumber: "", ...fields };
    db.doctors.push(doctor);
    save();
    return { created: doctor };
}

function updateDoctor(id, fields) {
    const doctor = doctorById(id);
    if (!doctor) return { error: "Doctor with id " + id + " not found" };
    ["name", "email", "specialisation", "phoneNumber"].forEach((key) => {
        if (fields[key]) doctor[key] = fields[key];
    });
    save();
    return { doctor };
}

function deleteDoctor(id) {
    if (!doctorById(id)) return { error: "Doctor with id " + id + " not found" };
    const appointmentIds = db.appointments.filter((item) => item.doctorId === id).map((item) => item.id);
    db.records = db.records.filter((record) => !appointmentIds.includes(record.appointmentId));
    db.appointments = db.appointments.filter((item) => item.doctorId !== id);
    db.doctors = db.doctors.filter((person) => person.id !== id);
    save();
    return { ok: true };
}

function filteredAppointments(filter) {
    return db.appointments.filter((item) => {
        if (filter.patientId && item.patientId !== filter.patientId) return false;
        if (filter.doctorId && item.doctorId !== filter.doctorId) return false;
        if (filter.status && item.status !== filter.status) return false;
        return true;
    });
}

function createAppointment(fields) {
    if (!fields.appointmentDate || !fields.status || !fields.patientId || !fields.doctorId) {
        return { error: "Missing required fields: appointmentDate, status, patient, doctor" };
    }
    if (!patientById(fields.patientId)) return { error: "Patient with id " + fields.patientId + " not found" };
    if (!doctorById(fields.doctorId)) return { error: "Doctor with id " + fields.doctorId + " not found" };
    const appointment = {
        id: db.nextAppointmentId++,
        appointmentDate: fields.appointmentDate,
        status: fields.status,
        notes: fields.notes || "",
        patientId: fields.patientId,
        doctorId: fields.doctorId
    };
    db.appointments.push(appointment);
    save();
    return { created: appointment };
}

function deleteAppointment(id) {
    if (!appointmentById(id)) return { error: "Appointment with id " + id + " not found" };
    db.records = db.records.filter((record) => record.appointmentId !== id);
    db.appointments = db.appointments.filter((item) => item.id !== id);
    save();
    return { ok: true };
}

function createRecord(fields) {
    if (!fields.appointmentId || !fields.diagnosis || !fields.recordDate) {
        return { error: "Missing required fields: appointment, diagnosis, recordDate" };
    }
    const appointment = appointmentById(fields.appointmentId);
    if (!appointment) return { error: "Appointment with id " + fields.appointmentId + " not found" };
    if (recordForAppointment(appointment.id)) return { error: "Appointment already has a medical record" };
    const record = {
        id: db.nextRecordId++,
        recordDate: fields.recordDate,
        diagnosis: fields.diagnosis,
        treatment: fields.treatment || "",
        notes: fields.notes || "",
        appointmentId: appointment.id
    };
    db.records.push(record);
    save();
    return { created: record };
}

function render() {
    document.querySelectorAll(".tabs button").forEach((button) => {
        button.classList.toggle("active", button.dataset.tab === tab);
    });
    if (tab === "patients") renderPatients();
    if (tab === "doctors") renderDoctors();
    if (tab === "appointments") renderAppointments();
}

function renderPatients() {
    const cards = db.patients.map((patient) => personCard(patient, patient.address, "patient")).join("");
    screenEl.innerHTML = `
        <form class="form" id="patient-form">
            <h2>Add a patient</h2>
            ${field("Name", "name")}
            ${field("Email", "email")}
            ${field("Phone", "phoneNumber")}
            ${field("Address", "address")}
            <button type="submit">Add patient</button>
        </form>
        <h2 class="list-title">Patients</h2>
        ${cards || `<p class="empty">No patients found</p>`}
    `;
    bindPersonForm("patient-form", createPatient, "Patient added");
    bindPersonActions("patient");
}

function renderDoctors() {
    const cards = db.doctors.map((doctor) => personCard(doctor, doctor.specialisation, "doctor")).join("");
    screenEl.innerHTML = `
        <form class="form" id="doctor-form">
            <h2>Add a doctor</h2>
            ${field("Name", "name")}
            ${field("Email", "email")}
            ${field("Specialisation", "specialisation")}
            ${field("Phone", "phoneNumber")}
            <button type="submit">Add doctor</button>
        </form>
        <h2 class="list-title">Doctors</h2>
        ${cards || `<p class="empty">No doctors found</p>`}
    `;
    bindPersonForm("doctor-form", createDoctor, "Doctor added");
    bindPersonActions("doctor");
}

function personCard(person, extra, kind) {
    const appointments = db.appointments.filter((item) => item[kind + "Id"] === person.id);
    const lines = appointments.map((item) => `<span>${escapeHtml(formatWhen(item.appointmentDate))} · ${escapeHtml(item.status)} · ${escapeHtml(otherName(item, kind))}</span>`).join("");
    return `
        <article class="card">
            <strong>${escapeHtml(person.name)}</strong>
            <span>${escapeHtml(person.email)}</span>
            <span>${escapeHtml(extra)}</span>
            ${person.phoneNumber ? `<span>${escapeHtml(person.phoneNumber)}</span>` : ""}
            ${lines}
            <div class="actions">
                <button type="button" data-edit="${kind}" data-id="${person.id}">Edit</button>
                <button class="danger" type="button" data-delete="${kind}" data-id="${person.id}">Delete</button>
            </div>
            <form class="form" data-editor="${kind}-${person.id}" hidden></form>
        </article>
    `;
}

function otherName(appointment, kind) {
    if (kind === "patient") {
        const doctor = doctorById(appointment.doctorId);
        return doctor ? doctor.name : "Doctor missing";
    }
    const patient = patientById(appointment.patientId);
    return patient ? patient.name : "Patient missing";
}

function bindPersonForm(id, create, success) {
    const form = document.getElementById(id);
    form.addEventListener("submit", (event) => {
        event.preventDefault();
        const result = create(readFields(form));
        if (result.error) {
            notice(result.error, true);
            return;
        }
        notice(success);
        render();
    });
}

function bindPersonActions(kind) {
    screenEl.querySelectorAll(`[data-delete="${kind}"]`).forEach((button) => {
        button.addEventListener("click", () => {
            const id = Number(button.dataset.id);
            const result = kind === "patient" ? deletePatient(id) : deleteDoctor(id);
            if (result.error) {
                notice(result.error, true);
                return;
            }
            notice(kind === "patient" ? "Patient deleted" : "Doctor deleted");
            render();
        });
    });
    screenEl.querySelectorAll(`[data-edit="${kind}"]`).forEach((button) => {
        button.addEventListener("click", () => openEditor(kind, Number(button.dataset.id)));
    });
}

function openEditor(kind, id) {
    const person = kind === "patient" ? patientById(id) : doctorById(id);
    const form = screenEl.querySelector(`[data-editor="${kind}-${id}"]`);
    const fields = kind === "patient"
        ? ["name", "email", "phoneNumber", "address"]
        : ["name", "email", "specialisation", "phoneNumber"];
    form.hidden = false;
    form.innerHTML = fields.map((key) => `
        <label for="${kind}-${id}-${key}">${labelFor(key)}</label>
        <input id="${kind}-${id}-${key}" name="${key}" value="${escapeHtml(person[key] || "")}">
    `).join("") + `<button type="submit">Save</button>`;
    form.onsubmit = (event) => {
        event.preventDefault();
        const result = kind === "patient" ? updatePatient(id, readFields(form)) : updateDoctor(id, readFields(form));
        if (result.error) {
            notice(result.error, true);
            return;
        }
        notice("Saved");
        render();
    };
}

function renderAppointments() {
    const filter = {
        patientId: Number(screenEl.querySelector("[name=filterPatient]")?.value) || 0,
        doctorId: Number(screenEl.querySelector("[name=filterDoctor]")?.value) || 0,
        status: screenEl.querySelector("[name=filterStatus]")?.value || ""
    };
    const appointments = filteredAppointments(filter);
    const cards = appointments.map((item) => appointmentCard(item)).join("");
    const empty = appointments.length ? "" : `<p class="empty">No appointments found</p>`;
    screenEl.innerHTML = `
        <form class="form" id="appointment-form">
            <h2>Add an appointment</h2>
            <label for="appointmentDate">Date</label>
            <input id="appointmentDate" name="appointmentDate" type="datetime-local">
            <label for="status">Status</label>
            <select id="status" name="status">
                <option value="">Choose a status</option>
                <option value="scheduled">scheduled</option>
                <option value="completed">completed</option>
                <option value="cancelled">cancelled</option>
            </select>
            ${selectPeople("Patient", "patientId", db.patients)}
            ${selectPeople("Doctor", "doctorId", db.doctors)}
            <label for="notes">Notes</label>
            <textarea id="notes" name="notes"></textarea>
            <button type="submit">Add appointment</button>
        </form>
        <form class="filters" id="filters">
            ${selectPeople("Patient", "filterPatient", db.patients, true)}
            ${selectPeople("Doctor", "filterDoctor", db.doctors, true)}
            <label>Status
                <select name="filterStatus">
                    <option value="">All</option>
                    <option value="scheduled">scheduled</option>
                    <option value="completed">completed</option>
                    <option value="cancelled">cancelled</option>
                </select>
            </label>
            <button type="submit">Filter</button>
        </form>
        <h2 class="list-title">Appointments</h2>
        ${cards}${empty}
    `;
    if (filter.patientId) screenEl.querySelector("[name=filterPatient]").value = String(filter.patientId);
    if (filter.doctorId) screenEl.querySelector("[name=filterDoctor]").value = String(filter.doctorId);
    if (filter.status) screenEl.querySelector("[name=filterStatus]").value = filter.status;
    document.getElementById("appointment-form").addEventListener("submit", (event) => {
        event.preventDefault();
        const fields = readFields(event.target);
        fields.patientId = Number(fields.patientId);
        fields.doctorId = Number(fields.doctorId);
        const result = createAppointment(fields);
        if (result.error) {
            notice(result.error, true);
            return;
        }
        notice("Appointment added");
        render();
    });
    document.getElementById("filters").addEventListener("submit", (event) => {
        event.preventDefault();
        render();
    });
    screenEl.querySelectorAll("[data-delete-appointment]").forEach((button) => {
        button.addEventListener("click", () => {
            const result = deleteAppointment(Number(button.dataset.deleteAppointment));
            if (result.error) {
                notice(result.error, true);
                return;
            }
            notice("Appointment deleted");
            render();
        });
    });
    screenEl.querySelectorAll("[data-record]").forEach((button) => {
        button.addEventListener("click", () => openRecordForm(Number(button.dataset.record)));
    });
}

function appointmentCard(item) {
    const patient = patientById(item.patientId);
    const doctor = doctorById(item.doctorId);
    const record = recordForAppointment(item.id);
    const recordHtml = record
        ? `<span>Record: ${escapeHtml(record.diagnosis)}${record.treatment ? " · " + escapeHtml(record.treatment) : ""}</span>`
        : `<span>No medical record</span>`;
    return `
        <article class="card">
            <strong>${escapeHtml(formatWhen(item.appointmentDate))} · ${escapeHtml(item.status)}</strong>
            <span>${escapeHtml(patient ? patient.name : "Patient missing")} with ${escapeHtml(doctor ? doctor.name : "Doctor missing")}</span>
            ${item.notes ? `<span>${escapeHtml(item.notes)}</span>` : ""}
            ${recordHtml}
            <div class="actions">
                ${record ? "" : `<button type="button" data-record="${item.id}">Add medical record</button>`}
                <button class="danger" type="button" data-delete-appointment="${item.id}">Delete</button>
            </div>
            <form class="form" data-record-form="${item.id}" hidden></form>
        </article>
    `;
}

function openRecordForm(appointmentId) {
    const form = screenEl.querySelector(`[data-record-form="${appointmentId}"]`);
    form.hidden = false;
    form.innerHTML = `
        <label for="recordDate-${appointmentId}">Record date</label>
        <input id="recordDate-${appointmentId}" name="recordDate" type="datetime-local">
        <label for="diagnosis-${appointmentId}">Diagnosis</label>
        <input id="diagnosis-${appointmentId}" name="diagnosis">
        <label for="treatment-${appointmentId}">Treatment</label>
        <input id="treatment-${appointmentId}" name="treatment">
        <label for="recordNotes-${appointmentId}">Notes</label>
        <textarea id="recordNotes-${appointmentId}" name="notes"></textarea>
        <button type="submit">Save record</button>
    `;
    form.onsubmit = (event) => {
        event.preventDefault();
        const fields = readFields(form);
        fields.appointmentId = appointmentId;
        const result = createRecord(fields);
        if (result.error) {
            notice(result.error, true);
            return;
        }
        notice("Medical record added");
        render();
    };
}

function selectPeople(label, name, people, includeAll) {
    const options = people.map((person) => `<option value="${person.id}">${escapeHtml(person.name)}</option>`).join("");
    const first = includeAll ? `<option value="">All</option>` : `<option value="">Choose</option>`;
    return `<label>${escapeHtml(label)}<select name="${name}">${first}${options}</select></label>`;
}

function field(label, name) {
    return `<label for="${name}">${label}</label><input id="${name}" name="${name}" autocomplete="off">`;
}

function labelFor(key) {
    return { name: "Name", email: "Email", phoneNumber: "Phone", address: "Address", specialisation: "Specialisation" }[key];
}

function readFields(form) {
    const data = {};
    new FormData(form).forEach((value, key) => {
        data[key] = String(value).trim();
    });
    return data;
}

function formatWhen(value) {
    if (!value) return "";
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return value;
    return date.toLocaleString("en-GB", { day: "numeric", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit" });
}

function escapeHtml(value) {
    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;");
}

render();
