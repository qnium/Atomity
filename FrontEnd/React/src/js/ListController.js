import dataProvider from './DemoDataProvider';

class ListController
{
    static get ctrlPrefix() { return "ListCtrl" }
    
    static get action() { return {
        refresh: "refresh",
        editRecord: "editRecord",
        deleteRecord: "deleteRecord",
        selectPage: "selectPage",
        applyFilter: "applyFilter"
    }}

    static get event() { return {
        stateChaged: "stateChanged"
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
            this.onAfterRefresh = params.onAfterRefresh;
            this.pageDataLength = params.pageDataLength || 10; // FIX for 0
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
        
        // event/action listeners
        this.refreshActionListener = function() {
            self.refresh();
        }
        this.deleteActionListener = function(target) {            
            self.deleteRecord(target);
        }
        this.selectPageActionListener = function(target) {            
            self.selectPage(target);
        }
        this.applyFilterActionListener = function(target) {            
            self.applyFilter(target);
        }
        
        window.QEventEmitter.addListener(ListController.buildEvent(this.ctrlName, ListController.action.refresh), this.refreshActionListener);
        window.QEventEmitter.addListener(ListController.buildEvent(this.ctrlName, ListController.action.deleteRecord), this.deleteActionListener);
        window.QEventEmitter.addListener(ListController.buildEvent(this.ctrlName, ListController.action.selectPage), this.selectPageActionListener);
        window.QEventEmitter.addListener(ListController.buildEvent(this.ctrlName, ListController.action.applyFilter), this.applyFilterActionListener);
        
        this.refresh();
    }
    
    deleteRecord(record)
    {
        this.setProgressState(true);
        dataProvider.executeAction(this.entitiesName, this.deleteAction, [record]).then(result => {
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

    setProgressState = function(newState){
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
    
    refresh() {
        this.pageData = [];
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
            this.pageData = result.data;
            this.totalRecords = result.totalCounter;
            this.updatePaginationInfo();
            
            if(this.currentPage > 1 && this.pageData.length === 0){
                this.currentPage = this.totalPages;
                this.refresh();
            } else {            
                this.sendStateChangedEvent();
                if(this.onAfterRefresh){
                    this.onAfterRefresh({
                            pageData: this.pageData
                        }
                    );
                }
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

export default ListController;