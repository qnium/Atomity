import React, { Component } from 'react';
import Table from 'react-bootstrap/lib/Table';
import ListController from '../js/ListController';
import QTableHeader from './QTableHeader';
import QColumn from './QColumn';

class QTable extends Component {
    
    constructor(props){
        super(props)
        let self = this;
        
        this.state = {
            pageData: []
        };
        
        this.listCtrl = new ListController(this.props);

        this.ctrlStateListener = function(target) {
            if(!target.actionInProgress){
                self.setState({pageData: target.pageData});
            }
        }
        window.QEventEmitter.addListener(ListController.buildEvent(this.listCtrl.ctrlName, ListController.event.stateChanged), this.ctrlStateListener);
        
        if(this.props.children){
            
            let childrenArray;
            
            if(this.props.children.length !== undefined){
                childrenArray = this.props.children;
            } else {
                childrenArray = [this.props.children];
            }

            let tableHeader = childrenArray.filter((element) => {
                return element.type === QTableHeader;
            });
            
            this.headerTemplate = 
                <thead><tr>{
                    tableHeader.map((headerItem, index) => <QTableHeader key={index}                        
                        {...headerItem.props}
                        targetListCtrlName={headerItem.props.targetListCtrlName || this.listCtrl.ctrlName}
                        />)
                }</tr></thead>;

            this.columns = childrenArray.filter((element) => {
                return element.type === QColumn;
            });            
        }        
    }
    
    renderRow(pageItem)
    {        
        if(this.columns) {
            return this.columns.map(function(column, index) {
                if(column.type === QColumn) {
                    if(pageItem.dummy === true){
                        return <td key={index} style={this.props.dummyStyle || {visibility: "hidden"}}>&nbsp;</td>
                    } else {
                        return <QColumn key={index} val={column.props.fieldName ? pageItem.data[column.props.fieldName] : pageItem.data}
                            pageItem={pageItem} columnChildren={column.props.children}
                            targetListCtrlName={column.props.targetListCtrlName || this.listCtrl.ctrlName}
                            />
                    }
                } else {
                    return null;
                }
            }, this)
        }
    }
    
    render() {
        return (
            <Table>
                
                {this.headerTemplate}
                
                <tbody>{
                    this.state.pageData.map(function(pageItem, index) {
                        return (                        
                            <tr key={index}>
                                {this.renderRow(pageItem)}
                            </tr>
                        )
                    }, this)
                }
                </tbody>
            </Table>
        );
    }
}

export default QTable;