import AddEmployeeForm from '../dialogs/AddEmployeeForm';
import {DialogService} from 'atomity-frontend-common';
import {DialogResult} from 'atomity-frontend-react';

let AddDepartmentWF =
{
    start: () =>
    {
        console.log("show add employee form");
        DialogService.showDialog(AddEmployeeForm).then(result =>
        {
            if(result.dialogResult === DialogResult.ok) {
                console.log("add employee step 1 success", result);
            } else {
                console.log("add employee step 1 reject", result);
            }            
        });
    }
}

export default AddDepartmentWF;