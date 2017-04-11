import dataProvider from '../services/DemoDataProvider'
import ListController from './ListController';

class SelectFilterController
{
    constructor(params)
    {
        this.params = params;

        this.targetCtrl = this.params.targetListCtrlName;
        this.readAction = this.params.readAction || "read";

        this.filter = {
            field: this.params.filteringField,
            operation: 'eq',
            value: undefined
        }
    }
    
    applyFilter(filterValue)
    {
        this.filter.value = filterValue;
        window.QEventEmitter.emitEvent(ListController.buildEvent(this.targetCtrl, ListController.action.applyFilter), [this.filter]);
    }

    loadOptions()
    {
        if(this.params.entitiesName)
        {
            let self = this;
            return dataProvider.executeAction(this.params.entitiesName, this.readAction, {})
            .then(result => {
                return result.data;
            });
        } else {
            return new Promise(() => {
                return [];
            });
        }
    }
}

export default SelectFilterController;