(function() {
    angular.module('CommonFrontend')
        .factory('FileStoreService', FileStoreService);
    
    function FileStoreService($http, $rootScope, FileUploader, DialogService) {
        
        var sessionKey = null;
        var appConfig = null;  

        return {
            getDownloadUrl: getDownloadUrl,
            getUploader: getUploader,
            init: init
        };
        
        // fileName is used to help a browser to save a file, it does not affect server side
        function getDownloadUrl(fileRecordId, fileName) {
            return appConfig.filesEndpoint + '/' + fileName + '?id=' + fileRecordId + '&sessionKey=' + sessionKey;
        }
        
        function init(config) {
            appConfig = config;
            $rootScope.$on('LoggedInEvent', function (e, data) {
                sessionKey = data.key;
            });
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
                    this.onCompleteFunction(fileRecordId, fileItem);
                }
            };
            
            uploader.onErrorItem = function(fileItem, response, status, headers) {
                if(this.onErrorItemFunction != undefined){
                    this.onErrorItemFunction(fileItem, response, status, headers);
                } else {
                    DialogService.alert(response, "Uploading error..." );
                }
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
