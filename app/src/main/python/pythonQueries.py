from mysql import connector

def main():
    print(getNewOptions("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq",""))

def getNewOptions(fen, moves):
    # Setup connection
    server = "chess.cizcr7arqko1.us-east-2.rds.amazonaws.com"
    database = "chess"
    username = "admin"
    password = "chessmaster"
    cnx = connector.connect(host=server, database=database, user=username, passwd=password, port=3306)
    cursor = cnx.cursor()

    # Only use moves parameter if not in starting board state
    q = " SELECT * FROM MASTER"
    if fen != "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq":
        q += " WHERE " + moves  # Check if every move applies to the current FEN. moves is set in a function beforehand
    q += " LIMIT 80000"     # Limit results to 88000 because that's the maximum we can receive before we get overflow errors in java

    #return q
    cursor.execute(q)
    rs = cursor.fetchall()
    cnx.close()
    return rs