# Chess Vision

A data-oriented chess app built to display and predict future moves.

![chessdemo](https://github.com/thomasszp/Chess.data/assets/28933646/8f20b47d-1f5d-4434-b813-cd58a74dcc18)


## Description

Any chess game state can be summarized by FEN notation in a simple string (e.g. *rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1*).
Chess Vision uses this notation to compare the current board of any given game and compare it with a database of historical games to give insights into the board state.

The app will give a summary of some of the available data, such as popular moves, and likely win percentages.
As a game progresses, less data is available to be drawn from and the relative popularity of each move decreases.

This tool is most useful for analyzing and displaying popular blunders and brilliant moves that lead to drastic swings in win percentage at any given moment.


## Authors

Tom Szpila
Adam Cerutti


## Acknowledgments

* [Lichess.com](https://https://lichess.org/)
* [Lichess Database](https://https://database.lichess.org/)
