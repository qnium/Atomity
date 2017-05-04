/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.auth.definitions;

/**
 *
 * @author Drozhin
 */
public enum AccountType
{
    NO_SESSION (-1, "NO_SESSION"),
    USER (1, "USER");
    
    private final int accountTypeId;
    private final String accountTypeString;
    
    private AccountType(int accountType, String accountTypeStr){
        accountTypeId = accountType;
        accountTypeString = accountTypeStr;
    }
    
    public int getValue(){
        return accountTypeId;
    }
    
    @Override
    public String toString()
    {
        return accountTypeString;
    }
    
    public boolean equals(AccountType type)
    {
        return this.accountTypeString.equals(type.accountTypeString);
    }
    
}
