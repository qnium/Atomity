import dataProvider from './DemoDataProvider';

class ListController
{
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
            this.pageDataLength = params.pageDataLength || 2;
        }

        // vars
        this.actionInProgress = false;
        this.pageData = [];
        this.totalRecords = 0;
        this.currentPage = 1;
        this.totalPages = 1;
        this.nextPageAvailable = false;
        this.prevPageAvailable = false;
        
        this.refreshActionListener = function() {
            self.refresh();
        }
        
        this.deleteActionListener = function(target) {            
            self.deleteRecord(target);
        }
        
        window.QEventEmitter.addListener('ListCtrl-' + this.ctrlName + '-refresh', this.refreshActionListener);
        window.QEventEmitter.addListener('ListCtrl-' + this.ctrlName + '-deleteRecord', this.deleteActionListener);
        
        this.refresh();
    }
    
    deleteRecord(record)
    {
        dataProvider.executeAction(this.entitiesName, this.deleteAction, [record]).then(result => {
            this.refresh();
        });        
    }

    // window.QEventEmitter.removeListener(this.refreshActionListener);

    setProgressState = function(newState){
        if(this.actionInProgress !== newState){
            this.actionInProgress = newState;
            window.QEventEmitter.emitEvent('ListCtrl-' + this.ctrlName + '-ProgressStateChanged', [{newState: this.actionInProgress}]);
        }
    }

    refresh() {
        this.pageData = [];
        this.setProgressState(true);
        
        let params = {
            filter: [],
            startIndex: (this.currentPage - 1) * this.pageDataLength,
            count: this.pageDataLength
        }
        
        let self = this;

        dataProvider.executeAction(this.entitiesName, this.readAction, params)
        .then(result =>
        {
            this.setProgressState(false);

            this.pageData = result.data;
            this.totalRecords = result.totalCounter;
            this.totalPages = Math.ceil(this.totalRecords / this.pageDataLength);
            this.totalPages = Math.max(1, this.totalPages);
            //console.log("Pages for " + this.entitiesName + ": " + this.totalPages);
            
            if(this.onAfterRefresh){
                this.onAfterRefresh({
                        pageData: this.pageData
                    }
                );
            }
        });                
    }

    editRecord(entity){
        console.log("ListCtrl - editRecord: ", entity);
    }
}

export default ListController;