package com.beloll.blockword;

import com.yandex.ydb.auth.iam.CloudAuthProvider;
import com.yandex.ydb.core.auth.AuthProvider;
import com.yandex.ydb.core.grpc.GrpcTransport;
import com.yandex.ydb.table.Session;
import com.yandex.ydb.table.TableClient;
import com.yandex.ydb.table.rpc.grpc.GrpcTableRpc;
import yandex.cloud.sdk.auth.provider.CredentialProvider;
import yandex.cloud.sdk.auth.provider.OauthCredentialProvider;

import java.time.Duration;

public class DBConnector {
    
    public DBConnector() {
    }
    
    public Session connect() {
        String database = System.getenv("DATABASE");
        String oauthToken = System.getenv("YC_TOKEN");
        String endpoint = System.getenv("ENDPOINT");
    
        CredentialProvider credentialProvider = OauthCredentialProvider.builder()
                .oauth(oauthToken)
                .build();
        AuthProvider authProvider = CloudAuthProvider.newAuthProvider(credentialProvider);
    
        GrpcTransport transport = GrpcTransport.forEndpoint(endpoint, database)
                .withAuthProvider(authProvider)
                .withSecureConnection()
                .build();
    
        TableClient tableClient = TableClient.newClient(GrpcTableRpc.useTransport(transport))
                .build();
    
        tableClient.getOrCreateSession(Duration.ofSeconds(10))
                .join()
                .expect("ok");
        Session session = tableClient.createSession()
                .join()
                .expect("cannot create session");
        return session;
    }
}
