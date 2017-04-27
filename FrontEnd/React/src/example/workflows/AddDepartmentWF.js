import AddDepartmentForm from '../dialogs/AddDepartmentForm';
import {DialogService} from 'atomity-core';
import moment from 'moment';

let AddDepartmentWF =
{
    start: () =>
    {
        console.log("show add department form");
        let newDep = {registrationDate: moment().format("x")};
        DialogService.showDialog(AddDepartmentForm, newDep).then(result =>
        {
            if(result === "'dialogOk'") {
                console.log("add department step 1 success", result);
            } else {
                console.log("add department step 1 reject", result);
            }            
        });
    }
}

export default AddDepartmentWF;