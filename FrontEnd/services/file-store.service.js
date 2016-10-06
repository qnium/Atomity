(function() {
    angular.module('CommonFrontend')
        .factory('FileStoreService', FileStoreService);
    
    FileStoreService.$inject = ['$http', 'FileUploader', 'DialogService'];
    
    function FileStoreService($http, FileUploader, DialogService) {
        
        var sessionKey = "upload_allow";
        var appConfig = null;  

        return {
            getDownloadUrl: getDownloadUrl,
            getUploader: getUploader,
            init: init
        };
        
        function getDownloadUrl(fileRecordId, tempAccountType, tempAccountId, doc) {
            return appConfig.filesEndpoint + '?id=' + fileRecordId + '&sessionKey=' + tempAccountType + ';' + tempAccountId;
        }        
        
        function setSessionKey(key) {
            sessionKey = key;
        }
        
        function init(config) {
            appConfig = config;
        }
        
        function getUploader()
        {
            var uploader = new FileUploader({ url: appConfig.filesEndpoint });

            uploader.removeAfterUpload = true;
            uploader.autoUpload = true;
            uploader.onBeforeUploadItem = function(item) {
                item.formData.push({ sessionKey: sessionKey })
            };
            
            uploader.onSuccessItem = function(fileItem, fileRecordId, status, headers) {
                if(this.onCompleteFunction != undefined){
                    this.onCompleteFunction(fileRecordId);
                }
            };
            
            uploader.onErrorItem = function(fileItem, response, status, headers) {
                DialogService.alert("Status code: " + status, "Uploading error..." );
            };
            
            uploader.onProgressAll = function(progress){
                if(this.onProgressFunction != undefined){
                    this.onProgressFunction(progress);
                }
            };
            
            uploader.onCompleteAll = function(){
                if(this.onProgressFunction != undefined){
                    this.onProgressFunction(100);
                }
            };
            
            return uploader;
        }
    }
})();