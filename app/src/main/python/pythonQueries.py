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