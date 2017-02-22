/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.filestore.interfaces;

import com.qnium.common.backend.exceptions.CommonException;

/**
 *
 * @author Drozhin
 */
public interface FileStoreProvider
{
    boolean checkUploadRights(String sessionKey) throws CommonException;
    boolean checkDownloadRights(String sessionKey, long fileRecordId) throws CommonException;
}
