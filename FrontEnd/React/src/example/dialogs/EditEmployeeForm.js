import React from 'react';
import Modal from 'react-bootstrap/lib/Modal';
import Button from 'react-bootstrap/lib/Button';
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import {ListControllerEvents} from '../../controllers/ListController';
import {EditDepartmentWF} from '../App'
import dataProvider from '../../services/DemoDataProvider'

var events = require('events')

class EditEmployeeForm extends React.Component {
    
    constructor(props){
        super(props);
        let self = this;

        //dataProvider.executeAction("department", "read")

        this.entity = this.props.val;

        this.state = {showDialog : true}
        
        this.closeDialog = function(ev){
            self.setState({showDialog: false});
        }

        this.cancel = function() {
            self.closeDialog();
            self.props.onCancel("'reject params'");
        }

        this.save = function()
        {
            self.entity.email = self.email.value;
            dataProvider.executeAction("employee", "update", [self.entity]).then(result => {
                self.closeDialog();
                self.props.onSuccess("'success params'");
                events(ListControllerEvents.updateEntities).send(["employee"]);
            });
        }

    }

    render() {
        return (        
            <Modal show={this.state.showDialog} onHide={this.cancel}>
                <Modal.Header closeButton={true}>
                    <Modal.Title>Edit employee</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                  <form>
                      <FormGroup>
                          <ControlLabel>ID</ControlLabel>
                          <FormControl.Static>
                              {this.props.val.id}
                          </FormControl.Static>
                      </FormGroup>
                      <FormGroup controlId="1">
                          <ControlLabel>Email</ControlLabel>
                          {/*<UniversalFormInput type="type" bind="email">*/}
                            <FormControl id="1" type="text" placeholder="Enter email" defaultValue={this.entity.email}
                              inputRef={email => this.email = email}/>
                          {/*</UniversalFormInput>*/}
                      </FormGroup>
                      <FormGroup controlId="2">
                          <ControlLabel>Department</ControlLabel>
                          <FormControl id="2" type="text" placeholder="" defaultValue={this.props.val.departmentId} />
                          <Button onClick={EditDepartmentWF.start.bind(this, {id: -1, name: "dep name"})}>Edit</Button>
                      </FormGroup>
                  </form>
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={this.cancel}>Close</Button>
                    <Button bsStyle="primary" onClick={this.save}>Save and next</Button>
                </Modal.Footer>
            </Modal>
        )
    }
}

export default EditEmployeeForm;
