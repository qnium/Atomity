import React from 'react';
import {QForm} from 'atomity-react'

class DeleteConfirmationForm extends React.Component
{   
    render = () => (
        <QForm okButtonText="Yes" cancelButtonText="No" disableConfirmation={true} title="Delete record..." entityObject={this.props.val.dialogData} entitiesName={this.props.val.params.entitiesName}
            entitiesToRefresh={this.props.val.params.entitiesToRefresh} okAction="delete" onDialogClose={this.props.onDialogClose} useArray={true}>
            <form>
                <p className="text-center"><strong>Are you sure you want to delete record?</strong></p>
            </form>
        </QForm>
    )
}

export default DeleteConfirmationForm;
