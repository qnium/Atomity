import dataProvider from '../services/DemoDataProvider';

var events = require('events');

let ListControllerEvents =
{
    // actions
    refresh: events().create({targetName: String}),
    deleteRecord: events().create({targetName: String, data: Object}),
    editRecord: events().create({targetName: String, data: Object}),
    selectPage: events().create({targetName: String, data: Object}),
    applyFilter: events().create({targetName: String, data: Object}),
    sort: events().create({targetName: String, data: Object}),
    setRowChecked: events().create({targetName: String, data: Object}),
    setAllChecked: events().create({targetName: String, data: Object}),
    customAction: events().create({targetName: String, data: Object}),

    // events
    stateChanged: events().create({targetName: String, data: Object})
}

//let RefreshAction = event().create({refreshParams: String});
//let TestEvent = event().create({tstRf: Number});
// event(TestEvent).handle(event =>
// {
//     console.log(event.ctrlName + " test handler: ", event.tstRf);
// });

class ListController
{
    static get ctrlPrefix() { return "ListCtrl" }
    
    // static get action() { return {
    //     refresh: "refresh",
    //     editRecord: "editRecord",
    //     deleteRecord: "deleteRecord",
    //     selectPage: "selectPage",
    //     applyFilter: "applyFilter",
    //     sort: "sort",
    //     setRowChecked: "setRowChecked",
    //     customAction: "customAction"
    // }}

    static get event() { return {
        stateChanged: "stateChanged"
    }}

    static buildEvent = (ctrlName, actionOrEvent) => {
        return "ListCtrl" + ctrlName + actionOrEvent;
    }

    static getFilterName = (filter) => {
        return filter.field + "-" + filter.operation;
    }

    constructor(params)
    {
        let self = this;
        
        dataProvider.init({apiEndpoint: 'demoApi'});
        dataProvider.setSessionKey('demoSessionKey');
        
        // params
        if(params) {
            this.entitiesName = params.entitiesName;
            this.ctrlName = params.ctrlName;
            this.readAction = params.readAction || "read";
            this.deleteAction = params.deleteAction || "delete";
            this.pageDataLength = params.pageDataLength || 10; // FIX for 0
            this.useDummyRows = params.useDummyRows;
            this.entityKeyField = params.entityKeyField || "id";
        }

        // vars
        this.actionInProgress = false;
        this.pageData = [];
        this.totalRecords = 0;
        this.currentPage = 1;
        this.totalPages = 1;
        this.nextPageAvailable = false;
        this.prevPageAvailable = false;
        this.filters = {};
        this.currentSort = {};
        
        // event/action listeners
        // this.refreshActionListener = function() {
        //     self.refresh();
        // }
        // this.deleteActionListener = function(target) {
        //     self.deleteRecord(target);
        // }
        // this.selectPageActionListener = function(target) {
        //     self.selectPage(target);
        // }
        // this.applyFilterActionListener = function(target) {
        //     self.applyFilter(target);
        // }
        // this.sortActionListener = function(target) {
        //     self.sortAction(target);
        // }
        // this.setRowCheckedActionListener = function(target) {
        //     self.setRowCheckedAction(target);
        // }
        // this.setAllCheckedActionListener = function(target) {
        //     self.setAllCheckedAction(target);
        // }
        // this.customActionListener = function(target) {
        //     self.customAction(target);
        // }
        
        events(ListControllerEvents.refresh).handle(event => {
            console.log("refresh ac");
            this.doAction(this.refresh, event);
        });
        events(ListControllerEvents.deleteRecord).handle(event => {
            console.log("delete ac");
            this.doAction(this.deleteRecord, event);
        });
        events(ListControllerEvents.editRecord).handle(event => {
            console.log("edit ac");
            this.doAction(this.editRecord, event);
        });
        events(ListControllerEvents.selectPage).handle(event => {
            console.log("selectPage ac");
            this.doAction(this.selectPage, event);
        });
        events(ListControllerEvents.applyFilter).handle(event => {
            console.log("applyFilter ac");
            this.doAction(this.applyFilter, event);
        });
        events(ListControllerEvents.sort).handle(event => {
            console.log("sort ac");
            this.doAction(this.sortAction, event);
        });
        events(ListControllerEvents.setRowChecked).handle(event => {
            console.log("setRowChecked ac");
            this.doAction(this.setRowCheckedAction, event);
        });
        events(ListControllerEvents.setAllChecked).handle(event => {
            console.log("setAllChecked ac");
            this.doAction(this.setAllCheckedAction, event);
        });
        events(ListControllerEvents.customAction).handle(event => {
            console.log("customAction ac");
            this.doAction(this.customAction, event);
        });

        // window.QEventEmitter.addListener(ListController.buildEvent(this.ctrlName, ListController.action.refresh), this.refreshActionListener);
        // window.QEventEmitter.addListener(ListController.buildEvent(this.ctrlName, ListController.action.deleteRecord), this.deleteActionListener);
        // window.QEventEmitter.addListener(ListController.buildEvent(this.ctrlName, ListController.action.selectPage), this.selectPageActionListener);
        // window.QEventEmitter.addListener(ListController.buildEvent(this.ctrlName, ListController.action.applyFilter), this.applyFilterActionListener);
        // window.QEventEmitter.addListener(ListController.buildEvent(this.ctrlName, ListController.action.sort), this.sortActionListener);
        // window.QEventEmitter.addListener(ListController.buildEvent(this.ctrlName, ListController.action.setRowChecked), this.setRowCheckedActionListener);
        // window.QEventEmitter.addListener(ListController.buildEvent(this.ctrlName, ListController.action.setAllChecked), this.setAllCheckedActionListener);
        // window.QEventEmitter.addListener(ListController.buildEvent(this.ctrlName, ListController.action.customAction), this.customActionListener);
        
        this.refresh();
    }

    doAction(actionPerformer, params) {
        if(this.ctrlName === params.targetName) {
            actionPerformer.bind(this)(params.data);
        }
    }
    
    deleteRecord(record)
    {
        console.log("delete rec");
        this.setProgressState(true);
        dataProvider.executeAction(this.entitiesName, this.deleteAction, [record]).then(result => {
            this.setProgressState(false);
            this.refresh();
        });        
    }
    
    customAction(params)
    {
        this.setProgressState(true);
        dataProvider.executeAction(this.entitiesName, params.action, params.payload).then(result => {
            this.setProgressState(false);
            this.refresh();
        });        
    }
    
    applyFilter(filter)
    {
        let filterName = ListController.getFilterName(filter);
        this.filters[filterName] = filter;
        this.refresh();
    }

    // window.QEventEmitter.removeListener(this.refreshActionListener);

    sortAction(sortParams)
    {
        let newSortingFilter = {
            field: sortParams.sortingField,
            operation: "sort"
        }

        let newFilterName = ListController.getFilterName(newSortingFilter);
        let currentSortingFilter = this.filters[newFilterName];
        
        if(currentSortingFilter) {
            if(sortParams.value !== undefined){
                newSortingFilter.value = sortParams.value;
            } else {
                newSortingFilter.value = !currentSortingFilter.value;
            }
        } else {
            let newFilters = {};
            for(let key in this.filters) {
                if(!key.endsWith("-sort")){
                    newFilters[key] = this.filters[key];
                }
            }
            newSortingFilter.value = true;
            this.filters = newFilters;
        }

        this.filters[newFilterName] = newSortingFilter;
        this.currentSort = {
            field: newSortingFilter.field,
            value: newSortingFilter.value
        };

        this.refresh();
    }

    setRowCheckedAction(params)
    {
        let item = this.pageData[params.rowIndex];
        item.checked = params.newState === undefined ? !item.checked : params.newState;
        this.sendStateChangedEvent();
    }

    setAllCheckedAction(params)
    {
        let newState = params && params.newState !== undefined ? params.newState : true;
        this.pageData.filter(item => !item.dummy).map(item => item.checked = newState);
        this.sendStateChangedEvent();
    }

    setProgressState(newState) {
        if(this.actionInProgress !== newState){
            this.actionInProgress = newState;
            this.sendStateChangedEvent();
        }
    }

    sendStateChangedEvent(){
        window.QEventEmitter.emitEvent(ListController.buildEvent(this.ctrlName, ListController.event.stateChanged), [this]);
    }

    updatePaginationInfo() {
        this.totalPages = Math.ceil(this.totalRecords / this.pageDataLength);
        this.totalPages = Math.max(1, this.totalPages);
        this.nextPageAvailable = this.currentPage < this.totalPages;
        this.prevPageAvailable = this.currentPage > 1;
    }

    selectPage(pageNumber){
        this.currentPage = pageNumber;
        this.refresh();
    }
    
    arrayDataToPageData(arrayData)
    {
        let newPageData = arrayData.map((item, index) => {
            return {
                index: index,
                checked: false,
                data: item
            }
        });
        
        let self = this;
        newPageData.forEach(newItem =>
        {
            let sameItems = self.pageData.filter(currentItem => {
                return !currentItem.dummy && currentItem.data[self.entityKeyField] === newItem.data[self.entityKeyField];
            });
            if(sameItems[0] !== undefined) {
                newItem.checked = sameItems[0].checked;
            }
        });

        return newPageData;
    }
    
    addDummyRows() {
        for(let i = this.pageData.length; i < this.pageDataLength; i++){
            this.pageData.push({dummy: true});
        }
    }

    refresh() {
        console.log("refresh");
        this.setProgressState(true);
        
        let params = {
            filter: this.objectToArray(this.filters),
            startIndex: (this.currentPage - 1) * this.pageDataLength,
            count: this.pageDataLength
        }
        
        dataProvider.executeAction(this.entitiesName, this.readAction, params)
        .then(result =>
        {
            this.setProgressState(false);
            this.pageData = this.arrayDataToPageData(result.data);
            this.totalRecords = result.totalCounter;
            this.updatePaginationInfo();
            
            if(this.currentPage > 1 && this.pageData.length === 0){
                this.currentPage = this.totalPages;
                this.refresh();
            } else {
                if(this.useDummyRows === true){
                    this.addDummyRows();
                }
                this.sendStateChangedEvent();
            }
        });                
    }

    editRecord(entity){
        console.log("ListCtrl - editRecord: ", entity);
    }

    objectToArray(obj) {
        var result = [];
        for (var key in obj) {
            result.push(obj[key]);
        }
        return result;
    }    
}

export {ListControllerEvents};
export default ListController;
