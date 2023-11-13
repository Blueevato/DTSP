package org.example.fabricjava;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.ChaincodeEventListener;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.channels.Channel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.function.Consumer;


import java.util.regex.*;

@Slf4j
public class EventListenUtil {
    /**
     * @title: FileUtil
     * @author valentinebeats
     * @version 1.0
     * @date 2022/11/27
     */
    //仅监听特定事件
    public static void listenSpecEvent (Contract contract, String eventName) {
        contract.addContractListener(new Consumer<ContractEvent>() {
            @Override
            public void accept(ContractEvent contractEvent) {
                Optional<byte[]> payload = contractEvent.getPayload();
                System.out.println(new String(payload.get()));
                System.out.println("1");
            }
        },eventName);
    }

    //监听所有事件
    public static void listenAllEvent(Contract contract) {
        contract.addContractListener(new Consumer<ContractEvent>() {
            @Override
            public void accept(ContractEvent contractEvent) {
                System.out.println(new String(contractEvent.getPayload().get()));
                System.out.println("2");
            }
        });
    }

    public static void waitEvent(int timeout) throws InterruptedException {
        Thread.currentThread().sleep(timeout * 100);
    }

}
