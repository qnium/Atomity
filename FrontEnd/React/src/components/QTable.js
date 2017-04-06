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
            entities: []
        };
        
        this.listCtrl = new ListController(this.props);
        this.listCtrl.onAfterRefresh = data => {
            self.setState({entities: data.pageData});
        };
        
        if(this.props.children){
            
            let childrenArray;
            
            if(this.props.children.length !== undefined){
                childrenArray = this.props.children;
            } else {
                childrenArray = [this.props.children];
            }

            this.headerClick = header =>
            {
                if(header.props.sortingField)
                {
                    let listCtrlName = header.props.targetListCtrlName || this.listCtrl.ctrlName;
                    let sortParams = {
                        sortingField: header.props.sortingField
                    }
                    window.QEventEmitter.emitEvent(ListController.buildEvent(listCtrlName, ListController.action.sort), [sortParams]);
                }
            }
            
            let tableHeader = childrenArray.filter((element) => {
                return element.type === QTableHeader;
            });
            
            this.headerTemplate = 
                <thead><tr>{
                    tableHeader.map((headerItem, index) => <QTableHeader key={index}
                        onClick={this.headerClick.bind(this, headerItem)}
                        targetListCtrlName={headerItem.props.targetListCtrlName ? headerItem.props.targetListCtrlName : this.listCtrl.ctrlName}
                        {...headerItem.props}
                        />)
                }</tr></thead>;

            this.columns = childrenArray.filter((element) => {
                return element.type === QColumn;
            });            
        }        
    }
    
    renderRow(item) {
        if(this.columns) {
            return this.columns.map(function(column, index) {
                if(column.type === QColumn){
                    return <QColumn key={index} val={column.props.fieldName ? item[column.props.fieldName] : item} columnChildren={column.props.children} />
                } else {
                    return null;
                }
            })
        }
    }
    
    render() {
        return (
            <Table>
                
                {this.headerTemplate}
                
                <tbody>{
                    this.state.entities.map(function(item, index) {
                        return (                        
                            <tr key={index}>
                                {this.renderRow(item)}
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