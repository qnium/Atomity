import EditDepartmentForm from '../dialogs/EditDepartmentForm';
import {DialogService} from 'atomity-core'

let EditDepartmentWF =
{
    start: dialogData =>
    {
        console.log("show edit department form");
        DialogService.showDialog(EditDepartmentForm, dialogData).then(result =>
        {
            if(result === "'dialogOk'") {
                console.log("edit department step 1 success", result);
            } else {
                console.log("edit department step 1 reject", result);
            }            
        });
    }
}

export default EditDepartmentWF;