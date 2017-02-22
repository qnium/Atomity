/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.dataobjects;

/**
 *
 * @author
 */
public class CountResponseMessage extends ResponseMessage {
    public long totalCount;
    
    public CountResponseMessage(long totalCount) {
        this.totalCount = totalCount;
    }
}
