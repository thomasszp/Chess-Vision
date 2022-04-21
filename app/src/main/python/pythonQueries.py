from mysql import connector

def main():
    #cnx = mysql.connector.connect(user='admin', password='chessmaster', host='chess.cizcr7arqko1.us-east-2.rds.amazonaws.com', database='chess')
    server = "chess.cizcr7arqko1.us-east-2.rds.amazonaws.com"
    database = "chess"
    username = "admin"
    password = "chessmaster"
    cnx = connector.connect(host=server, database=database, user=username, passwd=password, port=3306)
    query = "SELECT * FROM MASTER LIMIT 1"
    cursor = cnx.cursor(buffered = True)
    cursor.execute(query)
    cnx.commit()
    rs = cursor.fetchall()
    string = ""
    for x in rs:
        string = string + str(x) + " "
    cnx.close()
    return string

def getNewOptions(fen, moves):
    # Setup connection
    server = "chess.cizcr7arqko1.us-east-2.rds.amazonaws.com"
    database = "chess"
    username = "admin"
    password = "chessmaster"
    cnx = connector.connect(host=server, database=database, user=username, passwd=password, port=3306)
    cursor = cnx.cursor()

    q = " SELECT * FROM MASTER"
    q += " WHERE " + moves  # Check if every move applies to the current FEN. moves is set in a function beforehand
    cursor.execute(q)
    rs = cursor.fetchall()
    cnx.close()
    return rs