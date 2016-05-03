angular.module('ngPuzzle', []).controller('PuzzleBoard', function($scope, $timeout, $window)
{
	function setupBoard()
	{
		$scope.columnName	= function columnName(i, first)
		{
			// Cannot use String.fromCharCode in angular expression!?
			c	= "A".charCodeAt(0)+i;
			s	= String.fromCharCode(c);
			return first ? i*2<$scope.boardsize ? s : ""
				: (i+1)*2>$scope.boardsize ? s : "";
		};
		
		$scope.alert = alert.bind($window);	// for easy testing
		$scope.boardsize = 5;
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
	setupBoard();
});