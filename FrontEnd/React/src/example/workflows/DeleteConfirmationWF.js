import DeleteConfirmationForm from '../dialogs/DeleteConfirmationForm';
import {DialogService} from 'atomity-core';
import {DialogResult} from 'atomity-core';

let DeleteConfirmationWF =
{
    start: (dialogData, params) =>
    {
        DialogService.showDialog(DeleteConfirmationForm, {dialogData: dialogData, params: params}).then(result =>
        {
            if(result.dialogResult === DialogResult.ok) {
                console.log("delete record step 1 success", result);
            } else {
                console.log("delete record step 1 reject", result);
            }            
        });
    }
}

export default DeleteConfirmationWF;