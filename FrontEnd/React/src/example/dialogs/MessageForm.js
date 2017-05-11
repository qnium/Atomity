import React from 'react';
import Modal from 'react-bootstrap/lib/Modal';
import Button from 'react-bootstrap/lib/Button';

class MessageForm extends React.Component
{   
    constructor(props)
    {
        super(props);
        let self = this;

        this.state = {
            showDialog : true,
        }
        
        this.closeDialog = function(){
            self.setState({showDialog: false});
        }        
    }

    render = () => (
        
        <Modal show={this.state.showDialog} onHide={this.closeDialog}>
            <Modal.Header closeButton={true}>
                <Modal.Title>{this.props.val.title}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <form>
                    <p className="text-center"><strong>{this.props.val.message}</strong></p>
                </form>
            </Modal.Body>
            <Modal.Footer>
                <p className="text-center" style={{margin: 0}}>
                    <Button bsStyle="primary" onClick={this.closeDialog}>OK</Button>
                </p>
            </Modal.Footer>
        </Modal>
    )
}

export default MessageForm;
