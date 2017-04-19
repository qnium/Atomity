import React from 'react';
import Modal from 'react-bootstrap/lib/Modal';
import Button from 'react-bootstrap/lib/Button';
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import {ListControllerEvents} from 'atomity-core';
//import {EditEmployeeWF} from '../App'

var events = require('qnium-events')

class EditDepartmentForm extends React.Component {
    
    constructor(props){
        super(props);
        let self = this;

        this.state = {showDialog : true}
        
        this.closeDialog = function(){
            self.setState({showDialog: false});
        }

        this.cancel = function() {
            self.closeDialog();
            self.props.onDialogClose("'dialogCancel'");
        }

        this.save = function() {
            self.closeDialog();
            self.props.onDialogClose("'dialogOk'");
        }
    }

    render = () => (       
        
        <Modal show={this.state.showDialog} onHide={this.cancel}>
            <Modal.Header closeButton={true}>
                <Modal.Title>Edit department</Modal.Title>
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
                      <ControlLabel>Name</ControlLabel>
                      <FormControl id="1" type="text" placeholder="Enter email" defaultValue={this.props.val.name} />
                  </FormGroup>
              </form>
            </Modal.Body>
            <Modal.Footer>
                <Button onClick={this.cancel}>Close</Button>
                <Button bsStyle="primary" onClick={this.save}>Save</Button>
            </Modal.Footer>
        </Modal>
    )
}

export default EditDepartmentForm;
