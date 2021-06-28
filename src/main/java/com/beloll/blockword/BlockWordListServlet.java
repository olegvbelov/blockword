package com.beloll.blockword;

import com.jsoniter.output.JsonStream;
import com.yandex.ydb.table.Session;
import com.yandex.ydb.table.result.ResultSetReader;
import com.yandex.ydb.table.transaction.TxControl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class BlockWordListServlet extends HttpServlet {
    
    private final DBConnector dbConnector = new DBConnector();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
        Session session = dbConnector.connect();
        String database = System.getenv("DATABASE");
    
        String query = String.format(
                "\n" +
                        "PRAGMA TablePathPrefix(\"%s\");\n" +
                        "\n" +
                        "SELECT\n" +
                        "    word,\n" +
                        "FROM blockwords;",
                database);
        TxControl txControl = TxControl.serializableRw().setCommitTx(true);
        ResultSetReader result = session.executeDataQuery(query,  txControl)
                .join()
                .expect("ok")
                .getResultSet(0);
    
        List<String> words = new ArrayList<>();
        while (result.next()) {
            words.add(result.getColumn("word").getUtf8());
        }
    
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.print(JsonStream.serialize(words));
        out.flush();
    }
}
