import EditDepartmentForm from '../dialogs/EditDepartmentForm';
import {DialogService} from 'atomity-frontend-common';
import {DialogResult} from 'atomity-frontend-react';

let EditDepartmentWF =
{
    start: dialogData =>
    {
        console.log("show edit department form");
        DialogService.showDialog(EditDepartmentForm, dialogData).then(result =>
        {
            if(result.dialogResult === DialogResult.ok) {
                console.log("edit department step 1 success", result);
            } else {
                console.log("edit department step 1 reject", result);
            }            
        });
    }
}

export default EditDepartmentWF;