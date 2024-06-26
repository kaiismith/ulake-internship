import { BaseAdapter } from "./base.js";
import { userApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

export class UserAdapter extends BaseAdapter {
    constructor (config) {
        if (!config) config = {};
        config.api = userApi;
        super(config);
    }

    transform (raw) {
        return raw.map(u => { return {
            id: u.id,
            name: u.userName,
            size: 0,
            type: "User",
            firstName: u.firstName,
            lastName: u.lastName,
            action: u.id
        }});
    }

    getAllRenderers () {
        let ret = super.getAllRenderers();
        ret.name = (data, type, row) => {
            const fullName = row.firstName || row.lastName ? ` (${row.lastName} ${row.firstName})` : "";
            return `<a href="#" onclick="window.crud.click('u', '${row.id}', '${data}')">${data}${fullName}</a>`
        };
        ret.action = (data, type, row) => `<a href="/user/edit?id=${data}"><i class="fas fa-user-edit"></i></a>`;
        return ret;
    }
}
