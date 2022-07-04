// SSI CRUD: <!--# include virtual="/js/crud.js" -->

const tableCrud = new CRUD({
    api: tableApi,
    listUrl: "/tables",
    name: "Table",
    nameField: "name",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "name", render: (data, type, row) => `<a href="/table/view?id=${row.id}">${data}</a>` },
        { mData: "format" },
        { mData: "creationTime", render: (data, type, row) => new Date(data*1000).toLocaleDateString() },
        { mData: "id",
            render: (data, type, row) =>
                `<a href="/table/edit?id=${data}"><i class="fas fa-table"></i></a>
                 <a href="#"><i class="fas fa-user-trash" onclick="userCrud.listDeleteItem(${data})"></i></a>`
        }
    ]
});

// TODO: add user - usergroup - group relation
$(document).ready(() => tableCrud.listReady());