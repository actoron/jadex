angular.module('ngPuzzle', []).controller('PuzzleBoard', function($scope, $timeout, $window)
{
	//--- Define some helper functions ---
	$scope.columnName	= function columnName(i, first)
	{
		// Cannot use String.fromCharCode in angular expression!?
		c	= "A".charCodeAt(0)+i;
		s	= String.fromCharCode(c);
		return first ? i*2<$scope.boardsize ? s : ""
			: i*2+1>=$scope.boardsize ? s : "";
	};
	$scope.rowName	= function rowName(i, first)
	{
		// Not necessary as could be done inline, but for consistency and changeability.
		return first ? i*2<$scope.boardsize ? i+1 : ""
			: i*2+1>=$scope.boardsize ? i+1 : "";
	};
	$scope.alert = alert.bind($window);	// for easy testing

	//--- (re)set the board ---
	$scope.restart	= function restart()
	{		
		$scope.boardsize = $scope.newsize;
		$scope.board = [];
		var size2	= Math.floor($scope.boardsize/2);
		for (var i = 0; i < $scope.boardsize; i++)
		{
			$scope.board[i] = [];
			for(var j = 0; j < $scope.boardsize; j++)
			{
				$scope.board[i][j] =
					i<size2		&& j<=size2	? "white" :
					i<=size2	&& j<size2	? "white" :
					i>size2		&& j>=size2	? "red" :
					i>=size2	&& j>size2	? "red" :
					i==size2	&& j==size2	? "empty" : "";
			}
		}
	}
	
	// start with size 5
	$scope.newsize	= 5;
	$scope.sizes	= [3,5,7,9,11];
	
	$scope.restart();
});