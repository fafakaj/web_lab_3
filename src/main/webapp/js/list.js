const studentsTable = document.querySelector("#students-table");
const filterForm = document.querySelector("#filters");
const statusLine = document.querySelector("#status");
const pagination = document.querySelector("#pagination");

const COLUMN_LABELS = [
    "ФИО",
    "Группа",
    "ИСУ id",
    "Общежитие",
    "Комната",
    "Срок заселения",
    "Иностранец",
    "Действия"
];

// Поля формы над таблицей: фильтры, сортировка и размер страницы. Номер страницы — только в адресе
const FORM_NAMES = FILTER_NAMES.concat(["sort", "order", "size"]);
const BOOLEAN_PARAMS = ["is_foreign", "living"];
const NUMBER_PARAMS = ["page", "size"];

function fillDormitoryFilter() {
    const select = filterForm.elements.dormitory;

    Object.keys(DORMITORIES).forEach(function (code) {
        const option = document.createElement("option");
        option.value = code;
        option.textContent = DORMITORIES[code];
        select.appendChild(option);
    });
}

// Для метода QUERY значения уходят в JSON-теле, поэтому "true" и "2" надо превратить в true и 2
function parseParamValue(name, value) {
    if (BOOLEAN_PARAMS.indexOf(name) !== -1 && (value === "true" || value === "false")) {
        return value === "true";
    }

    if (NUMBER_PARAMS.indexOf(name) !== -1 && /^[0-9]+$/.test(value)) {
        return Number(value);
    }

    return value;
}

// Параметры списка берутся из адреса страницы: index.html?group=M3301&sort=room&page=2
function readParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const params = {};

    FORM_NAMES.forEach(function (name) {
        const value = (urlParams.get(name) || "").trim();

        if (value !== "") {
            filterForm.elements[name].value = value;
            params[name] = parseParamValue(name, value);
        }
    });

    const page = (urlParams.get("page") || "").trim();

    if (page !== "") {
        params.page = parseParamValue("page", page);
    }

    return params;
}

function describeError(error) {
    const parts = [error.message];

    for (let i = 0; i < error.details.length; i++) {
        parts.push(error.details[i].message);
    }

    return parts.join(". ");
}

function createCell(text, label) {
    const td = document.createElement("td");
    td.textContent = text;

    if (label) {
        td.dataset.label = label;
    }

    return td;
}

function createLink(text, href) {
    const a = document.createElement("a");
    a.textContent = text;
    a.href = href;
    return a;
}

// Изменять и удалять может только ADMIN, поэтому USER видит одну ссылку «Подробнее»
function createActionsCell(student) {
    const td = document.createElement("td");
    td.dataset.label = COLUMN_LABELS[7];

    td.appendChild(createLink("Подробнее", "student-info.html?id=" + student.isu_id));

    if (isAdmin()) {
        td.appendChild(createLink("Редактировать", "student-form.html?id=" + student.isu_id));

        const deleteButton = document.createElement("button");
        deleteButton.type = "button";
        deleteButton.textContent = "Удалить";
        deleteButton.dataset.id = student.isu_id;
        td.appendChild(deleteButton);
    }

    return td;
}

function showMessageRow(text) {
    const tbody = studentsTable.querySelector("tbody");
    const row = document.createElement("tr");
    const cell = createCell(text, "");

    cell.colSpan = COLUMN_LABELS.length;
    row.appendChild(cell);

    tbody.innerHTML = "";
    tbody.appendChild(row);
}

function showStudents(students) {
    const tbody = studentsTable.querySelector("tbody");

    if (students.length === 0) {
        showMessageRow("Студенты не найдены");
        return;
    }

    tbody.innerHTML = "";

    for (let i = 0; i < students.length; i++) {
        const student = students[i];
        const row = document.createElement("tr");

        row.appendChild(createCell(student.full_name, COLUMN_LABELS[0]));
        row.appendChild(createCell(student.group, COLUMN_LABELS[1]));
        row.appendChild(createCell(student.isu_id, COLUMN_LABELS[2]));
        row.appendChild(createCell(getDormitoryName(student.dormitory), COLUMN_LABELS[3]));
        row.appendChild(createCell(student.room, COLUMN_LABELS[4]));
        row.appendChild(createCell(formatPeriod(student.check_in, student.check_out), COLUMN_LABELS[5]));
        row.appendChild(createCell(student.is_foreign ? "да" : "нет", COLUMN_LABELS[6]));
        row.appendChild(createActionsCell(student));

        tbody.appendChild(row);
    }
}

// Адрес той же страницы списка, но с другим номером страницы: фильтры и сортировка сохраняются
function pageUrl(page) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set("page", page);
    return "index.html?" + urlParams.toString();
}

function createPageLink(text, page, enabled) {
    if (!enabled) {
        const span = document.createElement("span");
        span.className = "button button-disabled";
        span.textContent = text;
        return span;
    }

    const link = createLink(text, pageUrl(page));
    link.className = "button";
    return link;
}

// Под таблицей: «← Назад  Страница 2 из 3, всего 25  Вперёд →»
function showPagination(result) {
    const pageCount = Math.max(1, Math.ceil(result.total / result.size));
    const info = document.createElement("span");
    info.textContent = "Страница " + result.page + " из " + pageCount + ", всего студентов: " + result.total;

    pagination.innerHTML = "";
    pagination.appendChild(createPageLink("← Назад", result.page - 1, result.page > 1));
    pagination.appendChild(info);
    pagination.appendChild(createPageLink("Вперёд →", result.page + 1, result.page < pageCount));
}

async function loadStudents() {
    try {
        const result = await getStudents(currentParams);

        // Страницы с таким номером больше нет (например, удалили последнюю запись на ней) — к последней
        if (result.items.length === 0 && result.page > 1 && result.total > 0) {
            window.location.href = pageUrl(Math.ceil(result.total / result.size));
            return;
        }

        showStudents(result.items);
        showPagination(result);
    } catch (error) {
        showMessageRow("Не удалось загрузить список");
        pagination.innerHTML = "";
        statusLine.textContent = describeError(error);
    }
}

async function onTableClick(event) {
    if (event.target.tagName !== "BUTTON") {
        return;
    }

    if (!confirm("Удалить студента?")) {
        return;
    }

    statusLine.textContent = "";

    try {
        await deleteStudent(event.target.dataset.id);
    } catch (error) {
        statusLine.textContent = describeError(error);
    }

    await loadStudents();
}

let currentParams = {};

if (requireLogin()) {
    setupHeader();
    fillDormitoryFilter();
    currentParams = readParams();

    studentsTable.addEventListener("click", onTableClick);
    loadStudents();
}
