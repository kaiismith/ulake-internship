import { ListCRUD } from "./crud/listcrud.js";
import { UserWrapper } from "./datawrapper/user.js";
import { FolderWrapper } from "./datawrapper/folder.js";
import { userApi, folderApi, fileApi } from "./api.js";

// data browser, first level is users
class DataCRUD extends ListCRUD {
    constructor() {
        super({
            api: userApi,
            listUrl: "/folders",
            name: "Folders",
            nameField: "name"
        });

        this.id = 0;        // default 0: everyone. negative: userid, positive: folderid
        this.path = [ ];    //

        this.userWrapper = new UserWrapper();
        this.folderWrapper = new FolderWrapper();
        this.dataWrapper = this.userWrapper;
        this.fields = ["id", "name", "type", "size", "action" ];
        this.updateRenderers()
        $.fn.dataTable.ext.errMode = 'none';
    }

    /**
     * Update UI renderer whenver we change our data wrapper
     */
    updateRenderers() {
        if (!this.listFieldRenderer) {
            // create if not exist
            this.listFieldRenderer = this.fields.map(f => {return {
                mData: f,
                render: this.dataWrapper.getRenderer(f)
            }})
        }
        else {
            // update if there
            for (const k in this.listFieldRenderer) {
                this.listFieldRenderer[k].render = this.dataWrapper.getRenderer(k)
            }
        }
    }

    /**
     * Get transformed data from the data wrapper
     */
    async fetch() {
        if (this.id <= 0) this.dataWrapper = this.userWrapper;
        else this.dataWrapper = this.userWrapper;
        const raw = await this.dataWrapper.fetch(this.id);
        this.data = this.dataWrapper.transform(raw);
        console.log("transformed data", this.data);
    }

    delete(id) {
        console.log("nah, not yet", id);
    }

    clickUser(id) {
        if (id.indexOf("u") === 0) id = id.slice(1);
        this.id = -parseInt(id);
        console.log("Switching to folders of user", this.id);
    }
}

window.crud = new DataCRUD();

$(document).ready(() => window.crud.ready());