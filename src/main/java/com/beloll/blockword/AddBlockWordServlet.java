package com.beloll.blockword;

import com.yandex.ydb.table.Session;
import com.yandex.ydb.table.query.DataQuery;
import com.yandex.ydb.table.query.DataQueryResult;
import com.yandex.ydb.table.query.Params;
import com.yandex.ydb.table.transaction.TxControl;
import com.yandex.ydb.table.values.PrimitiveValue;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddBlockWordServlet extends HttpServlet {
    
    private final DBConnector dbConnector = new DBConnector();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String word = req.getReader().readLine();
        
        Session session = dbConnector.connect();
    
        DataQuery query = session.prepareDataQuery(
                "DECLARE $word AS Utf8;" +
                        "UPSERT INTO blockwords (word) VALUES\n" +
                        "($word);")
                .join()
                .expect("query failed");
    
        Params params = query.newParams()
                .put("$word", PrimitiveValue.utf8(word));
        DataQueryResult result = query.execute(TxControl.serializableRw().setCommitTx(true), params)
                .join()
                .expect("query failed");
    }
}
