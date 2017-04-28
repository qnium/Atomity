import EditEmployeeForm from '../dialogs/EditEmployeeForm';
import EmployeeNotificationForm from '../dialogs/EmployeeNotificationForm';
import {DialogService} from 'atomity-core';
import {DialogResult} from 'atomity-react';

let EditEmployeeWF =
{
    start: dialogData =>
    {        
        console.log("show edit employee form");
        
        DialogService.showDialog(EditEmployeeForm, dialogData)
        .then(result =>
        {
            if(result.dialogResult === DialogResult.ok) {
                console.log("edit employee step 1 success", result);
                console.log("show notification form");
                return DialogService.showDialog(EmployeeNotificationForm, {recipient: result.entityObject.email});
            } else {
                console.log("edit employee step 1 reject", result);
                return {};
            }
        })
        .then(result =>
        {
            if(result.dialogResult === DialogResult.ok) {
                console.log("edit employee step 2 success", result);
            } else {
                console.log("edit employee step 2 reject", result);
            }
        })
        .then(result =>
        {            
            console.log("edit employee fake step 3...", result);            
        });
    }
}

export default EditEmployeeWF;