import dataProvider from '../services/DemoDataProvider'
import ListController from './ListController';
import {ListControllerEvents} from './ListController';

var events = require('events');

class InputFilterController
{
    constructor(params)
    {
        this.params = params;
        this.targetCtrl = this.params.targetListCtrlName;

        this.filter = {
            field: this.params.filteringField,
            operation: this.params.complexFilter ? "in" : (this.params.filteringOperation || "like"),
            value: undefined
        }
    }
    
    applyFilter(filterValue)
    {
        if(this.params.complexFilter)
        {
            let complexFilter = {
                field: this.params.complexFilter.filteringField,
                operation: 'like',
                value: filterValue
            }

            dataProvider.executeAction(this.params.complexFilter.entitiesName, "read", {filter: [complexFilter]})
            .then(result => {
                this.filter.value = result.data.map(item => item[this.params.complexFilter.key]);
                //window-.QEventEmitter.emitEvent(ListController.buildEvent(this.targetCtrl, ListController.action.applyFilter), [this.filter]);
                events(ListControllerEvents.applyFilter).send({targetName: this.targetCtrl, data: this.filter});
            });
        } else {
            this.filter.value = filterValue;
            //window-.QEventEmitter.emitEvent(ListController.buildEvent(this.targetCtrl, ListController.action.applyFilter), [this.filter]);
            events(ListControllerEvents.applyFilter).send({targetName: this.targetCtrl, data: this.filter});
        }
    }
}

export default InputFilterController;