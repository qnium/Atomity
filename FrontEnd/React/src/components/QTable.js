import React, { Component } from 'react';
import Table from 'react-bootstrap/lib/Table';
import ListController from '../js/ListController';
import QTableHeader from './QTableHeader';
import QColumn from './QColumn';
import Row from 'react-bootstrap/lib/Row';
import FontAwesome from 'react-fontawesome';

class QTable extends Component {
    
    constructor(props){
        super(props)
        let self = this;
        
        this.state = {
            pageData: [],
            actionInProgress: false
        };
        
        this.listCtrl = new ListController(this.props);

        this.ctrlStateListener = function(target) {
            //if(!target.actionInProgress){
                self.setState({
                    pageData: target.pageData,
                    actionInProgress: target.actionInProgress
                });
            //}
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
                        return <QColumn {...column.props} key={index} val={column.props.fieldName ? pageItem.data[column.props.fieldName] : pageItem.data}
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
    
    renderOverlay() {
        if(this.state.actionInProgress){
            return (
                <div className="q-overlay text-center">
                    <div className="q-center"><FontAwesome name="refresh" size="3x" spin /></div>
                </div>)
        } else {
            return null
        }        
    }

    render() {
        return (
            //<div style={{position: "relative"}}>
            <div  className="q-table">
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
                {this.renderOverlay()}
            </div>
        );
    }
}

export default QTable;