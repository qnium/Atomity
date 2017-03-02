(function(angular){
    var app = angular.module('CommonFrontend');
    app.controller('LocationCtrl', function($scope, $rootScope, $location, $timeout, $window)
    {
        var location;
        var params = [];
        var progClick = false;
        
        function updateLocation(syncUpdateEvent)
        {
            if(!syncUpdateEvent && location != "/") {
                params = location;
                params = params.replace('/', '');
                params = params.split('&');
            }
            
            var arguments = {};
            
            for(paramIndex = 0; paramIndex < params.length; paramIndex++)
            {
                param = params[paramIndex];
                param = param.split('=');
                switch (param[0])
                {
                    case 'path':
                    {
                        if(syncUpdateEvent){
                            break;
                        }
                        var element = angular.element('#'+param[1]);
                        element.tab('show');
                        params.splice(paramIndex, 1);
                        paramIndex--;
                        break;
                    }
                    case 'button':
                    {
                        var args = param[1].split('<');
                        if(syncUpdateEvent && args.length > 1 && args[1] == syncUpdateEvent || !syncUpdateEvent && args.length == 1)
                        {
                            var elId = '#'+args[0];
                            $timeout(function(elId){
                                var element = angular.element(elId);
                                progClick = true;                                
                                element.trigger('click');
                            }, 0, false, elId);
                            params.splice(paramIndex, 1);
                            paramIndex--;
                        }                        
                        break;
                    }
                    case 'input':
                    {
                        if(syncUpdateEvent){
                            break;
                        }
                        var args = param[1].split('<');
                        var element = angular.element('#'+args[0]);
                        element.val(args[1]).trigger('input');
                        params.splice(paramIndex, 1);
                        paramIndex--;
                        break;
                    }
                    case 'checkbox':
                    {
                        if(syncUpdateEvent){
                            break;
                        }
                        var args = param[1].split('<');
                        var element = angular.element('#'+args[0]);
                        var newVal = eval(args[1]);
                        if(element.prop('checked') != newVal){
                            $timeout(function(element){
                                element.trigger('click');
                            }, 500, false, element);
                        }
                        params.splice(paramIndex, 1);
                        paramIndex--;
                        break;
                    }
                }
            }
        }
        
        $rootScope.$on("$locationChangeSuccess", function() {
             // Want to prevent re-loading when going from /dataEntry/1 to some other dataEntry path
            //angular.element('#foo').trigger('click');
            location = $location.path();
            updateLocation();
        });
        
        $rootScope.$on('LoggedInEvent', function (e, arg) {
            $timeout(updateLocation, 500);
        });
        
        $rootScope.$on('ReadCompletedEvent', function (e, arg) {
            if(params.length > 0 && params[0] != ""){
                $timeout(updateLocation(arg), 0);
            }
        });

        $rootScope.putLinkToHistory = function(linkId)
        {
            if(!progClick){
                var docUrl = document.URL;
                var hashInd = docUrl.indexOf("#");
                if(hashInd > -1){
                    docUrl = docUrl.slice(0, hashInd);
                }
                docUrl += '#/button=' + linkId;
                window.history.pushState(null, null, docUrl);
            }
            progClick = false;
        };
    });
})(angular);