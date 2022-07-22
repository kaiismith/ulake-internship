import { ListCRUD } from "./crud/listcrud.js";
import { userApi, ingestionApi } from "./api.js";

window.crud = new ListCRUD({
    api: ingestionApi,
    name: "Data Collection Requests",
    nameField: "id",
    listFieldRenderer: [
        { data: "id" },
        { data: "userName" },
        { data: "description", render: (data, type, row) => `<a href="/ingestion/view?id=${data}">${data}</a>` },
        { data: "creationTime", render: (data, type, row) => formatTime(data) },
        { data: "endTime", render: (data, type, row) => formatTime(data) },
        { data: "folderId" }
    ],
    joins: {
        apiMethod: (a) => userApi.many(a),
        fkField: "ownerId",
        targetId: "id",
        targetField: "userName"
    }
});

$(document).ready(() => window.crud.ready());