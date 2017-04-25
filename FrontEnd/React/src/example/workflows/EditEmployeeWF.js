import EditEmployeeForm from '../dialogs/EditEmployeeForm';
import EmployeeNotificationForm from '../dialogs/EmployeeNotificationForm';
import {DialogService} from 'atomity-core'

let EditEmployeeWF =
{
    start: dialogData =>
    {        
        console.log("show edit employee form");
        
        DialogService.showDialog(EditEmployeeForm, dialogData)
        .then(result =>
        {
            if(result === "'dialogOk'") {
                console.log("edit employee step 1 success", result);
                console.log("show notification form");
                return DialogService.showDialog(EmployeeNotificationForm, {recipient: "recipient val"});
            } else {
                console.log("edit employee step 1 reject", result);
            }
        })
        .then(result =>
        {
            if(result === "'dialogOk'") {
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