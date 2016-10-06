(function(angular){
    var app = angular.module('CommonFrontend');
    app.controller('LocationCtrl', function($scope, $rootScope, $location, $timeout)
    {
        var location;
        
        function updateLocation()
        {
            var params = location;
            params = params.replace('/', '');
            params = params.split('&');
            
            var arguments = {};
            
            for (param in params)
            {
                param = params[param];
                param = param.split('=');
                switch (param[0] )
                {
                    case 'path':
                    {
                        var element = angular.element('#'+param[1]);
                        element.tab('show');
                        break;
                    }
                    case 'input':
                    {
                        var args = param[1].split('>');
                        var element = angular.element('#'+args[0]);
                        element.val(args[1]).trigger('input');
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
        
        
    });
})(angular);