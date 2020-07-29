package gal;
/*
 * Joseph T. Galbreath III 
 * E01155874 
 * COSC 561 
 * Rook Maze Problem
 */

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class AgentFunction
{

	final int WHITE_PAWN = 1;
	final int WHITE_KING = 2;
	final int BLANK = 0;
	final int BLACK_PAWN = -1;
	final int BLACK_KING = -2;

	// Node class to serve as states
	private class Node
	{
		private Node parent;
		private LinkedList<Node> children;
		private int[] board;
		private boolean isWhite;
		private int movesRemaining;
		private int level;
		private int move[];
		private boolean capturePossible;

		public Node()
		{
			this.parent = null;
			this.board = new int[33];
			this.isWhite = false;
			this.movesRemaining = 100;
			this.level = 0;
			this.move = new int[2];
			this.capturePossible = false;
			this.children = new LinkedList<Node>();
		}

		public Node(Node parent, int[] board, boolean isWhite,
				int movesRemaining, int level)
		{
			this.parent = parent;
			this.board = board;
			this.isWhite = isWhite;
			this.movesRemaining = movesRemaining;
			this.level = level;
			this.children = new LinkedList<Node>();
			this.move = new int[2];
			this.capturePossible = false;
		}

		public Node getParent()
		{
			return parent;
		}

	}

	// Data members for the Agent Function class
	private Node start; // Starting board
	private List<Node> OPEN; // Points to be visited
	private List<Node> CLOSED; // Points that have already been visited
	private int[] board; // The Game board
	private int ply;
	private int alpha;
	private int beta;
	private Node best;
	private boolean isWhite;

	// Constructor for the Agent
	public AgentFunction(int[] board, boolean isWhite,
			int movesRemaining, int level)
	{
		this.start = new Node(null, board, isWhite, movesRemaining, 1);
		this.OPEN = new LinkedList<Node>();
		this.OPEN.add(0, this.start);
		this.CLOSED = new LinkedList<Node>();
		this.board = board;
		this.ply = 4;
		this.alpha = Integer.MIN_VALUE;
		this.beta = Integer.MAX_VALUE;
		this.best = new Node(null, board, isWhite, movesRemaining, 1);
		this.isWhite = isWhite;

	}

	public int getAlpha()
	{
		return this.alpha;
	}

	public void setAlpha(int alpha)
	{
		this.alpha = alpha;
	}

	public int getBeta()
	{
		return this.beta;
	}

	public void setBeta(int beta)
	{
		this.beta = beta;
	}

	public int[] moveSelection(int[] board, boolean isWhite, int movesRemaining,
			int level)
	{
		root = null;
		int bestVal = AlphaBetaSearch(board, isWhite, movesRemaining, 1, alpha,
				beta, this.best);

		return this.best.move;
	}

	public int AlphaBetaSearch(int[] board, boolean isWhite, int movesRemaining,
			int ply, int alpha, int beta, Node best)
	{
		// If there are no nodes left to visit then there is no solution
		if (this.OPEN.size() != 0)
		{
//			System.out.println("Alpha: " + alpha);
//			System.out.println("Beta: " + beta);

			if (ply >= this.ply)
			{
				//System.out.println("Eval: " + evaluateState(isWhite, board));
				return evaluateState(isWhite, board);
			}

			Node x = this.OPEN.get(0);

			for (int i = 0; i < this.CLOSED.size(); i++)
			{
				if (x.level < this.CLOSED.get(i).level)
				{
					this.CLOSED.remove(i);
					i = i - 1;
				}
			}

			addNode(x.getParent(), x);

			// We also add it to our Closed list so we don't visit it twice
			this.CLOSED.add(x);

			// Getting all possible boards
			List<Node> temp = reachableBoards(x);

			//System.out.println("Num of Boards: " + this.OPEN.size());

			this.best = this.OPEN.get(0);

			for (int i = 0; i < temp.size(); i++)
			{
				this.OPEN.add(0, temp.get(i));
			}

			for (int i = 0; i < this.OPEN.size(); i++)
			{
				Node temp1 = this.OPEN.get(i);

				int val = AlphaBetaSearch(temp1.board, !isWhite, movesRemaining,
						ply + 1, alpha, beta, this.best);
				if (isWhite == this.isWhite)
				{
//					System.out
//							.println("IsAlpha " + isWhite + " " + this.isWhite);
					if (val > alpha)
					{
						//System.out.println("Alpha Change");
						alpha = val;
						this.best.board = board;

//						System.out.println("Pre Move: " + this.best.move[0]
//								+ " " + this.best.move[1]);
						if (this.ply % 2 == 0)
						{
							this.best.move = temp1.move;
						}

//						System.out.println("Post Move: " + this.best.move[0]
//								+ " " + this.best.move[1]);

//						System.out.println();
					}
					if (alpha >= beta)
					{
						return alpha;
					}
				}
				else
				{
//					System.out
//							.println("IsBeta " + isWhite + " " + this.isWhite);
					if (val < beta)
					{
//						System.out.println("Beta change");
						beta = val;
						this.best.board = board;
//						System.out.println("Pre Move: " + this.best.move[0]
//								+ " " + this.best.move[1]);
						if (this.ply % 2 == 0)
						{
							this.best.move = temp1.move;
//							System.out.println("Post Move: " + this.best.move[0]
//									+ " " + this.best.move[1]);
						}
					}
//					for (int a = 0; a < this.best.board.length; a++)
//					{
//						System.out.print(this.best.board[a] + " ");
//					}
//					System.out.println();
					if (alpha >= beta)
					{
						return beta;
					}
				}

			}

			// Moving on to the next node
			return (isWhite == this.isWhite ? alpha : beta);
		}

		return beta;

	}

	// The root of the tree used in our alpha beta search
	public Node root;

	// Function to add a node to our tree
	public void addNode(Node parent, Node newNode)
	{
		// The tree is empty
		if (root == null || parent == null)
		{
			root = newNode;
		}
		else
		{
			parent.children.add(newNode);
		}

	}

	public int eval(int alpha, int beta, Node current)
	{
		return (int) Math.random() * 10;
	}

	// Evaluate board state
	public static int evaluateState(boolean isWhite, int[] inputBoard)
	{
		int[][] boardState = InitBoard(inputBoard);
		int stateValue = 0;

		int blackPieces = 0;
		int whitePieces = 0;

		// Count number of each color
		// pieces are worth 2, pieces in enemy territory worth 3
		for (int i = 1; i < inputBoard.length; i++)
		{
			// if current piece is white
			if (isWhitePiece(inputBoard[i]))
			{
				whitePieces++;
				// 2 points for a piece, 4 for a king
				if (inputBoard[i] == 1)
					stateValue += 2;
				else
					stateValue += 4;
				// additional point if pawn is in enemy territory
				if (i <= 12 && inputBoard[i] == 1)
					stateValue++;
				// another point if it's on an edge(can't be taken)
				if (isEdge(i))
					stateValue++;
				// additional point if one move from king
				if (i < 9 && inputBoard[i] == 1)
					stateValue++;
			}
			// if current piece is black
			if (inputBoard[i] < 0)
			{
				blackPieces++;

				if (inputBoard[i] == -1)
					stateValue -= 2;
				else
					stateValue -= 4;

				if (i >= 21 && inputBoard[i] == -1)
					stateValue--;

				if (isEdge(i))
					stateValue--;

				if (i > 24 && inputBoard[i] == -1)
					stateValue--;
			}
		}
		stateValue += capturePotential(boardState);

		// No black pieces == Win for White
		if (blackPieces == 0 && whitePieces > 0)
			stateValue = 1000000;
		// No white pieces == Win for black
		if (whitePieces == 0 && blackPieces > 0)
			stateValue = -1000000;

		return stateValue;
	}

	public static int capturePotential(int[][] boardState)
	{
		int canCapture = 0;

		int row = 0;
		int col = 3;
		for (int i = 1; i < 33; i++)
		{
			// check if current piece is white, check if it can capture anything
			// if it can capture something recursively check for double jumps
			if (isWhitePiece(boardState[row][col]))
				canCapture += captureCheckWhite(row, col, boardState,
						boardState[row][col]);
			if (boardState[row][col] < 0)
				canCapture += captureCheckBlack(row, col, boardState,
						boardState[row][col]);

			if (i % 4 == 0)
			{
				switch (i)
				{
					case 4:
						row = 0;
						col = 4;
						break;
					case 8:
						row = 1;
						col = 4;
						break;
					case 12:
						row = 1;
						col = 5;
						break;
					case 16:
						row = 2;
						col = 5;
						break;
					case 20:
						row = 2;
						col = 6;
						break;
					case 24:
						row = 3;
						col = 6;
						break;
					case 28:
						row = 3;
						col = 7;
						break;
				}
			}
			else
			{
				row++;
				col--;
			}
		}

		return canCapture;
	}

	/*
	 * Capture Check for white pieces takes row and column coordinates of piece
	 * being assessed and current board Checks if it has any available captures
	 * from current position if there are potential captures checks for
	 * potential captures from resulting coordinates for double jumps if in the
	 * process of checking captures hits the end of the board "kings" piece for
	 * future checks
	 */
	public static int captureCheckWhite(int row, int col, int[][] boardState,
			int isKing)
	{
		int vulnerableAdjacentPieces = 0;

		// recursive checking for pontential captures by a king piece
		if (isKing == 2)
		{
			if ((row + 2) < 7 && boardState[row + 2][col] == 0
					&& boardState[row + 1][col] < 0)
			{
				if (boardState[row + 1][col] == -1)
					vulnerableAdjacentPieces += 4;
				else
					vulnerableAdjacentPieces += 6;

				// mark square as empty because captured
				boardState[row + 1][col] = 0;

				vulnerableAdjacentPieces +=
						captureCheckWhite(row + 2, col, boardState, isKing);

			}
			if ((col + 2) < 8 && boardState[row][col + 2] == 0
					&& boardState[row][col + 1] < 0)
			{
				if (boardState[row][col + 1] == -1)
					vulnerableAdjacentPieces += 4;
				else
					vulnerableAdjacentPieces += 6;

				// mark square as empty because captured
				boardState[row][col + 1] = 0;

				vulnerableAdjacentPieces +=
						captureCheckWhite(row, col + 2, boardState, isKing);
			}
		}

		// recursive checking for possible captures for both pawns and kings.
		// "Kings" piece if it reaches the end
		if ((row - 2 > 0) && boardState[row - 2][col] == 0
				&& boardState[row - 1][col] < 0)
		{
			if (boardState[row - 1][col] == -1)
				vulnerableAdjacentPieces += 4;
			else
				vulnerableAdjacentPieces += 6;

			// mark square as empty because captured
			boardState[row - 1][col] = 0;

			// if is a pawn and hits the end, king it for future checks
			if (isKing == 1 && kingMe(row - 2, col, true))
				isKing = 2;

			vulnerableAdjacentPieces +=
					captureCheckWhite(row - 2, col, boardState, isKing);
		}

		if ((col - 2) > 0 && boardState[row][col - 2] == 0
				&& boardState[row][col - 1] < 0)
		{
			if (boardState[row][col - 1] == -1)
				vulnerableAdjacentPieces += 4;
			else
				vulnerableAdjacentPieces += 6;

			// mark square as empty because captured
			boardState[row][col - 1] = 0;

			if (isKing == 1 && kingMe(row, col - 2, true))
				isKing = 2;

			vulnerableAdjacentPieces +=
					captureCheckWhite(row, col - 2, boardState, isKing);
		}

		return vulnerableAdjacentPieces;
	}

	/*
	 * Capture Check for black pieces takes row and column coordinates of piece
	 * being assessed and current board Checks if it has any available captures
	 * from current position if there are potential captures checks for
	 * potential captures from resulting coordinates for double jumps if in the
	 * process of checking captures hits the end of the board "kings" piece for
	 * future checks
	 */
	public static int captureCheckBlack(int row, int col, int[][] boardState,
			int isKing)
	{
		int vulnerableAdjacentPieces = 0;

		// recursive checking for pontential captures by a king piece
		if (isKing == -2)
		{
			if ((row - 2) > 0 && boardState[row - 2][col] == 0
					&& isWhitePiece(boardState[row - 1][col]))
			{
				if (boardState[row - 1][col] == 1)
					vulnerableAdjacentPieces -= 4;
				else
					vulnerableAdjacentPieces -= 6;

				// mark space as empty because captured
				boardState[row - 1][col] = 0;

				vulnerableAdjacentPieces +=
						captureCheckBlack(row - 2, col, boardState, isKing);

			}
			if ((col - 2) > 0 && boardState[row][col - 2] == 0
					&& isWhitePiece(boardState[row][col - 1]))
			{
				if (boardState[row][col - 1] == 1)
					vulnerableAdjacentPieces -= 4;
				else
					vulnerableAdjacentPieces -= 6;

				// mark space as empty because captured
				boardState[row][col - 1] = 0;

				vulnerableAdjacentPieces +=
						captureCheckBlack(row, col - 2, boardState, isKing);
			}
		}

		// recursive checking for possible captures for both pawns and kings.
		// "Kings" piece if it reaches the end
		if ((row + 2 < 7) && boardState[row + 2][col] == 0
				&& isWhitePiece(boardState[row + 1][col]))
		{
			if (boardState[row + 1][col] == 1)
				vulnerableAdjacentPieces -= 4;
			else
				vulnerableAdjacentPieces -= 6;

			// mark space as empty because captured
			boardState[row + 1][col] = 0;

			// if is a pawn and hits the end, king it for future checks
			if (isKing == -1 && kingMe(row + 2, col, false))
				isKing = -2;

			vulnerableAdjacentPieces +=
					captureCheckBlack(row + 2, col, boardState, isKing);
		}

		if ((col + 2) < 8 && boardState[row][col + 2] == 0
				&& isWhitePiece(boardState[row][col + 1]))
		{
			if (boardState[row][col + 1] == 1)
				vulnerableAdjacentPieces -= 4;
			else
				vulnerableAdjacentPieces -= 6;

			// mark space as empty because captured
			boardState[row][col + 1] = 0;

			if (isKing == -1 && kingMe(row, col + 2, false))
				isKing = -2;

			vulnerableAdjacentPieces +=
					captureCheckBlack(row, col + 2, boardState, isKing);
		}

		return vulnerableAdjacentPieces;
	}

	// check if piece is on a SIDE edge, not ends
	public static boolean isEdge(int position)
	{
		int[] edgeSquares = { 5, 13, 21, 12, 20, 28 };
		for (int i = 0; i < edgeSquares.length; i++)
		{
			if (edgeSquares[i] == position)
				return true;
		}

		return false;
	}

	// Check if a piece is white
	public static boolean isWhitePiece(int i)
	{
		if (i == 1 || i == 2)
			return true;
		else
			return false;
	}

	public static boolean kingMe(int row, int col, boolean isWhitePiece)
	{
		// white piece
		if (isWhitePiece)
		{
			for (int i = 0; i < 4; i++)
			{
				for (int j = 3; j >= 0; j--)
				{
					if (i == row && j == col)
						return true;
				}
			}
		}
		// black piece
		else
		{
			for (int i = 6; i > 2; i--)
			{
				for (int j = 4; j < 8; j++)
				{
					if (i == row && j == col)
						return true;
				}
			}
		}

		return false;
	}

	// initialize tilted board
	public static int[][] InitBoard(int[] inputBoard)
	{
		int[][] boardState = new int[7][8];

		// initialize board to empty many indeces of array will remain empty
		for (int row = 0; row < boardState.length; row++)
		{
			for (int col = 0; col < boardState[row].length; col++)
				boardState[row][col] = 6;
		}

		int row = 0;
		int col = 3;
		for (int i = 1; i < 33; i++)
		{
			boardState[row][col] = inputBoard[i];

			if (i % 4 == 0)
			{
				switch (i)
				{
					case 4:
						row = 0;
						col = 4;
						break;
					case 8:
						row = 1;
						col = 4;
						break;
					case 12:
						row = 1;
						col = 5;
						break;
					case 16:
						row = 2;
						col = 5;
						break;
					case 20:
						row = 2;
						col = 6;
						break;
					case 24:
						row = 3;
						col = 6;
						break;
					case 28:
						row = 3;
						col = 7;
						break;
				}
			}
			else
			{
				row++;
				col--;
			}
		}

		return boardState;
	}

	public LinkedList<Node> reachableBoards(Node n)
	{
		LinkedList<Node> possibleMoves = new LinkedList<Node>();
		int moveCounter = 1;
		int jumpSize = 4;
		// CHECKERS MOVE FORWARD LEFT
		for (int i = 1; i < board.length; i++)
		{
			// White turn
			if (n.isWhite == true)
			{
				// Jump size changes based on left justified vs right justified
				// rows
				if (moveCounter <= 4)
				{
					jumpSize = 4;
				}
				else
				{
					jumpSize = 5;
				}
				// Making sure piece is white
				if (board[i] == WHITE_PAWN || board[i] == WHITE_KING)
				{
					// Backward Left if King
					if (board[i] == WHITE_KING)
					{
						if (moveCounter <= 4)
						{
							jumpSize = 5;
						}
						else
						{
							jumpSize = 4;
						}
						if (i != 4 && i != 12 && i != 20 && i != 28 && i < 28)
						{
							// Basic left movement
							if (board[i + jumpSize] == BLANK)
							{
								int[] newBoard = new int[33];
								for (int j = 1; j < board.length; j++)
								{
									if (j == i + jumpSize)
									{
										newBoard[j] = board[i];
									}
									else if (j == i)
									{
										newBoard[j] = BLANK;
									}
									else
									{
										newBoard[j] = board[j];
									}
								}
								// Adding new node if ply limit not reached
								if (n.level <= this.ply)
								{
									Node newNode = new Node(n, newBoard,
											!n.isWhite, n.movesRemaining - 1,
											n.level + 1);
									newNode.move[0] = i;
									newNode.move[1] = i + jumpSize;
									possibleMoves.add(0, newNode);
								}
							}
							// Left capture
							if (board[i + jumpSize] == BLACK_PAWN
									|| board[i + jumpSize] == BLACK_KING)
							{
								// 1, 2, 3, 5, 6, 7, 9, 10, 11, 13, 14, 15, 17,
								// 18, 19, 21, 22, 23
								boolean canJump = false;
								int counter = 1;
								for (int z = 1; z <= i && z < 24; z++)
								{
									if (z == i && counter != 4)
									{
										canJump = true;
									}
									if (counter == 4)
									{
										counter = 0;
									}
									counter = counter + 1;
								}
								if (i < 24 && board[i + 9] == BLANK
										&& canJump == true)
								{
									int[] newBoard = new int[33];
									for (int j = 1; j < board.length; j++)
									{
										if (j == i + 9)
										{
											newBoard[j] = WHITE_KING;
										}
										else if (j == i + jumpSize)
										{
											newBoard[j] = BLANK;
										}
										else if (j == i)
										{
											newBoard[j] = BLANK;
										}
										else
										{
											newBoard[j] = board[j];
										}
									}
									// Double capture
									boolean doubleCap = false;
									if ((i == 1 || i == 2 || i == 5 || i == 6 || i == 9 || i == 10 || i == 13 || i == 14) && (newBoard[i+9+jumpSize] == BLACK_KING || newBoard[i+9+jumpSize] == BLACK_PAWN) && newBoard[i + 18] == BLANK)
									{
										newBoard[i + 9] = BLANK;
										newBoard[i + 9 + jumpSize] = BLANK;
										newBoard[i + 18] = board[i];
										doubleCap = true;
									}
									if (n.level <= this.ply && doubleCap == true)
									{
										Node newNode = new Node(n, newBoard,
												!n.isWhite, n.movesRemaining - 1,
												n.level + 1);
										newNode.move = new int[3];
										newNode.move[0] = i;
										newNode.move[1] = i + 9;
										newNode.move[2] = i + 18;
										newNode.capturePossible = true;
										possibleMoves.add(0, newNode);
									}
									// Adding new node if ply limit not reached
									else if (n.level <= this.ply)
									{
										Node newNode = new Node(n, newBoard,
												!n.isWhite,
												n.movesRemaining - 1,
												n.level + 1);
										newNode.move[0] = i;
										newNode.move[1] = i + 9;
										newNode.capturePossible = true;
										possibleMoves.add(0, newNode);
									}

								}
							}
						}
						if (moveCounter <= 4)
						{
							jumpSize = 4;
						}
						else
						{
							jumpSize = 5;
						}
					}
					// Pawn or King Forward left
					if (i != 5 && i != 13 && i != 21 && i != 29 && i > 5)
					{
						// Standard left jump
						if (board[i - jumpSize] == BLANK)
						{
							int[] newBoard = new int[33];
							for (int j = 1; j < board.length; j++)
							{
								if (j == i - jumpSize)
								{
									newBoard[j] = board[i];
								}
								else if (j == i)
								{
									newBoard[j] = BLANK;
								}
								else
								{
									newBoard[j] = board[j];
								}
							}
							// Adding new node if ply limit not reached
							if (n.level <= this.ply)
							{
								Node newNode = new Node(n, newBoard, !n.isWhite,
										n.movesRemaining - 1, n.level + 1);
								newNode.move[0] = i;
								newNode.move[1] = i - jumpSize;
								possibleMoves.add(0, newNode);
							}
						}
						// Standard left capture
						if (board[i - jumpSize] == BLACK_PAWN
								|| board[i - jumpSize] == BLACK_KING)
						{
							// 10, 11, 12, 14, 15, 16, 18, 19, 20, 22, 23,
							// 24, 26, 27, 28, 30, 31, 32
							boolean canJump = false;
							int counter = 1;
							for (int z = 10; z <= i; z++)
							{
								if (z == i && counter != 4)
								{
									canJump = true;
								}
								if (counter == 4)
								{
									counter = 0;
								}
								counter = counter + 1;
							}
							if (i > 9 && board[i - 9] == BLANK
									&& canJump == true)
							{
								int[] newBoard = new int[33];
								int newPiece = board[i];
								for (int j = 1; j < board.length; j++)
								{
									if (j == i - 9)
									{
										newBoard[j] = newPiece;
									}
									else if (j == i - jumpSize)
									{
										newBoard[j] = BLANK;
									}
									else if (j == i)
									{
										newBoard[j] = BLANK;
									}
									else
									{
										newBoard[j] = board[j];
									}
								}
								// Double capture
								boolean doubleCap = false;
								if ((i == 19 || i == 20 || i == 23 || i == 24 || i == 27 || i == 28 || i == 31 || i ==32) && (newBoard[i-9-jumpSize] == BLACK_KING || newBoard[i-9-jumpSize] == BLACK_PAWN) && newBoard[i - 18] == BLANK)
								{
									newBoard[i - 9] = BLANK;
									newBoard[i - 9 - jumpSize] = BLANK;
									newBoard[i - 18] = board[i];
									doubleCap = true;
								}
								if (n.level <= this.ply && doubleCap == true)
								{
									Node newNode = new Node(n, newBoard,
											!n.isWhite, n.movesRemaining - 1,
											n.level + 1);
									newNode.move = new int[3];
									newNode.move[0] = i;
									newNode.move[1] = i - 9;
									newNode.move[2] = i - 18;
									newNode.capturePossible = true;
									possibleMoves.add(0, newNode);
								}
								// Adding new node if ply limit not reached
								else if (n.level <= this.ply)
								{
									Node newNode = new Node(n, newBoard,
											!n.isWhite, n.movesRemaining - 1,
											n.level + 1);
									newNode.move[0] = i;
									newNode.move[1] = i - 9;
									newNode.capturePossible = true;
									possibleMoves.add(0, newNode);
								}
							}
						}
					}

				}
			}
			// Black turn
			else
			{
				if (moveCounter <= 4)
				{
					jumpSize = 5;
				}
				else
				{
					jumpSize = 4;
				}
				if (board[i] == BLACK_PAWN || board[i] == BLACK_KING)
				{
					// King is backward left
					if (board[i] == BLACK_KING)
					{
						// Jump size changes based on left justified vs right
						// justified
						// rows
						if (moveCounter <= 4)
						{
							jumpSize = 4;
						}
						else
						{
							jumpSize = 5;
						}
						if (i != 5 && i != 13 && i != 21 && i != 29 && i > 5)
						{
							// Standard jump
							if (board[i - jumpSize] == BLANK)
							{
								int[] newBoard = new int[33];
								for (int j = 1; j < board.length; j++)
								{
									if (j == i - jumpSize)
									{
										newBoard[j] = board[i];
									}
									else if (j == i)
									{
										newBoard[j] = BLANK;
									}
									else
									{
										newBoard[j] = board[j];
									}
								}
								// Adding new node if ply limit not reached
								if (n.level <= this.ply)
								{
									Node newNode = new Node(n, newBoard,
											!n.isWhite, n.movesRemaining - 1,
											n.level + 1);
									newNode.move[0] = i;
									newNode.move[1] = i - jumpSize;
									possibleMoves.add(0, newNode);
								}
							}
							// Standard capture
							if (board[i - jumpSize] == WHITE_PAWN
									|| board[i - jumpSize] == WHITE_KING)
							{
								// 10, 11, 12, 14, 15, 16, 18, 19, 20, 22, 23,
								// 24, 26, 27, 28, 30, 31, 32
								boolean canJump = false;
								int counter = 1;
								for (int z = 10; z <= i; z++)
								{
									if (z == i && counter != 4)
									{
										canJump = true;
									}
									if (counter == 4)
									{
										counter = 0;
									}
									counter = counter + 1;
								}
								if (i > 9 && board[i - 9] == BLANK
										&& canJump == true)
								{
									int[] newBoard = new int[33];
									for (int j = 1; j < board.length; j++)
									{
										if (j == i - 9)
										{
											newBoard[j] = BLACK_KING;
										}
										else if (j == i - jumpSize)
										{
											newBoard[j] = BLANK;
										}
										else if (j == i)
										{
											newBoard[j] = BLANK;
										}
										else
										{
											newBoard[j] = board[j];
										}
									}
									// Double capture
									boolean doubleCap = false;
									if ((i == 19 || i == 20 || i == 23 || i == 24 || i == 27 || i == 28 || i == 31 || i == 32) && (newBoard[i-9-jumpSize] == WHITE_KING || newBoard[i-9-jumpSize] == WHITE_PAWN) && newBoard[i - 18] == BLANK)
									{
										newBoard[i - 9] = BLANK;
										newBoard[i - 9 + jumpSize] = BLANK;
										newBoard[i - 18] = board[i];
										doubleCap = true;
									}
									if (n.level <= this.ply && doubleCap == true)
									{
										Node newNode = new Node(n, newBoard,
												!n.isWhite, n.movesRemaining - 1,
												n.level + 1);
										newNode.move = new int[3];
										newNode.move[0] = i;
										newNode.move[1] = i - 9;
										newNode.move[2] = i - 18;
										newNode.capturePossible = true;
										possibleMoves.add(0, newNode);
									}
									// Adding new node if ply limit not reached
									else if (n.level <= this.ply)
									{
										Node newNode = new Node(n, newBoard,
												!n.isWhite,
												n.movesRemaining - 1,
												n.level + 1);
										newNode.move[0] = i;
										newNode.move[1] = i - 9;
										newNode.capturePossible = true;
										possibleMoves.add(0, newNode);
									}
								}
							}
						}
						if (moveCounter <= 4)
						{
							jumpSize = 5;
						}
						else
						{
							jumpSize = 4;
						}
					}
					// Forward left for King or pawn
					if (i != 4 && i != 12 && i != 20 && i != 28 && i < 28)
					{
						// Standard jump
						if (board[i + jumpSize] == BLANK)
						{
							int[] newBoard = new int[33];
							for (int j = 1; j < board.length; j++)
							{
								if (j == i + jumpSize)
								{
									newBoard[j] = board[i];
								}
								else if (j == i)
								{
									newBoard[j] = BLANK;
								}
								else
								{
									newBoard[j] = board[j];
								}
							}
							// Adding new node if ply limit not reached
							if (n.level <= this.ply)
							{
								Node newNode = new Node(n, newBoard, !n.isWhite,
										n.movesRemaining - 1, n.level + 1);
								newNode.move[0] = i;
								newNode.move[1] = i + jumpSize;
								possibleMoves.add(0, newNode);
							}
						}
						// Standard capture
						if (board[i + jumpSize] == WHITE_PAWN
								|| board[i + jumpSize] == WHITE_KING)
						{
							// 1, 2, 3, 5, 6, 7, 9, 10, 11, 13, 14, 15, 17,
							// 18, 19, 21, 22, 23
							boolean canJump = false;
							int counter = 1;
							for (int z = 1; z <= i && z < 24; z++)
							{
								if (z == i && counter != 4)
								{
									canJump = true;
								}
								if (counter == 4)
								{
									// System.out.println(z);
									counter = 0;
								}
								counter = counter + 1;
							}
							if (i < 24 && board[i + 9] == BLANK
									&& canJump == true)
							{
								int[] newBoard = new int[33];
								int newPiece = board[i];
								for (int j = 1; j < board.length; j++)
								{
									if (j == i + 9)
									{
										newBoard[j] = newPiece;
									}
									else if (j == i + jumpSize)
									{
										newBoard[j] = BLANK;
									}
									else if (j == i)
									{
										newBoard[j] = BLANK;
									}
									else
									{
										newBoard[j] = board[j];
									}
								}
								// Double capture
								boolean doubleCap = false;
								if ((i == 1 || i == 2 || i == 5 || i == 6 || i == 9 || i == 10 || i == 13 || i == 14) && (newBoard[i+9+jumpSize] == WHITE_KING || newBoard[i+9+jumpSize] == WHITE_PAWN) && newBoard[i + 18] == BLANK)
								{
									newBoard[i + 9] = BLANK;
									newBoard[i + 9 + jumpSize] = BLANK;
									newBoard[i + 18] = board[i];
									doubleCap = true;
								}
								if (n.level <= this.ply && doubleCap == true)
								{
									Node newNode = new Node(n, newBoard,
											!n.isWhite, n.movesRemaining - 1,
											n.level + 1);
									newNode.move = new int[3];
									newNode.move[0] = i;
									newNode.move[1] = i + 9;
									newNode.move[2] = i + 18;
									newNode.capturePossible = true;
									possibleMoves.add(0, newNode);
								}
								// Adding new node if ply limit not reached
								else if (n.level <= this.ply)
								{
									Node newNode = new Node(n, newBoard,
											!n.isWhite, n.movesRemaining - 1,
											n.level + 1);
									newNode.move[0] = i;
									newNode.move[1] = i + 9;
									newNode.capturePossible = true;
									possibleMoves.add(0, newNode);
								}
							}
						}
					}

				}
			}
			moveCounter++;
			if (moveCounter > 8)
			{
				moveCounter = 1;
			}
		}

		moveCounter = 1;
		jumpSize = 4;

		// CHECKERS MOVE FORWARD Right
		for (int i = 1; i < board.length; i++)
		{
			// White turn
			if (n.isWhite == true)
			{
				// Jump size changes based on left justified vs right justified
				// rows
				if (moveCounter <= 4)
				{
					jumpSize = 3;
				}
				else
				{
					jumpSize = 4;
				}
				if (board[i] == WHITE_PAWN || board[i] == WHITE_KING)
				{
					// King backwards right
					if (board[i] == WHITE_KING)
					{
						if (moveCounter <= 4)
						{
							jumpSize = 4;
						}
						else
						{
							jumpSize = 3;
						}
						if (i != 5 && i != 13 && i != 21 && i != 29 && i < 29)
						{
							// Standard jump
							if (board[i + jumpSize] == BLANK)
							{
								int[] newBoard = new int[33];
								for (int j = 1; j < board.length; j++)
								{
									if (j == i + jumpSize)
									{
										newBoard[j] = board[i];
									}
									else if (j == i)
									{
										newBoard[j] = BLANK;
									}
									else
									{
										newBoard[j] = board[j];
									}
								}
								// Adding new node if ply limit not reached
								if (n.level <= this.ply)
								{
									Node newNode = new Node(n, newBoard,
											!n.isWhite, n.movesRemaining - 1,
											n.level + 1);
									newNode.move[0] = i;
									newNode.move[1] = i + jumpSize;
									possibleMoves.add(0, newNode);
								}
							}
							// Standard capture
							if (board[i + jumpSize] == BLACK_PAWN
									|| board[i + jumpSize] == BLACK_KING)
							{
								// 2, 3, 4, 6, 7, 8, 10, 11, 12, 14, 15, 16, 18,
								// 19,
								// 20, 22, 23, 24
								boolean canJump = false;
								int counter = 1;
								for (int z = 2; z <= i && z < 25; z++)
								{
									if (z == i && counter != 4)
									{
										canJump = true;
									}
									if (counter == 4)
									{
										counter = 0;
									}
									counter = counter + 1;
								}
								if (i < 25 && board[i + 7] == BLANK
										&& canJump == true)
								{
									int[] newBoard = new int[33];
									int newPiece = board[i];
									for (int j = 1; j < board.length; j++)
									{
										if (j == i + 7)
										{
											newBoard[j] = newPiece;
										}
										else if (j == i + jumpSize)
										{
											newBoard[j] = BLANK;
										}
										else if (j == i)
										{
											newBoard[j] = BLANK;
										}
										else
										{
											newBoard[j] = board[j];
										}
									}
									// Double capture
									boolean doubleCap = false;
									if ((i == 3 || i == 4 || i == 7 || i == 8 || i == 11 || i == 12 || i == 15 || i == 16) && (newBoard[i+7+jumpSize] == BLACK_KING || newBoard[i+7+jumpSize] == BLACK_PAWN) && newBoard[i + 14] == BLANK)
									{
										newBoard[i + 7] = BLANK;
										newBoard[i + 7 + jumpSize] = BLANK;
										newBoard[i + 14] = board[i];
										doubleCap = true;
									}
									if (n.level <= this.ply && doubleCap == true)
									{
										Node newNode = new Node(n, newBoard,
												!n.isWhite, n.movesRemaining - 1,
												n.level + 1);
										newNode.move = new int[3];
										newNode.move[0] = i;
										newNode.move[1] = i + 7;
										newNode.move[2] = i + 14;
										newNode.capturePossible = true;
										possibleMoves.add(0, newNode);
									}
									// Adding new node if ply limit not reached
									else if (n.level <= this.ply)
									{
										Node newNode = new Node(n, newBoard,
												!n.isWhite,
												n.movesRemaining - 1,
												n.level + 1);
										newNode.move[0] = i;
										newNode.move[1] = i + 7;
										newNode.capturePossible = true;
										possibleMoves.add(0, newNode);
									}
								}
							}
						}
						if (moveCounter <= 4)
						{
							jumpSize = 3;
						}
						else
						{
							jumpSize = 4;
						}
					}
					// Pawn or King forward right
					if (i != 4 && i != 12 && i != 20 && i != 28 && i > 5)
					{
						// Standard jump
						if (board[i - jumpSize] == BLANK)
						{
							int[] newBoard = new int[33];
							for (int j = 1; j < board.length; j++)
							{
								if (j == i - jumpSize)
								{
									newBoard[j] = board[i];
								}
								else if (j == i)
								{
									newBoard[j] = BLANK;
								}
								else
								{
									newBoard[j] = board[j];
								}
							}
							// Adding new node if ply limit not reached
							if (n.level <= this.ply)
							{
								Node newNode = new Node(n, newBoard, !n.isWhite,
										n.movesRemaining - 1, n.level + 1);
								newNode.move[0] = i;
								newNode.move[1] = i - jumpSize;
								possibleMoves.add(0, newNode);
							}
						}
						// Standard capture
						if (board[i - jumpSize] == BLACK_PAWN
								|| board[i - jumpSize] == BLACK_KING)
						{
							// 9, 10, 11, 13, 14, 15, 17, 18, 19, 21, 22, 23,
							// 25, 26, 27, 29, 30, 31
							boolean canJump = false;
							int counter = 1;
							for (int z = 9; z <= i && i < 32; z++)
							{
								if (z == i && counter != 4)
								{
									canJump = true;
								}
								if (counter == 4)
								{
									counter = 0;
								}
								counter = counter + 1;
							}
							if (i > 7 && board[i - 7] == BLANK
									&& canJump == true)
							{
								int[] newBoard = new int[33];
								int newPiece = board[i];
								for (int j = 1; j < board.length; j++)
								{
									if (j == i - 7)
									{
										newBoard[j] = newPiece;
									}
									else if (j == i - jumpSize)
									{
										newBoard[j] = BLANK;
									}
									else if (j == i)
									{
										newBoard[j] = BLANK;
									}
									else
									{
										newBoard[j] = board[j];
									}
								}
								// Double capture
								boolean doubleCap = false;
								if ((i == 17 || i == 18 || i == 21 || i == 22 || i == 25 || i == 26 || i == 29 || i == 30) && (newBoard[i-7-jumpSize] == BLACK_KING || newBoard[i-7-jumpSize] == BLACK_PAWN) && newBoard[i - 14] == BLANK)
								{
									newBoard[i - 7] = BLANK;
									newBoard[i - 7 + jumpSize] = BLANK;
									newBoard[i - 14] = board[i];
									doubleCap = true;
								}
								if (n.level <= this.ply && doubleCap == true)
								{
									Node newNode = new Node(n, newBoard,
											!n.isWhite, n.movesRemaining - 1,
											n.level + 1);
									newNode.move = new int[3];
									newNode.move[0] = i;
									newNode.move[1] = i - 7;
									newNode.move[2] = i - 14;
									newNode.capturePossible = true;
									possibleMoves.add(0, newNode);
								}
								// Adding new node if ply limit is not reached
								else if (n.level <= this.ply)
								{
									Node newNode = new Node(n, newBoard,
											!n.isWhite, n.movesRemaining - 1,
											n.level + 1);
									newNode.move[0] = i;
									newNode.move[1] = i - 7;
									newNode.capturePossible = true;
									possibleMoves.add(0, newNode);
								}
							}
						}
					}

				}
			}
			// Black turn
			else
			{
				// Jump size changes based on left justified vs right justified
				// rows
				if (moveCounter <= 4)
				{
					jumpSize = 4;
				}
				else
				{
					jumpSize = 3;
				}
				if (board[i] == BLACK_PAWN || board[i] == BLACK_KING)
				{
					// King is backwards right
					if (board[i] == BLACK_KING)
					{
						if (moveCounter <= 4)
						{
							jumpSize = 3;
						}
						else
						{
							jumpSize = 4;
						}
						if (i != 4 && i != 12 && i != 20 && i != 28 && i > 5)
						{
							// Standard jump
							if (board[i - jumpSize] == BLANK)
							{
								int[] newBoard = new int[33];
								for (int j = 1; j < board.length; j++)
								{
									if (j == i - jumpSize)
									{
										newBoard[j] = board[i];
									}
									else if (j == i)
									{
										newBoard[j] = BLANK;
									}
									else
									{
										newBoard[j] = board[j];
									}
								}
								// Adding new node if ply limit is not reached
								if (n.level <= this.ply)
								{
									Node newNode = new Node(n, newBoard,
											!n.isWhite, n.movesRemaining - 1,
											n.level + 1);
									newNode.move[0] = i;
									newNode.move[1] = i - jumpSize;
									possibleMoves.add(0, newNode);
								}
							}
							// Standard capture
							if (board[i - jumpSize] == WHITE_PAWN
									|| board[i - jumpSize] == WHITE_KING)
							{
								// 9, 10, 11, 13, 14, 15, 17, 18, 19, 21, 22,
								// 23,
								// 25, 26, 27, 29, 30, 31
								boolean canJump = false;
								int counter = 1;
								for (int z = 9; z <= i && i < 32; z++)
								{
									if (z == i && counter != 4)
									{
										canJump = true;
									}
									if (counter == 4)
									{
										counter = 0;
									}
									counter = counter + 1;
								}
								if (i > 7 && board[i - 7] == BLANK
										&& canJump == true)
								{
									int[] newBoard = new int[33];
									for (int j = 1; j < board.length; j++)
									{
										if (j == i - 7)
										{
											newBoard[j] = BLACK_KING;
										}
										else if (j == i - jumpSize)
										{
											newBoard[j] = BLANK;
										}
										else if (j == i)
										{
											newBoard[j] = BLANK;
										}
										else
										{
											newBoard[j] = board[j];
										}
									}
									// Double capture
									boolean doubleCap = false;
									if ((i == 17 || i == 18 || i == 21 || i == 22 || i == 25 || i == 26 || i == 29 || i == 30) && (newBoard[i-7-jumpSize] == WHITE_KING || newBoard[i-7-jumpSize] == WHITE_PAWN) && newBoard[i - 14] == BLANK)
									{
										newBoard[i - 7] = BLANK;
										newBoard[i - 7 + jumpSize] = BLANK;
										newBoard[i - 14] = board[i];
										doubleCap = true;
									}
									if (n.level <= this.ply && doubleCap == true)
									{
										Node newNode = new Node(n, newBoard,
												!n.isWhite, n.movesRemaining - 1,
												n.level + 1);
										newNode.move = new int[3];
										newNode.move[0] = i;
										newNode.move[1] = i - 7;
										newNode.move[2] = i - 14;
										newNode.capturePossible = true;
										possibleMoves.add(0, newNode);
									}
									// Adding new node if ply limit is not
									// reached
									else if (n.level <= this.ply)
									{
										Node newNode = new Node(n, newBoard,
												!n.isWhite,
												n.movesRemaining - 1,
												n.level + 1);
										newNode.move[0] = i;
										newNode.move[1] = i - 7;
										newNode.capturePossible = true;
										possibleMoves.add(0, newNode);
									}
								}
							}
						}
						if (moveCounter <= 4)
						{
							jumpSize = 4;
						}
						else
						{
							jumpSize = 3;
						}
					}
					// King or pawn forward right
					if (i != 5 && i != 13 && i != 21 && i != 29 && i < 29)
					{
						// Standard jump
						if (board[i + jumpSize] == BLANK)
						{
							int[] newBoard = new int[33];
							for (int j = 1; j < board.length; j++)
							{
								if (j == i + jumpSize)
								{
									newBoard[j] = board[i];
								}
								else if (j == i)
								{
									newBoard[j] = BLANK;
								}
								else
								{
									newBoard[j] = board[j];
								}
							}
							// Adding new node if ply limit is not reached
							if (n.level <= this.ply)
							{
								Node newNode = new Node(n, newBoard, !n.isWhite,
										n.movesRemaining - 1, n.level + 1);
								newNode.move[0] = i;
								newNode.move[1] = i + jumpSize;
								possibleMoves.add(0, newNode);
							}
						}
						// Standard capture
						if (board[i + jumpSize] == WHITE_PAWN
								|| board[i + jumpSize] == WHITE_KING)
						{
							// 2, 3, 4, 6, 7, 8, 10, 11, 12, 14, 15, 16, 18, 19,
							// 20, 22, 23, 24
							boolean canJump = false;
							int counter = 1;
							for (int z = 2; z <= i && z < 25; z++)
							{
								if (z == i && counter != 4)
								{
									canJump = true;
								}
								if (counter == 4)
								{
									counter = 0;
								}
								counter = counter + 1;
							}
							if (i < 25 && board[i + 7] == BLANK
									&& canJump == true)
							{
								int[] newBoard = new int[33];
								int newPiece = board[i];
								for (int j = 1; j < board.length; j++)
								{
									if (j == i + 7)
									{
										newBoard[j] = newPiece;
									}
									else if (j == i + jumpSize)
									{
										newBoard[j] = BLANK;
									}
									else if (j == i)
									{
										newBoard[j] = BLANK;
									}
									else
									{
										newBoard[j] = board[j];
									}
								}
								// Double capture
								boolean doubleCap = false;
								if ((i == 3 || i == 4 || i == 7 || i == 8 || i == 11 || i == 12 || i == 15 || i == 16) && (newBoard[i+7+jumpSize] == WHITE_KING || newBoard[i+7+jumpSize] == WHITE_PAWN) && newBoard[i + 14] == BLANK)
								{
									newBoard[i + 7] = BLANK;
									newBoard[i + 7 + jumpSize] = BLANK;
									newBoard[i + 14] = board[i];
									doubleCap = true;
								}
								if (n.level <= this.ply && doubleCap == true)
								{
									Node newNode = new Node(n, newBoard,
											!n.isWhite, n.movesRemaining - 1,
											n.level + 1);
									newNode.move = new int[3];
									newNode.move[0] = i;
									newNode.move[1] = i + 7;
									newNode.move[2] = i + 14;
									newNode.capturePossible = true;
									possibleMoves.add(0, newNode);
								}
								// Adding a new node if ply limit is not reached
								else if (n.level <= this.ply)
								{
									Node newNode = new Node(n, newBoard,
											!n.isWhite, n.movesRemaining - 1,
											n.level + 1);
									newNode.move[0] = i;
									newNode.move[1] = i + 7;
									newNode.capturePossible = true;
									possibleMoves.add(0, newNode);
								}
							}
						}
					}
				}
			}
			moveCounter++;
			if (moveCounter > 8)
			{
				moveCounter = 1;
			}
		}

		// If there are captures possible, removing all illegal non capture
		// moves
		boolean hasCapture = false;
		for (int i = 0; i < possibleMoves.size(); i++)
		{
			if (possibleMoves.get(i).capturePossible == true)
			{
				hasCapture = true;
			}
		}
		if (hasCapture == true)
		{
			for (int i = 0; i < possibleMoves.size(); i++)
			{
				if (possibleMoves.get(i).capturePossible == false)
				{
					possibleMoves.remove(i);
					i = i - 1;
				}
			}
		}

		// Returning possible boards
		return possibleMoves;
	}

}
