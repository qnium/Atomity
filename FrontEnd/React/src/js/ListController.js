//import EventEmitter from '../../node_modules/wolfy87-eventemitter/EventEmitter.min.js';

class ListController
{
    constructor(params)
    {
        let self = this;
        this.entitiesName = params.entitiesName;
        this.ctrlName = params.ctrlName;
        this.onAfterRefresh = params.onAfterRefresh;
        this.actionInProgress = false;
        this.pageData = [];
        
        this.refreshActionListener = function() {
            self.refresh(self.pageData);
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
        var index = this.pageData.indexOf(this.pageData.find((value) => value.id === record.id));
        if(index != -1){
            this.pageData.splice(index, 1);
        }
        this.refresh(this.pageData);
    }

    // window.QEventEmitter.removeListener(this.refreshActionListener);

    setProgressState = function(newState){
        if(this.actionInProgress !== newState){
            this.actionInProgress = newState;
            window.QEventEmitter.emitEvent('ListCtrl-' + this.ctrlName + '-ProgressStateChanged', [{newState: this.actionInProgress}]);
        }
    }

    refresh(data) {
        this.pageData = [];
        this.setProgressState(true);
        let self = this;
        setTimeout(() => {
            
            if(data){
                self.pageData = data;
            } else {
                self.pageData = self.getSampleEntities(self.entitiesName);
            }

            self.setProgressState(false);
            
            if(self.onAfterRefresh){
                self.onAfterRefresh({
                        pageData: self.pageData                        
                    }
                );
            }
        }, 2000);            
    }
    
    getSampleEntities(entitiesName)
    {
        var data = [];
        
        if(entitiesName === 'employees') {
            for(var i = 0; i < 3; i++) {
                data.push({id: i, email: 'email_' + i, name: 'Name ' + i});
            }
        }
        
        if(entitiesName === 'departments') {
            for(var i = 0; i < 5; i++) {
                data.push({id: i, type: i + 1, depName: 'Department name ' + i, description: 'Description ' + i});
            }
        }

        return data;
    }
    
    editRecord(entity){
        console.log(entity);
    }
}

export default ListController;