import React, { Component } from 'react';
//import createFragment from 'react-addons-create-fragment';
import QAction from './QAction';
import QRowChecker from './QRowChecker';

class QColumn extends Component {

    constructor(props){
        super(props);
        this.columnChildren = this.props.columnChildren;
    }

    renderRecursively(children, index)
    {        
        if(children.type === QAction) {
            return <QAction {...children.props} val={this.props.val} />
        }

        if(children.type === QRowChecker) {
            return (<QRowChecker {...children.props}
                targetListCtrlName={children.props.targetListCtrlName || this.props.targetListCtrlName}
                val={this.props.pageItem}                
            />)
        }

        if(typeof children === "object" && children.length !== undefined){
            return children.map((child, index) => this.renderRecursively(child, index));
        }

        if(children.props && children.props.children){
            return React.createElement(children.type, {key: index, ...children.props,
                className: this.props.isHoverButtons ? "q-row-button" : ""}, this.renderRecursively(children.props.children));
        }

        if(typeof children === "function"){
            return children(this.props.val);
        }

        return children;
    }    
    
    render()
    {
        if(this.columnChildren) {
            return <td>{this.renderRecursively(this.columnChildren)}</td>
        } else {
            return <td>{this.props.val}</td>
        }
    }
}

export default QColumn;