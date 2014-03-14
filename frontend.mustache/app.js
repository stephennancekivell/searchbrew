'use strict';

var app = angular.module('searchbrew', []);

app.controller('SearchCtrl', function ($scope, $http, $timeout) {
        $scope.showAll = false;
        $scope.loading = false;

        function doSearch(){
            $scope.loading = true;
            $scope.showAll = false;
            $http.get('/api/search?q='+$scope.searchQuery).
                success(function(data){
                    if(data.query === $scope.searchQuery){
                        $scope.loading = false;
                        $scope.render(data.hits);
                    }
                });
        }

        var timeout;
        $scope.$watch('searchQuery', function(){
            if (timeout) $timeout.cancel(timeout);
            if ($scope.searchQuery && $scope.searchQuery.length > 0){
                timeout = $timeout(doSearch, 150);
            } else {
                $scope.clearTable();
            }
        });

        $scope.getAll = function(){
            $scope.showAll = true;
            $scope.searchQuery = "";
            $scope.loading = true;
            $http.get('/api/search?size=10000').
                success(function(data){
                    $scope.loading = false;
                    $scope.render(data.hits);
                });
        };

        $scope.clearTable = function(){
            var table = document.getElementById('results-table');
            if (table.children){
                $(table.children).remove();
            }
        };

        var template = document.getElementById('results-template').text;

        $scope.render = function(hits){
            var rendered = Mustache.render(template, hits);
            var table = document.getElementById('results-table');
            $scope.clearTable();
            $(table).append(rendered);
        };

        Mustache.parse(template);
    });