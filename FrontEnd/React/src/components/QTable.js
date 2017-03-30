import React, { Component } from 'react';
import Table from 'react-bootstrap/lib/Table';
import ListController from '../js/ListController';
import QTableHeader from './QTableHeader';
import QColumn from './QColumn';
import QCell from './QCell';

class QTable extends Component {
    
    constructor(props){
        super(props)
        let self = this;
        
        this.state = {
            entities: []
        };

        this.refreshEntities = function(data) {
            self.setState({ entities: data.pageData });
        };
        
        if(this.props.children){
            
            var childrenArray;
            
            if(this.props.children.length !== undefined){
                childrenArray = this.props.children;
            } else {
                childrenArray = [this.props.children];
            }

            var header = childrenArray.filter((element) => {
                return element.type === QTableHeader;
            });
            this.headerTemplate = <thead><tr>{header}</tr></thead>;

            this.columns = childrenArray.filter((element) => {
                return element.type === QColumn;
            });            
        }        
    }
    
    componentDidMount()
    {
        this.listCtrl = new ListController({
            ctrlName: this.props.ctrlName,
            entitiesName: this.props.entitiesName
        }); 
        
        this.listCtrl.onAfterRefresh = this.refreshEntities;
    }
    
    renderRow(item) {
        if(this.columns) {
            return this.columns.map(function(column, index) {
                if(column.type === QColumn){
                    return <QCell key={index} val={column.props.fieldName ? item[column.props.fieldName] : item} columnChildren={column.props.children} />
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