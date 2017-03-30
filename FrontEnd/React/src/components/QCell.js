import React, { Component } from 'react';
import QAction from './QAction';

class QCell extends Component {
    
    constructor(props){
        super(props);
        this.columnChildren = this.props.columnChildren;
    }

    renderRecursively(children, index)
    {        
        if(children.type === QAction) {            
            return <span key={index}><QAction {...children.props} val={this.props.val} /></span>
        }

        if(typeof children === "object" && children.length !== undefined){
            return children.map((child, index) => this.renderRecursively(child, index));
        }

        if(children.props && children.props.children){
            return React.createElement(children.type, children.props, this.renderRecursively(children.props.children));
        }

        if(typeof children === "function"){
            return children(this.props.val);
        }

        return children;
    }
    
    render() {

        if(this.columnChildren) {
            return <td>{this.renderRecursively(this.columnChildren)}</td>
        } else {
            return <td>{this.props.val}</td>
        }

        // if(this.columnChildren) {

        //     //this.parseChildren(this.columnChildren);

        //     if(this.columnChildren.length){
        //         return <td>
        //         {this.columnChildren.map((child, index) => {
        //             if(child.type === QAction){
        //                 return <span key={index}><QAction {...child.props} val={this.props.val} /></span>
        //             } else {
        //                 return (<span>{this.columnChildren}</span>);
        //                 //return (<span>{this.child}</span>);
        //             }
        //         }, this)}
        //         </td>

        //     } else {
        //         if(this.columnChildren.type === QAction){
        //             return <td><QAction {...this.columnChildren.props} val={this.props.val} /></td>                   
        //         } else {
        //             return (<td>{this.columnChildren}</td>);
        //         }
        //     }

        //     //return (<td>{this.columnChildren}</td>);
            
            
            
        //     // React.Children.map(this.child.props.children, function(self){
        //     //     console.log(self);
        //     //     self.replaceVal();
        //     // });

        //     // return (<td>{createFragment({childs: this.child.props.children})}</td>);            
            
            
        //     //return React.createElement('td', {className: 'this.props'}, createFragment({childs: this.child.props.children}));
        //     // return React.DOM.td(this.child.props.children);
        // }
        
        // return (
        //     <td>{this.props.val}</td>
        // );
    }
}

export default QCell;