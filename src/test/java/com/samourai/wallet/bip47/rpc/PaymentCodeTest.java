package com.samourai.wallet.bip47.rpc;

import com.samourai.wallet.bip47.BIP47UtilGeneric;
import com.samourai.wallet.bip47.rpc.secretPoint.SecretPointFactory;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.segwit.SegwitAddress;
import com.samourai.wallet.utils.TestUtils;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PaymentCodeTest {
    private static final NetworkParameters params = TestNet3Params.get();
    private static final BIP47UtilGeneric bip47Util = BIP47UtilGeneric.getInstance();

    private SecretPointFactory secretPointFactory = new SecretPointFactory();

    @Test
    public void testPaymentCode() throws Exception {
        HD_Wallet bip44Wallet1 = TestUtils.generateWallet(44, params);
        HD_Wallet bip44Wallet2 = TestUtils.generateWallet(44, params);

        BIP47Wallet bip47Wallet1 = new BIP47Wallet(47, bip44Wallet1, 1);
        BIP47Wallet bip47Wallet2 = new BIP47Wallet(47, bip44Wallet2, 1);

        PaymentCode paymentCode1 = new PaymentCode(bip47Wallet1.getAccount(0).getPaymentCode());
        PaymentCode paymentCode2 = new PaymentCode(bip47Wallet2.getAccount(0).getPaymentCode());

        int idx = 0;

        // calculate send addresses
        SegwitAddress sendAddress1 = bip47Util.getSendAddress(bip47Wallet1, paymentCode2, idx, params).getSegwitAddressSend(secretPointFactory);
        SegwitAddress sendAddress2 = bip47Util.getSendAddress(bip47Wallet2, paymentCode1, idx, params).getSegwitAddressSend(secretPointFactory);

        // calculate receive addresses
        SegwitAddress receiveAddress1 = bip47Util.getReceiveAddress(bip47Wallet1, paymentCode2, idx, params).getSegwitAddressReceive(secretPointFactory);
        SegwitAddress receiveAddress2 = bip47Util.getReceiveAddress(bip47Wallet2, paymentCode1, idx, params).getSegwitAddressReceive(secretPointFactory);

        // mutual confrontation should give same result
        Assertions.assertEquals(sendAddress1.getBech32AsString(), receiveAddress2.getBech32AsString());
        Assertions.assertEquals(receiveAddress1.getBech32AsString(), sendAddress2.getBech32AsString());
    }

}
