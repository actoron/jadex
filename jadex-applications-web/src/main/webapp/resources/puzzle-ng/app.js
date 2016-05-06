angular.module('ngPuzzle', []).controller('PuzzleBoard', function($scope, $timeout, $window)
{
	//-------- attributes --------
	$scope.newsize	= 5;
	$scope.sizes	= [3,5,7,9,11];
	
	//--- (re)set the board ---
	$scope.restart	= function restart()
	{		
		$scope.boardsize = $scope.newsize;
		$scope.board = [];
		$scope.moves	= [];

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
	};
	
	//--- generate the moveable class ---
	$scope.moveable	= function moveable(col, row)
	{
		check	= $scope.getMove(col, row)!=null;
		return check ? "moveable" : "";
	}
	
	//--- get the possible move of a piece, if any ---
	$scope.getMove	= function getMove(col, row)
	{
		ret	= null;
		// white can move/jump down or left
		if($scope.board[row][col]=="white")
		{
			ret	= row+1<$scope.boardsize && $scope.board[row+1][col]=="empty" ? [row+1, col]
				: col+1<$scope.boardsize && $scope.board[row][col+1]=="empty" ? [row, col+1]
				: row+2<$scope.boardsize && $scope.board[row+1][col]=="red" && $scope.board[row+2][col]=="empty" ? [row+2, col]
				: col+2<$scope.boardsize && $scope.board[row][col+1]=="red" && $scope.board[row][col+2]=="empty" ? [row, col+2] : null;
		}
		// red can move/jump up or right
		else if($scope.board[row][col]=="red")
		{
			ret	= row>0 && $scope.board[row-1][col]=="empty" ? [row-1, col]
				: col>0 && $scope.board[row][col-1]=="empty" ? [row, col-1]
				: row>1 && $scope.board[row-1][col]=="white" && $scope.board[row-2][col]=="empty" ? [row-2, col]
				: col>1 && $scope.board[row][col-1]=="white" && $scope.board[row][col-2]=="empty" ? [row, col-2] : null;
		}
		return ret;
	};
		
	//--- perform a move/jump ---
	$scope.doMove	= function doMove(col, row)
	{
		move	= $scope.getMove(col, row);
		if(move!=null)
		{
			$scope.board[move[0]][move[1]]	= $scope.board[row][col];
			$scope.board[row][col]	= "empty";
			$scope.moves.push([[row, col], move]);
		}
	};
	
	//--- take back a move/jump ---
	$scope.takeback	= function takeback()
	{
		move	= $scope.moves.pop();
		if(move!=null)
		{
			$scope.board[move[0][0]][move[0][1]]	= $scope.board[move[1][0]][move[1][1]];
			$scope.board[move[1][0]][move[1][1]]	= "empty";
		}		
	};
	
	//--- Define some helper functions ---
	$scope.columnName	= function columnName(i, first)
	{
		// Cannot use String.fromCharCode in angular expression!?
		c	= "A".charCodeAt(0)+i;
		s	= String.fromCharCode(c);
		return first==undefined ? s:
			first ? i*2<$scope.boardsize ? s : ""
			: i*2+1>=$scope.boardsize ? s : "";
	};
	$scope.rowName	= function rowName(i, first)
	{
		// Not necessary as could be done inline, but for consistency and changeability.
		return first==undefined ? i+1:
			first ? i*2<$scope.boardsize ? i+1 : ""
			: i*2+1>=$scope.boardsize ? i+1 : "";
	};
	$scope.moveName	= function moveName(move)
	{
		return $scope.columnName(move[0][1])+$scope.rowName(move[0][0])
			+ " -> " + $scope.columnName(move[1][1])+$scope.rowName(move[1][0]);
	};
	//$scope.alert = alert.bind($window);	// for easy testing

	// --- init ---
	$scope.restart();
});